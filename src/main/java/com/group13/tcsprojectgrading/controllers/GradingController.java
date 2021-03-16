package com.group13.tcsprojectgrading.controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group13.tcsprojectgrading.canvas.api.CanvasApi;
import com.group13.tcsprojectgrading.models.Project;
import com.group13.tcsprojectgrading.models.Submission;
import com.group13.tcsprojectgrading.models.grading.SubmissionAssessment;
import com.group13.tcsprojectgrading.models.rubric.Rubric;
import com.group13.tcsprojectgrading.services.ActivityService;
import com.group13.tcsprojectgrading.services.GraderService;
import com.group13.tcsprojectgrading.services.ProjectService;
import com.group13.tcsprojectgrading.services.SubmissionService;
import com.group13.tcsprojectgrading.services.grading.GradingService;
import com.group13.tcsprojectgrading.services.rubric.RubricService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;
import java.util.List;

@RestController
@RequestMapping("/api/courses/{courseId}/projects/{projectId}/submissions/{userId}/grading")
public class GradingController {
    private final CanvasApi canvasApi;

    private final ActivityService activityService;
    private final RubricService rubricService;
    private final GraderService graderService;
    private final ProjectService projectService;
    private final GradingService gradingService;
    private final SubmissionService submissionService;

    @Autowired
    public GradingController(CanvasApi canvasApi, ActivityService activityService, RubricService rubricService,
                             GraderService graderService , ProjectService projectService, GradingService gradingService,
                             SubmissionService submissionService) {
        this.canvasApi = canvasApi;
        this.activityService = activityService;
        this.rubricService = rubricService;
        this.graderService = graderService;
        this.projectService = projectService;
        this.gradingService = gradingService;
        this.submissionService = submissionService;
    }

    @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json")
    protected ResponseEntity<String> courses(@PathVariable String courseId, @PathVariable String projectId, @PathVariable String userId)
            throws JsonProcessingException {
//        Project project = projectService.getProjectById(courseId, projectId);
//        Submission submission = submissionService.findSubmissionById(userId);
        SubmissionAssessment submissionAssessment = gradingService.getAssessmentByProjectIdAndId(projectId, userId);
//
//        if (submissionAssessment == null && project != null) {
//
//        }

        if (submissionAssessment == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            ObjectMapper objectMapper = new ObjectMapper();
            String rubricString = objectMapper.writeValueAsString(submissionAssessment);
            System.out.println(rubricString);
            return new ResponseEntity<>(rubricString, HttpStatus.OK);
        }
    }
}