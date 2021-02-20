package com.group13.tcsprojectgrading.controller;

import com.group13.tcsprojectgrading.model.project.rubric.Rubric;
import com.group13.tcsprojectgrading.model.user.Participant;
import com.group13.tcsprojectgrading.service.user.ParticipantService;
import com.group13.tcsprojectgrading.service.project.rubric.RubricService;
import com.group13.tcsprojectgrading.service.user.TeacherService;
import com.group13.tcsprojectgrading.service.user.TeachingAssistantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/test/canvas")
public class CanvasParticipationController {

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private TeachingAssistantService teachingAssistantService;

    @Autowired
    private ParticipantService participantService;

    @Autowired
    private RubricService rubricService;

    @GetMapping("/participation")
    private List<Participant> getParticipation() {
        return participantService.findAllParticipant();
    }

    @GetMapping("/rubrics")
    private List<Rubric> getService() {
        return rubricService.getRubrics();
    }

}
