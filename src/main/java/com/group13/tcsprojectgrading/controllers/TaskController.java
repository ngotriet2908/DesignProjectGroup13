package com.group13.tcsprojectgrading.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.group13.tcsprojectgrading.canvas.api.CanvasApi;
import com.group13.tcsprojectgrading.models.Project;
import com.group13.tcsprojectgrading.models.Task;
import com.group13.tcsprojectgrading.services.ActivityService;
import com.group13.tcsprojectgrading.services.ProjectService;
import com.group13.tcsprojectgrading.services.TaskService;
import com.group13.tcsprojectgrading.services.rubric.RubricService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.group13.tcsprojectgrading.controllers.Utils.groupPages;

@RestController
@RequestMapping("/api/courses/{courseId}/projects/{projectId}/tasks")
public class TaskController {

    private final CanvasApi canvasApi;
    private final ProjectService projectService;
    private final TaskService taskService;

    public TaskController(CanvasApi canvasApi, ProjectService projectService, TaskService taskService) {
        this.canvasApi = canvasApi;
        this.projectService = projectService;
        this.taskService = taskService;
    }

    @GetMapping(value = "")
    protected JsonNode getTasks(@PathVariable String courseId, @PathVariable String projectId, Principal principal) throws JsonProcessingException {
        String projectResponse = this.canvasApi.getCanvasCoursesApi().getCourseProject(courseId, projectId);
        String courseString = this.canvasApi.getCanvasCoursesApi().getUserCourse(courseId);

        ObjectMapper objectMapper = new ObjectMapper();

        Project project = projectService.getProjectById(courseId, projectId);
        if (project == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "entity not found"
            );
        }

        List<Task> tasks = taskService.findTaskInProjectWithUserId(project, principal.getName());

        ArrayNode arrayNode = objectMapper.createArrayNode();
        ObjectNode resultNode = objectMapper.createObjectNode();

        resultNode.set("project", objectMapper.readTree(projectResponse));
        resultNode.set("course", objectMapper.readTree(courseString));

        for (Task task : tasks) {
            ObjectNode node = objectMapper.createObjectNode();

            String submissionResponse = this.canvasApi.getCanvasCoursesApi().getSubmission(courseId, projectId, task.getSubmitterId());
            JsonNode submissionNode = objectMapper.readTree(submissionResponse);

            node.put("stringId", String.format("%s/%s", task.isGroup(), task.getId()));
            node.put("taskId", task.getId());
            node.put("isGroup", task.isGroup());
            node.put("name", task.getName());
            node.put("submissionId", task.getSubmissionId());
            node.put("submitterId", task.getSubmitterId());
            node.put("progress", (int) (Math.random() * 100));
            node.put("submittedAt", submissionNode.get("submitted_at").asText());
            node.put("attempt", submissionNode.get("attempt").asText());

            arrayNode.add(node);
        }

        resultNode.set("tasks", arrayNode);

        return resultNode;
    }

    @GetMapping(value = "/{isGroup}/{taskId}")
    protected JsonNode getTaskInfo(@PathVariable String courseId,
                                   @PathVariable String projectId,
                                   @PathVariable String taskId,
                                   @PathVariable Boolean isGroup
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
        Task task = taskService.findTaskByTaskId(taskId, isGroup, project);
        if (task == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "task not found"
            );
        }

        String submissionResponse = this.canvasApi.getCanvasCoursesApi().getSubmission(courseId, projectId, task.getSubmitterId());
        resultNode.set("submission", objectMapper.readTree(submissionResponse));

        ObjectNode node = objectMapper.createObjectNode();
        node.put("stringId", String.format("%s/%s", task.isGroup(), task.getId()));
        node.put("taskId", task.getId());
        node.put("isGroup", task.isGroup());
        node.put("name", task.getName());
        node.put("submissionId", task.getSubmissionId());
        node.put("submitterId", task.getSubmitterId());
        node.put("progress", (int) (Math.random() * 100));

        if (task.getGrader() != null) {
            ObjectNode graderNode = objectMapper.createObjectNode();
            graderNode.put("name", task.getGrader().getName());
            graderNode.put("id", task.getGrader().getUserId());

            node.set("grader", graderNode);
        }

        if (task.isGroup()) {
            ArrayNode memberships = groupPages(objectMapper, this.canvasApi.getCanvasCoursesApi().getGroupUsers(task.getId()));
            node.set("members", memberships);
        }
        resultNode.set("task", node);

        return resultNode;
    }
}
