package com.group13.tcsprojectgrading.controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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

    private final AssessmentCoreService assessmentCoreService;

    @Autowired
    public AssessmentController(CanvasApi canvasApi, AssessmentCoreService assessmentCoreService) {
        this.canvasApi = canvasApi;
        this.assessmentCoreService = assessmentCoreService;
    }



    @RequestMapping(value = "/grading", method = RequestMethod.GET, produces = "application/json")
    protected String getAssessment(@PathVariable String courseId,
                                                   @PathVariable String projectId,
                                                   @PathVariable String submissionId,
                                                   @PathVariable String assessmentId)
            throws JsonProcessingException {
        return assessmentCoreService.getAssessment(courseId, projectId, submissionId, assessmentId);
    }

    @RequestMapping(value = "/grading/{criterionId}", method = RequestMethod.PUT)
    protected String alterCriterionAssessment(
            @PathVariable String courseId,
            @PathVariable String projectId,
            @PathVariable String submissionId,
            @PathVariable String assessmentId,
            @PathVariable String criterionId,
            @RequestBody Grade newGrade
    ) {
        return assessmentCoreService.alterCriterionAssessment(courseId, projectId, submissionId, assessmentId, criterionId, newGrade);
    }


    @RequestMapping(value = "/grading/{criterionId}/active/{id}", method = RequestMethod.PUT)
    protected String updateActiveGrading(
            @PathVariable String courseId,
            @PathVariable String projectId,
            @PathVariable String submissionId,
            @PathVariable String assessmentId,
            @PathVariable String criterionId,
            @PathVariable int id
    ) {
        return assessmentCoreService.updateActiveGrading(courseId, projectId, submissionId, assessmentId, criterionId, id);
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
        return assessmentCoreService.createIssue(courseId, projectId, submissionId, assessmentId, issue, principal.getName());
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
        return assessmentCoreService.resolveIssue(courseId, projectId, submissionId, assessmentId, issue, principal.getName());
    }

    @RequestMapping(value = "/issues", method = RequestMethod.GET)
    protected ArrayNode getIssues(
            @PathVariable String courseId,
            @PathVariable String projectId,
            @PathVariable String submissionId,
            @PathVariable String assessmentId
    ) {
        return assessmentCoreService.getIssues(courseId, projectId, submissionId, assessmentId);
    }
}