package com.group13.tcsprojectgrading.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.group13.tcsprojectgrading.canvas.api.CanvasApi;
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

@RestController
@RequestMapping("/api/courses/{courseId}/projects/{projectId}/submissions")
public class SubmissionController {
    private final CanvasApi canvasApi;
    private final SubmissionService submissionService;
    private final GradingParticipationService GradingParticipationService;

    @Autowired
    public SubmissionController(CanvasApi canvasApi, SubmissionService submissionService, GradingParticipationService GradingParticipationService) {
        this.canvasApi = canvasApi;
        this.submissionService = submissionService;
        this.GradingParticipationService = GradingParticipationService;
    }

    @GetMapping(value = "")
    protected ResponseEntity<?> getSubmissions(
            @PathVariable Long courseId,
            @PathVariable Long projectId,
            @RequestParam(required=false, name = "grader") String grader,
            Principal principal) {

//        List<PrivilegeEnum> privileges = securityService
//                .getPrivilegesFromUserIdAndProject(principal.getName(), courseId, projectId);
//        if (!(privileges != null && privileges.contains(SUBMISSIONS_READ))) {
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
//        }

        List<Submission> submissions;
        if (grader != null && grader.equals("unassigned")) {
            submissions = this.submissionService.getUnassignedSubmissions(courseId, projectId, Long.valueOf(principal.getName()));
        } else {
            submissions = this.submissionService.getSubmissions(courseId, projectId, Long.valueOf(principal.getName()));
        }

        return new ResponseEntity<>(submissions, HttpStatus.OK);
    }

    @GetMapping(value = "/{submissionId}")
    protected ResponseEntity<?> getSubmissionInfo(@PathVariable Long courseId,
                                         @PathVariable Long projectId,
                                         @PathVariable Long submissionId,
                                         Principal principal
    ) throws JsonProcessingException {
//        List<PrivilegeEnum> privileges = securityService
//                .getPrivilegesFromUserIdAndProject(principal.getName(), courseId, projectId);
//        if (!(privileges != null
//                && (privileges.contains(SUBMISSION_READ_ALL) || privileges.contains(SUBMISSION_READ_SINGLE)))) {
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
//        }

        Submission submission = this.submissionService.getSubmission(submissionId);
        return new ResponseEntity<>(submission, HttpStatus.OK);
    }

    /*
    Assign the submission to the grader.
     */
    @PostMapping(value = "/{submissionId}/assign")
    protected void assignSubmission(@PathVariable Long courseId,
                                                  @PathVariable Long projectId,
                                                  @PathVariable Long submissionId,
                                                 @RequestBody User grader,
                                                  Principal principal
    ) throws JsonProcessingException {
//        List<PrivilegeEnum> privileges = securityService
//                .getPrivilegesFromUserIdAndProject(principal.getName(), courseId, projectId);
//        if (!(privileges != null
//                && (privileges.contains(SUBMISSION_READ_ALL) || privileges.contains(SUBMISSION_READ_SINGLE)))) {
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
//        }

        this.submissionService.assignSubmission(submissionId, grader);
    }

    /*
    Move the submission from its grader back to the 'unassigned' list.
     */
    @PostMapping(value = "/{submissionId}/dissociate")
    protected void dissociateSubmission(@PathVariable Long courseId,
                                    @PathVariable Long projectId,
                                    @PathVariable Long submissionId,
                                    Principal principal
    ) throws JsonProcessingException {
//        List<PrivilegeEnum> privileges = securityService
//                .getPrivilegesFromUserIdAndProject(principal.getName(), courseId, projectId);
//        if (!(privileges != null
//                && (privileges.contains(SUBMISSION_READ_ALL) || privileges.contains(SUBMISSION_READ_SINGLE)))) {
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
//        }

        this.submissionService.dissociateSubmission(submissionId);
    }

    @PutMapping(value = "/{submissionId}/labels")
    protected void saveLabels(@PathVariable Long courseId,
                                  @PathVariable Long projectId,
                                  @PathVariable Long submissionId,
                                  @RequestBody Set<Label> labels,
                                  Principal principal
    ) throws JsonProcessingException {
//        List<PrivilegeEnum> privileges = securityService
//                .getPrivilegesFromUserIdAndProject(principal.getName(), courseId, projectId);
//        if (!(privileges != null
//                && (privileges.contains(SUBMISSION_EDIT_ALL) || privileges.contains(SUBMISSION_EDIT_SINGLE)))) {
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
//        }

        this.submissionService.saveLabels(labels, submissionId);
    }
}