package com.group13.tcsprojectgrading.controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.group13.tcsprojectgrading.canvas.api.CanvasApi;
import com.group13.tcsprojectgrading.models.*;
import com.group13.tcsprojectgrading.models.grading.Grade;
import com.group13.tcsprojectgrading.services.grading.AssessmentCoreService;
import com.group13.tcsprojectgrading.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

import static com.group13.tcsprojectgrading.models.PrivilegeEnum.*;

@RestController
@RequestMapping("/api/courses/{courseId}/projects/{projectId}/submissions/{submissionId}/{assessmentId}")
public class AssessmentController {
    private final CanvasApi canvasApi;

    private final AssessmentCoreService assessmentCoreService;
    private final SecurityService securityService;

    @Autowired
    public AssessmentController(CanvasApi canvasApi, AssessmentCoreService assessmentCoreService, SecurityService securityService) {
        this.canvasApi = canvasApi;
        this.assessmentCoreService = assessmentCoreService;
        this.securityService = securityService;
    }



    @RequestMapping(value = "/grading", method = RequestMethod.GET, produces = "application/json")
    protected String getAssessment(@PathVariable String courseId,
                                   @PathVariable String projectId,
                                   @PathVariable String submissionId,
                                   @PathVariable String assessmentId,
                                   Principal principal)
            throws JsonProcessingException {

        List<PrivilegeEnum> privileges = securityService
                .getPrivilegesFromUserIdAndProject(principal.getName(), courseId, projectId);
        if (!(privileges != null
                && (privileges.contains(GRADING_READ_ALL) || privileges.contains(GRADING_READ_SINGLE)))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
        }

        return assessmentCoreService.getAssessment(courseId, projectId, submissionId, assessmentId, privileges, principal.getName());
    }

    @RequestMapping(value = "/grading/{criterionId}", method = RequestMethod.PUT)
    protected String alterCriterionAssessment(
            @PathVariable String courseId,
            @PathVariable String projectId,
            @PathVariable String submissionId,
            @PathVariable String assessmentId,
            @PathVariable String criterionId,
            @RequestBody Grade newGrade,
            Principal principal
    ) {
        List<PrivilegeEnum> privileges = securityService
                .getPrivilegesFromUserIdAndProject(principal.getName(), courseId, projectId);
        if (!(privileges != null
                && (privileges.contains(GRADING_WRITE_ALL) || privileges.contains(GRADING_WRITE_SINGLE)))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
        }
        return assessmentCoreService.alterCriterionAssessment(courseId, projectId, submissionId, assessmentId, criterionId, newGrade, privileges, principal.getName());
    }

    @RequestMapping(value = "/grading/{criterionId}/active/{id}", method = RequestMethod.PUT)
    protected String updateActiveGrading(
            @PathVariable String courseId,
            @PathVariable String projectId,
            @PathVariable String submissionId,
            @PathVariable String assessmentId,
            @PathVariable String criterionId,
            @PathVariable int id,
            Principal principal
    ) {
        List<PrivilegeEnum> privileges = securityService
                .getPrivilegesFromUserIdAndProject(principal.getName(), courseId, projectId);
        if (!(privileges != null
                && (privileges.contains(GRADING_WRITE_ALL) || privileges.contains(GRADING_WRITE_SINGLE)))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
        }
        return assessmentCoreService.updateActiveGrading(courseId, projectId, submissionId, assessmentId, criterionId, id, privileges, principal.getName());
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
        List<PrivilegeEnum> privileges = securityService
                .getPrivilegesFromUserIdAndProject(principal.getName(), courseId, projectId);
        if (!(privileges != null
                && (privileges.contains(GRADING_WRITE_ALL) || privileges.contains(GRADING_WRITE_SINGLE)))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
        }

        return assessmentCoreService.createIssue(courseId, projectId, submissionId, assessmentId, issue, privileges, principal.getName());
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
        List<PrivilegeEnum> privileges = securityService
                .getPrivilegesFromUserIdAndProject(principal.getName(), courseId, projectId);
        if (!(privileges != null
                && (privileges.contains(GRADING_WRITE_ALL) || privileges.contains(GRADING_WRITE_SINGLE)))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
        }
        return assessmentCoreService.resolveIssue(courseId, projectId, submissionId, assessmentId, issue, privileges, principal.getName());
    }

    @RequestMapping(value = "/issues", method = RequestMethod.GET)
    protected ArrayNode getIssues(
            @PathVariable String courseId,
            @PathVariable String projectId,
            @PathVariable String submissionId,
            @PathVariable String assessmentId,
            Principal principal
    ) {
        List<PrivilegeEnum> privileges = securityService
                .getPrivilegesFromUserIdAndProject(principal.getName(), courseId, projectId);
        if (!(privileges != null
                && (privileges.contains(SUBMISSION_READ_ALL) || privileges.contains(SUBMISSION_READ_SINGLE)))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
        }
        return assessmentCoreService.getIssues(courseId, projectId, submissionId, assessmentId, privileges, principal.getName());
    }
}