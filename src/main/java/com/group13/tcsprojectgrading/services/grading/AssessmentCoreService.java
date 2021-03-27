package com.group13.tcsprojectgrading.services.grading;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.group13.tcsprojectgrading.models.*;
import com.group13.tcsprojectgrading.models.graders.Grader;
import com.group13.tcsprojectgrading.models.grading.Assessment;
import com.group13.tcsprojectgrading.models.grading.CriterionGrade;
import com.group13.tcsprojectgrading.models.grading.Grade;
import com.group13.tcsprojectgrading.models.permissions.PrivilegeEnum;
import com.group13.tcsprojectgrading.models.rubric.Rubric;
import com.group13.tcsprojectgrading.models.submissions.Issue;
import com.group13.tcsprojectgrading.models.submissions.Submission;
import com.group13.tcsprojectgrading.services.graders.GraderService;
import com.group13.tcsprojectgrading.services.ProjectService;
import com.group13.tcsprojectgrading.services.notifications.NotificationsService;
import com.group13.tcsprojectgrading.services.settings.SettingsService;
import com.group13.tcsprojectgrading.services.submissions.SubmissionService;
import com.group13.tcsprojectgrading.services.rubric.RubricService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.group13.tcsprojectgrading.models.permissions.PrivilegeEnum.*;

@Service
public class AssessmentCoreService {
    private final AssessmentService assessmentService;
    private final ProjectService projectService;
    private final SubmissionService submissionService;
    private final RubricService rubricService;
    private final IssueService issueService;
    private final GraderService graderService;
    private final NotificationsService notificationsService;
    private final SettingsService settingsService;

    @Autowired
    public AssessmentCoreService(AssessmentService assessmentService, ProjectService projectService,
                                 SubmissionService submissionService, RubricService rubricService,
                                 IssueService issueService, GraderService graderService,
                                 NotificationsService notificationsService,
                                 SettingsService settingsService) {
        this.assessmentService = assessmentService;
        this.projectService = projectService;
        this.submissionService = submissionService;
        this.rubricService = rubricService;
        this.issueService = issueService;
        this.graderService = graderService;
        this.notificationsService = notificationsService;
        this.settingsService = settingsService;
    }

    @Transactional
    public String getAssessment(String courseId, String projectId, String submissionId, String assessmentId, List<PrivilegeEnum> privileges, String userId) throws JsonProcessingException {
        Project project = projectService.getProjectById(courseId, projectId);
        if (project == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "no project");
        }

        Submission submission = submissionService.findSubmissionById(submissionId);

        if (submission == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "no submission");
        }

        if (privileges != null && privileges.contains(GRADING_READ_SINGLE)) {
            if (submission.getGrader() == null || !submission.getGrader().getUserId().equals(userId)) {
                throw new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "submission not assigned"
                );
            }
        }

        List<Assessment> assessmentList = assessmentService.getAssessmentBySubmission(submission);

        Assessment submissionAssessment = assessmentService.getAssessmentById(assessmentId);

        if (!assessmentList.contains(submissionAssessment)) {
            System.out.println("here");
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Conflict");
        }

        if (submissionAssessment == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "no submissionAssessment");
        } else {
            ObjectMapper objectMapper = new ObjectMapper();
            String rubricString = objectMapper.writeValueAsString(submissionAssessment);
            return rubricString;
        }
    }

    @Transactional(rollbackOn = Exception.class)
    public String alterCriterionAssessment(String courseId, String projectId, String submissionId,
                                           String assessmentId, String criterionId, Grade newGrade, List<PrivilegeEnum> privileges, String userId) {
        Submission submission = submissionService.findSubmissionById(submissionId);

        if (submission == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "no submission");
        }

        if (privileges != null && privileges.contains(GRADING_WRITE_SINGLE)) {
            if (submission.getGrader() == null || !submission.getGrader().getUserId().equals(userId)) {
                throw new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "submission not assigned"
                );
            }
        }

        List<Assessment> assessmentList = assessmentService.getAssessmentBySubmission(submission);

        Assessment submissionAssessment = assessmentService.getAssessmentById(assessmentId);

        if (!assessmentList.contains(submissionAssessment)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Conflict");
        }

        if (submissionAssessment == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "no submissionAssessment");
        } else {
            Map<String, CriterionGrade> grades = submissionAssessment.getGrades();

            // add the grade to history
            if (grades.containsKey(criterionId)) {
                grades.get(criterionId).getHistory().add(newGrade);
                grades.get(criterionId).setActive(grades.get(criterionId).getActive() + 1);
            } else {
                grades.put(criterionId, new CriterionGrade(
                        0, new ArrayList<>() {{
                    add(newGrade);
                }}
                ));
            }

            // update graded count of assessment (if it's not a regraded criterion)
            if (grades.get(criterionId).getHistory().size() == 1) {
                submissionAssessment.increaseGradedCount(1);

                // update graded count of project
                // -
            }

            // check if fully graded (should be possible to replace with a manually typed value)
            Rubric rubric = this.rubricService.getRubricById(projectId);
            if (rubric.getCriterionCount() == submissionAssessment.getGradedCount()) {
                int total = this.assessmentService.calculateFinalGrade(rubric, submissionAssessment);
                submissionAssessment.setFinalGrade(total);
            }

            this.assessmentService.saveAssessment(submissionAssessment);
            return "ok";
        }
    }

    @Transactional(rollbackOn = Exception.class)
    public String updateActiveGrading(String courseId, String projectId, String submissionId,
                                           String assessmentId, String criterionId, int id, List<PrivilegeEnum> privileges, String userId) {
        Project project = projectService.getProjectById(courseId, projectId);
        Submission submission = submissionService.findSubmissionById(submissionId);

        if (submission == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "no submission");
        }

        if (privileges != null && privileges.contains(GRADING_WRITE_SINGLE)) {
            if (submission.getGrader() == null || !submission.getGrader().getUserId().equals(userId)) {
                throw new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "submission not assigned"
                );
            }
        }

        List<Assessment> assessmentList = assessmentService.getAssessmentBySubmission(submission);

        Assessment submissionAssessment = assessmentService.getAssessmentById(assessmentId);

        if (!assessmentList.contains(submissionAssessment)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Conflict");
        }

        if (submissionAssessment == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "no submissionAssessment");
        } else {
            Map<String, CriterionGrade> grades = submissionAssessment.getGrades();

            if (grades.get(criterionId).getHistory().size() > id) {
                grades.get(criterionId).setActive(id);
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "active id not found");
            }

            this.assessmentService.saveAssessment(submissionAssessment);
            return "ok";
        }
    }

    @Transactional(rollbackOn = Exception.class)
    public ArrayNode createIssue(String courseId, String projectId, String submissionId,
                              String assessmentId, JsonNode issue, List<PrivilegeEnum> privileges, String userId) {
        Project project = projectService.getProjectById(courseId, projectId);
        Submission submission = submissionService.findSubmissionById(submissionId);

        if (submission == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "no submission");
        }

        if (privileges != null && privileges.contains(GRADING_WRITE_SINGLE)) {
            if (submission.getGrader() == null || !submission.getGrader().getUserId().equals(userId)) {
                throw new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "submission not assigned"
                );
            }
        }

        List<Assessment> assessmentList = assessmentService.getAssessmentBySubmission(submission);

        Assessment submissionAssessment = assessmentService.getAssessmentById(assessmentId);

        if (!assessmentList.contains(submissionAssessment)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        if (project == null || submissionAssessment == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } else {
            UUID target = UUID.fromString(issue.get("target").asText());
            String name = issue.get("targetName").asText();
            String type = issue.get("targetType").asText();
            Issue reference = null;
            if (!issue.get("reference").asText().equals("null"))
                reference = issueService.findById(UUID.fromString(issue.get("reference").asText()));
            String subject = issue.get("subject").asText();
            String description = issue.get("description").asText();
            Grader creator = graderService.getGraderFromGraderId(userId, project);
            String addresseeId = issue.get("addressee").asText();
            Grader addressee = null;
            if (!addresseeId.equals("null")) {
                addressee = graderService.getGraderFromGraderId(addresseeId, project);
            }
            if (creator == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }

            Issue issue1 = new Issue(
                    submissionAssessment.getId(),
                    target,
                    type,
                    name,
                    reference,
                    subject,
                    description,
                    creator,
                    "unresolved"
            );
            issue1.setAddressee(addressee);

            // save issue
            issueService.saveIssue(issue1);

            // send notification if addressee has notifications enabled
            // TODO: we need to save email addresses of users
            // TODO: the email will be sent if the transaction is rolled back, that's a bit of an issue...
            if (addressee != null && this.settingsService.getOrCreateSettings(courseId, projectId, addressee.getUserId()).isNotificationsEnabled()) {
                this.notificationsService.sendIssueNotification("o.khavrona@student.utwente.nl", project.getName());
            }

            List<Issue> issues = issueService.findIssuesByAssessment(submissionAssessment.getId());
            ObjectMapper objectMapper = new ObjectMapper();
            ArrayNode result = objectMapper.createArrayNode();
            for(Issue issue2: issues) {
                result.add(issue2.convertToJson());
            }
            return result;
        }
    }

    @Transactional(rollbackOn = Exception.class)
    public ArrayNode resolveIssue(String courseId, String projectId, String submissionId,
                                 String assessmentId, JsonNode issue, List<PrivilegeEnum> privileges, String userId) {
        Project project = projectService.getProjectById(courseId, projectId);
        Submission submission = submissionService.findSubmissionById(submissionId);

        if (submission == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "no submission");
        }

        if (privileges != null && privileges.contains(GRADING_WRITE_SINGLE)) {
            if (submission.getGrader() == null || !submission.getGrader().getUserId().equals(userId)) {
                throw new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "submission not assigned"
                );
            }
        }

        List<Assessment> assessmentList = assessmentService.getAssessmentBySubmission(submission);

        Assessment submissionAssessment = assessmentService.getAssessmentById(assessmentId);

        if (!assessmentList.contains(submissionAssessment)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        if (project == null || submissionAssessment == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } else {
            Issue issue1 = issueService.findById(UUID.fromString(issue.get("id").asText()));
            if (issue1 != null) {
                issue1.setStatus("resolved");
                issue1.setSolution(issue.get("solution").asText());

                // send a notification to the author that the issue has been resolved
                // TODO: we need to save email addresses of users
                // TODO: the email will be sent if the transaction is rolled back, that's a bit of an issue...
                if (issue1.getAddressee() != null && this.settingsService.getOrCreateSettings(courseId, projectId, issue1.getAddressee().getUserId()).isNotificationsEnabled()) {
                    this.notificationsService.sendResolvedNotification("o.khavrona@student.utwente.nl", issue1.getSubject(), project.getName());
                }
            }
            issueService.saveIssue(issue1);

            List<Issue> issues = issueService.findIssuesByAssessment(submissionAssessment.getId());
            ObjectMapper objectMapper = new ObjectMapper();
            ArrayNode result = objectMapper.createArrayNode();
            for(Issue issue2: issues) {
                result.add(issue2.convertToJson());
            }
            return result;
        }
    }

    @Transactional(rollbackOn = Exception.class)
    public ArrayNode getIssues(String courseId, String projectId, String submissionId,
                                  String assessmentId, List<PrivilegeEnum> privileges, String userId) {
        Project project = projectService.getProjectById(courseId, projectId);
        Submission submission = submissionService.findSubmissionById(submissionId);

        if (submission == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "no submission");
        }

        if (privileges != null && privileges.contains(SUBMISSION_READ_SINGLE)) {
            if (submission.getGrader() == null || !submission.getGrader().getUserId().equals(userId)) {
                throw new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "submission not assigned"
                );
            }
        }

        List<Assessment> assessmentList = assessmentService.getAssessmentBySubmission(submission);

        Assessment submissionAssessment = assessmentService.getAssessmentById(assessmentId);

        if (!assessmentList.contains(submissionAssessment)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "entity not found"
            );
        }
        if (project == null || submissionAssessment == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "entity not found"
            );
        } else {
            List<Issue> issues = issueService.findIssuesByAssessment(submissionAssessment.getId());
            ObjectMapper objectMapper = new ObjectMapper();
            ArrayNode result = objectMapper.createArrayNode();
            for(Issue issue: issues) {
                result.add(issue.convertToJson());
            }
            return result;
        }
    }
}
