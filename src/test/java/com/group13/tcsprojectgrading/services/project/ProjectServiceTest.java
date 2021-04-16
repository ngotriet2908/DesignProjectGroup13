package com.group13.tcsprojectgrading.services.project;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.group13.tcsprojectgrading.canvas.api.CanvasApi;
import com.group13.tcsprojectgrading.canvas.api.CanvasCoursesApi;
import com.group13.tcsprojectgrading.models.course.Course;
import com.group13.tcsprojectgrading.models.course.CourseParticipation;
import com.group13.tcsprojectgrading.models.feedback.FeedbackLog;
import com.group13.tcsprojectgrading.models.graders.GradingParticipation;
import com.group13.tcsprojectgrading.models.grading.Assessment;
import com.group13.tcsprojectgrading.models.grading.AssessmentLink;
import com.group13.tcsprojectgrading.models.grading.Grade;
import com.group13.tcsprojectgrading.models.grading.Issue;
import com.group13.tcsprojectgrading.models.permissions.Role;
import com.group13.tcsprojectgrading.models.permissions.RoleEnum;
import com.group13.tcsprojectgrading.models.project.Project;
import com.group13.tcsprojectgrading.models.rubric.Rubric;
import com.group13.tcsprojectgrading.models.submissions.Submission;
import com.group13.tcsprojectgrading.models.user.User;
import com.group13.tcsprojectgrading.repositories.course.CourseParticipationRepository;
import com.group13.tcsprojectgrading.repositories.grading.IssueRepository;
import com.group13.tcsprojectgrading.repositories.project.ProjectRepository;
import com.group13.tcsprojectgrading.repositories.submissions.LabelRepository;
import com.group13.tcsprojectgrading.services.course.CourseService;
import com.group13.tcsprojectgrading.services.feedback.FeedbackService;
import com.group13.tcsprojectgrading.services.graders.GradingParticipationService;
import com.group13.tcsprojectgrading.services.grading.AssessmentService;
import com.group13.tcsprojectgrading.services.rubric.RubricService;
import com.group13.tcsprojectgrading.services.submissions.SubmissionDetailsService;
import com.group13.tcsprojectgrading.services.submissions.SubmissionService;
import com.group13.tcsprojectgrading.services.user.UserService;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Date;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {
    @Mock
    private RubricService rubricService;
    @Mock
    private GradingParticipationService gradingParticipationService;
    @Mock
    private UserService userService;
    @Mock
    private SubmissionService submissionService;
    @Mock
    private SubmissionDetailsService submissionDetailsService;
    @Mock
    private AssessmentService assessmentService;
    @Mock
    private CourseService courseService;
    @Mock
    private FeedbackService feedbackService;

    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private CourseParticipationRepository courseParticipationRepository;
    @Mock
    private LabelRepository labelRepository;
    @Mock
    private IssueRepository issueRepository;

//    @Mock
//    private CanvasApi canvasApi;
//    @Mock
//    private CanvasCoursesApi canvasCoursesApi

    @InjectMocks
    private ProjectService projectService;

    private Long courseId;
    private Long projectId;
    private Long userId;
    private Course testCourse;
    private Project testProject;
    private ObjectMapper objectMapper;


    @BeforeEach
    public void init() {
        courseId = 97L;
        projectId = 78L;
        userId = 54L;
        testCourse = new Course(courseId, "Test Course", Date.from(Instant.now()));
        testProject = new Project(projectId, testCourse, "Test Project", "2020-01-05blablawhatever");
        objectMapper = new ObjectMapper();
    }

    @Test
    public void getProjectParticipantsWithSubmissionsProjectNotFound() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());
        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> projectService.getProjectParticipantsWithSubmissions(courseId, projectId))
                .withMessageContaining("Project not found");
    }

    @Test
    public void getProjectParticipantsWithSubmissionsOk() {
        User user1 = new User(userId + 1, "Student 1");
        User user2 = new User(userId + 2, "Student 2");

        Role studentRole = new Role(RoleEnum.STUDENT.getName());

        CourseParticipation participation1 = new CourseParticipation(user1, testCourse, studentRole);
        CourseParticipation participation2 = new CourseParticipation(user2, testCourse, studentRole);

        Submission submission1_1 = new Submission();
        submission1_1.setId(userId + 101);
        Submission submission1_2 = new Submission();
        submission1_2.setId(userId + 102);
        Submission submission2 = new Submission();
        submission2.setId(userId + 201);

        AssessmentLink assessmentLink1_1 = new AssessmentLink(user1, submission1_1, new Assessment(), false);
        AssessmentLink assessmentLink1_2 = new AssessmentLink(user1, submission1_2, new Assessment(), true);
        AssessmentLink assessmentLink2 = new AssessmentLink(user2, submission2, new Assessment(), true);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(testProject));
        when(courseService.getCourseStudents(courseId)).thenReturn(List.of(participation1, participation2));
        when(assessmentService.getAssessmentsByProjectAndUser(projectId, user1)).thenReturn(Set.of(assessmentLink1_1, assessmentLink1_2));
        when(assessmentService.getAssessmentsByProjectAndUser(projectId, user2)).thenReturn(Set.of(assessmentLink2));
        when(assessmentService.findCurrentAssessmentUser(testProject, user1)).thenReturn(assessmentLink1_2);
        when(assessmentService.findCurrentAssessmentUser(testProject, user2)).thenReturn(assessmentLink2);

        List<CourseParticipation> returned = projectService.getProjectParticipantsWithSubmissions(courseId, projectId);
        assertThat(returned.get(0).getSubmissions()).isEqualTo(List.of(submission1_1, submission1_2));
        assertThat(returned.get(1).getSubmissions()).isEqualTo(List.of(submission2));
    }

    @Test
    public void syncProjectProjectNotFound() {
        ArrayNode submissionsArray = objectMapper.createArrayNode();
        ObjectNode submission1 = objectMapper.createObjectNode();
        submission1.put("workflow_state", "unsubmitted");
        submission1.put("user_id", userId + 101);
        submissionsArray.add(submission1);
        when(projectRepository.findProjectById(projectId)).thenReturn(Optional.empty());
        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> projectService.syncProject(projectId, submissionsArray))
                .withMessageContaining("Project not found");
    }

    //This function is a real pain, could maybe be tested with more mock stubs
    @Test
    public void syncProjectOk() {
        ArrayNode submissionsArray = objectMapper.createArrayNode();
        ObjectNode submission1 = objectMapper.createObjectNode();
        submission1.put("workflow_state", "unsubmitted");
        submission1.put("user_id", userId + 101);

        ObjectNode submission2 = objectMapper.createObjectNode();
        submission2.put("workflow_state", "submitted");
        submission2.put("user_id", userId + 201);
        ArrayNode submission2Comments = objectMapper.createArrayNode();
        ObjectNode submission2Comment = objectMapper.createObjectNode();
        submission2Comment.put("author_id", userId + 201);
        submission2Comment.put("comment", "Blablabla");
        submission2Comments.add(submission2Comment);
        submission2.set("submission_comments", submission2Comments);
        submission2.set("group", null);

        ObjectNode submission3 = objectMapper.createObjectNode();
        submission3.put("workflow_state", "submitted");
        submission3.put("user_id", userId + 201);
        ArrayNode submission3Attachments = objectMapper.createArrayNode();
        ObjectNode submission3Attachment = objectMapper.createObjectNode();
        submission3Attachment.put("author_id", userId + 201);
        submission3Attachment.put("comment", "Blablabla");
        submission3Attachments.add(submission3Attachment);
        submission3.set("attachments", submission3Attachments);
        ObjectNode group1 = objectMapper.createObjectNode();
        group1.put("id", userId + 1001);
        submission3.set("group", group1);
        submission3.put("submitted_at", DateTimeFormatter.ISO_INSTANT.format(Instant.now()));

        submissionsArray.add(submission1);
        submissionsArray.add(submission2);
        submissionsArray.add(submission3);

        when(projectRepository.findProjectById(projectId)).thenReturn(Optional.of(testProject));

        assertThatNoException()
                .isThrownBy(() -> projectService.syncProject(projectId, submissionsArray));
    }

    @Test
    public void saveProjectGradersNoProject() {
        List<User> graders = new ArrayList<>();
        graders.add(new User(userId + 101));
        graders.add(new User(userId + 102));
        when(projectRepository.findProjectById(projectId)).thenReturn(Optional.empty());
        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> projectService.saveProjectGraders(projectId, graders, userId))
                .withMessageContaining("Project not found");
    }

    @Test
    public void saveProjectGradersSelfNotIncluded() {
        List<User> graders = new ArrayList<>();
        graders.add(new User(userId + 101));
        graders.add(new User(userId + 102));

        when(projectRepository.findProjectById(projectId)).thenReturn(Optional.of(testProject));
        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> projectService.saveProjectGraders(projectId, graders, userId))
                .withMessageContaining("Self must be explicitly included as a grader");
    }

    @Test
    public void saveProjectGradersOk() {
        List<User> graders = new ArrayList<>();
        graders.add(new User(userId + 101));
        graders.add(new User(userId + 102));
        graders.add(new User(userId));
        GradingParticipation participation1 = new GradingParticipation(graders.get(0), testProject, new Role(RoleEnum.TA_GRADING.getName()));
        GradingParticipation participation2 = new GradingParticipation(graders.get(1), testProject, new Role(RoleEnum.TEACHER.getName()));

        when(projectRepository.findProjectById(projectId)).thenReturn(Optional.of(testProject));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(testProject));
        when(gradingParticipationService.getGradingParticipationFromProject(testProject)).thenReturn(List.of(participation1, participation2));
        assertThatNoException()
                .isThrownBy(() -> projectService.saveProjectGraders(projectId, graders, userId));
    }

    @Test
    public void getProjectExcelOk() {
        Assessment testAssessment = new Assessment(userId + 701);
        Role studentRole = new Role(RoleEnum.STUDENT.getName());

        User user1 = new User(userId + 101);
        User user2 = new User(userId + 102);

        Rubric testRubric = new Rubric(projectId);

        Submission submission = new Submission();
        submission.setId(userId + 501);

        AssessmentLink link1 = new AssessmentLink(user1, submission, testAssessment, true);
        AssessmentLink link2 = new AssessmentLink(user2, submission, testAssessment, true);

        CourseParticipation participation1 = new CourseParticipation(user1, testCourse, studentRole);
        CourseParticipation participation2 = new CourseParticipation(user2, testCourse, studentRole);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(testProject));
        when(rubricService.getRubricById(projectId)).thenReturn(testRubric);
        when(courseParticipationRepository.findById_Course_IdAndRole_Name(courseId, RoleEnum.STUDENT.getName()))
                .thenReturn(List.of(participation1, participation2));
        when(assessmentService.findCurrentAssessmentUser(testProject, user1)).thenReturn(link1);
        when(assessmentService.findCurrentAssessmentUser(testProject, user2)).thenReturn(link2);
        assertThatNoException()
                .isThrownBy(() -> projectService.getProjectExcel(projectId));
    }

    @Test
    public void allFinishedGradedUserOk() {
        Assessment assessment1 = new Assessment(userId + 701);
        Assessment assessment2 = new Assessment(userId + 702);
        Role studentRole = new Role(RoleEnum.STUDENT.getName());

        User user1 = new User(userId + 101);
        User user2 = new User(userId + 102);

        Rubric testRubric = new Rubric(projectId);
        testRubric.setCriterionCount(4);

        Submission submission = new Submission();
        submission.setId(userId + 501);

        AssessmentLink link1 = new AssessmentLink(user1, submission, assessment1, true);
        AssessmentLink link2 = new AssessmentLink(user2, submission, assessment2, true);

        CourseParticipation participation1 = new CourseParticipation(user1, testCourse, studentRole);
        CourseParticipation participation2 = new CourseParticipation(user2, testCourse, studentRole);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(testProject));
        when(rubricService.getRubricById(projectId)).thenReturn(testRubric);
        when(courseParticipationRepository.findById_Course_IdAndRole_Name(courseId, RoleEnum.STUDENT.getName()))
                .thenReturn(List.of(participation1, participation2));
        when(assessmentService.findCurrentAssessmentUser(testProject, user1)).thenReturn(link1);
        when(assessmentService.findCurrentAssessmentUser(testProject, user2)).thenReturn(link2);
        when(assessmentService.findActiveGradesForAssignment(assessment1)).thenReturn(List.of(new Grade(), new Grade()));
        when(assessmentService.findActiveGradesForAssignment(assessment2))
                .thenReturn(List.of(new Grade(), new Grade(), new Grade(), new Grade()));

        assertThat(projectService.allFinishedGradedUser(projectId)).isEqualTo(List.of(participation2));
    }

    @Test
    public void allFinishedGradedUserNotSentOk() {
        Assessment assessment1 = new Assessment(userId + 701);
        Assessment assessment2 = new Assessment(userId + 702);
        Assessment assessment3 = new Assessment(userId + 703);
        Role studentRole = new Role(RoleEnum.STUDENT.getName());

        User user1 = new User(userId + 101);
        User user2 = new User(userId + 102);
        User user3 = new User(userId + 103);

        Rubric testRubric = new Rubric(projectId);
        testRubric.setCriterionCount(4);

        Submission submission = new Submission();
        submission.setId(userId + 501);

        AssessmentLink link1 = new AssessmentLink(user1, submission, assessment1, true);
        AssessmentLink link2 = new AssessmentLink(user2, submission, assessment2, true);
        AssessmentLink link3 = new AssessmentLink(user3, submission, assessment3, true);

        CourseParticipation participation1 = new CourseParticipation(user1, testCourse, studentRole);
        CourseParticipation participation2 = new CourseParticipation(user2, testCourse, studentRole);
        CourseParticipation participation3 = new CourseParticipation(user3, testCourse, studentRole);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(testProject));
        when(rubricService.getRubricById(projectId)).thenReturn(testRubric);
        when(courseParticipationRepository.findById_Course_IdAndRole_Name(courseId, RoleEnum.STUDENT.getName()))
                .thenReturn(List.of(participation1, participation2, participation3));
        when(assessmentService.findCurrentAssessmentUser(testProject, user1)).thenReturn(link1);
        when(assessmentService.findCurrentAssessmentUser(testProject, user2)).thenReturn(link2);
        when(assessmentService.findCurrentAssessmentUser(testProject, user3)).thenReturn(link3);
        when(assessmentService.findActiveGradesForAssignment(assessment1)).thenReturn(List.of(new Grade(), new Grade()));
        when(assessmentService.findActiveGradesForAssignment(assessment2))
                .thenReturn(List.of(new Grade(), new Grade(), new Grade(), new Grade()));
        when(assessmentService.findActiveGradesForAssignment(assessment3))
                .thenReturn(List.of(new Grade(), new Grade(), new Grade(), new Grade()));
        when(feedbackService.findLogFromLink(link2)).thenReturn(List.of(new FeedbackLog()));
        when(feedbackService.findLogFromLink(link3)).thenReturn(Collections.emptyList());


        assertThat(projectService.allFinishedGradedUserNotSent(projectId)).isEqualTo(List.of(participation3));
    }

    @Test
    public void uploadGradesToCanvasOk() {
        Assessment assessment1 = new Assessment(userId + 701);
        Assessment assessment2 = new Assessment(userId + 702);
        Assessment assessment3 = new Assessment(userId + 703);
        Role studentRole = new Role(RoleEnum.STUDENT.getName());

        User user1 = new User(userId + 101);
        User user2 = new User(userId + 102);
        User user3 = new User(userId + 103);

        Rubric testRubric = new Rubric(projectId);
        testRubric.setCriterionCount(4);

        Submission submission = new Submission();
        submission.setId(userId + 501);

        AssessmentLink link2 = new AssessmentLink(user2, submission, assessment2, true);
        AssessmentLink link3 = new AssessmentLink(user3, submission, assessment3, true);

        CourseParticipation participation1 = new CourseParticipation(user1, testCourse, studentRole);
        CourseParticipation participation2 = new CourseParticipation(user2, testCourse, studentRole);
        CourseParticipation participation3 = new CourseParticipation(user3, testCourse, studentRole);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(testProject));
        when(rubricService.getRubricById(projectId)).thenReturn(testRubric);
        when(courseParticipationRepository.findById_Course_IdAndRole_Name(courseId, RoleEnum.STUDENT.getName()))
                .thenReturn(List.of(participation1, participation2, participation3));
        when(assessmentService.findCurrentAssessmentUser(testProject, user1)).thenReturn(null);
        when(assessmentService.findCurrentAssessmentUser(testProject, user2)).thenReturn(link2);
        when(assessmentService.findCurrentAssessmentUser(testProject, user3)).thenReturn(link3);
        when(assessmentService.findActiveGradesForAssignment(assessment2))
                .thenReturn(List.of(new Grade(), new Grade(), new Grade(), new Grade()));
        when(assessmentService.findActiveGradesForAssignment(assessment3))
                .thenReturn(List.of(new Grade(), new Grade(), new Grade()));
        when(assessmentService.getAssessmentDetails(userId+702, testProject)).thenReturn(assessment2);

        CanvasApi canvasApi = mock(CanvasApi.class);
        CanvasCoursesApi canvasCoursesApi = mock(CanvasCoursesApi.class);
        when(canvasApi.getCanvasCoursesApi()).thenReturn(canvasCoursesApi);

        assertThatNoException().isThrownBy(() -> projectService.uploadGradesToCanvas(projectId, canvasApi));
    }

//    @Test
//    public void getIssuesInProjectOk() {
//        User user = new User(userId);
//        Long assessmentId1 = userId + 701;
//        Long assessmentId2 = userId + 702;
//        Issue issue1 = new Issue();
//        Issue issue2 = new Issue();
//        Assessment assessment1 = new Assessment(assessmentId1);
//        Submission submission1 = new Submission();
//        submission1.setProject(testProject);
//        Assessment assessment2 = new Assessment(assessmentId2);
//        Submission submission2 = new Submission();
//        submission2.setProject(new Project(projectId + 1));
//        issue1.setAssessment(assessment1);
//        issue2.setAssessment(assessment2);
//
//        when(userService.findById(userId)).thenReturn(user);
//        when(issueRepository.findIssuesByCreatorOrAddressee(user, user)).thenReturn(List.of(issue1, issue2));
//        when(submissionService.findSubmissionsFromAssessment(assessmentId1)).thenReturn(submission1);
//        when(submissionService.findSubmissionsFromAssessment(assessmentId2)).thenReturn(submission2);
//        assertThat(projectService.getIssuesInProject(projectId, userId)).isEqualTo(List.of(issue1));
//    }
}
