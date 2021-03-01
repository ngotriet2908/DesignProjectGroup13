package com.group13.tcsprojectgrading.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.group13.tcsprojectgrading.canvas.api.CanvasApi;
import com.group13.tcsprojectgrading.models.Activity;
import com.group13.tcsprojectgrading.services.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

@RestController
@RequestMapping("/api/courses")
class CoursesController {
    private final CanvasApi canvasApi;

    private final ActivityService activityService;

    @Autowired
    public CoursesController(CanvasApi canvasApi, ActivityService activityService) {
        this.canvasApi = canvasApi;
        this.activityService = activityService;
    }

    @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    protected ResponseEntity<String> courses() {
        List<String> response = this.canvasApi.getCanvasCoursesApi().getUserCourseList();

        if (response == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } else {
            // TODO: the following line sends back only the first batch of the list of courses
            return new ResponseEntity<>(response.get(0), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/course/{course_id}", method = RequestMethod.GET, produces = "application/json")
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

    @RequestMapping(value = "/course/{courseId}/project/{projectId}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    protected ResponseEntity<JsonNode> getProject(@PathVariable String courseId, @PathVariable String projectId, Principal principal) throws JsonProcessingException, ParseException {
        String projectResponse = this.canvasApi.getCanvasCoursesApi().getCourseProject(courseId, projectId);
        String courseResponse = this.canvasApi.getCanvasCoursesApi().getUserCourse(courseId);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode projectJson = objectMapper.readTree(projectResponse);
        JsonNode courseJson = objectMapper.readTree(courseResponse);

        JsonNode resultJson = objectMapper.createObjectNode();
        ((ObjectNode)resultJson).set("course", courseJson);
        ((ObjectNode)resultJson).set("project", projectJson);

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
}
