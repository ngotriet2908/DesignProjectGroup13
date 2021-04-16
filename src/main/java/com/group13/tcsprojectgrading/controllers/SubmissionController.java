package com.group13.tcsprojectgrading.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.group13.tcsprojectgrading.canvas.api.CanvasApi;
import com.group13.tcsprojectgrading.models.grading.Assessment;
import com.group13.tcsprojectgrading.models.permissions.PrivilegeEnum;
import com.group13.tcsprojectgrading.models.submissions.Label;
import com.group13.tcsprojectgrading.models.submissions.Submission;
import com.group13.tcsprojectgrading.models.user.User;
import com.group13.tcsprojectgrading.services.graders.GradingParticipationService;
import com.group13.tcsprojectgrading.services.submissions.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;
import java.util.Set;

import static com.group13.tcsprojectgrading.models.permissions.PrivilegeEnum.*;

/**
 * Handles submission-related endpoints
 */
@RestController
@RequestMapping("/api/courses/{courseId}/projects/{projectId}/submissions")
public class SubmissionController {
    private final CanvasApi canvasApi;
    private final SubmissionService submissionService;
    private final GradingParticipationService gradingParticipationService;

    @Autowired
    public SubmissionController(CanvasApi canvasApi, SubmissionService submissionService, GradingParticipationService gradingParticipationService) {
        this.canvasApi = canvasApi;
        this.submissionService = submissionService;
        this.gradingParticipationService = gradingParticipationService;
    }

    /**
     * Gets submissions from database either to return submissions based on graders/unassigned
     * This method requires privilege SUBMISSIONS_READ
     * @param courseId Canvas course id
     * @param projectId Canvas project id
     * @param grader unassigned/id
     * @param principal injected oauth2 client's information
     * @return list of Submissions from database
     */
    @GetMapping(value = "")
    protected ResponseEntity<?> getSubmissions(
            @PathVariable Long courseId,
            @PathVariable Long projectId,
            @RequestParam(required=false, name = "grader") String grader,
            Principal principal) {

        List<PrivilegeEnum> privileges = this.gradingParticipationService
                .getPrivilegesFromUserIdAndProject(Long.valueOf(principal.getName()), projectId);
        if (!(privileges != null && privileges.contains(SUBMISSIONS_READ))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        List<Submission> submissions;
        if (grader != null && grader.equals("unassigned")) {
            submissions = this.submissionService.getUnassignedSubmissions(courseId, projectId, Long.valueOf(principal.getName()));
        } else {
            submissions = this.submissionService.getSubmissions(courseId, projectId, Long.valueOf(principal.getName()));
        }

        return new ResponseEntity<>(submissions, HttpStatus.OK);
    }

    /**
     * Gets info from a submission
     * This method requires privileges SUBMISSION_READ_SINGLE or SUBMISSION_READ_ALL
     * @param courseId Canvas course id
     * @param projectId Canvas project id
     * @param submissionId submission id
     * @param principal injected oauth2 client's information
     * @return a Submission
     * @throws JsonProcessingException json parsing exception
     */
    @GetMapping(value = "/{submissionId}")
    protected ResponseEntity<?> getSubmissionInfo(@PathVariable Long courseId,
                                                  @PathVariable Long projectId,
                                                  @PathVariable Long submissionId,
                                                  Principal principal
    ) throws JsonProcessingException {
        List<PrivilegeEnum> privileges = this.gradingParticipationService
                .getPrivilegesFromUserIdAndProject(Long.valueOf(principal.getName()), projectId);
        if (!(privileges != null &&
                ((privileges.contains(SUBMISSION_READ_SINGLE)) ||
                (privileges.contains(SUBMISSION_READ_ALL)))
        )) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        Submission submission = this.submissionService.getSubmissionController(projectId, submissionId, privileges, Long.valueOf(principal.getName()));
        return new ResponseEntity<>(submission, HttpStatus.OK);
    }

    /**
     * Assigns the submission to the grader.
     * This method requires privilege MANAGE_GRADERS_EDIT
     * @param courseId Canvas course id
     * @param projectId Canvas project id
     * @param submissionId submission id
     * @param grader user entity that will be assigned submission to
     * @param principal injected oauth2 client's information
     * @throws JsonProcessingException json parsing exception
     */
    @PostMapping(value = "/{submissionId}/assign")
    protected void assignSubmission(@PathVariable Long courseId,
                                    @PathVariable Long projectId,
                                    @PathVariable Long submissionId,
                                    @RequestBody User grader,
                                    Principal principal
    ) throws JsonProcessingException {
        List<PrivilegeEnum> privileges = this.gradingParticipationService
                .getPrivilegesFromUserIdAndProject(Long.valueOf(principal.getName()), projectId);
        if (!(privileges != null &&
                ((privileges.contains(MANAGE_GRADERS_EDIT)))
        )) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        this.submissionService.assignSubmission(submissionId, grader);
    }

    /**
     * Moves the submission from its grader back to the 'unassigned'.
     * This method requires privileges MANAGE_GRADERS_EDIT or MANAGE_GRADERS_SELF_EDIT
     * @param courseId Canvas course id
     * @param projectId Canvas project id
     * @param submissionId submission id
     * @param principal injected oauth2 client's information
     * @throws JsonProcessingException json parsing exception
     */
    @PostMapping(value = "/{submissionId}/dissociate")
    protected void dissociateSubmission(@PathVariable Long courseId,
                                        @PathVariable Long projectId,
                                        @PathVariable Long submissionId,
                                        Principal principal
    ) throws JsonProcessingException {
        List<PrivilegeEnum> privileges = this.gradingParticipationService
                .getPrivilegesFromUserIdAndProject(Long.valueOf(principal.getName()), projectId);
        if (!(privileges != null &&
                ((privileges.contains(MANAGE_GRADERS_EDIT)) ||
                        (privileges.contains(MANAGE_GRADERS_SELF_EDIT)))
        )) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        this.submissionService.dissociateSubmission(submissionId, privileges, Long.valueOf(principal.getName()));
    }

    /**
     * Manages labels for a submission
     * This method requires privileges SUBMISSION_EDIT_ALL or SUBMISSION_EDIT_SINGLE
     * @param courseId Canvas course id
     * @param projectId Canvas project id
     * @param submissionId submission id
     * @param labels list of label soon to be active
     * @param principal injected oauth2 client's information
     * @throws JsonProcessingException json parsing exception
     */
    @PutMapping(value = "/{submissionId}/labels")
    protected void saveLabels(@PathVariable Long courseId,
                              @PathVariable Long projectId,
                              @PathVariable Long submissionId,
                              @RequestBody Set<Label> labels,
                              Principal principal
    ) throws JsonProcessingException {

        List<PrivilegeEnum> privileges = this.gradingParticipationService
                .getPrivilegesFromUserIdAndProject(Long.valueOf(principal.getName()), projectId);
        if (!(privileges != null &&
                ((privileges.contains(SUBMISSION_EDIT_ALL)) ||
                        (privileges.contains(SUBMISSION_EDIT_SINGLE)))
        )) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        this.submissionService.saveLabels(projectId, Long.valueOf(principal.getName()), labels, submissionId, privileges);
    }

    /**
     * Manages grading sheets (new, clone, move, delete)
     * This method requires privileges SUBMISSION_EDIT_ALL or SUBMISSION_EDIT_SINGLE
     * @param courseId Canvas course id
     * @param projectId Canvas project id
     * @param submissionId submission id
     * @param object action object
     * @param principal injected oauth2 client's information
     * @return updated list of Grading sheets
     * @throws JsonProcessingException json parsing exception
     */
    @PostMapping(value = "/{submissionId}/assessmentManagement")
    protected List<Assessment> manageAssessment(@PathVariable Long courseId,
                                                   @PathVariable Long projectId,
                                                   @PathVariable Long submissionId,
                                                   @RequestBody JsonNode object,
                                                   Principal principal
    ) throws JsonProcessingException {
        List<PrivilegeEnum> privileges = gradingParticipationService
                .getPrivilegesFromUserIdAndProject(Long.valueOf(principal.getName()), projectId);
        if (!(privileges != null
                && (privileges.contains(SUBMISSION_EDIT_ALL) || privileges.contains(SUBMISSION_EDIT_SINGLE)))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
        }

        submissionService.assessmentManagement(courseId, projectId, submissionId, object, privileges, Long.valueOf(principal.getName()));
        return submissionService.getAssessmentsBySubmission(submissionId);
    }

    /**
     * Adds student to a grading sheet in a submission
     * This method requires privileges SUBMISSION_EDIT_ALL or SUBMISSION_EDIT_SINGLE
     * @param courseId Canvas course id
     * @param projectId Canvas project id
     * @param submissionId submission id
     * @param participantId student user id
     * @param assessmentId grading sheet id
     * @param principal injected oauth2 client's information
     * @return updated Submission from database
     * @throws JsonProcessingException json parsing exception
     */
    @PostMapping(value = "/{submissionId}/addParticipant/{participantId}/{assessmentId}")
    protected Submission addParticipantToSubmission(@PathVariable Long courseId,
                                            @PathVariable Long projectId,
                                            @PathVariable Long submissionId,
                                            @PathVariable Long participantId,
                                            @PathVariable Long assessmentId,
                                            Principal principal) throws JsonProcessingException {
        List<PrivilegeEnum> privileges = gradingParticipationService
                .getPrivilegesFromUserIdAndProject(Long.valueOf(principal.getName()), projectId);
        if (!(privileges != null
                && (privileges.contains(SUBMISSION_EDIT_ALL) || privileges.contains(SUBMISSION_EDIT_SINGLE)))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
        }

        return submissionService.addParticipantToSubmission(courseId, projectId, submissionId, participantId, assessmentId, privileges, Long.valueOf(principal.getName()));
    }

    /**
     * Removes student to a grading sheet in a submission
     * This method requires privileges SUBMISSION_EDIT_ALL or SUBMISSION_EDIT_SINGLE
     * @param courseId Canvas course id
     * @param projectId Canvas project id
     * @param submissionId submission id
     * @param participantId student user id
     * @param returnAll choose whether return all submissions
     * @param principal injected oauth2 client's information
     * @return either a updated Submission or a list of updated Submission
     * @throws JsonProcessingException json parsing exception
     */
    @DeleteMapping(value = "/{submissionId}/removeParticipant/{participantId}")
    protected Object removeParticipantFromSubmission(@PathVariable Long courseId,
                                                         @PathVariable Long projectId,
                                                         @PathVariable Long submissionId,
                                                         @PathVariable Long participantId,
                                                         @RequestParam("returnAllSubmissions") boolean returnAll,
                                                         Principal principal) throws JsonProcessingException {
        List<PrivilegeEnum> privileges = gradingParticipationService
                .getPrivilegesFromUserIdAndProject(Long.valueOf(principal.getName()), projectId);
        if (!(privileges != null
                && (privileges.contains(SUBMISSION_EDIT_ALL) || privileges.contains(SUBMISSION_EDIT_SINGLE)))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
        }
        System.out.println("return all: " + returnAll);
        return submissionService.removeParticipantFromSubmission(courseId, projectId, submissionId, participantId, privileges, Long.valueOf(principal.getName()), returnAll);
    }
}