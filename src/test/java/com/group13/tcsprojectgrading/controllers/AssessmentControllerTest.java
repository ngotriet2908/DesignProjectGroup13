package com.group13.tcsprojectgrading.controllers;

import com.group13.tcsprojectgrading.canvas.api.CanvasApi;
import com.group13.tcsprojectgrading.services.*;
import com.group13.tcsprojectgrading.services.grading.AssessmentService;
import com.group13.tcsprojectgrading.services.rubric.RubricService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AssessmentController.class)
public class AssessmentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CanvasApi canvasApi;
    @MockBean
    private ActivityService activityService;
    @MockBean
    private RubricService rubricService;
    @MockBean
    private GraderService graderService;
    @MockBean
    private ProjectService projectService;
    @MockBean
    private SubmissionService submissionService;
    @MockBean
    private AssessmentService assessmentService;
    @MockBean
    private AssessmentLinkerService assessmentLinkerService;
    @MockBean
    private ParticipantService participantService;
    @MockBean
    private IssueService issueService;

    @Test
    public void getAssessmentNotFound() throws Exception {
        mockMvc.perform(get("/api/courses/{courseId}/projects/{projectId}/" +
                "submissions/{submissionId}/{assessmentId}/grading", "1", "2", "3", "4", "5"))
                .andExpect(status().isNotFound());
    }
}
