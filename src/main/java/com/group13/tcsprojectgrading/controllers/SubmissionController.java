package com.group13.tcsprojectgrading.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.group13.tcsprojectgrading.canvas.api.CanvasApi;
import com.group13.tcsprojectgrading.services.submissions.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/courses/{courseId}/projects/{projectId}/submissions")
public class SubmissionController {

    private final CanvasApi canvasApi;
    private final SubmissionService submissionService;

    @Autowired
    public SubmissionController(CanvasApi canvasApi, SubmissionService submissionService) {
        this.canvasApi = canvasApi;
        this.submissionService = submissionService;
    }

    @GetMapping(value = "")
    protected JsonNode getSubmissions(@PathVariable String courseId, @PathVariable String projectId, Principal principal) throws JsonProcessingException {
        String courseString = this.canvasApi.getCanvasCoursesApi().getUserCourse(courseId);

        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode resultNode = submissionService.getSubmission(courseId, projectId, principal.getName());
        resultNode.set("course", objectMapper.readTree(courseString));

        return resultNode;
    }

    @GetMapping(value = "/{id}")
    protected JsonNode getSubmissionInfo(@PathVariable String courseId,
                                         @PathVariable String projectId,
                                         @PathVariable String id,
                                         Principal principal
//                                   @RequestParam Map<String, String> queryParameters
    ) throws JsonProcessingException {
        String courseString = this.canvasApi.getCanvasCoursesApi().getUserCourse(courseId);


        ObjectNode resultNode = submissionService.getSubmissionInfo(courseId, projectId, id, principal.getName());
        ObjectMapper objectMapper = new ObjectMapper();
        resultNode.set("course", objectMapper.readTree(courseString));

        return resultNode;
    }

    @PostMapping(value = "/{submissionId}/assessmentManagement")
    protected ArrayNode createNewAssessment(@PathVariable String courseId,
                                         @PathVariable String projectId,
                                         @PathVariable String submissionId,
                                         @RequestBody JsonNode object,
                                         Principal principal
//                                   @RequestParam Map<String, String> queryParameters
    ) throws JsonProcessingException {

        return submissionService.assessmentManagement(courseId, projectId, submissionId, object);

    }

    @PostMapping(value = "/{id}/flag")
    protected JsonNode addFlag(@PathVariable String courseId,
                                         @PathVariable String projectId,
                                         @PathVariable String id,
                                         @RequestBody ObjectNode flag,
                                         Principal principal
//                                   @RequestParam Map<String, String> queryParameters
    ) throws JsonProcessingException {

        return submissionService.addFlag(courseId, projectId, id, flag, principal.getName());
    }

    @PostMapping(value = "/{id}/flag/create")
    protected JsonNode createFlag(@PathVariable String courseId,
                               @PathVariable String projectId,
                               @PathVariable String id,
                               @RequestBody ObjectNode flag,
                               Principal principal
//                                   @RequestParam Map<String, String> queryParameters
    ) throws JsonProcessingException {

        return submissionService.createFlag(courseId, projectId, id, flag, principal.getName());
    }

    @DeleteMapping(value = "/{id}/flag/{flagId}")
    protected JsonNode deleteFlag(@PathVariable String courseId,
                               @PathVariable String projectId,
                               @PathVariable String id,
                               @PathVariable String flagId,
                               Principal principal
//                                   @RequestParam Map<String, String> queryParameters
    ) throws JsonProcessingException {
        return submissionService.deleteFlag(courseId, projectId, id, flagId, principal.getName());
    }

//    private double submissionProgress(SubmissionAssessment assessment, Rubric rubric) {
//        List<Element> criteria = rubric.fetchAllCriteria();
//        return assessment.getGradedCriteria(criteria).size()*1.0/criteria.size()*100;
//    }

}