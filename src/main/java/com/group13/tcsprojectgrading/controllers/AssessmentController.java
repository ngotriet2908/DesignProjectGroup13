package com.group13.tcsprojectgrading.controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.group13.tcsprojectgrading.canvas.api.CanvasApi;
import com.group13.tcsprojectgrading.models.grading.Assessment;
import com.group13.tcsprojectgrading.models.grading.Grade;
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


    @RequestMapping(value = "/grades", method = RequestMethod.POST)
    protected ResponseEntity<?> alterCriterionAssessment(
            @PathVariable Long courseId,
            @PathVariable Long projectId,
            @PathVariable Long submissionId,
            @PathVariable Long assessmentId,
            @RequestBody Grade grade,
            Principal principal
    ) {

//        List<PrivilegeEnum> privileges = securityService
//                .getPrivilegesFromUserIdAndProject(principal.getName(), courseId, projectId);
//        if (!(privileges != null
//                && (privileges.contains(GRADING_WRITE_ALL) || privileges.contains(GRADING_WRITE_SINGLE)))) {
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
//        }

        Grade createdGrade = this.assessmentService.addGrade(assessmentId, grade, Long.valueOf(principal.getName()));
        return new ResponseEntity<>(createdGrade, HttpStatus.OK);
    }

    @RequestMapping(value = "/grades/{grade_id}/activate", method = RequestMethod.POST)
    protected ResponseEntity<?> activateGrade(
            @PathVariable Long grade_id
    ) {
        this.assessmentService.activateGrade(grade_id);
        return new ResponseEntity<>(HttpStatus.OK);
    }




//    @RequestMapping(value = "/grading/{criterionId}/active/{id}", method = RequestMethod.PUT)
//    protected String updateActiveGrading(
//            @PathVariable String courseId,
//            @PathVariable String projectId,
//            @PathVariable String submissionId,
//            @PathVariable String assessmentId,
//            @PathVariable String criterionId,
//            @PathVariable int id,
//            Principal principal
//    ) {
//        List<PrivilegeEnum> privileges = securityService
//                .getPrivilegesFromUserIdAndProject(principal.getName(), courseId, projectId);
//        if (!(privileges != null
//                && (privileges.contains(GRADING_WRITE_ALL) || privileges.contains(GRADING_WRITE_SINGLE)))) {
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
//        }
//        return assessmentService.updateActiveGrading(courseId, projectId, submissionId, assessmentId, criterionId, id, privileges, principal.getName());
//    }
//
//    @RequestMapping(value = "/issues", method = RequestMethod.POST)
//    protected ArrayNode createIssue(
//            @PathVariable String courseId,
//            @PathVariable String projectId,
//            @PathVariable String submissionId,
//            @PathVariable String assessmentId,
//            @RequestBody JsonNode issue,
//            Principal principal
//    ) {
//        List<PrivilegeEnum> privileges = securityService
//                .getPrivilegesFromUserIdAndProject(principal.getName(), courseId, projectId);
//        if (!(privileges != null
//                && (privileges.contains(GRADING_WRITE_ALL) || privileges.contains(GRADING_WRITE_SINGLE)))) {
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
//        }
//
//        return assessmentService.createIssue(courseId, projectId, submissionId, assessmentId, issue, privileges, principal.getName());
//    }
//
//    @RequestMapping(value = "/issues/resolve", method = RequestMethod.POST)
//    protected ArrayNode resolveIssue(
//            @PathVariable String courseId,
//            @PathVariable String projectId,
//            @PathVariable String submissionId,
//            @PathVariable String assessmentId,
//            @RequestBody JsonNode issue,
//            Principal principal
//    ) {
//        List<PrivilegeEnum> privileges = securityService
//                .getPrivilegesFromUserIdAndProject(principal.getName(), courseId, projectId);
//        if (!(privileges != null
//                && (privileges.contains(GRADING_WRITE_ALL) || privileges.contains(GRADING_WRITE_SINGLE)))) {
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
//        }
//        return assessmentService.resolveIssue(courseId, projectId, submissionId, assessmentId, issue, privileges, principal.getName());
//    }
//
//    @RequestMapping(value = "/issues", method = RequestMethod.GET)
//    protected ArrayNode getIssues(
//            @PathVariable String courseId,
//            @PathVariable String projectId,
//            @PathVariable String submissionId,
//            @PathVariable String assessmentId,
//            Principal principal
//    ) {
//        List<PrivilegeEnum> privileges = securityService
//                .getPrivilegesFromUserIdAndProject(principal.getName(), courseId, projectId);
//        if (!(privileges != null
//                && (privileges.contains(SUBMISSION_READ_ALL) || privileges.contains(SUBMISSION_READ_SINGLE)))) {
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
//        }
//        return assessmentService.getIssues(courseId, projectId, submissionId, assessmentId, privileges, principal.getName());
//    }
}