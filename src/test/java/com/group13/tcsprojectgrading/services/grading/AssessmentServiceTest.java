package com.group13.tcsprojectgrading.services.grading;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.group13.tcsprojectgrading.models.course.Course;
import com.group13.tcsprojectgrading.models.course.CourseParticipation;
import com.group13.tcsprojectgrading.models.graders.GradingParticipation;
import com.group13.tcsprojectgrading.models.grading.*;
import com.group13.tcsprojectgrading.models.permissions.Privilege;
import com.group13.tcsprojectgrading.models.permissions.PrivilegeEnum;
import com.group13.tcsprojectgrading.models.permissions.Role;
import com.group13.tcsprojectgrading.models.project.Project;
import com.group13.tcsprojectgrading.models.rubric.Element;
import com.group13.tcsprojectgrading.models.rubric.Rubric;
import com.group13.tcsprojectgrading.models.rubric.RubricContent;
import com.group13.tcsprojectgrading.models.rubric.RubricGrade;
import com.group13.tcsprojectgrading.models.settings.Settings;
import com.group13.tcsprojectgrading.models.submissions.Submission;
import com.group13.tcsprojectgrading.models.user.User;
import com.group13.tcsprojectgrading.repositories.course.CourseRepository;
import com.group13.tcsprojectgrading.repositories.grading.*;
import com.group13.tcsprojectgrading.repositories.submissions.SubmissionRepository;
import com.group13.tcsprojectgrading.services.graders.GradingParticipationService;
import com.group13.tcsprojectgrading.services.notifications.NotificationService;
import com.group13.tcsprojectgrading.services.project.ProjectService;
import com.group13.tcsprojectgrading.services.rubric.RubricService;
import com.group13.tcsprojectgrading.services.settings.SettingsService;
import com.group13.tcsprojectgrading.services.submissions.SubmissionService;
import com.group13.tcsprojectgrading.services.user.UserService;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.result.ContentResultMatchers;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class AssessmentServiceTest {
    @Mock
    private GradingParticipationService gradingParticipationService;
    @Mock
    private RubricService rubricService;
    @Mock
    private UserService userService;
    @Mock
    private SubmissionService submissionService;
    @Mock
    private SettingsService settingsService;

    @Mock
    private AssessmentRepository assessmentRepository;
    @Mock
    private GradeRepository gradeRepository;
    @Mock
    private AssessmentLinkRepository assessmentLinkRepository;
    @Mock
    private IssueRepository issueRepository;
    @Mock
    private IssueStatusRepository issueStatusRepository;
    @Mock
    private SubmissionRepository submissionRepository;

    @InjectMocks
    private AssessmentService assessmentService;

    private final Long courseId = 1L;
    private final Long projectId = 2L;
    private final Long submissionId = 3L;
    private final Long assessmentId = 4L;
    private final Assessment testAssessment = new Assessment(assessmentId);
    private final Submission testSubmission = new Submission();
    private final Long userId = 44L;
    Course testCourse = new Course(courseId, "test", new Date());
    Project testProject = new Project(projectId, testCourse, "test", "0");

    @Test
    public void createNewAssessmentWithLink() throws Exception {
        User testStudent = new User(userId - 1);
        testAssessment.setProject(testProject);
        testSubmission.setId(submissionId);
        AssessmentLink testLink = new AssessmentLink(testStudent, testSubmission, testAssessment, true);
        when(assessmentLinkRepository.existsById_UserAndId_Submission_ProjectAndCurrentIsTrue(testStudent, testProject))
                .thenReturn(false);
        assertThat(assessmentService.createNewAssessmentWithLink(testSubmission, testStudent, testProject, testAssessment))
                .isEqualTo(testLink);
    }

    @Test
    public void cloneAssessment() throws JsonProcessingException {
        User testStudent = new User(userId - 1);
        CourseParticipation participation = new CourseParticipation(testStudent, testCourse, new Role("Student"));
        testAssessment.setProject(testProject);
        testSubmission.setId(submissionId);
        AssessmentLink testLink = new AssessmentLink(testStudent, testSubmission, testAssessment, true);

        when(assessmentLinkRepository.findAssessmentLinkById_Submission_IdAndAndId_User_Id(submissionId, userId - 1))
                .thenReturn(testLink);
        when(assessmentLinkRepository.save(any(AssessmentLink.class))).then(returnsFirstArg());
        assertThat(assessmentService.cloneAssessment(testSubmission, testAssessment, participation)).isEqualTo(testLink);
    }

    @Test
    public void moveAssessment() throws JsonProcessingException {
        User testStudent = new User(userId - 1);
        Set<AssessmentLink> links = new HashSet<>();
        Assessment destinationAssessment = new Assessment(assessmentId + 1);
        destinationAssessment.setProject(testProject);
        testAssessment.setProject(testProject);
        testAssessment.setGrades(Set.of(new Grade()));
        testSubmission.setId(submissionId);
        AssessmentLink oldLink = new AssessmentLink(testStudent, testSubmission, testAssessment, true);
        links.add(oldLink);
        AssessmentLink newLink = new AssessmentLink(testStudent, testSubmission, destinationAssessment, true);

        when(assessmentLinkRepository.save(any(AssessmentLink.class))).then(returnsFirstArg());
        assertThat(assessmentService.moveAssessment(oldLink, destinationAssessment, links)).isEqualTo(newLink);
    }

    @Test
    public void getAssessmentSubmissionNotFound() throws Exception {
        when(submissionService.getSubmission(anyLong())).thenReturn(null);

        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> {
                    assessmentService.getAssessment(assessmentId, submissionId, userId, new ArrayList<PrivilegeEnum>());
                }).withMessageContaining("Submission not found");
    }

    @Test
    public void getAssessmentNoGrader() throws Exception {
        List<PrivilegeEnum> privileges = new ArrayList<>();
        privileges.add(PrivilegeEnum.GRADING_READ_SINGLE);

        when(submissionService.getSubmission(submissionId)).thenReturn(testSubmission);
        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> {
                    assessmentService.getAssessment(assessmentId, submissionId, userId, privileges);
                }).withMessageContaining("Unauthorised");
    }

    @Test
    public void getAssessmentGraderDifferentId() throws Exception {
        testSubmission.setGrader(new User(userId+1));
        List<PrivilegeEnum> privileges = new ArrayList<>();
        privileges.add(PrivilegeEnum.GRADING_READ_SINGLE);

        when(submissionService.getSubmission(submissionId)).thenReturn(testSubmission);
        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> {
                    assessmentService.getAssessment(assessmentId, submissionId, userId, privileges);
                }).withMessageContaining("Unauthorised");
    }

    @Test
    public void getAssessmentOk() throws Exception{
        testSubmission.setGrader(new User(userId));
        List<PrivilegeEnum> privileges = new ArrayList<>();
        privileges.add(PrivilegeEnum.GRADING_READ_SINGLE);

        when(submissionService.getSubmission(anyLong())).thenReturn(testSubmission);
        when(assessmentRepository.findById(assessmentId)).thenReturn(Optional.of(testAssessment));
        assertThat(assessmentService.getAssessment(assessmentId, submissionId, userId, privileges))
                .isEqualTo(testAssessment);
    }

    @Test
    public void getSubmissionMembers() throws Exception {
        User member1 = new User(userId + 5);
        User member2 = new User(userId + 6);
        AssessmentLink link1 = new AssessmentLink(member1, testSubmission, testAssessment, true);
        AssessmentLink link2 = new AssessmentLink(member2, testSubmission, testAssessment, false);
        Set<AssessmentLink> testLinks = new HashSet<>();
        testLinks.add(link1);
        testLinks.add(link2);

        Set<User> expected = new HashSet<>();
        User member1Expected = new User(userId + 5);
        member1Expected.setCurrent(true);
        User member2Expected = new User(userId + 6);
        member2Expected.setCurrent(false);
        expected.add(member1Expected);
        expected.add(member2Expected);

        when(assessmentLinkRepository.findById_Submission(testSubmission)).thenReturn(testLinks);
        assertThat(assessmentService.getSubmissionMembers(testSubmission)).isEqualTo(expected);
    }

    @Test
    public void getAssessmentsBySubmissionOneAssessment() {
        testSubmission.setProject(testProject);
        User member1 = new User(userId + 5);
        User member2 = new User(userId + 6);
        AssessmentLink link1 = new AssessmentLink(member1, testSubmission, testAssessment, true);
        AssessmentLink link2 = new AssessmentLink(member2, testSubmission, testAssessment, false);
        Set<AssessmentLink> testLinks = new HashSet<>();
        testLinks.add(link1);
        testLinks.add(link2);

        when(rubricService.getRubricById(projectId)).thenReturn(new Rubric(23L));
        when(assessmentLinkRepository.findById_Submission(testSubmission)).thenReturn(testLinks);
        assertThat(assessmentService.getAssessmentsBySubmission(testSubmission)).isEqualTo(List.of(testAssessment));
    }

    @Test
    public void getAssessmentsBySubmissionMultipleAssessments() {
        testSubmission.setProject(testProject);
        Assessment testAssessment2 = new Assessment(assessmentId + 1);
        Assessment testAssessment3 = new Assessment(assessmentId - 1);
        User member1 = new User(userId + 5);
        User member2 = new User(userId + 6);
        User member3 = new User(userId + 7);
        AssessmentLink link1 = new AssessmentLink(member1, testSubmission, testAssessment, true);
        AssessmentLink link2 = new AssessmentLink(member2, testSubmission, testAssessment2, true);
        AssessmentLink link3 = new AssessmentLink(member3, testSubmission, testAssessment3, true);
        Set<AssessmentLink> testLinks = new HashSet<>();
        testLinks.add(link1);
        testLinks.add(link2);
        testLinks.add(link3);
        List<Assessment> expected = List.of(testAssessment3, testAssessment, testAssessment2);

        when(rubricService.getRubricById(projectId)).thenReturn(new Rubric(23L));
        when(assessmentLinkRepository.findById_Submission(testSubmission)).thenReturn(testLinks);
        assertThat(assessmentService.getAssessmentsBySubmission(testSubmission)).isEqualTo(expected);
    }

    @Test
    public void addGradeAssessmentNotFound() throws Exception {
        testSubmission.setProject(testProject);
        List<PrivilegeEnum> privileges = new ArrayList<>();
        privileges.add(PrivilegeEnum.GRADING_WRITE_SINGLE);

        when(assessmentRepository.findAssessmentById(assessmentId)).thenReturn(Optional.empty());
        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> {
                    assessmentService.addGrade(submissionId, assessmentId, new Grade(), userId, privileges);
                }).withMessageContaining("Assessment not found");
    }

    @Test
    public void addGradeUserNotFound() throws Exception {
        testSubmission.setProject(testProject);
        User testGrader = new User(userId);
        List<PrivilegeEnum> privileges = new ArrayList<>();
        privileges.add(PrivilegeEnum.GRADING_WRITE_SINGLE);

        when(assessmentRepository.findAssessmentById(assessmentId)).thenReturn(Optional.of(testAssessment));
        when(userService.findById(userId)).thenReturn(null);
        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> {
                    assessmentService.addGrade(submissionId, assessmentId, new Grade(), userId, privileges);
                }).withMessageContaining("User not found");
    }

    @Test
    public void addGradeSubmissionNotFound() throws Exception {
        testSubmission.setProject(testProject);
        User testGrader = new User(userId);
        List<PrivilegeEnum> privileges = new ArrayList<>();
        privileges.add(PrivilegeEnum.GRADING_WRITE_SINGLE);

        when(submissionRepository.findById(submissionId)).thenReturn(Optional.empty());
        when(assessmentRepository.findAssessmentById(assessmentId)).thenReturn(Optional.of(testAssessment));
        when(userService.findById(userId)).thenReturn(testGrader);
        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> assessmentService.addGrade(submissionId, assessmentId, new Grade(), userId, privileges)).withMessageContaining("Submission not found");
    }

    @Test
    public void addGradeNoGrader() throws Exception {
        testSubmission.setProject(testProject);
        User testGrader = new User(userId);
        List<PrivilegeEnum> privileges = new ArrayList<>();
        privileges.add(PrivilegeEnum.GRADING_WRITE_SINGLE);

        when(submissionRepository.findById(submissionId)).thenReturn(Optional.of(testSubmission));
        when(assessmentRepository.findAssessmentById(assessmentId)).thenReturn(Optional.of(testAssessment));
        when(userService.findById(userId)).thenReturn(testGrader);
        when(gradingParticipationService.getGradingParticipationByUserAndProject(userId, projectId)).thenReturn(null);
        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> assessmentService.addGrade(submissionId, assessmentId, new Grade(), userId, privileges)).withMessageContaining("Unauthorised");

    }

    @Test
    public void addGradeNotAssigned() throws Exception {
        testSubmission.setProject(testProject);
        User testGrader = new User(userId);
        testSubmission.setGrader(new User(userId + 1));
        List<PrivilegeEnum> privileges = new ArrayList<>();
        privileges.add(PrivilegeEnum.GRADING_WRITE_SINGLE);
        GradingParticipation participation = new GradingParticipation(testGrader, testProject, new Role("TA"));

        when(submissionRepository.findById(submissionId)).thenReturn(Optional.of(testSubmission));
        when(assessmentRepository.findAssessmentById(assessmentId)).thenReturn(Optional.of(testAssessment));
        when(userService.findById(userId)).thenReturn(testGrader);
        when(gradingParticipationService.getGradingParticipationByUserAndProject(userId, projectId)).thenReturn(participation);

        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> assessmentService.addGrade(submissionId, assessmentId, new Grade(), userId, privileges)).withMessageContaining("Unauthorised");
    }

    @Test
    public void addGradeOk() throws Exception {
        testSubmission.setProject(testProject);
        User testGrader = new User(userId);
        testSubmission.setGrader(testGrader);
        List<PrivilegeEnum> privileges = new ArrayList<>();
        privileges.add(PrivilegeEnum.GRADING_WRITE_SINGLE);
        GradingParticipation participation = new GradingParticipation(testGrader, testProject, new Role("TA"));
        Grade testGrade = new Grade();

        when(submissionRepository.findById(submissionId)).thenReturn(Optional.of(testSubmission));
        when(assessmentRepository.findAssessmentById(assessmentId)).thenReturn(Optional.of(testAssessment));
        when(assessmentRepository.save(any(Assessment.class))).then(returnsFirstArg());
        when(userService.findById(userId)).thenReturn(testGrader);
        when(gradingParticipationService.getGradingParticipationByUserAndProject(userId, projectId)).thenReturn(participation);
        when(gradeRepository.save(any(Grade.class))).then(returnsFirstArg());

        assertThat(assessmentService.addGrade(submissionId, assessmentId, testGrade, userId, privileges))
                .isEqualTo(testGrade);
        assertThat(testGrade.getGradedAt()).isCloseTo(Date.from(Instant.now()), 1000);
        assertThat(testGrade.getAssessment()).isEqualTo(testAssessment);
        assertThat(testGrade.getGrader()).isEqualTo(testGrader);
    }

    @Test
    public void activateGradeSubmissionNotFound() throws Exception {
        testSubmission.setProject(testProject);
        Long gradeId = 62L;
        List<PrivilegeEnum> privileges = new ArrayList<>();
        privileges.add(PrivilegeEnum.GRADING_WRITE_SINGLE);

        when(submissionRepository.findById(submissionId)).thenReturn(Optional.empty());
        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> assessmentService.activateGrade(submissionId, assessmentId, userId, gradeId, privileges))
                .withMessageContaining("Submission not found");
    }

    @Test
    public void activateGradeNoGrader() throws Exception {
        testSubmission.setProject(testProject);
        Long gradeId = 62L;
        User testGrader = new User(userId);
        List<PrivilegeEnum> privileges = new ArrayList<>();
        privileges.add(PrivilegeEnum.GRADING_WRITE_SINGLE);

        when(submissionRepository.findById(submissionId)).thenReturn(Optional.of(testSubmission));
        when(gradingParticipationService.getGradingParticipationByUserAndProject(userId, projectId)).thenReturn(null);
        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> assessmentService.activateGrade(submissionId, assessmentId, userId, gradeId, privileges))
                .withMessageContaining("Unauthorised");
    }

    @Test
    public void activateGradeNotAssigned() throws Exception {
        testSubmission.setProject(testProject);
        testSubmission.setGrader(new User(userId + 1));
        Long gradeId = 62L;
        User testGrader = new User(userId);
        List<PrivilegeEnum> privileges = new ArrayList<>();
        privileges.add(PrivilegeEnum.GRADING_WRITE_SINGLE);
        GradingParticipation participation = new GradingParticipation(testGrader, testProject, new Role("TA"));

        when(submissionRepository.findById(submissionId)).thenReturn(Optional.of(testSubmission));
        when(gradingParticipationService.getGradingParticipationByUserAndProject(userId, projectId)).thenReturn(participation);
        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> assessmentService.activateGrade(submissionId, assessmentId, userId, gradeId, privileges))
                .withMessageContaining("Unauthorised");
    }

    @Test
    public void activateGradeGradeNotFound() throws Exception {
        testSubmission.setProject(testProject);
        Long gradeId = 62L;
        User testGrader = new User(userId);
        testSubmission.setGrader(testGrader);
        List<PrivilegeEnum> privileges = new ArrayList<>();
        privileges.add(PrivilegeEnum.GRADING_WRITE_SINGLE);
        GradingParticipation participation = new GradingParticipation(testGrader, testProject, new Role("TA"));

        when(submissionRepository.findById(submissionId)).thenReturn(Optional.of(testSubmission));
        when(gradingParticipationService.getGradingParticipationByUserAndProject(userId, projectId)).thenReturn(participation);
        when(gradeRepository.findGradeById(gradeId)).thenReturn(Optional.empty());

        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> assessmentService.activateGrade(submissionId, assessmentId, userId, gradeId, privileges))
                .withMessageContaining("Grade not found");
    }

    @Test
    public void activateGradeOk() throws Exception {
        testSubmission.setProject(testProject);
        Long gradeId = 62L;
        User testGrader = new User(userId);
        testSubmission.setGrader(testGrader);
        Grade testGrade = new Grade();
        testGrade.setAssessment(testAssessment);
        List<PrivilegeEnum> privileges = new ArrayList<>();
        privileges.add(PrivilegeEnum.GRADING_WRITE_SINGLE);
        GradingParticipation participation = new GradingParticipation(testGrader, testProject, new Role("TA"));

        when(submissionRepository.findById(submissionId)).thenReturn(Optional.of(testSubmission));
        when(gradingParticipationService.getGradingParticipationByUserAndProject(userId, projectId)).thenReturn(participation);
        when(gradeRepository.findGradeById(gradeId)).thenReturn(Optional.of(testGrade));
        when(gradeRepository.save(any(Grade.class))).then(returnsFirstArg());

        assertThat(assessmentService.activateGrade(submissionId, assessmentId, userId, gradeId, privileges))
                .isEqualTo(testGrade);
        assertThat(testGrade.getActive()).isTrue();
    }

    @Test
    public void createIssueSubmissionNotFound() throws Exception {
        testSubmission.setProject(testProject);
        Issue testIssue = new Issue();
        List<PrivilegeEnum> privileges = new ArrayList<>();
        privileges.add(PrivilegeEnum.GRADING_WRITE_SINGLE);

        when(submissionRepository.findById(submissionId)).thenReturn(Optional.empty());
        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> assessmentService.createIssue(testIssue, submissionId, assessmentId, userId, privileges))
                .withMessageContaining("Submission not found");
    }

    @Test
    public void createIssueNoPrivileges() throws Exception {
        testSubmission.setProject(testProject);
        Issue testIssue = new Issue();
        List<PrivilegeEnum> privileges = new ArrayList<>();

        when(submissionRepository.findById(submissionId)).thenReturn(Optional.of(testSubmission));
        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> assessmentService.createIssue(testIssue, submissionId, assessmentId, userId, privileges))
                .withMessageContaining("Unauthorised");
    }

    @Test
    public void createIssueNoGrader() throws Exception {
        testSubmission.setProject(testProject);
        Issue testIssue = new Issue();
        List<PrivilegeEnum> privileges = new ArrayList<>();
        privileges.add(PrivilegeEnum.GRADING_WRITE_SINGLE);

        when(submissionRepository.findById(submissionId)).thenReturn(Optional.of(testSubmission));
        when(gradingParticipationService.getGradingParticipationByUserAndProject(userId, projectId)).thenReturn(null);
        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> assessmentService.createIssue(testIssue, submissionId, assessmentId, userId, privileges))
                .withMessageContaining("Unauthorised");
    }

    @Test
    public void createIssueAssessmentNotFound() throws Exception {
        testSubmission.setProject(testProject);
        Issue testIssue = new Issue();
        User testGrader = new User(userId);
        testSubmission.setGrader(testGrader);
        List<PrivilegeEnum> privileges = new ArrayList<>();
        privileges.add(PrivilegeEnum.GRADING_WRITE_SINGLE);
        GradingParticipation participation = new GradingParticipation(testGrader, testProject, new Role("TA"));

        when(submissionRepository.findById(submissionId)).thenReturn(Optional.of(testSubmission));
        when(gradingParticipationService.getGradingParticipationByUserAndProject(userId, projectId)).thenReturn(participation);
        when(assessmentRepository.findById(assessmentId)).thenReturn(Optional.empty());
        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> assessmentService.createIssue(testIssue, submissionId, assessmentId, userId, privileges))
                .withMessageContaining("Assessment not found");

    }
    @Test
    public void createIssueOk() throws Exception {
        testAssessment.setProject(testProject);
        testSubmission.setProject(testProject);
        Issue testIssue = new Issue();
        User testGrader = new User(userId);
        testSubmission.setGrader(testGrader);
        List<PrivilegeEnum> privileges = new ArrayList<>();
        privileges.add(PrivilegeEnum.GRADING_WRITE_SINGLE);
        GradingParticipation participation = new GradingParticipation(testGrader, testProject, new Role("TA"));
        IssueStatus status = new IssueStatus(IssueStatusEnum.OPEN);

        when(submissionRepository.findById(submissionId)).thenReturn(Optional.of(testSubmission));
        when(gradingParticipationService.getGradingParticipationByUserAndProject(userId, projectId)).thenReturn(participation);
        when(assessmentRepository.findById(assessmentId)).thenReturn(Optional.of(testAssessment));
        when(userService.findById(userId)).thenReturn(testGrader);
        when(issueRepository.save(any(Issue.class))).then(returnsFirstArg());
        when(issueStatusRepository.findByName(IssueStatusEnum.OPEN.toString())).thenReturn(status);
        when(settingsService.getSettings(projectId, userId)).thenReturn(new Settings(userId, projectId));


        assertThat(assessmentService.createIssue(testIssue, submissionId, assessmentId, userId, privileges))
                .isEqualTo(testIssue);
        assertThat(testIssue.getAssessment()).isEqualTo(testAssessment);
        assertThat(testIssue.getSolution()).isNull();
        assertThat(testIssue.getStatus()).isEqualTo(status);
        assertThat(testIssue.getCreator()).isEqualTo(testGrader);
    }

    @Test
    public void resolveIssueSubmissionNotFound() throws Exception {
        testSubmission.setProject(testProject);
        Long issueId = 62L;
        List<PrivilegeEnum> privileges = new ArrayList<>();
        privileges.add(PrivilegeEnum.GRADING_WRITE_SINGLE);
        String solutionText = "Test Solution";
        IssueSolution solution = new IssueSolution(solutionText);

        when(submissionRepository.findById(submissionId)).thenReturn(Optional.empty());
        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> assessmentService.resolveIssue(submissionId, issueId, userId, solution, privileges))
                .withMessageContaining("Submission not found");
    }

    @Test
    public void resolveIssueNoGrader() throws Exception {
        testSubmission.setProject(testProject);
        Long issueId = 62L;
        List<PrivilegeEnum> privileges = new ArrayList<>();
        privileges.add(PrivilegeEnum.GRADING_WRITE_SINGLE);
        String solutionText = "Test Solution";
        IssueSolution solution = new IssueSolution(solutionText);

        when(submissionRepository.findById(submissionId)).thenReturn(Optional.of(testSubmission));
        when(gradingParticipationService.getGradingParticipationByUserAndProject(userId, projectId)).thenReturn(null);
        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> assessmentService.resolveIssue(submissionId, issueId, userId, solution, privileges))
                .withMessageContaining("Unauthorised");
    }

    @Test
    public void resolveIssueNotAssigned() throws Exception {
        testSubmission.setProject(testProject);
        testSubmission.setGrader(new User(userId + 1));
        Long issueId = 62L;
        User testGrader = new User(userId);
        List<PrivilegeEnum> privileges = new ArrayList<>();
        privileges.add(PrivilegeEnum.GRADING_WRITE_SINGLE);
        GradingParticipation participation = new GradingParticipation(testGrader, testProject, new Role("TA"));
        String solutionText = "Test Solution";
        IssueSolution solution = new IssueSolution(solutionText);

        when(submissionRepository.findById(submissionId)).thenReturn(Optional.of(testSubmission));
        when(gradingParticipationService.getGradingParticipationByUserAndProject(userId, projectId)).thenReturn(participation);
        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> assessmentService.resolveIssue(submissionId, issueId, userId, solution, privileges))
                .withMessageContaining("Unauthorised");
    }

    @Test
    public void resolveIssueIssueNotFound() throws Exception {
        testSubmission.setProject(testProject);
        Issue testIssue = new Issue();
        Long issueId = 62L;
        User testGrader = new User(userId);
        testSubmission.setGrader(testGrader);
        List<PrivilegeEnum> privileges = new ArrayList<>();
        privileges.add(PrivilegeEnum.GRADING_WRITE_SINGLE);
        GradingParticipation participation = new GradingParticipation(testGrader, testProject, new Role("TA"));
        String solutionText = "Test Solution";
        IssueSolution solution = new IssueSolution(solutionText);

        when(submissionRepository.findById(submissionId)).thenReturn(Optional.of(testSubmission));
        when(gradingParticipationService.getGradingParticipationByUserAndProject(userId, projectId)).thenReturn(participation);
        when(issueRepository.findIssueById(issueId)).thenReturn(Optional.empty());
        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> assessmentService.resolveIssue(submissionId, issueId, userId, solution, privileges))
                .withMessageContaining("Issue not found");
    }

    @Test
    public void resolveIssueOk() throws Exception {
        testAssessment.setProject(testProject);
        testSubmission.setProject(testProject);
        Issue testIssue = new Issue();
        Long issueId = 62L;
        User testGrader = new User(userId);
        testSubmission.setGrader(testGrader);
        List<PrivilegeEnum> privileges = new ArrayList<>();
        privileges.add(PrivilegeEnum.GRADING_WRITE_SINGLE);
        GradingParticipation participation = new GradingParticipation(testGrader, testProject, new Role("TA"));
        IssueStatus status = new IssueStatus(IssueStatusEnum.RESOLVED);
        String solutionText = "Test Solution";
        IssueSolution solution = new IssueSolution(solutionText);

        when(submissionRepository.findById(submissionId)).thenReturn(Optional.of(testSubmission));
        when(gradingParticipationService.getGradingParticipationByUserAndProject(userId, projectId)).thenReturn(participation);
        when(issueRepository.findIssueById(issueId)).thenReturn(Optional.of(testIssue));
        when(issueStatusRepository.findByName(IssueStatusEnum.RESOLVED.toString())).thenReturn(status);
        when(issueRepository.save(any(Issue.class))).then(returnsFirstArg());


        assertThat(assessmentService.resolveIssue(submissionId, issueId, userId, solution, privileges))
                .isEqualTo(testIssue);
        assertThat(testIssue.getSolution()).isEqualTo(solutionText);
        assertThat(testIssue.getStatus()).isEqualTo(status);
    }
}
