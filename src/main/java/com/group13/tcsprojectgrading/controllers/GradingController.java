package com.group13.tcsprojectgrading.controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group13.tcsprojectgrading.canvas.api.CanvasApi;
import com.group13.tcsprojectgrading.models.grading.CriterionGrade;
import com.group13.tcsprojectgrading.models.grading.Grade;
import com.group13.tcsprojectgrading.models.grading.SubmissionAssessment;
import com.group13.tcsprojectgrading.services.ActivityService;
import com.group13.tcsprojectgrading.services.GraderService;
import com.group13.tcsprojectgrading.services.ProjectService;
import com.group13.tcsprojectgrading.services.SubmissionService;
import com.group13.tcsprojectgrading.services.grading.GradingService;
import com.group13.tcsprojectgrading.services.rubric.RubricService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    protected ResponseEntity<String> getAssessment(@PathVariable String courseId, @PathVariable String projectId, @PathVariable String userId)
            throws JsonProcessingException {
//        Project project = projectService.getProjectById(courseId, projectId);
//        Submission submission = submissionService.findSubmissionById(userId);
        SubmissionAssessment submissionAssessment = gradingService.getAssessmentByProjectIdAndUserId(projectId, userId);

        if (submissionAssessment == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            ObjectMapper objectMapper = new ObjectMapper();
            String rubricString = objectMapper.writeValueAsString(submissionAssessment);
            return new ResponseEntity<>(rubricString, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/{criterionId}", method = RequestMethod.PUT)
    protected ResponseEntity<String> alterCriterionAssessment(
            @PathVariable String courseId, @PathVariable String projectId,
            @PathVariable String userId, @PathVariable String criterionId,
            @RequestBody Grade newGrade
            ) {
        SubmissionAssessment submissionAssessment = gradingService.getAssessmentByProjectIdAndUserId(projectId, userId);

        if (submissionAssessment == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            Map<String, CriterionGrade> grades = submissionAssessment.getGrades();

            if (grades.containsKey(criterionId)) {
                grades.get(criterionId).getHistory().add(newGrade);
                grades.get(criterionId).setActive(grades.get(criterionId).getActive() + 1);
            } else {
                grades.put(criterionId, new CriterionGrade(
                        0, new ArrayList<>() {{
                            add(newGrade);
                        }}
                        ));
            }

            this.gradingService.saveAssessment(submissionAssessment);
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/{criterionId}/active/{id}", method = RequestMethod.PUT)
    protected ResponseEntity<String> updateActiveGrading(
            @PathVariable String courseId, @PathVariable String projectId,
            @PathVariable String userId, @PathVariable String criterionId,
            @PathVariable int id
    ) {
        SubmissionAssessment submissionAssessment = gradingService.getAssessmentByProjectIdAndUserId(projectId, userId);

        if (submissionAssessment == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            Map<String, CriterionGrade> grades = submissionAssessment.getGrades();

            if (grades.get(criterionId).getHistory().size() > id) {
                grades.get(criterionId).setActive(id);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            this.gradingService.saveAssessment(submissionAssessment);
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }
}