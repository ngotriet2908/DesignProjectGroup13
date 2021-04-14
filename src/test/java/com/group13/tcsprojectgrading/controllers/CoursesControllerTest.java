//package com.group13.tcsprojectgrading.controllers;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.node.ArrayNode;
//import com.fasterxml.jackson.databind.node.ObjectNode;
//import com.group13.tcsprojectgrading.canvas.api.CanvasApi;
//import com.group13.tcsprojectgrading.canvas.api.CanvasCoursesApi;
//import com.group13.tcsprojectgrading.canvas.api.CanvasUsersApi;
//import com.group13.tcsprojectgrading.models.Project;
//import com.group13.tcsprojectgrading.services.*;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mock;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.security.core.parameters.P;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.Collections;
//import java.util.List;
//
//import static org.mockito.Mockito.doReturn;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//public class CoursesControllerTest {
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @MockBean
//    private CourseServices courseServices;
//
//    @MockBean
//    private ProjectService projectService;
//
//    @MockBean
//    private CanvasApi canvasApi;
//
//    @Mock
//    private CanvasCoursesApi canvasCoursesApi;
//
//    @Mock
//    private CanvasUsersApi canvasUsersApi;
//
//    private final String userId = "44";
//    private final String courseId = "1";
//
//    @Test
//    public void getCoursesUnauthorized() throws Exception {
//        when(canvasApi.getCanvasCoursesApi()).thenReturn(new CanvasCoursesApi(canvasApi));
//        when(canvasApi.getCanvasCoursesApi().getUserCourseList()).thenReturn(null);
//
//        mockMvc.perform(get("/api/courses")).andExpect(status().isUnauthorized());
//    }
//
//    @WithMockUser
//    @Test
//    public void getCoursesOk() throws Exception {
//        ArrayNode responsePage = objectMapper.createArrayNode();
//
//        ObjectNode enrollment1 = objectMapper.createObjectNode();
//        enrollment1.put("id",1);
//        enrollment1.put("role", "TA");
//
//        ArrayNode enrollments1 = objectMapper.createArrayNode();
//        enrollments1.add(enrollment1);
//
//        ObjectNode testCourse1 = objectMapper.createObjectNode();
//        testCourse1.put("id",1);
//        testCourse1.put("name", "Test Course 1");
//        testCourse1.set("enrollments", enrollments1);
//
//        ObjectNode enrollment2 = objectMapper.createObjectNode();
//        enrollment2.put("id",2);
//        enrollment2.put("role", "Teacher");
//
//        ArrayNode enrollments2 = objectMapper.createArrayNode();
//        enrollments2.add(enrollment2);
//
//        ObjectNode testCourse2 = objectMapper.createObjectNode();
//        testCourse2.put("id", 2);
//        testCourse2.put("name", "Test Course 2");
//        testCourse2.set("enrollments", enrollments2);
//
//
//        responsePage.add(testCourse1);
//        responsePage.add(testCourse2);
//
//        List<String> response = List.of(responsePage.toString());
//
//        when(canvasApi.getCanvasCoursesApi()).thenReturn(canvasCoursesApi);
//        when(canvasCoursesApi.getUserCourseList()).thenReturn(response);
//        mockMvc.perform(get("/api/courses")).andExpect(status().isOk()).andExpect(content().json(responsePage.toString()));
//    }
//
//    @WithMockUser(username = userId)
//    @Test
//    public void getCourseNoEnrollment() throws Exception {
//        String courseId = "1";
//
//        ObjectNode testCourse = objectMapper.createObjectNode();
//        testCourse.put("id",courseId);
//        testCourse.put("name", "Test Course 1");
//
//        ObjectNode testUser = objectMapper.createObjectNode();
//        testUser.put("id", userId);
//
//        when(canvasApi.getCanvasCoursesApi()).thenReturn(canvasCoursesApi);
//        when(canvasApi.getCanvasUsersApi()).thenReturn(canvasUsersApi);
//        when(canvasCoursesApi.getUserCourse(courseId)).thenReturn(testCourse.toString());
//        when(canvasCoursesApi.getCourseUser(courseId, userId)).thenReturn(testUser.toString());
//        mockMvc.perform(get("/api/courses/{course_id}", courseId)).andExpect(status().isUnauthorized());
//    }
//
//    @WithMockUser(username = userId)
//    @Test
//    public void getCourseTA() throws Exception {
//        String enrollmentId = "2";
//        String projectId = "3";
//
//        ObjectNode testCourse = objectMapper.createObjectNode();
//        testCourse.put("id",courseId);
//        testCourse.put("name", "Test Course 1");
//
//        ObjectNode testUser = objectMapper.createObjectNode();
//        testUser.put("id", userId);
//
//        Project testProject = new Project(courseId, projectId, "Test", "Description", "2000");
//        ArrayNode projects = objectMapper.createArrayNode();
//
//        ObjectNode canvasProject = objectMapper.createObjectNode();
//        canvasProject.put("id", projectId);
//        canvasProject.put("name", "Test");
//        canvasProject.put("description", "Description");
//        canvasProject.put("created_at", "2000");
//        canvasProject.put("course_id", courseId);
//
//        projects.add(canvasProject);
//
//        ArrayNode enrollmentsPage = objectMapper.createArrayNode();
//        ObjectNode testEnrollment = objectMapper.createObjectNode();
//        testEnrollment.put("id", enrollmentId);
//        testEnrollment.put("course_id", courseId);
//        testEnrollment.put("role", "TaEnrollment");
//
//        ObjectNode expectedUser = objectMapper.createObjectNode();
//        expectedUser.put("id", userId);
//        expectedUser.put("role", "ta");
//
//        enrollmentsPage.add(testEnrollment);
//
//        ObjectNode expectedNode = objectMapper.createObjectNode();
//        expectedNode.set("course", testCourse);
//        expectedNode.set("projects", projects);
//        expectedNode.set("user", expectedUser);
//        String expected = expectedNode.toString();
//
//        when(canvasApi.getCanvasCoursesApi()).thenReturn(canvasCoursesApi);
//        when(canvasApi.getCanvasUsersApi()).thenReturn(canvasUsersApi);
//        when(canvasCoursesApi.getUserCourse(courseId)).thenReturn(testCourse.toString());
//        when(canvasCoursesApi.getCourseUser(courseId, userId)).thenReturn(testUser.toString());
//        when(canvasCoursesApi.getCourseProject(courseId, projectId)).thenReturn(canvasProject.toString());
//        when(canvasUsersApi.getEnrolments(userId)).thenReturn(List.of(enrollmentsPage.toString()));
//        when(courseServices.getProjectsInCourse(courseId)).thenReturn(List.of(testProject));
//
//        mockMvc.perform(get("/api/courses/{course_id}", courseId)).andExpect(status().isOk())
//                .andExpect(content().json(expected));
//    }
//
//    @WithMockUser(username = userId)
//    @Test
//    public void getCourseUserStudent() throws Exception {
//        String enrollmentId = "5";
//        ObjectNode enrollment = objectMapper.createObjectNode();
//        enrollment.put("id", enrollmentId);
//        enrollment.put("course_id", courseId);
//        enrollment.put("role", "StudentEnrollment");
//        ArrayNode enrollments = objectMapper.createArrayNode();
//        enrollments.add(enrollment);
//
//        ObjectNode testUser = objectMapper.createObjectNode();
//        testUser.put("id", userId);
//        testUser.set("enrollments", enrollments);
//
//        ArrayNode page = objectMapper.createArrayNode();
//        page.add(testUser);
//
//        when(canvasApi.getCanvasCoursesApi()).thenReturn(canvasCoursesApi);
//        when(canvasCoursesApi.getCourseStudents(courseId)).thenReturn(List.of(page.toString()));
//        mockMvc.perform(get("/api/courses/{course_id}/participants", courseId)
//        .param("role", "student"))
//                .andExpect(status().isOk())
//                .andExpect(content().json(page.toString()));
//    }
//
//    @WithMockUser(username = userId)
//    @Test
//    public void getCourseUserParticipant() throws Exception {
//        String enrollmentId = "5";
//        ObjectNode enrollment = objectMapper.createObjectNode();
//        enrollment.put("id", enrollmentId);
//        enrollment.put("course_id", courseId);
//        enrollment.put("role", "StudentEnrollment");
//        ArrayNode enrollments = objectMapper.createArrayNode();
//        enrollments.add(enrollment);
//
//        ObjectNode testUser = objectMapper.createObjectNode();
//        testUser.put("id", userId);
//        testUser.set("enrollments", enrollments);
//
//        ArrayNode page = objectMapper.createArrayNode();
//        page.add(testUser);
//
//        when(canvasApi.getCanvasCoursesApi()).thenReturn(canvasCoursesApi);
//        when(canvasCoursesApi.getCourseParticipants(courseId)).thenReturn(List.of(page.toString()));
//        mockMvc.perform(get("/api/courses/{course_id}/participants", courseId)
//                .param("role", "notstudent"))
//                .andExpect(status().isOk())
//                .andExpect(content().json(page.toString()));
//    }
//
//    @WithMockUser
//    @Test
//    public void getCourseProjects() throws Exception {
//        String project1Id = "3";
//        String project2Id = "7";
//        ObjectNode canvasProject1 = objectMapper.createObjectNode();
//        canvasProject1.put("id", project1Id);
//        canvasProject1.put("name", "Test 1");
//        canvasProject1.put("description", "Description");
//        canvasProject1.put("created_at", "2000");
//        canvasProject1.put("course_id", courseId);
//
//        ObjectNode canvasProject2 = objectMapper.createObjectNode();
//        canvasProject2.put("id", project2Id);
//        canvasProject2.put("name", "Test 2");
//        canvasProject2.put("description", "Description");
//        canvasProject2.put("created_at", "2000");
//        canvasProject2.put("course_id", courseId);
//
//        ArrayNode projects = objectMapper.createArrayNode();
//        projects.add(canvasProject1);
//        projects.add(canvasProject2);
//
//        ArrayNode expectedProjects = objectMapper.createArrayNode();
//
//        ObjectNode expected1 = objectMapper.createObjectNode();
//        expected1.put("id", project1Id);
//        expected1.put("name", "Test 1");
//        expected1.put("isVolatile", true);
//
//        ObjectNode expected2 = objectMapper.createObjectNode();
//        expected2.put("id", project2Id);
//        expected2.put("name", "Test 2");
//        expected2.put("isVolatile", false);
//
//        expectedProjects.add(expected1);
//        expectedProjects.add(expected2);
//
//        when(canvasApi.getCanvasCoursesApi()).thenReturn(canvasCoursesApi);
//        when(canvasCoursesApi.getCourseProjects(courseId)).thenReturn(List.of(projects.toString()));
//        when(projectService.getVolatileProjectsId(courseId)).thenReturn(List.of(project1Id));
//        when(projectService.getProjectById(courseId, project1Id)).thenReturn(new Project());
//        when(projectService.getProjectById(courseId, project2Id)).thenReturn(new Project());
//
//        mockMvc.perform(get("/api/courses/{course_id}/projects", courseId))
//                .andExpect(status().isOk())
//                .andExpect(content().json(expectedProjects.toString()));
//
//    }
//
//    //TODO not sure how this function works
//    @WithMockUser
//    @Test
//    public void editProjects() throws Exception {
//
//    }
//
//}
