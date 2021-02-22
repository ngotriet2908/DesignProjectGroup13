package com.group13.tcsprojectgrading.service.canvasFetching;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group13.tcsprojectgrading.model.course.Course;
import com.group13.tcsprojectgrading.model.user.Account;
import com.group13.tcsprojectgrading.model.user.Participant;
import com.group13.tcsprojectgrading.service.course.CourseService;
import com.group13.tcsprojectgrading.service.user.AccountService;
import com.group13.tcsprojectgrading.service.user.StudentService;
import com.group13.tcsprojectgrading.service.user.TeacherService;
import com.group13.tcsprojectgrading.service.user.TeachingAssistantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static com.group13.tcsprojectgrading.utils.DefaultCanvasUrls.*;

@Service
public class CanvasUserService {

    private WebClient webClient;

    private AccountService accountService;

    private CourseService courseService;

    private StudentService studentService;

    private TeachingAssistantService teachingAssistantService;

    private TeacherService teacherService;

    @Autowired
    public CanvasUserService(WebClient webClient, AccountService accountService, CourseService courseService, StudentService studentService, TeachingAssistantService teachingAssistantService, TeacherService teacherService) {
        this.webClient = webClient;
        this.accountService = accountService;
        this.courseService = courseService;
        this.studentService = studentService;
        this.teachingAssistantService = teachingAssistantService;
        this.teacherService = teacherService;
    }

    public void addNewAccount(String token) throws JsonProcessingException {
        JsonNode jsonNode = sendWebClient(token, PROFILE_URL);
        accountService.addNewAccount(new Account(
                jsonNode.get("id").asText(),
                jsonNode.get("name").asText(),
                jsonNode.get("login_id").asText(),
                jsonNode.get("short_name").asText(),
                jsonNode.get("sortable_name").asText(),
                jsonNode.get("primary_email").asText()
        ));
    }

    public void selfSyncCourseAndUser(String token, String userId) throws JsonProcessingException {
        List<String> jsonNodeStringList = null;
        try {
            jsonNodeStringList = sendWebClientWithPagination(token, new URI(SELF_COURSE_URL));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        for (String jsonNodeString: jsonNodeStringList) {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonNodeString);
            
            for (Iterator<JsonNode> it = jsonNode.elements(); it.hasNext(); ) {
                JsonNode node = it.next();
                Course course = courseService.addNewCourse(new Course(
                        node.get("id").asText(),
                        node.get("name").asText(),
                        node.get("course_code").asText()));

                JsonNode enrollmentsNode = node.get("enrollments");
                for (Iterator<JsonNode> iter = enrollmentsNode.elements(); iter.hasNext(); ) {
                    JsonNode enroll = iter.next();
                    if (enroll.get("user_id").asText().equals(userId)) {
                        //TODO add the other two roles

                        if (enroll.get("role").asText().equals(TEACHER_ROLE)) {
                            Account account = accountService.findAccountById(userId);
                            if (account != null) {
                                teacherService.addNewTeacher(course, account);
                            }
                        } else if (enroll.get("role").asText().equals(TA_ROLE)) {
                            Account account = accountService.findAccountById(userId);
                            if (account != null) {
                                teachingAssistantService.addNewTA(course, account);
                            }
                        } else {
                            System.out.println("Invalid role for sign in user: " + node.get("name") + " -> "+ enroll.get("role"));
                        }
                    }
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
                    System.out.println(next);
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
