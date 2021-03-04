package com.group13.tcsprojectgrading.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.group13.tcsprojectgrading.canvas.api.CanvasApi;
import com.group13.tcsprojectgrading.models.Activity;
import com.group13.tcsprojectgrading.models.Grader;
import com.group13.tcsprojectgrading.models.Task;
import com.group13.tcsprojectgrading.models.rubric.Rubric;
import com.group13.tcsprojectgrading.services.ActivityService;
import com.group13.tcsprojectgrading.services.GraderService;
import com.group13.tcsprojectgrading.services.TaskService;
import com.group13.tcsprojectgrading.services.rubric.RubricService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api/courses")
class CoursesController {
    private final CanvasApi canvasApi;

    private final ActivityService activityService;
    private final RubricService rubricService;
    private final GraderService graderService;
    private final TaskService taskService;

    @Autowired
    public CoursesController(CanvasApi canvasApi, ActivityService activityService, RubricService rubricService, GraderService graderService, TaskService taskService) {
        this.canvasApi = canvasApi;
        this.activityService = activityService;
        this.rubricService = rubricService;
        this.graderService = graderService;
        this.taskService = taskService;
    }

    @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json")
    protected ResponseEntity<String> courses() {
        List<String> response = this.canvasApi.getCanvasCoursesApi().getUserCourseList();

        if (response == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } else {
            // TODO: the following line sends back only the first batch of the list of courses
            return new ResponseEntity<>(response.get(0), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/{course_id}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    protected ResponseEntity<JsonNode> getCourse(@PathVariable String course_id) throws JsonProcessingException {
        List<String> responseString = this.canvasApi.getCanvasCoursesApi().getCourseProjects(course_id);
        String courseString = this.canvasApi.getCanvasCoursesApi().getUserCourse(course_id);

        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode arrayNode = objectMapper.createArrayNode();
        JsonNode jsonCourseNode = objectMapper.readTree(courseString);
        for(String nodeListString: responseString) {
            JsonNode jsonNode = objectMapper.readTree(nodeListString);
            for (Iterator<JsonNode> it = jsonNode.elements(); it.hasNext(); ) {
                JsonNode node = it.next();
                arrayNode.add(node);
            }
        }
        JsonNode resultNode = objectMapper.createObjectNode();
        ((ObjectNode)resultNode).set("course", jsonCourseNode);
        ((ObjectNode)resultNode).set("projects", arrayNode);

        if (arrayNode == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } else {
            return new ResponseEntity<>(resultNode, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/{courseId}/projects/{projectId}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    protected ResponseEntity<JsonNode> getProject(@PathVariable String courseId, @PathVariable String projectId, Principal principal) throws JsonProcessingException, ParseException {
        String projectResponse = this.canvasApi.getCanvasCoursesApi().getCourseProject(courseId, projectId);
        String courseResponse = this.canvasApi.getCanvasCoursesApi().getUserCourse(courseId);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode projectJson = objectMapper.readTree(projectResponse);
        JsonNode courseJson = objectMapper.readTree(courseResponse);

        // including rubric to the response
        List<Rubric> rubric = rubricService.getAllRubrics();
        JsonNode rubricJson;
        if (rubric.size() == 0) {
            rubricJson = objectMapper.readTree("{\"rubric\": null}");
        } else {
            String rubricString = objectMapper.writeValueAsString(rubric.get(0));
            String response = "{\"rubric\":" + rubricString + "}";
            rubricJson = objectMapper.readTree(response);
        }

        ObjectNode resultJson = objectMapper.createObjectNode();
        resultJson.set("course", courseJson);
        resultJson.set("project", projectJson);
        resultJson.set("rubric", rubricJson);

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        SimpleDateFormat format = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss'Z'");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        Timestamp createdAt = new Timestamp(format.parse(projectJson.get("created_at").asText()).getTime());

        Activity activity = new Activity(
                projectId,
                courseId,
                principal.getName(),
                timestamp,
                projectJson.get("name").asText(),
                createdAt
        );

        activityService.addOrUpdateActivity(activity);

        if (projectResponse == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } else {
            return new ResponseEntity<>(resultJson, HttpStatus.OK);
        }
    }

    @GetMapping("/{courseId}/projects/{projectId}/rubric")
    public ResponseEntity<String> getProject() throws JsonProcessingException {
        List<Rubric> rubric = rubricService.getAllRubrics();

        if (rubric.size() == 0) {
            return new ResponseEntity<>("{\"rubric\": null}", HttpStatus.OK);
        } else {
            ObjectMapper objectMapper = new ObjectMapper();
            String rubricString = objectMapper.writeValueAsString(rubric.get(0));
            String response = "{\"rubric\":" + rubricString + "}";
            System.out.println(response);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    @PostMapping("/{courseId}/projects/{projectId}/rubric")
    public Rubric newRubric(@RequestBody Rubric newRubric) {
        System.out.println("Creating a rubric...");
        return rubricService.addNewRubric(newRubric);
    }

    @DeleteMapping("/{courseId}/projects/{projectId}/rubric")
    public void deleteRubric() {
        System.out.println("Deleting the rubric...");
        // TODO currently deletes all rubrics
//        rubricService.removeRubric();
//        return rubricService.addNewRubric(newRubric);
    }

    @PostMapping(value = "/{courseId}/projects/{projectId}/addGraders")
    protected JsonNode addGrader(@PathVariable String courseId,
                             @PathVariable String projectId,
                             @RequestBody ArrayNode activeGraders,
                             Principal principal) throws JsonProcessingException, ParseException {
        List<Grader> gradersDatabase = graderService.getGraderFromId(courseId, projectId);
        List<String> editedActiveGraders = new ArrayList<>();
        List<String> gradersResponse = this.canvasApi.getCanvasCoursesApi().getCourseGraders(courseId);
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Grader> availableGrader = new HashMap<>();
        ArrayNode gradersArrayFromCanvas = groupPages(objectMapper, gradersResponse);
        for (Iterator<JsonNode> it = gradersArrayFromCanvas.elements(); it.hasNext(); ) {
            JsonNode node = it.next();
            availableGrader.put(node.get("id").asText(), new Grader(
                    projectId,
                    courseId,
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

        List<Grader> graders = graderService.getGraderFromId(courseId, projectId);
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

        List<Task> tasks = taskService.getTasksFromId(courseId, projectId);
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

    @GetMapping(value = "/{courseId}/projects/{projectId}/addGraders/getAllGraders")
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

    @GetMapping(value = "/{courseId}/projects/{projectId}/management")
    @ResponseBody
    protected JsonNode getManagementInfo(@PathVariable String courseId, @PathVariable String projectId, Principal principal) throws JsonProcessingException, ParseException {
        String projectResponse = this.canvasApi.getCanvasCoursesApi().getCourseProject(courseId, projectId);

        List<Grader> graders = graderService.getGraderFromId(courseId, projectId);
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
                    courseId,
                    projectId,
                    jsonNode.get("id").asText(),
                    name
            );
            validSubmissionId.add(task.getSubmissionId());
            Task existedTask = taskService.findTaskById(jsonNode.get("id").asText(), courseId, projectId);
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

        List<Task> tasks = taskService.getTasksFromId(courseId, projectId);
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

    @GetMapping(value = "/{courseId}/projects/{projectId}/management/assign/{assignmentId}/{isGroup}/{toUserId}")
    @ResponseBody
    protected JsonNode assignTask(@PathVariable String courseId,
                                  @PathVariable String projectId,
                                  @PathVariable String assignmentId,
                                  @PathVariable Boolean isGroup,
//                                  @PathVariable String fromUserId,
                                  @PathVariable String toUserId,
                                  Principal principal) throws JsonProcessingException, ParseException {

        Task task = taskService.findTaskByTaskId(assignmentId, isGroup, courseId, projectId);
        Grader grader = graderService.getGraderFromGraderId(toUserId, courseId, projectId);
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

        List<Grader> graders = graderService.getGraderFromId(courseId, projectId);
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

        List<Task> tasks = taskService.getTasksFromId(courseId, projectId);
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

    @GetMapping(value = "/{courseId}/projects/{projectId}/management/return/{userId}")
    @ResponseBody
    protected JsonNode returnTasks(@PathVariable String courseId,
                                  @PathVariable String projectId,
                                  @PathVariable String userId,
                                  Principal principal) throws JsonProcessingException, ParseException {

        List<Task> tasks1 = taskService.getTasksFromId(courseId, projectId);
        for(Task task: tasks1) {
            if (task.getGrader() == null) continue;
            if (task.getGrader().getUserId().equals(userId)) {
                task.setGrader(null);
                taskService.addNewTask(task);
            }
        }

        //remake notAssigned & graders

        List<Grader> graders = graderService.getGraderFromId(courseId, projectId);
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

        List<Task> tasks = taskService.getTasksFromId(courseId, projectId);
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

    public ArrayNode groupPages(ObjectMapper objectMapper, List<String> responseString) throws JsonProcessingException {
        ArrayNode arrayNode = objectMapper.createArrayNode();
        for(String nodeListString: responseString) {
            JsonNode jsonNode = objectMapper.readTree(nodeListString);
            for (Iterator<JsonNode> it = jsonNode.elements(); it.hasNext(); ) {
                JsonNode node = it.next();
                arrayNode.add(node);
            }
        }
        return arrayNode;
    }

    // TODO temporary unsafe method
    @GetMapping("/{courseId}/projects/{projectId}/submissions/sample")
    public ResponseEntity<byte[]> getSamplePdf() throws IOException {
        Path pdfPath = Paths.get("src","main", "resources","static", "testPdf.pdf");
        byte[] contents = Files.readAllBytes(pdfPath);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        String filename = "output.pdf";
        headers.setContentDispositionFormData(filename, filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        ResponseEntity<byte[]> response = new ResponseEntity<>(contents, headers, HttpStatus.OK);
        return response;
    }
}
