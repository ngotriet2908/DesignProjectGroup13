//package com.group13.tcsprojectgrading.controllers;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.node.ArrayNode;
//import com.fasterxml.jackson.databind.node.ObjectNode;
//import com.group13.tcsprojectgrading.canvas.api.CanvasApi;
//import com.group13.tcsprojectgrading.models.course.CourseParticipation;
//import com.group13.tcsprojectgrading.models.grading.Assessment;
//import com.group13.tcsprojectgrading.models.permissions.PrivilegeEnum;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.server.ResponseStatusException;
//
//import java.util.Arrays;
//import java.util.List;
//
//import static com.group13.tcsprojectgrading.models.permissions.PrivilegeEnum.PROJECT_READ;
//
//@RestController
//@RequestMapping("/api/courses")
//public class StatsController {
//    private final CanvasApi canvasApi;
//    private final ObjectMapper mapper;
//
//    @Autowired
//    public StatsController(CanvasApi canvasApi) {
//        this.canvasApi = canvasApi;
//        this.mapper = new ObjectMapper();
//    }
//
//    /*
//    Retrieve count statistics for the given course denoted by courseId, such as amount of students, TAs, submissions,
//    etc.
//    */
//    @GetMapping(value = "/{courseId}/stats/count", produces = "application/json")
//    @ResponseBody
//    protected ResponseEntity<ArrayNode> getCount(@PathVariable Long courseId) throws JsonProcessingException {
//        List<String> students = this.canvasApi.getCanvasCoursesApi().getCourseStudents(courseId);
////        System.out.println(students);
//        int studentCount = 0;
//        for(String studentsPage : students) {
//            ArrayNode arrayNode = mapper.readValue(studentsPage, ArrayNode.class);
//            studentCount += arrayNode.size();
//        }
//
//        ObjectNode studentNode = mapper.createObjectNode();
//        studentNode.put("title", "Amount of students");
//        studentNode.put("type", "number");
//        studentNode.put("data", studentCount);
//        studentNode.put("category", "Member");
//        studentNode.put("unit", "Students");
//
//        ArrayNode resultNode = mapper.createArrayNode();
//        resultNode.add(studentNode);
//
//        return new ResponseEntity<>(resultNode, HttpStatus.OK);
//    }
//
//    @GetMapping(value="/{courseId}/projects/{projectId}/stats/groups", produces = "application/json")
//    @ResponseBody
//    protected ResponseEntity<ObjectNode> getGroupStats(@PathVariable String courseId, @PathVariable String projectId) throws JsonProcessingException {
//        String groupsString = this.canvasApi.getCanvasCoursesApi().getAssignmentGroups(Long.parseLong(courseId), Long.parseLong(projectId));
//
//        ArrayNode groups = mapper.readValue(groupsString, ArrayNode.class);
//        int groupCount = groups.size();
//
//        ObjectNode resultNode = mapper.createObjectNode();
//        resultNode.put("title", "Amount of groups");
//        resultNode.put("type", "number");
//        resultNode.put("data", groupCount);
//        resultNode.put("category", "Member");
//        resultNode.put("unit", "Groups");
//
//        return new ResponseEntity<>(resultNode, HttpStatus.OK);
//    }
//
//    @GetMapping(value="/{courseId}/projects/{projectId}/stats/submissions", produces = "application/json")
//    @ResponseBody
//    protected ResponseEntity<ObjectNode> getSubmissionStats(@PathVariable String courseId, @PathVariable String projectId) throws JsonProcessingException {
//        String summary = this.canvasApi.getCanvasCoursesApi().getSubmissionsSummary(Long.parseLong(courseId), Long.parseLong(projectId));
//
//        ObjectNode summaryNode = mapper.createObjectNode();
//        summaryNode.put("title", "Submissions summary");
//        summaryNode.put("type", "piechart");
//        summaryNode.put("category", "Grading");
//        summaryNode.set("data", mapper.readTree(summary));
//
//        return new ResponseEntity<>(summaryNode, HttpStatus.OK);
//    }
//
//    @GetMapping(value = "/{courseId}/projects/{projectId}/stats/grades", produces = "application/json")
//    @ResponseBody
//    protected ResponseEntity<ArrayNode> getGradeStats(@PathVariable Long courseId, @PathVariable Long projectId) {
//        double mean = 6.7;
//        double median = 6.9;
//        double min = 3.2;
//        double max = 8.9;
//
//        ObjectNode meanNode = mapper.createObjectNode();
//        meanNode.put("title","Mean grade");
//        meanNode.put("type", "number");
//        meanNode.put("category", "Grading");
//        meanNode.put("data", mean);
//
//        ObjectNode medianNode = mapper.createObjectNode();
//        medianNode.put("title","Median grade");
//        medianNode.put("type", "number");
//        medianNode.put("category", "Grading");
//        medianNode.put("data", median);
//
//        ObjectNode minNode = mapper.createObjectNode();
//        minNode.put("title","Min grade");
//        minNode.put("type", "number");
//        minNode.put("category", "Grading");
//        minNode.put("data", min);
//
//        ObjectNode maxNode = mapper.createObjectNode();
//        maxNode.put("title","Max grade");
//        maxNode.put("type", "number");
//        maxNode.put("category", "Grading");
//        maxNode.put("data", max);
//
//        ArrayNode resultNode = mapper.createArrayNode();
//        resultNode.addAll(Arrays.asList(meanNode,medianNode,minNode,maxNode));
//
//        return new ResponseEntity<>(resultNode, HttpStatus.OK);
//    }
//}
