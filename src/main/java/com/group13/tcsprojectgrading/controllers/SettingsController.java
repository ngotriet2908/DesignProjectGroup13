package com.group13.tcsprojectgrading.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.group13.tcsprojectgrading.services.grading.AssessmentCoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/settings")
public class SettingsController {
//    private final AssessmentCoreService assessmentCoreService;

    @Autowired
    public SettingsController(AssessmentCoreService assessmentCoreService) {
//        this.assessmentCoreService = assessmentCoreService;
    }


//    @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json")
//    protected String getSettings(@PathVariable String courseId,
//                                 @PathVariable String projectId,
//                                 @PathVariable String submissionId,
//                                 @PathVariable String assessmentId)
//            throws JsonProcessingException {
//        return assessmentCoreService.getAssessment(courseId, projectId, submissionId, assessmentId);
//    }
}