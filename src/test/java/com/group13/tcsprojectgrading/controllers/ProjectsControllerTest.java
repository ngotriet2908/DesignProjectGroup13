package com.group13.tcsprojectgrading.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.group13.tcsprojectgrading.canvas.api.CanvasApi;
import com.group13.tcsprojectgrading.canvas.api.CanvasCoursesApi;
import com.group13.tcsprojectgrading.canvas.api.CanvasUsersApi;
import com.group13.tcsprojectgrading.models.*;
import com.group13.tcsprojectgrading.models.grading.CriterionGrade;
import com.group13.tcsprojectgrading.models.grading.Grade;
import com.group13.tcsprojectgrading.models.rubric.Element;
import com.group13.tcsprojectgrading.models.rubric.Rubric;
import com.group13.tcsprojectgrading.models.rubric.RubricContent;
import com.group13.tcsprojectgrading.models.rubric.RubricGrade;
import com.group13.tcsprojectgrading.services.*;
import com.group13.tcsprojectgrading.services.grading.AssessmentService;
import com.group13.tcsprojectgrading.services.rubric.RubricService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.extractProperty;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ProjectsControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RubricService rubricService;
    @MockBean
    private GraderService graderService;
    @MockBean
    private ProjectService projectService;
    @MockBean
    private SubmissionService submissionService;
    @MockBean
    private AssessmentService assessmentService;
    @MockBean
    private ParticipantService participantService;
    @MockBean
    private CanvasApi canvasApi;
    @MockBean
    private CanvasCoursesApi canvasCoursesApi;
    @MockBean
    private CanvasUsersApi canvasUsersApi;


    private final String courseId = "1";
    private final String projectId = "2";
    private final String submissionId = "3";
    private final String participantId = "5";

    private final UUID assessmentUuid = new UUID(1, 1);
    private final String assessmentId = assessmentUuid.toString();
    private final Assessment testAssessment = new Assessment(assessmentUuid);
    private final String userId = "44";
    Project testProject = new Project(courseId, projectId, "test", "test", "0");
    Submission testSubmission = new Submission();
    Participant testParticipant = new Participant();
    Rubric testRubric = new Rubric();

    @WithMockUser(username = userId)
    @Test
    public void getProjectTeacher() throws Exception {
        String enrollmentId = "2";

        ObjectNode testCourse = objectMapper.createObjectNode();
        testCourse.put("id", courseId);
        testCourse.put("name", "Test Course 1");

        Grader testGrader = new Grader(testProject, userId, "John Doe", new ProjectRole(testProject, new Role(), List.of(new Privilege())));


        ArrayNode enrollmentsPage = objectMapper.createArrayNode();
        ObjectNode testEnrollment = objectMapper.createObjectNode();
        testEnrollment.put("id", enrollmentId);
        testEnrollment.put("course_id", courseId);
        testEnrollment.put("role", "TeacherEnrollment");

        ObjectNode testUser = objectMapper.createObjectNode();
        testUser.put("id", userId);
        testUser.put("role", "teacher");

        enrollmentsPage.add(testEnrollment);

        ObjectNode getProjectNode = objectMapper.createObjectNode();
        getProjectNode.set("project", testProject.convertToJson());
        getProjectNode.set("rubric", objectMapper.readTree(objectMapper.writeValueAsString(testRubric)));
        getProjectNode.set("grader", testGrader.getGraderJson());
        ObjectNode expectedNode = getProjectNode.deepCopy();
        expectedNode.set("course", testCourse);

        when(canvasApi.getCanvasCoursesApi()).thenReturn(canvasCoursesApi);
        when(canvasApi.getCanvasUsersApi()).thenReturn(canvasUsersApi);
        when(canvasCoursesApi.getCourseUser(courseId, userId)).thenReturn(testUser.toString());
        when(canvasCoursesApi.getUserCourse(courseId)).thenReturn(testCourse.toString());
        when(canvasUsersApi.getEnrolments(userId)).thenReturn(List.of(enrollmentsPage.toString()));
        when(projectService.getProject(courseId, projectId, RoleEnum.TEACHER, testUser, userId))
                .thenReturn(getProjectNode);


        mockMvc.perform(get("/api/courses/{courseId}/projects/{projectId}", courseId, projectId, submissionId, assessmentId))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedNode.toString()));
    }

    @WithMockUser(username = userId)
    @Test
    public void getProjectTa() throws Exception {
        String enrollmentId = "2";

        ObjectNode testCourse = objectMapper.createObjectNode();
        testCourse.put("id", courseId);
        testCourse.put("name", "Test Course 1");

        Rubric testRubric = new Rubric();
        Grader testGrader = new Grader(testProject, userId, "John Doe", new ProjectRole(testProject, new Role(), List.of(new Privilege())));


        ArrayNode enrollmentsPage = objectMapper.createArrayNode();
        ObjectNode testEnrollment = objectMapper.createObjectNode();
        testEnrollment.put("id", enrollmentId);
        testEnrollment.put("course_id", courseId);
        testEnrollment.put("role", "TaEnrollment");

        ObjectNode testUser = objectMapper.createObjectNode();
        testUser.put("id", userId);
        testUser.put("role", "ta");

        enrollmentsPage.add(testEnrollment);

        ObjectNode getProjectNode = objectMapper.createObjectNode();
        getProjectNode.set("project", testProject.convertToJson());
        getProjectNode.set("rubric", objectMapper.readTree(objectMapper.writeValueAsString(testRubric)));
        getProjectNode.set("grader", testGrader.getGraderJson());
        ObjectNode expectedNode = getProjectNode.deepCopy();
        expectedNode.set("course", testCourse);

        when(canvasApi.getCanvasCoursesApi()).thenReturn(canvasCoursesApi);
        when(canvasApi.getCanvasUsersApi()).thenReturn(canvasUsersApi);
        when(canvasCoursesApi.getCourseUser(courseId, userId)).thenReturn(testUser.toString());
        when(canvasCoursesApi.getUserCourse(courseId)).thenReturn(testCourse.toString());
        when(canvasUsersApi.getEnrolments(userId)).thenReturn(List.of(enrollmentsPage.toString()));
        when(projectService.getProject(courseId, projectId, RoleEnum.TA, testUser, userId))
                .thenReturn(getProjectNode);


        mockMvc.perform(get("/api/courses/{courseId}/projects/{projectId}", courseId, projectId))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedNode.toString()));
    }

    @WithMockUser
    @Test
    public void getProjectGraders() throws Exception {
        Grader grader1 = new Grader(testProject, userId, "John Doe", new ProjectRole(testProject, new Role(), List.of(new Privilege())));
        Grader grader2 = new Grader(testProject, "8", "Jane Doe", new ProjectRole(testProject, new Role(), List.of(new Privilege())));
        ArrayNode expectedNode = objectMapper.createArrayNode();
        expectedNode.add(grader1.getGraderJson());
        expectedNode.add(grader2.getGraderJson());
        when(projectService.getProjectsGrader(courseId, projectId)).thenReturn(List.of(grader1, grader2));
        mockMvc.perform(get("/api/courses/{courseId}/projects/{projectId}/graders", courseId, projectId))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedNode.toString()));
    }

    @WithMockUser
    @Test
    public void sendFeedbackProjectNotFound() throws Exception {
        ObjectNode feedback = objectMapper.createObjectNode();
        feedback.put("id", participantId);
        feedback.put("isGroup", true);
        feedback.put("body", submissionId);
        feedback.put("subject", "A subject.");

        when(projectService.getProjectById(courseId, projectId)).thenReturn(null);

        mockMvc.perform(post("/api/courses/{courseId}/projects/{projectId}/feedback", courseId, projectId)
                .contentType("application/json")
                .content(feedback.toString())
                .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @WithMockUser
    @Test
    public void sendFeedbackPdfProjectNotFound() throws Exception {
        ObjectNode feedback = objectMapper.createObjectNode();
        feedback.put("id", participantId);
        feedback.put("isGroup", true);
        feedback.put("body", submissionId);
        feedback.put("subject", "A subject.");

        when(projectService.getProjectById(courseId, projectId)).thenReturn(null);

        mockMvc.perform(post("/api/courses/{courseId}/projects/{projectId}/feedbackPdf", courseId, projectId)
                .contentType("application/json")
                .content(feedback.toString())
                .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @WithMockUser
    @Test
    public void sendFeedbackProjectOk() throws Exception {
        ObjectNode feedback = objectMapper.createObjectNode();
        feedback.put("id", participantId);
        feedback.put("isGroup", true);
        feedback.put("body", submissionId);
        feedback.put("subject", "A subject.");

        when(projectService.getProjectById(courseId, projectId)).thenReturn(testProject);
        when(canvasApi.getCanvasUsersApi()).thenReturn(canvasUsersApi);


        mockMvc.perform(post("/api/courses/{courseId}/projects/{projectId}/feedback", courseId, projectId)
                .contentType("application/json")
                .content(feedback.toString())
                .with(csrf()))
                .andExpect(status().isOk());
    }


    //TODO: perhaps test the contents of the pdf, seems very complicated
    @WithMockUser
    @Test
    public void sendFeedbackPdfOk() throws Exception {
        ObjectNode feedback = objectMapper.createObjectNode();
        feedback.put("id", participantId);
        feedback.put("isGroup", true);
        feedback.put("body", submissionId);
        feedback.put("subject", "A subject.");

        when(projectService.getProjectById(courseId, projectId)).thenReturn(testProject);
        when(participantService.findParticipantWithId(participantId, testProject)).thenReturn(testParticipant);
        when(submissionService.findSubmissionById(submissionId)).thenReturn(testSubmission);
        when(rubricService.getRubricById(projectId)).thenReturn(testRubric);
        when(canvasApi.getCanvasUsersApi()).thenReturn(canvasUsersApi);


        mockMvc.perform(post("/api/courses/{courseId}/projects/{projectId}/feedback", courseId, projectId)
                .contentType("application/json")
                .content(feedback.toString())
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @WithMockUser
    @Test
    public void getFeedbackInfoPageProjectNotFound() throws Exception {
        when(projectService.getProjectById(courseId, projectId)).thenReturn(null);

        mockMvc.perform(get("/api/courses/{courseId}/projects/{projectId}/feedback", courseId, projectId))
                .andExpect(status().isNotFound());
    }

    @WithMockUser
    @Test
    public void getFeedbackInfoPageOk() throws Exception {
        ObjectNode projectResponse = objectMapper.createObjectNode();
        ObjectNode courseResponse = objectMapper.createObjectNode();
        ArrayNode participantResponse = objectMapper.createArrayNode();

        projectResponse.put("id", projectId);
        projectResponse.put("name", "test");
        projectResponse.put("description", "test");
        projectResponse.put("created_at", 0);

        courseResponse.put("id", courseId);
        courseResponse.put("name", "Test Course 1");

        ObjectNode participant1 = objectMapper.createObjectNode();
        ObjectNode participant2 = objectMapper.createObjectNode();

        participant1.put("id", "631");
        participant2.put("id", "431");

        participantResponse.add(participant1);
        participantResponse.add(participant2);

        ObjectNode expected = objectMapper.createObjectNode();
        expected.set("users", participantResponse);
        expected.set("course", courseResponse);
        expected.set("project", projectResponse);

        when(projectService.getProjectById(courseId, projectId)).thenReturn(testProject);
        when(canvasApi.getCanvasCoursesApi()).thenReturn(canvasCoursesApi);
        when(canvasCoursesApi.getCourseProject(courseId, projectId)).thenReturn(projectResponse.toString());
        when(canvasCoursesApi.getUserCourse(courseId)).thenReturn(courseResponse.toString());
        when(canvasCoursesApi.getCourseParticipants(courseId)).thenReturn(List.of(participantResponse.toString()));

        mockMvc.perform(get("/api/courses/{courseId}/projects/{projectId}/feedback", courseId, projectId))
                .andExpect(status().isOk())
                .andExpect(content().json(expected.toString()));
    }
}