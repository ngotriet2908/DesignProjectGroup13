package com.group13.tcsprojectgrading.controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.group13.tcsprojectgrading.canvas.api.CanvasApi;
import com.group13.tcsprojectgrading.models.Grader;
import com.group13.tcsprojectgrading.models.Project;
import com.group13.tcsprojectgrading.models.RoleEnum;
import com.group13.tcsprojectgrading.models.Submission;
import com.group13.tcsprojectgrading.services.*;
import com.group13.tcsprojectgrading.services.rubric.RubricService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.security.Principal;
import java.text.ParseException;
import java.util.*;

import static com.group13.tcsprojectgrading.controllers.Utils.groupPages;

@RestController
@RequestMapping("/api/courses/{courseId}/projects/{projectId}/management")
public class ProjectsManagementController {
    private final CanvasApi canvasApi;
    private final ProjectsManagementService projectsManagementService;

    @Autowired
    public ProjectsManagementController(CanvasApi canvasApi, ProjectsManagementService projectsManagementService) {
        this.canvasApi = canvasApi;
        this.projectsManagementService = projectsManagementService;
    }

    @GetMapping(value = "")
    @ResponseBody
    protected JsonNode getManagementInfo(@PathVariable String courseId, @PathVariable String projectId, Principal principal) throws JsonProcessingException, ParseException {
        return projectsManagementService.getManagementInfo(courseId, projectId, principal.getName());
    }

    @PostMapping(value = "/addGraders")
    protected JsonNode addGrader(@PathVariable String courseId,
                                 @PathVariable String projectId,
                                 @RequestBody ArrayNode activeGraders,
                                 Principal principal) throws JsonProcessingException, ParseException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> gradersResponse = this.canvasApi.getCanvasCoursesApi().getCourseGraders(courseId);
        ArrayNode gradersArrayFromCanvas = groupPages(objectMapper, gradersResponse);
        return projectsManagementService.addGrader(courseId, projectId, principal.getName(), gradersArrayFromCanvas, activeGraders);
    }



    @GetMapping(value = "/addGraders/getAllGraders")
    @ResponseBody
    protected ArrayNode getActiveGrader(@PathVariable String courseId, @PathVariable String projectId, Principal principal) throws JsonProcessingException, ParseException {
        List<String> gradersResponse = this.canvasApi.getCanvasCoursesApi().getCourseGraders(courseId);
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode results = objectMapper.createArrayNode();
        ArrayNode gradersArrayFromCanvas = groupPages(objectMapper, gradersResponse);
        for (Iterator<JsonNode> it = gradersArrayFromCanvas.elements(); it.hasNext(); ) {
            JsonNode node = it.next();
            ObjectNode grader = objectMapper.createObjectNode();
            grader.put("id", node.get("id").asText());
            grader.put("name", node.get("name").asText());
            grader.put("role", RoleEnum.getRoleFromEnrolment(node.get("enrollments").get(0).get("type").asText()).toString());
            results.add(grader);
        }
        return results;
    }

    @GetMapping(value = "/assign/{id}/{toUserId}")
    @ResponseBody
    protected JsonNode assignSubmission(@PathVariable String courseId,
                                        @PathVariable String projectId,
                                        @PathVariable String id,
//                                  @PathVariable String fromUserId,
                                        @PathVariable String toUserId,
                                        Principal principal) throws JsonProcessingException, ParseException {
        return projectsManagementService.assignSubmission(courseId, projectId, id, toUserId);
    }

    @GetMapping(value = "/return/{userId}")
    @ResponseBody
    protected JsonNode returnTasks(@PathVariable String courseId,
                                   @PathVariable String projectId,
                                   @PathVariable String userId,
                                   Principal principal) throws JsonProcessingException, ParseException {
        return projectsManagementService.returnSubmissions(courseId, projectId, userId);
    }

    @PostMapping(value = "/bulkAssign")
    protected JsonNode bulkAssign(@PathVariable String courseId,
                                  @PathVariable String projectId,
                                  @RequestBody ObjectNode object,
                                  Principal principal) throws JsonProcessingException, ParseException {
        return projectsManagementService.bulkAssign(courseId, projectId, object);
    }

}