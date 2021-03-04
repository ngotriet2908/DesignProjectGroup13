package com.group13.tcsprojectgrading.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.group13.tcsprojectgrading.canvas.api.CanvasApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Iterator;
import java.util.List;

@RestController
@RequestMapping("/api/stats")
public class StatsController {
    private final CanvasApi canvasApi;

    @Autowired
    public StatsController(CanvasApi canvasApi) {
        this.canvasApi = canvasApi;
    }

    /*
    Retrieve count statistics for the given course denoted by courseId, such as amount of students, TAs, submissions,
    etc.
    */
    @GetMapping(value = "/courses/{courseId}/count", produces = "application/json")
    @ResponseBody
    protected ResponseEntity<ArrayNode> getCount(@PathVariable String courseId) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        List<String> students = this.canvasApi.getCanvasCoursesApi().getCourseStudents(courseId);
        System.out.println(students);
        int studentCount = 0;
        for(String studentsPage : students) {
            ArrayNode arrayNode = mapper.readValue(studentsPage, ArrayNode.class);
            studentCount += arrayNode.size();
        }

        ObjectNode studentNode = mapper.createObjectNode();
        studentNode.put("title", "Amount of students");
        studentNode.put("type", "number");
        studentNode.put("data", studentCount);
        studentNode.put("unit", "Students");

        ArrayNode resultNode = mapper.createArrayNode();
        resultNode.add(studentNode);

        return new ResponseEntity<>(resultNode, HttpStatus.OK);
    }
}
