package com.group13.tcsprojectgrading.controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.group13.tcsprojectgrading.canvas.api.CanvasApi;
import com.group13.tcsprojectgrading.models.*;
import com.group13.tcsprojectgrading.models.grading.CriterionGrade;
import com.group13.tcsprojectgrading.models.grading.Grade;
import com.group13.tcsprojectgrading.models.rubric.Rubric;
import com.group13.tcsprojectgrading.services.*;
import com.group13.tcsprojectgrading.services.grading.AssessmentService;
import com.group13.tcsprojectgrading.services.rubric.RubricService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/courses/{courseId}/projects/{projectId}/submissions/{submissionId}/{assessmentId}")
public class AssessmentController {
    private final CanvasApi canvasApi;

    private final ActivityService activityService;
    private final RubricService rubricService;
    private final GraderService graderService;
    private final ProjectService projectService;
    private final SubmissionService submissionService;
    private final AssessmentService assessmentService;
    private final AssessmentLinkerService assessmentLinkerService;
    private final ParticipantService participantService;
    private final IssueService issueService;

    @Autowired
    public AssessmentController(CanvasApi canvasApi, ActivityService activityService, RubricService rubricService, GraderService graderService, ProjectService projectService, SubmissionService submissionService, AssessmentService assessmentService, AssessmentLinkerService assessmentLinkerService, ParticipantService participantService, IssueService issueService) {
        this.canvasApi = canvasApi;
        this.activityService = activityService;
        this.rubricService = rubricService;
        this.graderService = graderService;
        this.projectService = projectService;
        this.submissionService = submissionService;
        this.assessmentService = assessmentService;
        this.assessmentLinkerService = assessmentLinkerService;
        this.participantService = participantService;
        this.issueService = issueService;
    }

    @RequestMapping(value = "/grading", method = RequestMethod.GET, produces = "application/json")
    protected ResponseEntity<String> getAssessment(@PathVariable String courseId,
                                                   @PathVariable String projectId,
                                                   @PathVariable String submissionId,
                                                   @PathVariable String assessmentId)
            throws JsonProcessingException {
        Project project = projectService.getProjectById(courseId, projectId);
        Submission submission = submissionService.findSubmissionById(submissionId);
        List<Assessment> assessmentList = assessmentService.getAssessmentBySubmission(submission);

        Assessment submissionAssessment = assessmentService.getAssessmentById(assessmentId);

        if (submissionAssessment == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else if (!assessmentList.contains(submissionAssessment)) {
            System.out.println("here");
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } else {
            ObjectMapper objectMapper = new ObjectMapper();
            String rubricString = objectMapper.writeValueAsString(submissionAssessment);
            return new ResponseEntity<>(rubricString, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/grading/{criterionId}", method = RequestMethod.PUT)
    protected ResponseEntity<String> alterCriterionAssessment(
            @PathVariable String courseId,
            @PathVariable String projectId,
            @PathVariable String submissionId,
            @PathVariable String assessmentId,
            @PathVariable String criterionId,
            @RequestBody Grade newGrade
    ) {
        Submission submission = submissionService.findSubmissionById(submissionId);
        List<Assessment> assessmentList = assessmentService.getAssessmentBySubmission(submission);

        Assessment submissionAssessment = assessmentService.getAssessmentById(assessmentId);
        if (submissionAssessment == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else if (!assessmentList.contains(submissionAssessment)) {
            System.out.println(assessmentList);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } else {Map<String, CriterionGrade> grades = submissionAssessment.getGrades();

            // add the grade to history
            if (grades.containsKey(criterionId)) {
                grades.get(criterionId).getHistory().add(newGrade);
                System.out.println(grades.get(criterionId).getHistory());
                grades.get(criterionId).setActive(grades.get(criterionId).getActive() + 1);
            } else {
                grades.put(criterionId, new CriterionGrade(
                        0, new ArrayList<>() {{
                    add(newGrade);
                }}
                ));
            }

            // update graded count and progress of assessment
            submissionAssessment.increaseGradedCount(1);
            double progress = assessmentService.getProgress(rubricService.getRubricById(projectId), submissionAssessment);
            System.out.println("Progress: " + progress);
            submissionAssessment.setProgress(progress);
            // update progress of project
            Project project = projectService.getProjectById(courseId, projectId);
            projectService.updateProgress(project, progress);

            // check if fully graded (should be possible to replace with a manually typed value)
            Rubric rubric = this.rubricService.getRubricById(projectId);
            if (rubric.getCriterionCount() == submissionAssessment.getGradedCount()) {
                double total = this.assessmentService.calculateFinalGrade(rubric, submissionAssessment);
                submissionAssessment.setFinalGrade(total);
            }

            this.assessmentService.saveAssessment(submissionAssessment);
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    // TODO, doesn't work
    @RequestMapping(value = "/grading/{criterionId}/active/{id}", method = RequestMethod.PUT)
    protected ResponseEntity<String> updateActiveGrading(
            @PathVariable String courseId,
            @PathVariable String projectId,
            @PathVariable String submissionId,
            @PathVariable String assessmentId,
            @PathVariable String criterionId,
            @PathVariable int id
    ) {
        Project project = projectService.getProjectById(courseId, projectId);
        Submission submission = submissionService.findSubmissionById(submissionId);
        List<Assessment> assessmentList = assessmentService.getAssessmentBySubmission(submission);

        Assessment submissionAssessment = assessmentService.getAssessmentById(assessmentId);

        if (!assessmentList.contains(submissionAssessment)) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }


        if (submissionAssessment == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            Map<String, CriterionGrade> grades = submissionAssessment.getGrades();

            if (grades.get(criterionId).getHistory().size() > id) {
                grades.get(criterionId).setActive(id);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            this.assessmentService.saveAssessment(submissionAssessment);
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/issues", method = RequestMethod.POST)
    protected ArrayNode createIssue(
            @PathVariable String courseId,
            @PathVariable String projectId,
            @PathVariable String submissionId,
            @PathVariable String assessmentId,
            @RequestBody JsonNode issue,
            Principal principal
    ) {
        Project project = projectService.getProjectById(courseId, projectId);
        Submission submission = submissionService.findSubmissionById(submissionId);
        List<Assessment> assessmentList = assessmentService.getAssessmentBySubmission(submission);

        Assessment submissionAssessment = assessmentService.getAssessmentById(assessmentId);

        if (project == null || submissionAssessment == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } else if (!assessmentList.contains(submissionAssessment)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        } else {
            UUID target = UUID.fromString(issue.get("target").asText());
            String name = issue.get("targetName").asText();
            String type = issue.get("targetType").asText();
            Issue reference = null;
            if (!issue.get("reference").asText().equals("null"))
                reference = issueService.findById(UUID.fromString(issue.get("reference").asText()));
            String subject = issue.get("subject").asText();
            String description = issue.get("description").asText();
            Grader creator = graderService.getGraderFromGraderId(principal.getName(), project);
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
                    "unresolved",
                    addressee
            );

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

    @RequestMapping(value = "/issues/resolve", method = RequestMethod.POST)
    protected ArrayNode resolveIssue(
            @PathVariable String courseId,
            @PathVariable String projectId,
            @PathVariable String submissionId,
            @PathVariable String assessmentId,
            @RequestBody JsonNode issue,
            Principal principal
    ) {
        Project project = projectService.getProjectById(courseId, projectId);
        Submission submission = submissionService.findSubmissionById(submissionId);
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

    @RequestMapping(value = "/issues", method = RequestMethod.GET)
    protected ArrayNode getIssues(
            @PathVariable String courseId,
            @PathVariable String projectId,
            @PathVariable String submissionId,
            @PathVariable String assessmentId
    ) {
        Project project = projectService.getProjectById(courseId, projectId);
        Submission submission = submissionService.findSubmissionById(submissionId);
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