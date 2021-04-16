package com.group13.tcsprojectgrading.controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.group13.tcsprojectgrading.canvas.api.CanvasApi;
import com.group13.tcsprojectgrading.models.grading.Assessment;
import com.group13.tcsprojectgrading.models.grading.Grade;
import com.group13.tcsprojectgrading.models.grading.Issue;
import com.group13.tcsprojectgrading.models.grading.IssueSolution;
import com.group13.tcsprojectgrading.models.permissions.PrivilegeEnum;
import com.group13.tcsprojectgrading.services.graders.GradingParticipationService;
import com.group13.tcsprojectgrading.services.grading.AssessmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

/**
 * Handles assessment endpoints
 */
@RestController
@RequestMapping("/api/courses/{courseId}/projects/{projectId}/submissions/{submissionId}/assessments/{assessmentId}")
public class AssessmentController {
    private final CanvasApi canvasApi;

    private final AssessmentService assessmentService;
    private final GradingParticipationService gradingParticipationService;

    @Autowired
    public AssessmentController(CanvasApi canvasApi, AssessmentService assessmentService,
                                GradingParticipationService gradingParticipationService) {
        this.canvasApi = canvasApi;
        this.assessmentService = assessmentService;
        this.gradingParticipationService = gradingParticipationService;
    }

    /**
     * Gets Assessment from database with details (progress, issues, grades, final grade)
     * This method requires privileges GRADING_READ_ALL or GRADING_READ_SINGLE
     * @param projectId Canvas project id
     * @param submissionId submission id
     * @param assessmentId grading sheet id
     * @param principal injected oauth2 client's information
     * @return Assessment entity
     * @throws JsonProcessingException json parsing exception
     */
    @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json")
    protected ResponseEntity<?> getAssessment(@PathVariable Long projectId,
                                   @PathVariable Long submissionId,
                                   @PathVariable Long assessmentId,
                                   Principal principal)
            throws JsonProcessingException {
        List<PrivilegeEnum> privileges = this.gradingParticipationService
                .getPrivilegesFromUserIdAndProject(Long.valueOf(principal.getName()), projectId);

        if (!(privileges != null
                && (privileges.contains(PrivilegeEnum.GRADING_READ_ALL) || privileges.contains(PrivilegeEnum.GRADING_READ_SINGLE)))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        Assessment assessment =  this.assessmentService.getAssessment(assessmentId, submissionId, Long.valueOf(principal.getName()), privileges);
        return new ResponseEntity<>(assessment, HttpStatus.OK);
    }

    /**
     * Posts a new grade for a criterion in a grading sheet
     * This method requires privileges GRADING_WRITE_ALL or GRADING_WRITE_SINGLE
     * @param projectId Canvas project id
     * @param submissionId submission id
     * @param assessmentId grading sheet id
     * @param grade Grade entity from front-end
     * @param principal injected oauth2 client's information
     * @return created Grade from database
     */
    @RequestMapping(value = "/grades", method = RequestMethod.POST)
    protected ResponseEntity<?> alterCriterionAssessment(
            @PathVariable Long projectId,
            @PathVariable Long submissionId,
            @PathVariable Long assessmentId,
            @RequestBody Grade grade,
            Principal principal
    ) {

        List<PrivilegeEnum> privileges = this.gradingParticipationService
                .getPrivilegesFromUserIdAndProject(Long.valueOf(principal.getName()), projectId);

        if (!(privileges != null
                && (privileges.contains(PrivilegeEnum.GRADING_WRITE_ALL) ||
                privileges.contains(PrivilegeEnum.GRADING_WRITE_SINGLE)))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        Grade createdGrade = this.assessmentService.addGrade(submissionId, assessmentId, grade, Long.valueOf(principal.getName()), privileges);
        return new ResponseEntity<>(createdGrade, HttpStatus.OK);
    }

    /**
     * Sets a grade as current grade in a criterion in a grading sheet
     * This method requires privileges GRADING_WRITE_ALL or GRADING_WRITE_SINGLE
     * @param gradeId id of the grade that need to be activate
     * @param projectId Canvas project id
     * @param submissionId submission id
     * @param assessmentId grading sheet id
     * @param principal injected oauth2 client's information
     * @return activated Grade from database
     */
    @RequestMapping(value = "/grades/{gradeId}/activate", method = RequestMethod.POST)
    protected ResponseEntity<?> activateGrade(
            @PathVariable Long gradeId,
            @PathVariable Long projectId,
            @PathVariable Long submissionId,
            @PathVariable Long assessmentId,
            Principal principal
    ) {

        List<PrivilegeEnum> privileges = this.gradingParticipationService
                .getPrivilegesFromUserIdAndProject(Long.valueOf(principal.getName()), projectId);

        if (!(privileges != null
                && (privileges.contains(PrivilegeEnum.GRADING_WRITE_ALL) ||
                privileges.contains(PrivilegeEnum.GRADING_WRITE_SINGLE)))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        this.assessmentService.activateGrade(submissionId, assessmentId, Long.valueOf(principal.getName()), gradeId, privileges);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Gets a list of issues in a grading sheet
     * This method requires privileges SUBMISSION_READ_ALL or SUBMISSION_READ_SINGLE
     * @param projectId Canvas project id
     * @param submissionId submission id
     * @param assessmentId grading sheet id
     * @param principal injected oauth2 client's information
     * @return a list of Issue from database
     */
    @RequestMapping(value = "/issues", method = RequestMethod.GET)
    protected List<Issue> getIssues(
            @PathVariable Long projectId,
            @PathVariable Long submissionId,
            @PathVariable Long assessmentId,
            Principal principal
    ) {

        List<PrivilegeEnum> privileges = this.gradingParticipationService
                .getPrivilegesFromUserIdAndProject(Long.valueOf(principal.getName()), projectId);

        if (!(privileges != null
                && (privileges.contains(PrivilegeEnum.SUBMISSION_READ_ALL) || privileges.contains(PrivilegeEnum.SUBMISSION_READ_SINGLE)))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        return this.assessmentService.getIssues(submissionId, assessmentId, Long.valueOf(principal.getName()), privileges);
    }

    /**
     * Creates a new issue in a grading sheet
     * This method requires privileges GRADING_WRITE_SINGLE or GRADING_WRITE_ALL
     * @param projectId Canvas project id
     * @param submissionId submission id
     * @param assessmentId grading sheet id
     * @param issue Issue entity from front-end
     * @param principal injected oauth2 client's information
     * @return created Issue from database
     */
    @RequestMapping(value = "/issues", method = RequestMethod.POST)
    protected Issue createIssue(
            @PathVariable Long projectId,
            @PathVariable Long submissionId,
            @PathVariable Long assessmentId,
            @RequestBody Issue issue,
            Principal principal
    ) {

        List<PrivilegeEnum> privileges = this.gradingParticipationService
                .getPrivilegesFromUserIdAndProject(Long.valueOf(principal.getName()), projectId);

        if (!(privileges != null
                && (privileges.contains(PrivilegeEnum.GRADING_WRITE_SINGLE) || privileges.contains(PrivilegeEnum.GRADING_WRITE_ALL)))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        return this.assessmentService.createIssue(issue, submissionId, assessmentId, Long.valueOf(principal.getName()), privileges);
    }

    /**
     * Resolves an issue in a grading sheet
     * This method requires privileges GRADING_WRITE_SINGLE or GRADING_WRITE_ALL
     * @param projectId Canvas project id
     * @param submissionId submission id
     * @param assessmentId grading sheet id
     * @param issueId issue id that need to be resolve
     * @param solution issue solution
     * @param principal injected oauth2 client's information
     * @return a list of Issue from database
     */
    @RequestMapping(value = "/issues/{issueId}/resolve", method = RequestMethod.POST)
    protected Issue resolveIssue(
            @PathVariable Long projectId,
            @PathVariable Long submissionId,
            @PathVariable Long assessmentId,
            @PathVariable Long issueId,
            @RequestBody IssueSolution solution,
            Principal principal
    ) {
        List<PrivilegeEnum> privileges = this.gradingParticipationService
                .getPrivilegesFromUserIdAndProject(Long.valueOf(principal.getName()), projectId);

        if (!(privileges != null
                && (privileges.contains(PrivilegeEnum.GRADING_WRITE_SINGLE) || privileges.contains(PrivilegeEnum.GRADING_WRITE_ALL)))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        return this.assessmentService.resolveIssue(submissionId, issueId, Long.valueOf(principal.getName()), solution, privileges);
    }
}