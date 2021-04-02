package com.group13.tcsprojectgrading.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.group13.tcsprojectgrading.canvas.api.CanvasApi;
import com.group13.tcsprojectgrading.models.user.Activity;
import com.group13.tcsprojectgrading.models.project.Project;
import com.group13.tcsprojectgrading.models.user.User;
import com.group13.tcsprojectgrading.services.graders.GradingParticipationService;
import com.group13.tcsprojectgrading.services.user.ActivityService;
import com.group13.tcsprojectgrading.services.project.ProjectService;
import com.group13.tcsprojectgrading.services.submissions.SubmissionService;
import com.group13.tcsprojectgrading.services.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping("/api/users")
class UsersController {
    private final CanvasApi canvasApi;

    private final ActivityService activityService;

    private final UserService userService;

    private final GradingParticipationService gradingParticipationService;
    private final SubmissionService submissionService;
    private final ProjectService projectService;

    @Autowired
    public UsersController(CanvasApi canvasApi, ActivityService activityService, UserService userService,
                           GradingParticipationService gradingParticipationService, SubmissionService submissionService, ProjectService projectService) {
        this.canvasApi = canvasApi;
        this.activityService = activityService;
        this.userService = userService;
        this.gradingParticipationService = gradingParticipationService;
        this.submissionService = submissionService;
        this.projectService = projectService;
    }

    /*
    Returns user's profile data.
     */
    @RequestMapping(value = "/{userId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    protected ResponseEntity<?> getUserInfo(@PathVariable Long userId) {
        User user = this.userService.findById(userId);

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        } else {
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
    }

    /*
    Returns profile data of the signed in users from Canvas.
     */
    @RequestMapping(value = "/self", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    protected ResponseEntity<?> selfInfo(Principal principal) {
//        String response = this.canvasApi.getCanvasUsersApi().getAccount();
//
//        if (response == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        } else {
//            return new ResponseEntity<>(response, HttpStatus.OK);
//        }

        User user = this.userService.findById(Long.valueOf(principal.getName()));

        if (user == null) {
            // TODO
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found");
        } else {
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
    }

    /*
    Return the list of most recently accessed projects.
     */
    @GetMapping(value = "/recent")
    protected List<Activity> recentProject(Principal principal) {
        List<Activity> activities = activityService.getActivities(Long.valueOf(principal.getName()));
        return activities.subList(0, Math.min(3, activities.size()));
    }

    @GetMapping(value = "/to-do")
    protected List<Project> getTasks(Principal principal) throws JsonProcessingException {
//        ObjectMapper objectMapper = new ObjectMapper();
//        List<Submission> submissions = userService.findSubmissionsForGraderAll(principal.getName());
//        Map<Project, List<Submission>> projectListMap = new HashMap<>();
//
//        for(Submission submission : submissions) {
//            if (!projectListMap.containsKey(submission.getProject())) {
//                List<Submission> tasks1 = new ArrayList<>();
//                tasks1.add(submission);
//                projectListMap.put(submission.getProject(), tasks1);
//            } else {
//                projectListMap.get(submission.getProject()).add(submission);
//            }
//        }
//
//        ArrayNode arrayNode = objectMapper.createArrayNode();
//
//        for(Map.Entry<Project, List<Submission>> entry: projectListMap.entrySet()) {
//            ObjectNode node = objectMapper.createObjectNode();
//
//            String courseString = this.canvasApi.getCanvasCoursesApi().getUserCourse(entry.getKey().getCourseId());
//            String projectResponse = this.canvasApi.getCanvasCoursesApi().getCourseProject(entry.getKey().getCourseId(), entry.getKey().getProjectId());
//
//            node.set("course", objectMapper.readTree(courseString));
//            node.set("project", objectMapper.readTree(projectResponse));
//            node.put("submissions", entry.getValue().size());
//            node.put("progress", (int)(Math.random()*100));
//
//            arrayNode.add(node);
//        }
//
//        return arrayNode;

        return this.projectService.getToDoList(Long.valueOf(principal.getName()));
    }
}