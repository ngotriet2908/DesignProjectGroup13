package com.group13.tcsprojectgrading.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.group13.tcsprojectgrading.canvas.api.CanvasApi;
import com.group13.tcsprojectgrading.canvas.api.CanvasCoursesApi;
import com.group13.tcsprojectgrading.canvas.api.CanvasUsersApi;
import com.group13.tcsprojectgrading.models.*;
import com.group13.tcsprojectgrading.models.rubric.Rubric;
import com.group13.tcsprojectgrading.services.*;
import com.group13.tcsprojectgrading.services.grading.AssessmentService;
import com.group13.tcsprojectgrading.services.rubric.RubricService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ProjectsManagementControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProjectsManagementService projectsManagementService;
    @MockBean
    private CanvasApi canvasApi;
    @MockBean
    private CanvasCoursesApi canvasCoursesApi;


    private final String courseId = "1";
    private final String projectId = "2";

    private final UUID assessmentUuid = new UUID(1, 1);
    private final String assessmentId = assessmentUuid.toString();
    private final Assessment testAssessment = new Assessment(assessmentUuid);
    private final String userId = "44";
    Project testProject = new Project(courseId, projectId, "test", "test", "0");

    @WithMockUser
    @Test
    public void addGraderOk() {
        ObjectNode enrollment1 = objectMapper.createObjectNode();
        enrollment1.put("id", "1");
        enrollment1.put("course_id", courseId);
        enrollment1.put("role", "TaEnrollment");
        ArrayNode enrollments1 = objectMapper.createArrayNode();
        enrollments1.add(enrollment1);

        ObjectNode enrollment2 = objectMapper.createObjectNode();
        enrollment2.put("id", "2");
        enrollment2.put("course_id", courseId);
        enrollment2.put("role", "TaEnrollment");
        ArrayNode enrollments2 = objectMapper.createArrayNode();
        enrollments2.add(enrollment2);

        ObjectNode enrollment3 = objectMapper.createObjectNode();
        enrollment3.put("id", "3");
        enrollment3.put("course_id", courseId);
        enrollment3.put("role", "TaEnrollment");
        ArrayNode enrollments3 = objectMapper.createArrayNode();
        enrollments3.add(enrollment3);

        ObjectNode testGrader1 = objectMapper.createObjectNode();
        testGrader1.put("id", userId);
        testGrader1.set("enrollments", enrollments1);

        ObjectNode testGrader2 = objectMapper.createObjectNode();
        testGrader2.put("id", "82");
        testGrader2.set("enrollments", enrollments2);

        ObjectNode graderToBeAdded = objectMapper.createObjectNode();
        graderToBeAdded.put("id", "35");
        graderToBeAdded.set("enrollments", enrollments3);
        ArrayNode gradersToBeAdded = objectMapper.createArrayNode();

        ArrayNode testGradersPage = objectMapper.createArrayNode();
        testGradersPage.add(testGrader1);
        testGradersPage.add(testGrader2);

        when(canvasApi.getCanvasCoursesApi()).thenReturn(canvasCoursesApi);
        when(canvasCoursesApi.getCourseGraders(courseId)).thenReturn(List.of(testGradersPage.toString()));

//        mockMvc.perform(get("/api/courses/{courseId}/projects/{projectId}/management/addGraders", courseId, projectId))
//                .andExpect(status().isOk())
//                .andExpect(content().json(expectedNode.toString()));
    }
}
