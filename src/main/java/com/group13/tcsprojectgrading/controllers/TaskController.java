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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.Iterator;
import java.util.List;

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
        JsonNode resultNode = objectMapper.createObjectNode();

        ((ObjectNode) resultNode).set("project", objectMapper.readTree(projectResponse));
        ((ObjectNode) resultNode).set("course", objectMapper.readTree(courseString));

        for (Task task : tasks) {
            JsonNode node = objectMapper.createObjectNode();

            String submissionResponse = this.canvasApi.getCanvasCoursesApi().getSubmission(courseId, projectId, task.getSubmitterId());
            JsonNode submissionNode = objectMapper.readTree(submissionResponse);

            ((ObjectNode) node).put("stringId", String.format("%s/%s", task.isGroup(), task.getId()));
            ((ObjectNode) node).put("taskId", task.getId());
            ((ObjectNode) node).put("isGroup", task.isGroup());
            ((ObjectNode) node).put("name", task.getName());
            ((ObjectNode) node).put("submissionId", task.getSubmissionId());
            ((ObjectNode) node).put("submitterId", task.getSubmitterId());
            ((ObjectNode) node).put("progress", (int) (Math.random() * 100));
            ((ObjectNode) node).put("submittedAt", submissionNode.get("submitted_at").asText());
            ((ObjectNode) node).put("attempt", submissionNode.get("attempt").asText());

            arrayNode.add(node);
        }

        ((ObjectNode) resultNode).set("tasks", arrayNode);

        return resultNode;
    }

    @GetMapping(value = "/{isGroup}/{taskId}")
    protected JsonNode getTaskInfo(@PathVariable String courseId,
                                   @PathVariable String projectId,
                                   @PathVariable Boolean isGroup,
                                   @PathVariable String taskId,
                                   Principal principal) throws JsonProcessingException {
        String projectResponse = this.canvasApi.getCanvasCoursesApi().getCourseProject(courseId, projectId);
        String courseString = this.canvasApi.getCanvasCoursesApi().getUserCourse(courseId);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode resultNode = objectMapper.createObjectNode();

        ((ObjectNode) resultNode).set("course", objectMapper.readTree(courseString));
        ((ObjectNode) resultNode).set("project", objectMapper.readTree(projectResponse));

        Project project = projectService.getProjectById(courseId, projectId);
        if (project == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "project not found"
            );
        }

        Task task = taskService.findTaskByTaskId(taskId, isGroup, project);
        if (task == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "task not found"
            );
        }

        String submissionResponse = this.canvasApi.getCanvasCoursesApi().getSubmission(courseId, projectId, task.getSubmitterId());
        ((ObjectNode) resultNode).set("submission", objectMapper.readTree(submissionResponse));

        JsonNode node = objectMapper.createObjectNode();
        ((ObjectNode) node).put("stringId", String.format("%s/%s", task.isGroup(), task.getId()));
        ((ObjectNode) node).put("taskId", task.getId());
        ((ObjectNode) node).put("isGroup", task.isGroup());
        ((ObjectNode) node).put("name", task.getName());
        ((ObjectNode) node).put("submissionId", task.getSubmissionId());
        ((ObjectNode) node).put("submitterId", task.getSubmitterId());
        ((ObjectNode) node).put("progress", (int) (Math.random() * 100));

        if (task.getGrader() != null) {
            ObjectNode graderNode = objectMapper.createObjectNode();
            graderNode.put("name", task.getGrader().getName());
            graderNode.put("id", task.getGrader().getUserId());

            ((ObjectNode) node).set("grader", graderNode);
        }

        if (task.isGroup()) {
            ArrayNode memberships = groupPages(objectMapper, this.canvasApi.getCanvasCoursesApi().getGroupUsers(task.getId()));
            ((ObjectNode) node).set("members", memberships);
        }
        ((ObjectNode) resultNode).set("task", node);

        return resultNode;
    }

}
