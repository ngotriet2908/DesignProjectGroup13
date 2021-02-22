package com.group13.tcsprojectgrading.service.canvasFetching;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group13.tcsprojectgrading.model.course.Course;
import com.group13.tcsprojectgrading.model.project.CourseGroup;
import com.group13.tcsprojectgrading.model.project.CourseGroupCategory;
import com.group13.tcsprojectgrading.model.project.Project;
import com.group13.tcsprojectgrading.model.user.Account;
import com.group13.tcsprojectgrading.model.user.Student;
import com.group13.tcsprojectgrading.service.course.CourseService;
import com.group13.tcsprojectgrading.service.project.CourseGroupCategoryService;
import com.group13.tcsprojectgrading.service.project.CourseGroupService;
import com.group13.tcsprojectgrading.service.project.ProjectService;
import com.group13.tcsprojectgrading.service.user.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.*;

import static com.group13.tcsprojectgrading.utils.DefaultCanvasUrls.*;

@Service
public class CanvasCourseService {

    private WebClient webClient;

    private AccountService accountService;

    private CourseService courseService;

    private TeacherService teacherService;

    private TeachingAssistantService teachingAssistantService;

    private StudentService studentService;

    private CourseGroupCategoryService courseGroupCategoryService;

    private CourseGroupService courseGroupService;

    private GroupParticipantService groupParticipantService;

    private ProjectService projectService;

    @Autowired
    public CanvasCourseService(WebClient webClient, AccountService accountService, CourseService courseService, TeacherService teacherService, TeachingAssistantService teachingAssistantService, StudentService studentService, CourseGroupCategoryService courseGroupCategoryService, CourseGroupService courseGroupService, GroupParticipantService groupParticipantService, ProjectService projectService) {
        this.webClient = webClient;
        this.accountService = accountService;
        this.courseService = courseService;
        this.teacherService = teacherService;
        this.teachingAssistantService = teachingAssistantService;
        this.studentService = studentService;
        this.courseGroupCategoryService = courseGroupCategoryService;
        this.courseGroupService = courseGroupService;
        this.groupParticipantService = groupParticipantService;
        this.projectService = projectService;
    }

    public void syncCourseProjects(String token, String course_id) throws JsonProcessingException {
        URI uriComponents = UriComponentsBuilder.newInstance()
                .scheme(SCHEME)
                .host(HOST)
                .path(COURSE_ASSIGNMENT_PATH)
                .build(course_id);

        List<String> jsonNodeStringList = sendWebClientWithPagination(token, uriComponents);

        Course course = courseService.findCourseById(course_id);

        for (String jsonNodeString: jsonNodeStringList) {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonNodeString);

            for (Iterator<JsonNode> it = jsonNode.elements(); it.hasNext(); ) {
                JsonNode node = it.next();

                Project project = new Project(
                        node.get("id").asLong(),
                        node.get("name").asText(),
                        node.get("points_possible").asDouble(),
                        course
                );
                if (node.get("group_category_id") != null && !node.get("group_category_id").asText().equals("null"))
                    project.setGroupCategoryCanvasId(node.get("group_category_id").asLong());
                if (node.get("due_at") != null && !node.get("group_category_id").asText().equals("null")) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                    dateFormat.setTimeZone(TimeZone.getTimeZone(ZoneId.of("UTC")));
                    try {
                        Date parsedDate = dateFormat.parse(node.get("due_at").asText());
                        Timestamp timestamp = new Timestamp(parsedDate.getTime());
                        project.setDeadline(timestamp);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                projectService.addNewProject(project);

            }
        }

    }

    public void syncParticipants(String token, String course_id) throws JsonProcessingException {
        URI uriComponents = UriComponentsBuilder.newInstance()
                .scheme(SCHEME)
                .host(HOST)
                .path(COURSE_USERS_PATH)
                .queryParam("include[]", "enrollments")
                .build(course_id);

        List<String> jsonNodeStringList = sendWebClientWithPagination(token, uriComponents);

        for(String jsonNodeString: jsonNodeStringList) {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonNodeString);

            Course course = courseService.findCourseById(course_id);

            for (Iterator<JsonNode> it = jsonNode.elements(); it.hasNext(); ) {
                JsonNode node = it.next();

                Account account = accountService.addNewAccount(new Account(
                        node.get("id").asText(),
                        node.get("name").asText(),
                        node.get("login_id").asText(),
                        node.get("short_name").asText(),
                        node.get("sortable_name").asText(),
                        node.get("email").asText()
                ));

                JsonNode enrollmentsNode = node.get("enrollments");

                label:
                for (Iterator<JsonNode> iter = enrollmentsNode.elements(); iter.hasNext(); ) {
                    JsonNode enroll = iter.next();
                    if (enroll.get("user_id").asText().equals(account.getId()) &&
                            enroll.get("course_id").asText().equals(course.getId())) {
                        switch (enroll.get("type").asText()) {
                            case TEACHER_ROLE:
                                teacherService.addNewTeacher(course, account);
                                break label;
                            case STUDENT_ROLE:
                                studentService.addNewStudent(course, account);
                                break label;
                            case TA_ROLE:
                                teachingAssistantService.addNewTA(course, account);
                                break label;
                            default:
                                System.out.println("Unknown type: " + enroll.get("type").asText());
                                break label;
                        }
                    }
                }
            }
        }
    }

    public void syncCourseGroupCategory(String token, String course_id) throws JsonProcessingException {
        URI uriComponents = UriComponentsBuilder.newInstance()
                .scheme(SCHEME)
                .host(HOST)
                .path(COURSE_GROUP_CATEGORY_PATH)
                .build(course_id);

        List<String> jsonNodeStringList = sendWebClientWithPagination(token, uriComponents);

        for (String jsonNodeString: jsonNodeStringList) {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonNodeString);

            for (Iterator<JsonNode> it = jsonNode.elements(); it.hasNext(); ) {
                JsonNode node = it.next();

                Course course = courseService.findCourseById(node.get("course_id").asText());

                courseGroupCategoryService.addNewCourseGroupCategory(new CourseGroupCategory(
                        node.get("id").asText(),
                        node.get("name").asText(),
                        course));
            }
        }

    }

    public void syncCourseGroups(String token, String course_id) throws JsonProcessingException {
        URI uriComponents = UriComponentsBuilder.newInstance()
                .scheme(SCHEME)
                .host(HOST)
                .path(COURSE_GROUPS_PATH)
                .build(course_id);

        List<String> jsonNodeStringList = sendWebClientWithPagination(token, uriComponents);

        for (String jsonNodeString: jsonNodeStringList) {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonNodeString);

            for (Iterator<JsonNode> it = jsonNode.elements(); it.hasNext(); ) {
                JsonNode node = it.next();

                CourseGroupCategory category = courseGroupCategoryService.findById(node.get("group_category_id").asText());
//                System.out.println(category);

                courseGroupService.addCourseGroup(new CourseGroup(
                        node.get("id").asText(),
                        node.get("name").asText(),
                        node.get("max_membership").asLong(),
                        node.get("members_count").asLong(),
                        category));
            }
        }

    }

    public void syncCourseGroupParticipant(String token, String course_id) throws JsonProcessingException {
        List<CourseGroup> courseGroups = courseGroupService.getCourseGroups();
        Course course = courseService.findCourseById(course_id);

        for(CourseGroup courseGroup: courseGroups) {
            if (courseGroup.getMembers_count() == 0) continue;
            if (courseGroup.getCanvas_id() == null) {
                //TODO create single group
            }

            URI uriComponents = UriComponentsBuilder.newInstance()
                    .scheme(SCHEME)
                    .host(HOST)
                    .path(COURSE_GROUP_MEMBERSHIP_PATH)
                    .build(courseGroup.getCanvas_id());

            List<String> jsonNodeStringList = sendWebClientWithPagination(token, uriComponents);

            for (String jsonNodeString: jsonNodeStringList) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(jsonNodeString);

                for (Iterator<JsonNode> it = jsonNode.elements(); it.hasNext(); ) {
                    JsonNode node = it.next();
                    Account account = accountService.findAccountById(node.get("user_id").asText());
                    Student student = studentService.findStudentByAccountAndCourse(account, course);
                    groupParticipantService.addGroupParticipant(
                            student,
                            courseGroup,
                            node.get("id").asLong()
                    );
                }
            }
        }
    }

    private JsonNode sendWebClient(String token, String url) throws JsonProcessingException {
        Mono<String> response = webClient
                .get()
                .uri(url)
                .headers(h -> h.setBearerAuth(token))
                .retrieve()
                .bodyToMono(String.class);

        String res = response.block();
        System.out.println(res);

        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.readTree(res);
    }

    public List<String> sendWebClientWithPagination(String token, URI url) {
        System.out.println(url);

        Mono<List<String>> entityMono = fetchItems(url, token)
                .expand(response -> {
                    String headerLink = response.getHeaders().get("Link").get(0);
                    String[] links = headerLink.split(",");
                    String next = null;
                    for(String link: links) {
                        String actual_link = link.split("; ")[0].substring(1, link.split("; ")[0].length() - 1);
                        String header_link = link.split("; ")[1];
                        if (header_link.contains("next")) {
                            next = actual_link;
                        }
                    }
                    if (next == null) {
                        System.out.println("next == null");
                        return Mono.empty();
                    }
                    URI uri = null;
                    try {
                        uri = new URI(next);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                    return fetchItems(uri, token);
                }).flatMap(clientResponse -> Mono.just(clientResponse.getBody())).collectList();

        List<String> res = entityMono.block();
        System.out.println(Arrays.toString(res.toArray()));
        return res;
    }

    private Mono<ResponseEntity<String>> fetchItems(URI url, String token) {
        return webClient
                .get()
                .uri(url)
                .headers(h -> h.setBearerAuth(token))
                .retrieve()
                .toEntity(String.class);
    }
}
