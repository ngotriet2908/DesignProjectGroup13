package com.group13.tcsprojectgrading.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group13.tcsprojectgrading.services.*;
import com.group13.tcsprojectgrading.services.grading.AssessmentService;
import com.group13.tcsprojectgrading.services.rubric.RubricService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class CoursesControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RubricService rubricService;
    @MockBean
    private GraderService graderService;
    @MockBean
    private ProjectService projectService;
    @MockBean
    private SubmissionService submissionService;
    @MockBean
    private ActivityService activityService;
}
