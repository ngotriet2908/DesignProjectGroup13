package com.group13.tcsprojectgrading.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.group13.tcsprojectgrading.canvas.api.CanvasApi;
import com.group13.tcsprojectgrading.models.submissions.Submission;
import com.group13.tcsprojectgrading.services.graders.GradingParticipationService;
import com.group13.tcsprojectgrading.services.submissions.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

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
            Principal principal) {

//        List<PrivilegeEnum> privileges = securityService
//                .getPrivilegesFromUserIdAndProject(principal.getName(), courseId, projectId);
//        if (!(privileges != null && privileges.contains(SUBMISSIONS_READ))) {
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
//        }

        List<Submission> submissions = this.submissionService.getSubmissions(courseId, projectId, Long.valueOf(principal.getName()));
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

//        Submission submission = this.submissionService.getSubmission(courseId, projectId, submissionId, Long.valueOf(principal.getName()));
        Submission submission = this.submissionService.getSubmission(submissionId);
        return new ResponseEntity<>(submission, HttpStatus.OK);
    }
//
////    @PostMapping(value = "/{submissionId}/assessmentManagement")
////    protected ArrayNode createNewAssessment(@PathVariable String courseId,
////                                         @PathVariable String projectId,
////                                         @PathVariable String submissionId,
////                                         @RequestBody JsonNode object,
////                                            Principal principal
////    ) throws JsonProcessingException {
////        List<PrivilegeEnum> privileges = securityService
////                .getPrivilegesFromUserIdAndProject(principal.getName(), courseId, projectId);
////        if (!(privileges != null
////                && (privileges.contains(SUBMISSION_EDIT_ALL) || privileges.contains(SUBMISSION_EDIT_SINGLE)))) {
////            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
////        }
////
////        return submissionService.assessmentManagement(courseId, projectId, submissionId, object, privileges, principal.getName());
////    }
//
////    @PostMapping(value = "/{submissionId}/addParticipant/{participantId}/{assessmentId}")
////    protected ObjectNode addParticipantToSubmission(@PathVariable String courseId,
////                                            @PathVariable String projectId,
////                                            @PathVariable String submissionId,
////                                            @PathVariable String participantId,
////                                            @PathVariable String assessmentId,
////                                            Principal principal) throws JsonProcessingException {
////        List<PrivilegeEnum> privileges = securityService
////                .getPrivilegesFromUserIdAndProject(principal.getName(), courseId, projectId);
////        if (!(privileges != null
////                && (privileges.contains(SUBMISSION_EDIT_ALL) || privileges.contains(SUBMISSION_EDIT_SINGLE)))) {
////            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
////        }
////
////        return submissionService.addParticipantToSubmission(courseId, projectId, submissionId, participantId, assessmentId, privileges, principal.getName());
////    }
//
//
////    @DeleteMapping(value = "/{submissionId}/removeParticipant/{participantId}")
////    protected ObjectNode removeParticipantFromSubmission(@PathVariable String courseId,
////                                                         @PathVariable String projectId,
////                                                         @PathVariable String submissionId,
////                                                         @PathVariable String participantId,
////                                                         @RequestParam("returnAllSubmissions") boolean returnAll,
////                                                         Principal principal) throws JsonProcessingException {
////        List<PrivilegeEnum> privileges = securityService
////                .getPrivilegesFromUserIdAndProject(principal.getName(), courseId, projectId);
////        if (!(privileges != null
////                && (privileges.contains(SUBMISSION_EDIT_ALL) || privileges.contains(SUBMISSION_EDIT_SINGLE)))) {
////            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
////        }
////        System.out.println("return all: " + returnAll);
////        return submissionService.removeParticipantFromSubmission(courseId, projectId, submissionId, participantId, privileges, principal.getName(), returnAll);
////    }
//
//    @PostMapping(value = "/{id}/flag")
//    protected JsonNode addFlag(@PathVariable String courseId,
//                                         @PathVariable String projectId,
//                                         @PathVariable String id,
//                                         @RequestBody ObjectNode flag,
//                                         Principal principal
////                                   @RequestParam Map<String, String> queryParameters
//    ) throws JsonProcessingException {
//        List<PrivilegeEnum> privileges = securityService
//                .getPrivilegesFromUserIdAndProject(principal.getName(), courseId, projectId);
//        if (!(privileges != null
//                && (privileges.contains(SUBMISSION_EDIT_ALL) || privileges.contains(SUBMISSION_EDIT_SINGLE)))) {
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
//        }
//
//        return submissionService.addFlag(courseId, projectId, id, flag, principal.getName(), privileges);
//    }
//
//    @PostMapping(value = "/{id}/flag/create")
//    protected JsonNode createFlag(@PathVariable String courseId,
//                               @PathVariable String projectId,
//                               @PathVariable String id,
//                               @RequestBody ObjectNode flag,
//                               Principal principal
////                                   @RequestParam Map<String, String> queryParameters
//    ) throws JsonProcessingException {
//
//        List<PrivilegeEnum> privileges = securityService
//                .getPrivilegesFromUserIdAndProject(principal.getName(), courseId, projectId);
//        if (!(privileges != null
//                && (privileges.contains(SUBMISSION_EDIT_ALL) || privileges.contains(SUBMISSION_EDIT_SINGLE)))) {
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
//        }
//
//        return submissionService.createFlag(courseId, projectId, id, flag, principal.getName(), privileges);
//    }
//
//    @DeleteMapping(value = "/{id}/flag/{flagId}")
//    protected JsonNode deleteFlag(@PathVariable String courseId,
//                               @PathVariable String projectId,
//                               @PathVariable String id,
//                               @PathVariable String flagId,
//                               Principal principal
////                                   @RequestParam Map<String, String> queryParameters
//    ) throws JsonProcessingException {
//        List<PrivilegeEnum> privileges = securityService
//                .getPrivilegesFromUserIdAndProject(principal.getName(), courseId, projectId);
//        if (!(privileges != null
//                && (privileges.contains(SUBMISSION_EDIT_ALL) || privileges.contains(SUBMISSION_EDIT_SINGLE)))) {
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
//        }
//
//        return submissionService.deleteFlag(courseId, projectId, id, flagId, principal.getName(), privileges);
//    }

//    private double submissionProgress(SubmissionAssessment assessment, Rubric rubric) {
//        List<Element> criteria = rubric.fetchAllCriteria();
//        return assessment.getGradedCriteria(criteria).size()*1.0/criteria.size()*100;
//    }

}