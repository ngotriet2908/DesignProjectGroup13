package com.group13.tcsprojectgrading.controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.group13.tcsprojectgrading.canvas.api.CanvasApi;
import com.group13.tcsprojectgrading.models.Grader;
import com.group13.tcsprojectgrading.models.Project;
import com.group13.tcsprojectgrading.models.Task;
import com.group13.tcsprojectgrading.services.GraderService;
import com.group13.tcsprojectgrading.services.ProjectService;
import com.group13.tcsprojectgrading.services.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.text.ParseException;
import java.util.*;

import static com.group13.tcsprojectgrading.controllers.Utils.groupPages;

@RestController
@RequestMapping("/api/courses/{courseId}/projects/{projectId}/management")
public class ProjectsManagementController {
    private final CanvasApi canvasApi;
    private final GraderService graderService;
    private final TaskService taskService;
    private final ProjectService projectService;

    public ProjectsManagementController(CanvasApi canvasApi, GraderService graderService, TaskService taskService, ProjectService projectService) {
        this.canvasApi = canvasApi;
        this.graderService = graderService;
        this.taskService = taskService;
        this.projectService = projectService;
    }

    @GetMapping(value = "")
    @ResponseBody
    protected JsonNode getManagementInfo(@PathVariable String courseId, @PathVariable String projectId, Principal principal) throws JsonProcessingException, ParseException {
        String projectResponse = this.canvasApi.getCanvasCoursesApi().getCourseProject(courseId, projectId);

        Project project = projectService.getProjectById(courseId, projectId);
        if (project == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "entity not found"
            );
        }

        List<Grader> graders = graderService.getGraderFromProject(project);
        List<String> submissionsString = this.canvasApi.getCanvasCoursesApi().getSubmissionsInfo(courseId, Long.parseLong(projectId));
        List<String> studentsString = this.canvasApi.getCanvasCoursesApi().getCourseStudents(courseId);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode resultNode = objectMapper.createObjectNode();
        ArrayNode notAssignedArray = objectMapper.createArrayNode();
        ArrayNode gradersArray = objectMapper.createArrayNode();

        JsonNode projectJson = objectMapper.readTree(projectResponse);
        String projectCatId = projectJson.get("group_category_id").asText();
        Map<String, String> groupIdToNameMap = new HashMap<>();
        Map<String, String> userIdToGroupIdMap = new HashMap<>();

        if (!projectCatId.equals("null")) {
            ArrayNode groupsString = groupPages(objectMapper, canvasApi.getCanvasCoursesApi().getCourseGroupCategoryGroup(projectCatId));
//            ArrayNode groupsString1 = groupPages(objectMapper, canvasApi.getCanvasCoursesApi().getCourseGroups(courseId));

            for (Iterator<JsonNode> it = groupsString.elements(); it.hasNext(); ) {
                JsonNode group = it.next();
                if (group.get("members_count").asInt(0) <= 0) continue;
                ArrayNode memberships = groupPages(objectMapper, this.canvasApi.getCanvasCoursesApi().getGroupMemberships(group.get("id").asText()));
                groupIdToNameMap.put(group.get("id").asText(), group.get("name").asText());

                for (Iterator<JsonNode> iter = memberships.elements(); iter.hasNext(); ) {
                    JsonNode membership = iter.next();
                    userIdToGroupIdMap.put(membership.get("user_id").asText(), membership.get("group_id").asText());
                }
            }
        }
        ArrayNode studentArray = groupPages(objectMapper, studentsString);
        Map<String, JsonNode> studentMap = new HashMap<>();
        for (Iterator<JsonNode> it = studentArray.elements(); it.hasNext(); ) {
            JsonNode jsonNode = it.next();
            studentMap.put(jsonNode.get("id").asText(), jsonNode);
        }

        ArrayNode submissionArray = groupPages(objectMapper, submissionsString);
        List<String> validSubmissionId = new ArrayList<>();

        for (Iterator<JsonNode> it = submissionArray.elements(); it.hasNext(); ) {
            JsonNode jsonNode = it.next();

            if (!jsonNode.get("workflow_state").asText().equals("submitted")) continue;
            if (!studentMap.containsKey(jsonNode.get("user_id").asText())) continue;

            boolean isGroup = userIdToGroupIdMap.containsKey(jsonNode.get("user_id").asText());
            String id = (isGroup)? userIdToGroupIdMap.get(jsonNode.get("user_id").asText()): jsonNode.get("user_id").asText();
            String name = (isGroup)? groupIdToNameMap.get(userIdToGroupIdMap.get(jsonNode.get("user_id").asText())): studentMap.get(id).get("name").asText();
            Task task = new Task(
                    id,
                    isGroup,
                    project,
                    jsonNode.get("id").asText(),
                    jsonNode.get("user_id").asText(),
                    name
            );
            validSubmissionId.add(task.getSubmissionId());
            Task existedTask = taskService.findTaskById(jsonNode.get("id").asText(), project);
            if (existedTask != null) {
                if (existedTask.isGroup() != isGroup || !existedTask.getId().equals(id)) {
                    taskService.deleteTask(existedTask);
                }
            }
            taskService.addNewTask(task);
        }

        Map<String, ArrayNode> graderMap = new HashMap<>();
        for (Grader grader: graders) {
            JsonNode formatNode = objectMapper.createObjectNode();
            ArrayNode tasksArray = objectMapper.createArrayNode();
            graderMap.put(grader.getUserId(), tasksArray);
            ((ObjectNode) formatNode).put("id", grader.getUserId());
            ((ObjectNode) formatNode).put("name", grader.getName());
            ((ObjectNode) formatNode).put("role", grader.getRole().toString());
            ((ObjectNode) formatNode).set("groups", tasksArray);
            gradersArray.add(formatNode);
        }

        List<Task> tasks = taskService.getTasksFromId(project);
        for (Task task: tasks) {
            if (!validSubmissionId.contains(task.getSubmissionId())) {
                taskService.deleteTask(task);
                continue;
            }

            JsonNode taskNode = objectMapper.createObjectNode();
            ((ObjectNode) taskNode).put("id", task.getId());
            ((ObjectNode) taskNode).put("submission_id", task.getSubmissionId());
            ((ObjectNode) taskNode).put("isGroup", task.isGroup());
            ((ObjectNode) taskNode).put("name", task.getName());

            if (task.getGrader() == null) {
                notAssignedArray.add(taskNode);
            } else {
                graderMap.get(task.getGrader().getUserId()).add(taskNode);
            }
        }

        ((ObjectNode)resultNode).set("graders", gradersArray);
        ((ObjectNode)resultNode).set("notAssigned", notAssignedArray);

        return resultNode;
    }

    @PostMapping(value = "/addGraders")
    protected JsonNode addGrader(@PathVariable String courseId,
                                 @PathVariable String projectId,
                                 @RequestBody ArrayNode activeGraders,
                                 Principal principal) throws JsonProcessingException, ParseException {
        Project project = projectService.getProjectById(courseId, projectId);
        if (project == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "entity not found"
            );
        }

        List<Grader> gradersDatabase = graderService.getGraderFromProject(project);
        List<String> editedActiveGraders = new ArrayList<>();
        List<String> gradersResponse = this.canvasApi.getCanvasCoursesApi().getCourseGraders(courseId);
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Grader> availableGrader = new HashMap<>();
        ArrayNode gradersArrayFromCanvas = groupPages(objectMapper, gradersResponse);
        for (Iterator<JsonNode> it = gradersArrayFromCanvas.elements(); it.hasNext(); ) {
            JsonNode node = it.next();
            availableGrader.put(node.get("id").asText(), new Grader(
                    project,
                    node.get("id").asText(),
                    node.get("name").asText(),
                    Grader.getRoleFromString(node.get("enrollments").get(0).get("type").asText()))
            );
        }

        for (Iterator<JsonNode> it = activeGraders.elements(); it.hasNext(); ) {
            JsonNode grader = it.next();
            editedActiveGraders.add(grader.get("id").asText());
        }
        for (Grader grader: gradersDatabase) {
            if (!editedActiveGraders.contains(grader.getUserId())) {
                graderService.deleteGrader(grader);
            }
        }

        for (String activeGraderId: editedActiveGraders) {
            graderService.addNewGrader(availableGrader.get(activeGraderId));
        }

        //remake notAssigned & graders

        List<Grader> graders = graderService.getGraderFromProject(project);
        JsonNode resultNode = objectMapper.createObjectNode();
        ArrayNode notAssignedArray = objectMapper.createArrayNode();
        ArrayNode gradersArray = objectMapper.createArrayNode();

        Map<String, ArrayNode> graderMap = new HashMap<>();
        for (Grader grader1: graders) {
            JsonNode formatNode = objectMapper.createObjectNode();
            ArrayNode tasksArray = objectMapper.createArrayNode();
            graderMap.put(grader1.getUserId(), tasksArray);
            ((ObjectNode) formatNode).put("id", grader1.getUserId());
            ((ObjectNode) formatNode).put("name", grader1.getName());
            ((ObjectNode) formatNode).put("role", grader1.getRole().toString());
            ((ObjectNode) formatNode).set("groups", tasksArray);
            gradersArray.add(formatNode);
        }

        List<Task> tasks = taskService.getTasksFromId(project);
        System.out.println(tasks);
        for (Task task1: tasks) {

            JsonNode taskNode = objectMapper.createObjectNode();
            ((ObjectNode) taskNode).put("id", task1.getId());
            ((ObjectNode) taskNode).put("submission_id", task1.getSubmissionId());
            ((ObjectNode) taskNode).put("isGroup", task1.isGroup());
            ((ObjectNode) taskNode).put("name", task1.getName());

            if (task1.getGrader() == null) {
                notAssignedArray.add(taskNode);
            } else {
                graderMap.get(task1.getGrader().getUserId()).add(taskNode);
            }
        }

        ((ObjectNode)resultNode).set("graders", gradersArray);
        ((ObjectNode)resultNode).set("notAssigned", notAssignedArray);

        return resultNode;

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
            JsonNode grader = objectMapper.createObjectNode();
            ((ObjectNode) grader).put("id", node.get("id").asText());
            ((ObjectNode) grader).put("name", node.get("name").asText());
            ((ObjectNode) grader).put("role", Grader.getRoleFromString(node.get("enrollments").get(0).get("type").asText()).toString());
            results.add(grader);
        }
        return results;
    }

    @GetMapping(value = "/assign/{assignmentId}/{isGroup}/{toUserId}")
    @ResponseBody
    protected JsonNode assignTask(@PathVariable String courseId,
                                  @PathVariable String projectId,
                                  @PathVariable String assignmentId,
                                  @PathVariable Boolean isGroup,
//                                  @PathVariable String fromUserId,
                                  @PathVariable String toUserId,
                                  Principal principal) throws JsonProcessingException, ParseException {
        Project project = projectService.getProjectById(courseId, projectId);
        if (project == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "entity not found"
            );
        }
        Task task = taskService.findTaskByTaskId(assignmentId, isGroup, project);
        Grader grader = graderService.getGraderFromGraderId(toUserId, project);
        if (task == null) return null;
        if (!toUserId.equals("notAssigned")) {
            if (grader == null) return null;
            task.setGrader(grader);
        } else {
            System.out.println("notAssigned");
            task.setGrader(null);
        }
        taskService.addNewTask(task);

        //remake notAssigned & graders
        List<Grader> graders = graderService.getGraderFromProject(project);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode resultNode = objectMapper.createObjectNode();
        ArrayNode notAssignedArray = objectMapper.createArrayNode();
        ArrayNode gradersArray = objectMapper.createArrayNode();

        Map<String, ArrayNode> graderMap = new HashMap<>();
        for (Grader grader1: graders) {
            JsonNode formatNode = objectMapper.createObjectNode();
            ArrayNode tasksArray = objectMapper.createArrayNode();
            graderMap.put(grader1.getUserId(), tasksArray);
            ((ObjectNode) formatNode).put("id", grader1.getUserId());
            ((ObjectNode) formatNode).put("name", grader1.getName());
            ((ObjectNode) formatNode).put("role", grader1.getRole().toString());
            ((ObjectNode) formatNode).set("groups", tasksArray);
            gradersArray.add(formatNode);
        }

        List<Task> tasks = taskService.getTasksFromId(project);
        System.out.println(tasks);
        for (Task task1: tasks) {

            JsonNode taskNode = objectMapper.createObjectNode();
            ((ObjectNode) taskNode).put("id", task1.getId());
            ((ObjectNode) taskNode).put("submission_id", task1.getSubmissionId());
            ((ObjectNode) taskNode).put("isGroup", task1.isGroup());
            ((ObjectNode) taskNode).put("name", task1.getName());

            if (task1.getGrader() == null) {
                notAssignedArray.add(taskNode);
            } else {
                graderMap.get(task1.getGrader().getUserId()).add(taskNode);
            }
        }

        ((ObjectNode)resultNode).set("graders", gradersArray);
        ((ObjectNode)resultNode).set("notAssigned", notAssignedArray);

        return resultNode;
    }

    @GetMapping(value = "/return/{userId}")
    @ResponseBody
    protected JsonNode returnTasks(@PathVariable String courseId,
                                   @PathVariable String projectId,
                                   @PathVariable String userId,
                                   Principal principal) throws JsonProcessingException, ParseException {
        Project project = projectService.getProjectById(courseId, projectId);
        if (project == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "entity not found"
            );
        }
        List<Task> tasks1 = taskService.getTasksFromId(project);
        for(Task task: tasks1) {
            if (task.getGrader() == null) continue;
            if (task.getGrader().getUserId().equals(userId)) {
                task.setGrader(null);
                taskService.addNewTask(task);
            }
        }

        //remake notAssigned & graders

        List<Grader> graders = graderService.getGraderFromProject(project);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode resultNode = objectMapper.createObjectNode();
        ArrayNode notAssignedArray = objectMapper.createArrayNode();
        ArrayNode gradersArray = objectMapper.createArrayNode();

        Map<String, ArrayNode> graderMap = new HashMap<>();
        for (Grader grader1: graders) {
            JsonNode formatNode = objectMapper.createObjectNode();
            ArrayNode tasksArray = objectMapper.createArrayNode();
            graderMap.put(grader1.getUserId(), tasksArray);
            ((ObjectNode) formatNode).put("id", grader1.getUserId());
            ((ObjectNode) formatNode).put("name", grader1.getName());
            ((ObjectNode) formatNode).put("role", grader1.getRole().toString());
            ((ObjectNode) formatNode).set("groups", tasksArray);
            gradersArray.add(formatNode);
        }

        List<Task> tasks = taskService.getTasksFromId(project);
        System.out.println(tasks);
        for (Task task1: tasks) {

            JsonNode taskNode = objectMapper.createObjectNode();
            ((ObjectNode) taskNode).put("id", task1.getId());
            ((ObjectNode) taskNode).put("submission_id", task1.getSubmissionId());
            ((ObjectNode) taskNode).put("isGroup", task1.isGroup());
            ((ObjectNode) taskNode).put("name", task1.getName());

            if (task1.getGrader() == null) {
                notAssignedArray.add(taskNode);
            } else {
                graderMap.get(task1.getGrader().getUserId()).add(taskNode);
            }
        }

        ((ObjectNode)resultNode).set("graders", gradersArray);
        ((ObjectNode)resultNode).set("notAssigned", notAssignedArray);

        return resultNode;
    }

    @PostMapping(value = "/bulkAssign")
    protected JsonNode bulkAssign(@PathVariable String courseId,
                                   @PathVariable String projectId,
                                   @RequestBody ObjectNode object,
                                   Principal principal) throws JsonProcessingException, ParseException {
        Project project = projectService.getProjectById(courseId, projectId);
        if (project == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "entity not found"
            );
        }
        List<Task> tasks1 = taskService.getTasksFromId(project);

        List<Task> notAssigned = new ArrayList<>();
        for(Task task: tasks1) {
            if (task.getGrader() == null) {
                notAssigned.add(task);
            }
        }

        int notAssignNum = object.get("tasks").asInt();
        if (notAssignNum > notAssigned.size()) {
            System.out.println("different sync");
            return null;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        Collections.shuffle(notAssigned);
        ArrayNode gradersNeedToAssign = (ArrayNode) object.get("graders");
        List<JsonNode> gradersNeedToBeAssigned = new ArrayList<>();
        for (Iterator<JsonNode> it = gradersNeedToAssign.elements(); it.hasNext(); ) {
            JsonNode node = it.next();
            gradersNeedToBeAssigned.add(node);
        }

        int[] amountOfTasks = new int[gradersNeedToBeAssigned.size()];

        Arrays.fill(amountOfTasks, (int) notAssignNum / amountOfTasks.length);
        Random rand = new Random();
        notAssignNum -= amountOfTasks.length*((int) notAssignNum / amountOfTasks.length);

        while(notAssignNum > 0) {
            int random = rand.nextInt(amountOfTasks.length);
            amountOfTasks[random] += 1;
            notAssignNum -= 1;
        }

        for(int i = 0; i < gradersNeedToBeAssigned.size(); i++) {
            JsonNode grader = gradersNeedToBeAssigned.get(i);
            int num = amountOfTasks[i];
            for(int j = 0; j < num; j++) {
                if (notAssigned.size() == 0) {
                    System.out.println("something is wrong, not assigned is overflow");
                    break;
                }
                Task task = notAssigned.remove(0);
                Grader grader1 = graderService.getGraderFromGraderId(grader.get("id").asText(), project);
                if (grader1 == null) {
                    System.out.println("Grader not found");
                    return null;
                }
                task.setGrader(grader1);
                taskService.addNewTask(task);
            }
        }


        //remake notAssigned & graders

        List<Grader> graders = graderService.getGraderFromProject(project);
        JsonNode resultNode = objectMapper.createObjectNode();
        ArrayNode notAssignedArray = objectMapper.createArrayNode();
        ArrayNode gradersArray = objectMapper.createArrayNode();

        Map<String, ArrayNode> graderMap = new HashMap<>();
        for (Grader grader1: graders) {
            JsonNode formatNode = objectMapper.createObjectNode();
            ArrayNode tasksArray = objectMapper.createArrayNode();
            graderMap.put(grader1.getUserId(), tasksArray);
            ((ObjectNode) formatNode).put("id", grader1.getUserId());
            ((ObjectNode) formatNode).put("name", grader1.getName());
            ((ObjectNode) formatNode).put("role", grader1.getRole().toString());
            ((ObjectNode) formatNode).set("groups", tasksArray);
            gradersArray.add(formatNode);
        }

        List<Task> tasks = taskService.getTasksFromId(project);
        System.out.println(tasks);
        for (Task task1: tasks) {

            JsonNode taskNode = objectMapper.createObjectNode();
            ((ObjectNode) taskNode).put("id", task1.getId());
            ((ObjectNode) taskNode).put("submission_id", task1.getSubmissionId());
            ((ObjectNode) taskNode).put("isGroup", task1.isGroup());
            ((ObjectNode) taskNode).put("name", task1.getName());

            if (task1.getGrader() == null) {
                notAssignedArray.add(taskNode);
            } else {
                graderMap.get(task1.getGrader().getUserId()).add(taskNode);
            }
        }

        ((ObjectNode)resultNode).set("graders", gradersArray);
        ((ObjectNode)resultNode).set("notAssigned", notAssignedArray);

        return resultNode;
    }

}
