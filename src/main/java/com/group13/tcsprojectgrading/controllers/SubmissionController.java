package com.group13.tcsprojectgrading.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.group13.tcsprojectgrading.canvas.api.CanvasApi;
import com.group13.tcsprojectgrading.models.Project;
import com.group13.tcsprojectgrading.models.Submission;
import com.group13.tcsprojectgrading.services.ActivityService;
import com.group13.tcsprojectgrading.services.ProjectService;
import com.group13.tcsprojectgrading.services.SubmissionService;
import com.group13.tcsprojectgrading.services.rubric.RubricService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.group13.tcsprojectgrading.controllers.Utils.groupPages;

@RestController
@RequestMapping("/api/courses/{courseId}/projects/{projectId}/submissions")
public class SubmissionController {

    private final CanvasApi canvasApi;
    private final ProjectService projectService;
    private final SubmissionService submissionService;

    @Autowired
    public SubmissionController(CanvasApi canvasApi, ProjectService projectService, SubmissionService submissionService) {
        this.canvasApi = canvasApi;
        this.projectService = projectService;
        this.submissionService = submissionService;
    }

    @GetMapping(value = "")
    protected JsonNode getSubmissions(@PathVariable String courseId, @PathVariable String projectId, Principal principal) throws JsonProcessingException {
        String projectResponse = this.canvasApi.getCanvasCoursesApi().getCourseProject(courseId, projectId);
        String courseString = this.canvasApi.getCanvasCoursesApi().getUserCourse(courseId);

        ObjectMapper objectMapper = new ObjectMapper();

        Project project = projectService.getProjectById(courseId, projectId);
        if (project == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "entity not found"
            );
        }

        List <Submission> submissions =  submissionService.findSubmissionsForGrader(project, principal.getName());
        ArrayNode arrayNode = objectMapper.createArrayNode();
        ObjectNode resultNode = objectMapper.createObjectNode();

        resultNode.set("project", objectMapper.readTree(projectResponse));
        resultNode.set("course", objectMapper.readTree(courseString));

        for (Submission submission : submissions) {
            ObjectNode node = objectMapper.createObjectNode();

            String submissionResponse = this.canvasApi.getCanvasCoursesApi().getSubmission(courseId, projectId, submission.getId());
            JsonNode submissionNode = objectMapper.readTree(submissionResponse);

            node.put("stringId", String.format("%s/%s", (submission.getGroupId() != null)? submission.getGroupId():"individual", submission.getId()));
            node.put("id", submission.getId());
            node.put("isGroup", (submission.getGroupId() != null));
            node.put("name", submission.getName());
            node.put("progress", (int) (Math.random() * 100));
            node.put("submittedAt", submissionNode.get("submitted_at").asText());
            node.put("attempt", submissionNode.get("attempt").asText());

            arrayNode.add(node);
        }

        resultNode.set("submissions", arrayNode);

        return resultNode;
    }

    @GetMapping(value = "/{id}")
    protected JsonNode getSubmissionInfo(@PathVariable String courseId,
                                   @PathVariable String projectId,
                                   @PathVariable String id
//                                   @RequestParam Map<String, String> queryParameters
    ) throws JsonProcessingException {
        String projectResponse = this.canvasApi.getCanvasCoursesApi().getCourseProject(courseId, projectId);
        String courseString = this.canvasApi.getCanvasCoursesApi().getUserCourse(courseId);

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode resultNode = objectMapper.createObjectNode();

        resultNode.set("course", objectMapper.readTree(courseString));
        resultNode.set("project", objectMapper.readTree(projectResponse));

        Project project = projectService.getProjectById(courseId, projectId);
        if (project == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "project not found"
            );
        }

//        Task task = taskService.findTaskByTaskId(taskId, Boolean.parseBoolean(queryParameters.get("is_group")), project);
        Submission submission = submissionService.findSubmissionById(id, project);
        if (submission == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "task not found"
            );
        }

        String submissionResponse = this.canvasApi.getCanvasCoursesApi().getSubmission(courseId, projectId, submission.getId());
        resultNode.set("submission", objectMapper.readTree(submissionResponse));

        ObjectNode node = objectMapper.createObjectNode();
        node.put("stringId", String.format("%s/%s", (submission.getGroupId() != null)? submission.getGroupId():"individual", submission.getId()));
        node.put("id", submission.getId());
        node.put("isGroup", (submission.getGroupId() != null));
        node.put("name", submission.getName());
        node.put("progress", (int) (Math.random() * 100));

        if (submission.getGrader() != null) {
            ObjectNode graderNode = objectMapper.createObjectNode();
            graderNode.put("name", submission.getGrader().getName());
            graderNode.put("id", submission.getGrader().getUserId());

            node.set("grader", graderNode);
        }

        if (submission.getGroupId() != null) {
            ArrayNode memberships = groupPages(objectMapper, this.canvasApi.getCanvasCoursesApi().getGroupUsers(submission.getId()));
            node.set("members", memberships);
        }
        resultNode.set("submission", node);

        return resultNode;
    }
}
