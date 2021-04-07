package com.group13.tcsprojectgrading.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.group13.tcsprojectgrading.canvas.api.CanvasApi;
import com.group13.tcsprojectgrading.models.*;
import com.group13.tcsprojectgrading.models.grading.CriterionGrade;
import com.group13.tcsprojectgrading.models.grading.Grade;
import com.group13.tcsprojectgrading.models.rubric.Element;
import com.group13.tcsprojectgrading.models.rubric.Rubric;
import com.group13.tcsprojectgrading.models.rubric.RubricContent;
import com.group13.tcsprojectgrading.models.rubric.RubricGrade;
import com.group13.tcsprojectgrading.services.*;
import com.group13.tcsprojectgrading.services.grading.AssessmentService;
import com.group13.tcsprojectgrading.services.rubric.RubricService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//TODO: make this a test for the assessmentCoreService instead, since all the controller does now is call functions from that
@SpringBootTest
@AutoConfigureMockMvc
public class AssessmentControllerTest {
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
    private AssessmentService assessmentService;
    @MockBean
    private IssueService issueService;

    private final String courseId = "1";
    private final String projectId = "2";
    private final String submissionId = "3";
    private final UUID assessmentUuid = new UUID(1, 1);
    private final String assessmentId = assessmentUuid.toString();
    private final Assessment testAssessment = new Assessment(assessmentUuid);
    private final String userId = "44";
    Project testProject = new Project(courseId, projectId, "test", "test", "0");

    @WithMockUser
    @Test
    public void getAssessmentNotFound() throws Exception {
        when(submissionService.findSubmissionById(anyString())).thenReturn(new Submission());
        when(assessmentService.getAssessmentBySubmission(any(Submission.class)))
                .thenReturn(Collections.emptyList());
        when(assessmentService.getAssessmentById(anyString())).thenReturn(null);
        mockMvc.perform(get("/api/courses/{courseId}/projects/{projectId}/" +
                "submissions/{submissionId}/{assessmentId}/grading", courseId, projectId, submissionId, assessmentId))
                .andExpect(status().isNotFound());
    }

    @WithMockUser
    @Test
    public void getAssessmentOk() throws Exception {
        when(projectService.getProjectById(anyString(), eq(projectId))).thenReturn(testProject);
        when(submissionService.findSubmissionById(anyString())).thenReturn(new Submission());
        when(assessmentService.getAssessmentBySubmission(any(Submission.class)))
                .thenReturn(List.of(testAssessment));
        when(assessmentService.getAssessmentById(anyString())).thenReturn(testAssessment);
        mockMvc.perform(get("/api/courses/{courseId}/projects/{projectId}/" +
                "submissions/{submissionId}/{assessmentId}/grading", courseId, projectId, submissionId, assessmentId))
                .andExpect(status().isOk());
    }

    @WithMockUser
    @Test
    public void alterGradeAssessmentNotFound() throws Exception {
        Grade testGrade = new Grade();
        String criterionId = "4";
        when(submissionService.findSubmissionById(anyString())).thenReturn(new Submission());
        when(assessmentService.getAssessmentBySubmission(any(Submission.class)))
                .thenReturn(Collections.emptyList());
        when(assessmentService.getAssessmentById(anyString())).thenReturn(null);
        mockMvc.perform(put("/api/courses/{courseId}/projects/{projectId}/" +
                "/submissions/{submissionId}/{assessmentId}/grading/{criterionId}",
                courseId, projectId, submissionId, assessmentId, criterionId)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(testGrade))
                .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @WithMockUser
    @Test
    public void alterGradeAssessmentOk() throws Exception {
        Grade testGrade = new Grade();
        String criterionId = "4";
        when(submissionService.findSubmissionById(anyString())).thenReturn(new Submission());
        when(assessmentService.getAssessmentBySubmission(any(Submission.class)))
                .thenReturn(List.of(testAssessment));
        when(assessmentService.getAssessmentById(anyString())).thenReturn(testAssessment);
        when(rubricService.getRubricById(anyString())).thenReturn(new Rubric());

        mockMvc.perform(put("/api/courses/{courseId}/projects/{projectId}/" +
                        "/submissions/{submissionId}/{assessmentId}/grading/{criterionId}",
                courseId, projectId, submissionId, assessmentId, criterionId)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(testGrade))
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @WithMockUser
    @Test
    public void alterExistingGradeAssessmentAddedToHistory() throws Exception {
        Grade testGrade = new Grade();
        String criterionId = "4";
        int active = 0;
        testAssessment.getGrades().put(criterionId, new CriterionGrade(active, new ArrayList<>()));
        when(submissionService.findSubmissionById(anyString())).thenReturn(new Submission());
        when(assessmentService.getAssessmentBySubmission(any(Submission.class)))
                .thenReturn(List.of(testAssessment));
        when(assessmentService.getAssessmentById(anyString())).thenReturn(testAssessment);
        when(rubricService.getRubricById(anyString())).thenReturn(new Rubric());

        mockMvc.perform(put("/api/courses/{courseId}/projects/{projectId}/" +
                        "/submissions/{submissionId}/{assessmentId}/grading/{criterionId}",
                courseId, projectId, submissionId, assessmentId, criterionId)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(testGrade))
                .with(csrf()));

        System.out.println(testAssessment.getGrades().get(criterionId).getHistory());
        assertThat(testAssessment.getGrades().get(criterionId).getHistory().get(0).toString()).isEqualTo(testGrade.toString());
        assertThat(testAssessment.getGrades().get(criterionId).getActive()).isEqualTo(active + 1);
    }

    @WithMockUser
    @Test
    public void addNewGradeAssessmentAddedToHistory() throws Exception {
        Grade testGrade = new Grade();
        String criterionId = "4";
        when(submissionService.findSubmissionById(anyString())).thenReturn(new Submission());
        when(assessmentService.getAssessmentBySubmission(any(Submission.class)))
                .thenReturn(List.of(testAssessment));
        when(assessmentService.getAssessmentById(anyString())).thenReturn(testAssessment);
        when(rubricService.getRubricById(anyString())).thenReturn(new Rubric());

        mockMvc.perform(put("/api/courses/{courseId}/projects/{projectId}/" +
                        "/submissions/{submissionId}/{assessmentId}/grading/{criterionId}",
                courseId, projectId, submissionId, assessmentId, criterionId)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(testGrade))
                .with(csrf()));

        assertThat(testAssessment.getGrades().get(criterionId).getHistory().get(0).toString()).isEqualTo(testGrade.toString());
        assertThat(testAssessment.getGrades().get(criterionId).getActive()).isEqualTo(0);
    }

    @WithMockUser
    @Test
    public void addGradeAssessmentProgressUpdatedNotFullyGraded() throws Exception {
        Grade testGrade = new Grade();
        Rubric testRubric = new Rubric(projectId);
        testRubric.setCriterionCount(2);
        String criterionId = "4";
        Submission submission = new Submission();
        testProject.setSubmissions(List.of(submission));

        when(submissionService.findSubmissionById(anyString())).thenReturn(new Submission());
        when(assessmentService.getAssessmentBySubmission(any(Submission.class)))
                .thenReturn(List.of(testAssessment));
        when(assessmentService.getAssessmentById(anyString())).thenReturn(testAssessment);
        when(assessmentService.getProgress(any(Rubric.class), any(Assessment.class))).thenCallRealMethod();
        when(rubricService.getRubricById(anyString())).thenReturn(testRubric);
        when(projectService.getProjectById(anyString(), eq(projectId))).thenReturn(testProject);
        when(projectService.updateProgress(eq(testProject), anyDouble())).thenAnswer((invocationOnMock -> {
            Project project = invocationOnMock.getArgument(0);
            double currProgress = project.getProgress();
            int submissionAmount = project.getSubmissions().size();
            if (submissionAmount != 0) project.setProgress(currProgress + (double) invocationOnMock.getArgument(1) / submissionAmount);
            else project.setProgress(0);
            return project;
        }));

        mockMvc.perform(put("/api/courses/{courseId}/projects/{projectId}/" +
                        "/submissions/{submissionId}/{assessmentId}/grading/{criterionId}",
                courseId, projectId, submissionId, assessmentId, criterionId)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(testGrade))
                .with(csrf()));

        assertThat(testAssessment.getGradedCount()).isEqualTo(1);
        assertThat(testAssessment.getProgress()).isEqualTo(0.5);
        assertThat(testAssessment.getFinalGrade()).isEqualTo(0);
        assertThat(testProject.getProgress()).isEqualTo(0.5);
    }

    @WithMockUser
    @Test
    public void addGradeAssessmentFullyGraded() throws Exception {
        Grade testGrade = new Grade();
        testGrade.setGrade(7);
        Rubric testRubric = new Rubric(projectId);
        testRubric.setCriterionCount(1);
        String criterionId = "4";
        testRubric.setChildren(List.of(new Element(new RubricContent(criterionId, RubricContent.CRITERION_TYPE, "test", "test",
                new RubricGrade(0, 10, 1, 1)))));
        when(submissionService.findSubmissionById(anyString())).thenReturn(new Submission());
        when(assessmentService.getAssessmentBySubmission(any(Submission.class)))
                .thenReturn(List.of(testAssessment));
        when(assessmentService.getAssessmentById(anyString())).thenReturn(testAssessment);
        when(rubricService.getRubricById(anyString())).thenReturn(testRubric);
        when(assessmentService.calculateFinalGrade(any(Rubric.class), any(Assessment.class))).thenCallRealMethod();
        mockMvc.perform(put("/api/courses/{courseId}/projects/{projectId}/" +
                        "/submissions/{submissionId}/{assessmentId}/grading/{criterionId}",
                courseId, projectId, submissionId, assessmentId, criterionId)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(testGrade))
                .with(csrf()));

        assertThat(testAssessment.getFinalGrade()).isEqualTo(7);
    }

    //TODO updateActiveGrading test

    @WithMockUser
    @Test
    public void createIssueAssessmentNotFound() throws Exception {
        Issue testIssue = new Issue();
        when(projectService.getProjectById(anyString(), eq(projectId))).thenReturn(testProject);
        when(submissionService.findSubmissionById(anyString())).thenReturn(new Submission());
        when(assessmentService.getAssessmentBySubmission(any(Submission.class)))
                .thenReturn(Collections.emptyList());
        when(assessmentService.getAssessmentById(assessmentId)).thenReturn(null);
        mockMvc.perform(post("/api/courses/{courseId}/projects/{projectId}" +
                        "/submissions/{submissionId}/{assessmentId}/issues",
                courseId, projectId, submissionId, assessmentId)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(testIssue))
                .with(csrf()))
                .andExpect(status().isNotFound());
    }

//    @WithMockUser
//    @Test
//    public void createIssueConflict() throws Exception {
//        Issue testIssue = new Issue();
//        when(projectService.getProjectById(anyString(), eq(projectId))).thenReturn(testProject);
//        when(submissionService.findSubmissionById(anyString())).thenReturn(new Submission());
//        when(assessmentService.getAssessmentBySubmission(any(Submission.class)))
//                .thenReturn(Collections.emptyList());
//        when(assessmentService.getAssessmentById(assessmentId)).thenReturn(testAssessment);
//        mockMvc.perform(post("/api/courses/{courseId}/projects/{projectId}" +
//                        "/submissions/{submissionId}/{assessmentId}/issues",
//                courseId, projectId, submissionId, assessmentId)
//                .contentType("application/json")
//                .content(objectMapper.writeValueAsString(testIssue))
//                .with(csrf()))
//                .andExpect(status().isConflict());
//    }

    @WithMockUser(username=userId)
    @Test
    public void createIssueCreatorNotFound() throws Exception {
        ObjectNode testJsonIssue = objectMapper.createObjectNode();
        String addresseeId = "45";
        String targetName = "Test";
        String targetType = "TestType";
        String subject = "Testing";
        String description = "This is a test description.";
        ProjectRole role = new ProjectRole(testProject, new Role(), Collections.emptyList());
        Grader addressee = new Grader(testProject, addresseeId, "Jane Doe", role);
        UUID targetUUID = new UUID(2, 1);
        UUID referenceUUID = new UUID(1, 2);

        testJsonIssue.put("target", assessmentUuid.toString());
        testJsonIssue.put("targetName", targetName);
        testJsonIssue.put("targetType", targetType);
        testJsonIssue.put("reference", referenceUUID.toString());
        testJsonIssue.put("subject", subject);
        testJsonIssue.put("description", description);
        testJsonIssue.put("addressee", addressee.getUserId());

        when(projectService.getProjectById(anyString(), eq(projectId))).thenReturn(testProject);
        when(submissionService.findSubmissionById(anyString())).thenReturn(new Submission());
        when(assessmentService.getAssessmentBySubmission(any(Submission.class)))
                .thenReturn(List.of(testAssessment));
        when(assessmentService.getAssessmentById(assessmentId)).thenReturn(testAssessment);
        when(graderService.getGraderFromGraderId(userId, testProject)).thenReturn(null);
        mockMvc.perform(post("/api/courses/{courseId}/projects/{projectId}" +
                        "/submissions/{submissionId}/{assessmentId}/issues",
                courseId, projectId, submissionId, assessmentId)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(testJsonIssue))
                .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @WithMockUser(username=userId)
    @Test
    public void createdIssueAdded() throws Exception {
        String addresseeId = "45";
        String targetName = "Test";
        String targetType = "TestType";
        String subject = "Testing";
        String description = "This is a test description.";
        ProjectRole role = new ProjectRole(testProject, new Role(), Collections.emptyList());
        Grader creator = new Grader(testProject, userId, "John Doe", role);
        Grader addressee = new Grader(testProject, addresseeId, "Jane Doe", role);
        UUID targetUUID = new UUID(2, 1);
        UUID referenceUUID = new UUID(1, 2);

        Issue reference = new Issue(assessmentUuid, targetUUID, targetType, targetName, null, subject, description,
                creator, "unresolved", addressee);
        reference.setId(new UUID(2, 3));
        Issue testIssue = new Issue(assessmentUuid, targetUUID, targetType, targetName, reference, subject, description,
                creator, "unresolved", addressee);
        testIssue.setId(new UUID(2, 2));

        ArrayNode expectedNode = objectMapper.createArrayNode();
        expectedNode.add(testIssue.convertToJson());
        String expected = expectedNode.toString();

        when(projectService.getProjectById(anyString(), eq(projectId))).thenReturn(testProject);
        when(submissionService.findSubmissionById(anyString())).thenReturn(new Submission());
        when(assessmentService.getAssessmentBySubmission(any(Submission.class)))
                .thenReturn(List.of(testAssessment));
        when(assessmentService.getAssessmentById(assessmentId)).thenReturn(testAssessment);
        when(graderService.getGraderFromGraderId(userId, testProject)).thenReturn(creator);
        when(graderService.getGraderFromGraderId(addresseeId, testProject)).thenReturn(addressee);
        when(issueService.findById(referenceUUID)).thenReturn(reference);
        when(issueService.findIssuesByAssessment(assessmentUuid)).thenReturn(List.of(testIssue));

        mockMvc.perform(post("/api/courses/{courseId}/projects/{projectId}" +
                        "/submissions/{submissionId}/{assessmentId}/issues",
                courseId, projectId, submissionId, assessmentId)
                .contentType("application/json")
                .content(testIssue.convertToJson().toString())
                .with(csrf()))
                .andExpect(content().json(expected));
    }

    @WithMockUser
    @Test
    public void resolveIssueNotFound() throws Exception {
        Issue testIssue = new Issue();
        when(projectService.getProjectById(anyString(), eq(projectId))).thenReturn(testProject);
        when(submissionService.findSubmissionById(anyString())).thenReturn(new Submission());
        when(assessmentService.getAssessmentBySubmission(any(Submission.class)))
                .thenReturn(Collections.emptyList());
        when(assessmentService.getAssessmentById(assessmentId)).thenReturn(null);
        mockMvc.perform(post("/api/courses/{courseId}/projects/{projectId}" +
                        "/submissions/{submissionId}/{assessmentId}/issues/resolve",
                courseId, projectId, submissionId, assessmentId)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(testIssue))
                .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @WithMockUser
    @Test
    public void resolveIssueConflict() throws Exception {
        Issue testIssue = new Issue();
        when(projectService.getProjectById(anyString(), eq(projectId))).thenReturn(testProject);
        when(submissionService.findSubmissionById(anyString())).thenReturn(new Submission());
        when(assessmentService.getAssessmentBySubmission(any(Submission.class)))
                .thenReturn(Collections.emptyList());
        when(assessmentService.getAssessmentById(assessmentId)).thenReturn(testAssessment);
        mockMvc.perform(post("/api/courses/{courseId}/projects/{projectId}" +
                        "/submissions/{submissionId}/{assessmentId}/issues/resolve",
                courseId, projectId, submissionId, assessmentId)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(testIssue))
                .with(csrf()))
                .andExpect(status().isConflict());
    }

    @WithMockUser(username=userId)
    @Test
    public void resolveIssueResolved() throws Exception {
        String addresseeId = "45";
        String targetName = "Test";
        String targetType = "TestType";
        String subject = "Testing";
        String description = "This is a test description.";
        ProjectRole role = new ProjectRole(testProject, new Role(), Collections.emptyList());
        Grader creator = new Grader(testProject, userId, "John Doe", role);
        Grader addressee = new Grader(testProject, addresseeId, "Jane Doe", role);
        UUID targetUUID = new UUID(2, 1);
        UUID referenceUUID = new UUID(1, 2);
        String solution = "A solution.";
        UUID testIssueUUID= new UUID(2, 3);

        Issue reference = new Issue(assessmentUuid, targetUUID, targetType, targetName, null, subject, description,
                creator, "unresolved", addressee);
        reference.setId(testIssueUUID);
        Issue testIssue = new Issue(assessmentUuid, targetUUID, targetType, targetName, reference, subject, description,
                creator, "unresolved", addressee);
        testIssue.setId(new UUID(2, 2));
        Issue solutionIssue = new Issue();
        solutionIssue.setSolution(solution);
        solutionIssue.setId(testIssueUUID);

        ArrayNode expectedNode = objectMapper.createArrayNode();
        ObjectNode expectedIssue = (ObjectNode) testIssue.convertToJson();
        expectedIssue.put("status", "resolved");
        expectedIssue.put("solution", solution);
        expectedNode.add(expectedIssue);
        String expected = expectedNode.toString();

        when(projectService.getProjectById(anyString(), eq(projectId))).thenReturn(testProject);
        when(submissionService.findSubmissionById(anyString())).thenReturn(new Submission());
        when(assessmentService.getAssessmentBySubmission(any(Submission.class)))
                .thenReturn(List.of(testAssessment));
        when(assessmentService.getAssessmentById(assessmentId)).thenReturn(testAssessment);
        when(issueService.findById(testIssueUUID)).thenReturn(testIssue);
        when(issueService.findIssuesByAssessment(assessmentUuid)).thenReturn(List.of(testIssue));

        mockMvc.perform(post("/api/courses/{courseId}/projects/{projectId}" +
                        "/submissions/{submissionId}/{assessmentId}/issues/resolve",
                courseId, projectId, submissionId, assessmentId)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(solutionIssue))
                .with(csrf()))
                .andExpect(content().json(expected));
    }

    @WithMockUser
    @Test
    public void getIssuesNotFound() throws Exception {
        when(projectService.getProjectById(anyString(), eq(projectId))).thenReturn(testProject);
        when(submissionService.findSubmissionById(anyString())).thenReturn(new Submission());
        when(assessmentService.getAssessmentBySubmission(any(Submission.class)))
                .thenReturn(Collections.emptyList());
        when(assessmentService.getAssessmentById(assessmentId)).thenReturn(null);
        mockMvc.perform(get("/api/courses/{courseId}/projects/{projectId}" +
                        "/submissions/{submissionId}/{assessmentId}/issues",
                courseId, projectId, submissionId, assessmentId))
                .andExpect(status().isNotFound());
    }

    @WithMockUser
    @Test
    public void getIssuesConflict() throws Exception {
        when(projectService.getProjectById(anyString(), eq(projectId))).thenReturn(testProject);
        when(submissionService.findSubmissionById(anyString())).thenReturn(new Submission());
        when(assessmentService.getAssessmentBySubmission(any(Submission.class)))
                .thenReturn(Collections.emptyList());
        when(assessmentService.getAssessmentById(assessmentId)).thenReturn(testAssessment);
        mockMvc.perform(get("/api/courses/{courseId}/projects/{projectId}" +
                        "/submissions/{submissionId}/{assessmentId}/issues",
                courseId, projectId, submissionId, assessmentId))
                .andExpect(status().isConflict());
    }

    @WithMockUser
    @Test
    public void getIssuesOk() throws Exception {
        String addresseeId = "45";
        String targetName = "Test";
        String targetType = "TestType";
        String subject = "Testing";
        String description = "This is a test description.";
        ProjectRole role = new ProjectRole(testProject, new Role(), Collections.emptyList());
        Grader creator = new Grader(testProject, userId, "John Doe", role);
        Grader addressee = new Grader(testProject, addresseeId, "Jane Doe", role);
        UUID targetUUID = new UUID(2, 1);
        UUID referenceUUID = new UUID(1, 2);

        Issue reference = new Issue(assessmentUuid, targetUUID, targetType, targetName, null, subject, description,
                creator, "unresolved", addressee);
        reference.setId(new UUID(2, 3));
        Issue testIssue = new Issue(assessmentUuid, targetUUID, targetType, targetName, reference, subject, description,
                creator, "unresolved", addressee);
        testIssue.setId(new UUID(2, 2));

        ArrayNode expectedNode = objectMapper.createArrayNode();
        expectedNode.add(testIssue.convertToJson());
        String expected = expectedNode.toString();

        when(projectService.getProjectById(anyString(), eq(projectId))).thenReturn(testProject);
        when(submissionService.findSubmissionById(anyString())).thenReturn(new Submission());
        when(assessmentService.getAssessmentBySubmission(any(Submission.class)))
                .thenReturn(List.of(testAssessment));
        when(assessmentService.getAssessmentById(assessmentId)).thenReturn(testAssessment);
        when(issueService.findIssuesByAssessment(assessmentUuid)).thenReturn(List.of(testIssue));
        mockMvc.perform(get("/api/courses/{courseId}/projects/{projectId}" +
                        "/submissions/{submissionId}/{assessmentId}/issues",
                courseId, projectId, submissionId, assessmentId)
                .contentType("application/json"))
                .andExpect(content().json(expected));
    }
}
