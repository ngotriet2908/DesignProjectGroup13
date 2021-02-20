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
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Iterator;

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

    public Participant addNewParticipant(String token, String userId) throws JsonProcessingException {
        JsonNode jsonNode = sendWebClient(token, SELF_COURSE_URL);
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
                    if (enroll.get("role").asText().equals(TEACHER_ROLE)) {
                        Account account = accountService.findAccountById(userId);
                        if (account != null) {
                            return teacherService.addNewTeacher(course, account);
                        }
                    }
                }
            }
        }
        return null;
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
        JsonNode jsonNode = objectMapper.readTree(res);

        return jsonNode;
    }
}
