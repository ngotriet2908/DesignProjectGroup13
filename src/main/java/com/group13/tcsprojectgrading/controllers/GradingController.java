package com.group13.tcsprojectgrading.controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group13.tcsprojectgrading.canvas.api.CanvasApi;
import com.group13.tcsprojectgrading.models.*;
import com.group13.tcsprojectgrading.models.grading.CriterionGrade;
import com.group13.tcsprojectgrading.models.grading.Grade;
import com.group13.tcsprojectgrading.services.*;
import com.group13.tcsprojectgrading.services.grading.AssessmentService;
import com.group13.tcsprojectgrading.services.rubric.RubricService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/courses/{courseId}/projects/{projectId}/submissions/{submissionId}/{assessmentId}/grading")
public class GradingController {
    private final CanvasApi canvasApi;

    private final ActivityService activityService;
    private final RubricService rubricService;
    private final GraderService graderService;
    private final ProjectService projectService;
    private final SubmissionService submissionService;
    private final AssessmentService assessmentService;
    private final AssessmentLinkerService assessmentLinkerService;
    private final ParticipantService participantService;

    @Autowired
    public GradingController(CanvasApi canvasApi, ActivityService activityService, RubricService rubricService, GraderService graderService, ProjectService projectService, SubmissionService submissionService, AssessmentService assessmentService, AssessmentLinkerService assessmentLinkerService, ParticipantService participantService) {
        this.canvasApi = canvasApi;
        this.activityService = activityService;
        this.rubricService = rubricService;
        this.graderService = graderService;
        this.projectService = projectService;
        this.submissionService = submissionService;
        this.assessmentService = assessmentService;
        this.assessmentLinkerService = assessmentLinkerService;
        this.participantService = participantService;
    }

    @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json")
    protected ResponseEntity<String> getAssessment(@PathVariable String courseId,
                                                   @PathVariable String projectId,
                                                   @PathVariable String submissionId,
                                                   @PathVariable String assessmentId)
            throws JsonProcessingException {
        Project project = projectService.getProjectById(courseId, projectId);
        Submission submission = submissionService.findSubmissionById(submissionId);
        List<Assessment> assessmentList = assessmentService.getAssessmentBySubmission(submission);

        Assessment submissionAssessment = assessmentService.getAssessmentById(assessmentId);

        if (!assessmentList.contains(submissionAssessment)) {
            System.out.println("here");
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

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
            @PathVariable String courseId,
            @PathVariable String projectId,
            @PathVariable String submissionId,
            @PathVariable String assessmentId,
            @PathVariable String criterionId,
            @RequestBody Grade newGrade
            ) {
        Project project = projectService.getProjectById(courseId, projectId);
        Submission submission = submissionService.findSubmissionById(submissionId);
        List<Assessment> assessmentList = assessmentService.getAssessmentBySubmission(submission);

        Assessment submissionAssessment = assessmentService.getAssessmentById(assessmentId);

        if (!assessmentList.contains(submissionAssessment)) {
            System.out.println("sub");
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

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

            this.assessmentService.saveAssessment(submissionAssessment);
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/{criterionId}/active/{id}", method = RequestMethod.PUT)
    protected ResponseEntity<String> updateActiveGrading(
            @PathVariable String courseId,
            @PathVariable String projectId,
            @PathVariable String submissionId,
            @PathVariable String assessmentId,
            @PathVariable String criterionId,
            @PathVariable int id
    ) {
        Project project = projectService.getProjectById(courseId, projectId);
        Submission submission = submissionService.findSubmissionById(submissionId);
        List<Assessment> assessmentList = assessmentService.getAssessmentBySubmission(submission);

        Assessment submissionAssessment = assessmentService.getAssessmentById(assessmentId);

        if (!assessmentList.contains(submissionAssessment)) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }


        if (submissionAssessment == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            Map<String, CriterionGrade> grades = submissionAssessment.getGrades();

            if (grades.get(criterionId).getHistory().size() > id) {
                grades.get(criterionId).setActive(id);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            this.assessmentService.saveAssessment(submissionAssessment);
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }
}