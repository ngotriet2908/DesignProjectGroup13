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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

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
        JsonNode jsonCourseNode = objectMapper.readTree(courseString);
        ArrayNode arrayNode = groupPages(objectMapper, responseString);
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
    @ResponseBody
    protected void addGrader(@PathVariable String courseId, @PathVariable String projectId, Principal principal) throws JsonProcessingException, ParseException {
        List<String> gradersResponse = this.canvasApi.getCanvasCoursesApi().getCourseGraders(courseId);
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode gradersArrayFromCanvas = groupPages(objectMapper, gradersResponse);
        for (Iterator<JsonNode> it = gradersArrayFromCanvas.elements(); it.hasNext(); ) {
            JsonNode node = it.next();
            Grader grader = new Grader(projectId,
                    courseId,
                    node.get("id").asText(),
                    node.get("name").asText(),
                    Grader.getRoleFromString(node.get("enrollments").get(0).get("type").asText()));
            graderService.addNewGrader(grader);
        }
    }

    @GetMapping(value = "/{courseId}/projects/{projectId}/management")
    @ResponseBody
    protected JsonNode getManagementInfo(@PathVariable String courseId, @PathVariable String projectId, Principal principal) throws JsonProcessingException, ParseException {
        List<Grader> graders = graderService.getGraderFromId(courseId, projectId);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode resultNode = objectMapper.createObjectNode();
        ArrayNode notAssignedArray = objectMapper.createArrayNode();
        ArrayNode gradersArray = objectMapper.createArrayNode();

        for (Grader grader: graders) {
            JsonNode formatNode = objectMapper.createObjectNode();
            ArrayNode tasksArray = objectMapper.createArrayNode();
            ((ObjectNode) formatNode).put("id", grader.getUserId());
            ((ObjectNode) formatNode).put("name", grader.getName());
            ((ObjectNode) formatNode).put("role", grader.getRole().toString());
            ((ObjectNode) formatNode).set("groups", tasksArray);
            gradersArray.add(formatNode);
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
}
