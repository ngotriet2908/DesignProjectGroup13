package com.group13.tcsprojectgrading.services.course;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.group13.tcsprojectgrading.canvas.api.CanvasApi;
import com.group13.tcsprojectgrading.canvas.api.CanvasCoursesApi;
import com.group13.tcsprojectgrading.models.course.Course;
import com.group13.tcsprojectgrading.models.course.CourseParticipation;
import com.group13.tcsprojectgrading.models.graders.GradingParticipation;
import com.group13.tcsprojectgrading.models.permissions.Role;
import com.group13.tcsprojectgrading.models.permissions.RoleEnum;
import com.group13.tcsprojectgrading.models.project.Project;
import com.group13.tcsprojectgrading.models.user.User;
import com.group13.tcsprojectgrading.repositories.course.CourseParticipationRepository;
import com.group13.tcsprojectgrading.repositories.course.CourseRepository;
import com.group13.tcsprojectgrading.repositories.graders.GradingParticipationRepository;
import com.group13.tcsprojectgrading.repositories.project.ProjectRepository;
import com.group13.tcsprojectgrading.services.permissions.ProjectRoleService;
import com.group13.tcsprojectgrading.services.permissions.RoleService;
import com.group13.tcsprojectgrading.services.project.ProjectService;
import com.group13.tcsprojectgrading.services.rubric.RubricService;
import com.group13.tcsprojectgrading.services.settings.SettingsService;
import com.group13.tcsprojectgrading.services.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CourseServiceTest {
    @Mock
    private RubricService rubricService;
    @Mock
    private ProjectService projectService;
    @Mock
    private SettingsService settingsService;
    @Mock
    private ProjectRoleService projectRoleService;
    @Mock
    private RoleService roleService;
    @Mock
    private UserService userService;

    @Mock
    private CourseRepository courseRepository;
    @Mock
    private GradingParticipationRepository gradingParticipationRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private CourseParticipationRepository courseParticipationRepository;

    @Mock
    private CanvasApi canvasApi;
    @Mock
    private CanvasCoursesApi canvasCoursesApi;

    @InjectMocks
    private CourseService courseService;

    private ObjectMapper objectMapper;
    private Long courseId;
    private Long userId;
    private Course testCourse;

    @BeforeEach
    public void init() {
        objectMapper = new ObjectMapper();
        courseId = 72L;
        userId = 51L;
        testCourse = new Course(courseId, "Test Course", Date.from(Instant.now()));
    }

    @Test
    public void importCoursesCourseAlreadyExists() {
        ObjectNode testCourseCanvas = objectMapper.createObjectNode();
        testCourseCanvas.put("id",courseId);
        testCourseCanvas.put("name", "Test Course");
        testCourseCanvas.put("start_at", "2020-06-01T00:00:00+02:00");

        ArrayNode coursesToImport = objectMapper.createArrayNode();
        coursesToImport.add(testCourseCanvas); //Using the canvas response does not matter, only need id

        when(canvasApi.getCanvasCoursesApi()).thenReturn(canvasCoursesApi);
        when(canvasCoursesApi.getUserCourse(courseId)).thenReturn(testCourseCanvas.toString());
        when(courseRepository.existsById(courseId)).thenReturn(true);

        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> {
                    courseService.importCourses(coursesToImport, userId);
                }).withMessageContaining("Course already exists");
    }

    @Test
    public void importCoursesNotTeacher() {
        ObjectNode testCourseCanvas = objectMapper.createObjectNode();
        ArrayNode enrollments = objectMapper.createArrayNode();
        ObjectNode enrollment = objectMapper.createObjectNode();
        enrollment.put("role", "TaEnrollment");
        enrollment.put("user_id", userId);
        enrollments.add(enrollment);
        testCourseCanvas.put("id",courseId);
        testCourseCanvas.put("name", "Test Course");
        testCourseCanvas.put("start_at", "2020-06-01T00:00:00+02:00");
        testCourseCanvas.set("enrollments", enrollments);

        ArrayNode coursesToImport = objectMapper.createArrayNode();
        coursesToImport.add(testCourseCanvas); //Using the canvas response does not matter, only need id

        when(canvasApi.getCanvasCoursesApi()).thenReturn(canvasCoursesApi);
        when(canvasCoursesApi.getUserCourse(courseId)).thenReturn(testCourseCanvas.toString());
        when(courseRepository.existsById(courseId)).thenReturn(false);

        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> {
                    courseService.importCourses(coursesToImport, userId);
                }).withMessageContaining("You are not a teacher in course Test Course");

    }

    @Test
    public void importCoursesOk() {
        ObjectNode testCourseCanvas = objectMapper.createObjectNode();
        ArrayNode enrollments = objectMapper.createArrayNode();
        ObjectNode teacherEnrollment = objectMapper.createObjectNode();
        teacherEnrollment.put("role", "TeacherEnrollment");
        teacherEnrollment.put("course_id", courseId);
        teacherEnrollment.put("user_id", userId);
        enrollments.add(teacherEnrollment);

        testCourseCanvas.put("id",courseId);
        testCourseCanvas.put("name", "Test Course");
        testCourseCanvas.put("start_at", "2020-06-01T00:00:00+02:00");
        testCourseCanvas.set("enrollments", enrollments);

        ArrayNode coursesToImport = objectMapper.createArrayNode();
        coursesToImport.add(testCourseCanvas); //Using the canvas response does not matter, only need id
        ArrayNode users = objectMapper.createArrayNode();


        ObjectNode taEnrollment = objectMapper.createObjectNode();
        taEnrollment.put("role", "TaEnrollment");
        taEnrollment.put("course_id", courseId);
        taEnrollment.put("user_id", userId + 3);

        ObjectNode studentEnrollment = objectMapper.createObjectNode();
        studentEnrollment.put("role", "StudentEnrollment");
        studentEnrollment.put("course_id", courseId);
        studentEnrollment.put("user_id", userId + 2);

        ObjectNode user1 = objectMapper.createObjectNode();
        user1.put("id", userId + 1);
        user1.put("name", "Student 1");
        user1.put("email", "test1@testmail.com");
        user1.put("enrollments", List.of(studentEnrollment).toString());

        ObjectNode user2 = objectMapper.createObjectNode();
        user2.put("id", userId + 2);
        user2.put("name", "Student 2");
        user2.put("email", "test2@testmail.com");
        user2.put("enrollments", List.of(studentEnrollment).toString());

        ObjectNode user3 = objectMapper.createObjectNode();
        user3.put("id", userId + 3);
        user3.put("name", "TA 1");
        user3.put("email", "test3@testmail.com");
        user3.put("enrollments", List.of(taEnrollment).toString());

        ObjectNode user4 = objectMapper.createObjectNode();
        user4.put("id", userId);
        user4.put("name", "Teacher 1");
        user4.put("email", "test4@testmail.com");
        user4.put("enrollments", List.of(teacherEnrollment).toString());

        users.add(user1);
        users.add(user2);
        users.add(user3);
        users.add(user4);

        when(canvasApi.getCanvasCoursesApi()).thenReturn(canvasCoursesApi);
        when(canvasCoursesApi.getUserCourse(courseId)).thenReturn(testCourseCanvas.toString());
        when(courseRepository.existsById(courseId)).thenReturn(false);
        when(canvasCoursesApi.getCourseParticipantsWithAvatars(courseId)).thenReturn(List.of(users.toString()));

        assertThatNoException().isThrownBy(() -> courseService.importCourses(coursesToImport, userId));
    }

    @Test
    public void syncCourseCourseNotFound() {
        when(courseRepository.findCourseById(courseId)).thenReturn(Optional.empty());

        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> {
                    courseService.syncCourse(courseId);
                }).withMessageContaining("Course not found");
    }

    @Test
    public void syncCourseOk() {
        ArrayNode users = objectMapper.createArrayNode();

        ObjectNode taEnrollment = objectMapper.createObjectNode();
        taEnrollment.put("role", "TaEnrollment");
        taEnrollment.put("course_id", courseId);

        ObjectNode studentEnrollment = objectMapper.createObjectNode();
        studentEnrollment.put("role", "StudentEnrollment");
        studentEnrollment.put("course_id", courseId);

        ObjectNode teacherEnrollment = objectMapper.createObjectNode();
        teacherEnrollment.put("role", "TeacherEnrollment");
        teacherEnrollment.put("course_id", courseId);

        ObjectNode user1 = objectMapper.createObjectNode();
        user1.put("id", userId + 1);
        user1.put("name", "Student 1");
        user1.put("email", "test1@testmail.com");
        user1.put("enrollments", List.of(studentEnrollment).toString());

        ObjectNode user2 = objectMapper.createObjectNode();
        user2.put("id", userId + 2);
        user2.put("name", "Student 2");
        user2.put("email", "test2@testmail.com");
        user2.put("enrollments", List.of(studentEnrollment).toString());

        ObjectNode user3 = objectMapper.createObjectNode();
        user3.put("id", userId + 3);
        user3.put("name", "TA 1");
        user3.put("email", "test3@testmail.com");
        user3.put("enrollments", List.of(taEnrollment).toString());

        ObjectNode user4 = objectMapper.createObjectNode();
        user4.put("id", userId);
        user4.put("name", "Teacher 1");
        user4.put("email", "test4@testmail.com");
        user4.put("enrollments", List.of(teacherEnrollment).toString());

        users.add(user1);
        users.add(user2);
        users.add(user3);
        users.add(user4);

        when(courseRepository.findCourseById(courseId)).thenReturn(Optional.of(testCourse));
        when(canvasApi.getCanvasCoursesApi()).thenReturn(canvasCoursesApi);
        when(canvasCoursesApi.getCourseParticipantsWithAvatars(courseId)).thenReturn(List.of(users.toString()));

        assertThatNoException().isThrownBy(() -> courseService.syncCourse(courseId));
    }

    @Test
    public void getCourseCourseNotFound() {
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> {
                    courseService.getCourse(courseId, userId);
                }).withMessageContaining("Course not found");
    }

    @Test
    public void getCourseOk() throws IOException {
        Project project1 = new Project(courseId+101, testCourse,"Test Project 1", "0");
        Project project2 = new Project(courseId+102, testCourse, "Test Project 2", "0");
        Project project3 = new Project(courseId+103, testCourse, "Test Project 3","0");
        testCourse.setProjects(Set.of(project1, project2, project3));
        User testUser = new User(userId);
        Project.ProjectShortSerialiser serialiser = new Project.ProjectShortSerialiser();
        SimpleModule module = new SimpleModule("ProjectShortSerialiser");
        module.addSerializer(Project.class, serialiser);
        objectMapper.registerModule(module);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(testCourse));
        when(courseRepository.existsById(courseId)).thenReturn(true);
        when(userService.findById(userId)).thenReturn(testUser);
        when(courseParticipationRepository.findById_User_IdAndId_Course_Id(userId, courseId))
                .thenReturn(new CourseParticipation(testUser, testCourse, new Role(RoleEnum.TA.getName())));
        when(gradingParticipationRepository.findById_User_IdAndId_Project_Id(userId, courseId+101))
                .thenReturn(new GradingParticipation());
        when(gradingParticipationRepository.findById_User_IdAndId_Project_Id(userId, courseId+102))
                .thenReturn(new GradingParticipation());
        when(gradingParticipationRepository.findById_User_IdAndId_Project_Id(userId, courseId+103))
                .thenReturn(null);

        JsonNode returned = objectMapper.readTree(courseService.getCourse(courseId, userId));
        ObjectNode withoutRole = returned.deepCopy();
        withoutRole.remove("role");
        Course returnedCourse = objectMapper.convertValue(withoutRole, Course.class);

        assertThat(returnedCourse.getId()).isEqualTo(courseId);
        assertThat(returnedCourse.getProjects().toString()).isEqualTo(testCourse.getProjects().toString());
        assertThat(returnedCourse.getName()).isEqualTo(testCourse.getName());
        assertThat(returnedCourse.getStartAt()).isEqualTo(testCourse.getStartAt());
        assertThat(returned.get("role").asText()).isEqualTo(RoleEnum.TA.toString());
        assertThat(testCourse.getProjects()).containsExactlyInAnyOrder(project1, project2);
    }

    @Test
    public void importProjectsCourseNotFound() throws JsonProcessingException {
        ObjectNode testProjectCanvas = objectMapper.createObjectNode();
        testProjectCanvas.put("id",courseId+101);
        testProjectCanvas.put("name", "Test Course");
        testProjectCanvas.put("created_at", "2020-06-01T00:00:00+02:00");

        ArrayNode projectsToImport = new ObjectMapper().createArrayNode();
        projectsToImport.add(testProjectCanvas);

        when(courseRepository.findCourseById(courseId)).thenReturn(Optional.empty());

        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> courseService.importProjects(projectsToImport, courseId, userId))
                .withMessageContaining("Course not found");
    }

    @Test
    public void importProjectsProjectExists() throws JsonProcessingException {
        Project testProject = new Project(courseId+101, testCourse,"Test Project 1", "0");

        ObjectNode testProjectCanvas = objectMapper.createObjectNode();
        testProjectCanvas.put("id",courseId+101);
        testProjectCanvas.put("name", "Test Course");
        testProjectCanvas.put("created_at", "2020-06-01T00:00:00+02:00");

        ArrayNode projectsToImport = new ObjectMapper().createArrayNode();
        projectsToImport.add(testProjectCanvas);

        when(courseRepository.findCourseById(courseId)).thenReturn(Optional.of(testCourse));
        when(canvasApi.getCanvasCoursesApi()).thenReturn(canvasCoursesApi);
        when(canvasCoursesApi.getCourseProject(courseId, testProject.getId())).thenReturn(testProjectCanvas.toString());
        when(projectRepository.findProjectsById(testProject.getId())).thenReturn(testProject);

        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> courseService.importProjects(projectsToImport, courseId, userId))
                .withMessageContaining("Project already exists");
    }

    @Test
    public void importProjectsOk() throws JsonProcessingException {
        Project testProject = new Project(courseId+101, testCourse,"Test Project 1", "0");

        ObjectNode testProjectCanvas = objectMapper.createObjectNode();
        testProjectCanvas.put("id",courseId+101);
        testProjectCanvas.put("name", "Test Course");
        testProjectCanvas.put("created_at", "2020-06-01T00:00:00+02:00");

        ArrayNode projectsToImport = new ObjectMapper().createArrayNode();
        projectsToImport.add(testProjectCanvas);


        ObjectNode taEnrollment = objectMapper.createObjectNode();
        taEnrollment.put("role", "TaEnrollment");
        taEnrollment.put("course_id", courseId);

        ObjectNode studentEnrollment = objectMapper.createObjectNode();
        studentEnrollment.put("role", "StudentEnrollment");
        studentEnrollment.put("course_id", courseId);

        ObjectNode teacherEnrollment = objectMapper.createObjectNode();
        teacherEnrollment.put("role", "TeacherEnrollment");
        teacherEnrollment.put("course_id", courseId);

        User user1 = new User(userId + 1, "Student 1");
        User user2 = new User(userId + 2, "Student 2");
        User user3 = new User(userId + 3, "TA 1");
        User user4 = new User(userId, "Teacher 1");

        Role teacherRole = new Role(RoleEnum.TEACHER.getName());
        Role taRole = new Role(RoleEnum.TA.getName());
        Role studentRole = new Role(RoleEnum.STUDENT.getName());

        CourseParticipation participation1 = new CourseParticipation(user1, testCourse, studentRole);
        CourseParticipation participation2 = new CourseParticipation(user2, testCourse, studentRole);
        CourseParticipation participation3 = new CourseParticipation(user3, testCourse, taRole);
        CourseParticipation participation4 = new CourseParticipation(user4, testCourse, teacherRole);

        ArrayNode submissions = objectMapper.createArrayNode();
        ObjectNode submission = objectMapper.createObjectNode();
        submission.put("user", userId + 1);
        submissions.add(submission);

        testCourse.setUsers(List.of(participation1, participation2, participation3, participation4));

        when(courseRepository.findCourseById(courseId)).thenReturn(Optional.of(testCourse));
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(testCourse));
        when(canvasApi.getCanvasCoursesApi()).thenReturn(canvasCoursesApi);
        when(canvasCoursesApi.getCourseProject(courseId, testProject.getId())).thenReturn(testProjectCanvas.toString());
        when(canvasCoursesApi.getSubmissionsInfo(courseId, testProject.getId())).thenReturn(List.of(submissions.toString()));
        when(projectRepository.findProjectsById(testProject.getId())).thenReturn(null);
        when(courseParticipationRepository.findById_Course_IdAndRole_Name(courseId, RoleEnum.TEACHER.toString()))
                .thenReturn(List.of(participation4));
        when(roleService.findRoleByName(RoleEnum.TEACHER.toString())).thenReturn(teacherRole);


        assertThatNoException()
                .isThrownBy(() -> courseService.importProjects(projectsToImport, courseId, userId));
    }
}