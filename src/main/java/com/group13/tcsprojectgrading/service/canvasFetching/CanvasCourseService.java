package com.group13.tcsprojectgrading.service.canvasFetching;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group13.tcsprojectgrading.model.course.Course;
import com.group13.tcsprojectgrading.model.user.Account;
import com.group13.tcsprojectgrading.service.course.CourseService;
import com.group13.tcsprojectgrading.service.user.AccountService;
import com.group13.tcsprojectgrading.service.user.StudentService;
import com.group13.tcsprojectgrading.service.user.TeacherService;
import com.group13.tcsprojectgrading.service.user.TeachingAssistantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Iterator;

import static com.group13.tcsprojectgrading.utils.DefaultCanvasUrls.*;

@Service
public class CanvasCourseService {

    private WebClient webClient;

    private AccountService accountService;

    private CourseService courseService;

    private TeacherService teacherService;

    private TeachingAssistantService teachingAssistantService;

    private StudentService studentService;

    @Autowired
    public CanvasCourseService(WebClient webClient, AccountService accountService, CourseService courseService, TeacherService teacherService, TeachingAssistantService teachingAssistantService, StudentService studentService) {
        this.webClient = webClient;
        this.accountService = accountService;
        this.courseService = courseService;
        this.teacherService = teacherService;
        this.teachingAssistantService = teachingAssistantService;
        this.studentService = studentService;
    }

    public void syncParticipants(String token, String course_id) throws JsonProcessingException {

        Mono<String> response = webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme(SCHEME)
                        .host(HOST)
                        .path(COURSE_USERS_PATH)
                        .queryParam("include[]", "enrollments")
                        .build(course_id))
                .headers(h -> h.setBearerAuth(token))
                .retrieve()
                .bodyToMono(String.class);

        String res = response.block();
        System.out.println(res);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(res);

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
