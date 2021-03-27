package com.group13.tcsprojectgrading.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.group13.tcsprojectgrading.canvas.api.CanvasApi;
import com.group13.tcsprojectgrading.models.Activity;
import com.group13.tcsprojectgrading.models.Project;
import com.group13.tcsprojectgrading.models.submissions.Submission;
import com.group13.tcsprojectgrading.services.*;
import com.group13.tcsprojectgrading.services.graders.GraderService;
import com.group13.tcsprojectgrading.services.grading.AssessmentLinkerService;
import com.group13.tcsprojectgrading.services.submissions.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping("/api/users")
class UsersController {
    private final CanvasApi canvasApi;

    private final ActivityService activityService;

    private final UserService userService;

    private final GraderService graderService;

    private final SubmissionService submissionService;

    private final ParticipantService participantService;

    private final AssessmentLinkerService assessmentLinkerService;

    @Autowired
    public UsersController(CanvasApi canvasApi, ActivityService activityService, UserService userService, GraderService graderService, SubmissionService submissionService, ParticipantService participantService, AssessmentLinkerService assessmentLinkerService) {
        this.canvasApi = canvasApi;
        this.activityService = activityService;
        this.userService = userService;
        this.graderService = graderService;
        this.submissionService = submissionService;
        this.participantService = participantService;
        this.assessmentLinkerService = assessmentLinkerService;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    protected ResponseEntity<String> getUserInfo(@PathVariable String id) {
        String response = this.canvasApi.getCanvasUsersApi().getAccountWithId(id);

        if (response == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else {
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/self", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    protected ResponseEntity<String> selfInfo() {
        String response = this.canvasApi.getCanvasUsersApi().getAccount();

        if (response == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } else {
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    @GetMapping(value = "/recent")
    protected List<Activity> recentProject(Principal principal) throws JsonProcessingException {
        List<Activity> activities = activityService.getActivities(principal.getName());
        return activities.subList(0, Math.min(3, activities.size()));
    }

    @GetMapping(value = "/submissions")
    protected ArrayNode getTasks(Principal principal) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Submission> submissions = userService.findSubmissionsForGraderAll(principal.getName());
        Map<Project, List<Submission>> projectListMap = new HashMap<>();

        for(Submission submission : submissions) {
            if (!projectListMap.containsKey(submission.getProject())) {
                List<Submission> tasks1 = new ArrayList<>();
                tasks1.add(submission);
                projectListMap.put(submission.getProject(), tasks1);
            } else {
                projectListMap.get(submission.getProject()).add(submission);
            }
        }

        ArrayNode arrayNode = objectMapper.createArrayNode();

        for(Map.Entry<Project, List<Submission>> entry: projectListMap.entrySet()) {
            JsonNode node = objectMapper.createObjectNode();

            String courseString = this.canvasApi.getCanvasCoursesApi().getUserCourse(entry.getKey().getCourseId());
            String projectResponse = this.canvasApi.getCanvasCoursesApi().getCourseProject(entry.getKey().getCourseId(), entry.getKey().getProjectId());

            ((ObjectNode) node).set("course", objectMapper.readTree(courseString));
            ((ObjectNode) node).set("project", objectMapper.readTree(projectResponse));
            ((ObjectNode) node).put("submissions", entry.getValue().size());
            ((ObjectNode) node).put("progress", (int)(Math.random()*100));

            arrayNode.add(node);
        }

//        return nodes.subList(0, Math.min(3, nodes.size()));
        return arrayNode;
    }
}