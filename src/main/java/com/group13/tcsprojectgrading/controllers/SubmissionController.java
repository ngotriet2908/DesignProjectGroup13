package com.group13.tcsprojectgrading.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.group13.tcsprojectgrading.canvas.api.CanvasApi;
import com.group13.tcsprojectgrading.models.Grader;
import com.group13.tcsprojectgrading.models.Project;
import com.group13.tcsprojectgrading.models.Submission;
import com.group13.tcsprojectgrading.models.grading.SubmissionAssessment;
import com.group13.tcsprojectgrading.services.GraderService;
import com.group13.tcsprojectgrading.services.ProjectService;
import com.group13.tcsprojectgrading.services.SubmissionService;
import com.group13.tcsprojectgrading.services.grading.GradingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.*;

import static com.group13.tcsprojectgrading.controllers.Utils.groupPages;

@RestController
@RequestMapping("/api/courses/{courseId}/projects/{projectId}/submissions")
public class SubmissionController {

    private final CanvasApi canvasApi;
    private final ProjectService projectService;
    private final SubmissionService submissionService;
    private final GraderService graderService;
    private final GradingService gradingService;

    @Autowired
    public SubmissionController(CanvasApi canvasApi, ProjectService projectService, SubmissionService submissionService,
                                GraderService graderService, GradingService gradingService) {
        this.canvasApi = canvasApi;
        this.projectService = projectService;
        this.submissionService = submissionService;
        this.graderService = graderService;
        this.gradingService = gradingService;
    }

    @GetMapping(value = "/syncCanvas")
    protected void syncWithCanvas(@PathVariable String courseId,
                                      @PathVariable String projectId,
                                      Principal principal) throws JsonProcessingException {

        Project project = projectService.getProjectById(courseId, projectId);
        if (project == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "project not found"
            );
        }

        String projectResponse = this.canvasApi.getCanvasCoursesApi().getCourseProject(courseId, projectId);
        ObjectMapper objectMapper = new ObjectMapper();
        List<Grader> graders = graderService.getGraderFromProject(project);
        List<String> submissionsString = this.canvasApi.getCanvasCoursesApi().getSubmissionsInfo(courseId, Long.parseLong(projectId));
        List<String> studentsString = this.canvasApi.getCanvasCoursesApi().getCourseStudents(courseId);
        JsonNode projectJson = objectMapper.readTree(projectResponse);
        String projectCatId = projectJson.get("group_category_id").asText();
        Map<String, String> groupIdToNameMap = new HashMap<>();
        Map<String, String> userIdToGroupIdMap = new HashMap<>();

        if (!projectCatId.equals("null")) {
            ArrayNode groupsString = groupPages(objectMapper, canvasApi.getCanvasCoursesApi().getCourseGroupCategoryGroup(projectCatId));
//            ArrayNode groupsString1 = groupPages(objectMapper, canvasApi.getCanvasCoursesApi().getCourseGroups(courseId));

            for (Iterator<JsonNode> it = groupsString.elements(); it.hasNext(); ) {
                JsonNode group = it.next();
                if (group.get("members_count").asInt(0) <= 0) continue;
                ArrayNode memberships = groupPages(objectMapper, this.canvasApi.getCanvasCoursesApi().getGroupMemberships(group.get("id").asText()));
                groupIdToNameMap.put(group.get("id").asText(), group.get("name").asText());

                for (Iterator<JsonNode> iter = memberships.elements(); iter.hasNext(); ) {
                    JsonNode membership = iter.next();
                    userIdToGroupIdMap.put(membership.get("user_id").asText(), membership.get("group_id").asText());
                }
            }
        }
        ArrayNode studentArray = groupPages(objectMapper, studentsString);
        Map<String, JsonNode> studentMap = new HashMap<>();
        for (Iterator<JsonNode> it = studentArray.elements(); it.hasNext(); ) {
            JsonNode jsonNode = it.next();
            studentMap.put(jsonNode.get("id").asText(), jsonNode);
        }

        ArrayNode submissionArray = groupPages(objectMapper, submissionsString);
        List<String> validSubmissionId = new ArrayList<>();
        List<Submission> validSubmissions = new ArrayList<>();

        for (Iterator<JsonNode> it = submissionArray.elements(); it.hasNext(); ) {
            JsonNode jsonNode = it.next();

            if (jsonNode.get("workflow_state").asText().equals("unsubmitted")) continue;
            if (!studentMap.containsKey(jsonNode.get("user_id").asText())) continue;

            boolean isGroup = userIdToGroupIdMap.containsKey(jsonNode.get("user_id").asText());
            String user_id = jsonNode.get("user_id").asText();
            String group_id = (isGroup)? userIdToGroupIdMap.get(jsonNode.get("user_id").asText()): null;
            String name = (isGroup)? groupIdToNameMap.get(userIdToGroupIdMap.get(jsonNode.get("user_id").asText())): studentMap.get(user_id).get("name").asText();
            Submission submission = new Submission(
                    user_id,
                    project,
                    name,
                    group_id
            );

            validSubmissionId.add(submission.getId());
            validSubmissions.add(submission);
        }

        List<Submission> submissions = submissionService.findSubmissionWithProject(project);
        for(Submission submission: submissions) {
            if (!validSubmissionId.contains(submission.getId())) {
                submissionService.deleteSubmission(submission);
            }
        }

        for(Submission submission: validSubmissions) {
            submissionService.addNewSubmission(submission);
        }

    }

    @GetMapping(value = "")
    protected JsonNode getSubmissions(@PathVariable String courseId, @PathVariable String projectId, Principal principal) throws JsonProcessingException {
        String projectResponse = this.canvasApi.getCanvasCoursesApi().getCourseProject(courseId, projectId);
        String courseString = this.canvasApi.getCanvasCoursesApi().getUserCourse(courseId);
        String userString = this.canvasApi.getCanvasUsersApi().getAccountWithId(principal.getName());

        ObjectMapper objectMapper = new ObjectMapper();

        Project project = projectService.getProjectById(courseId, projectId);
        if (project == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "entity not found"
            );
        }

        //sync submissions <-> canvas submissions

        //sync submissions <-> canvas submissions

        List<Submission> submissions =  submissionService.findSubmissionWithProject(project);
        ArrayNode arrayNode = objectMapper.createArrayNode();
        ObjectNode resultNode = objectMapper.createObjectNode();

        resultNode.set("project", objectMapper.readTree(projectResponse));
        resultNode.set("course", objectMapper.readTree(courseString));

        JsonNode userCanvasNode = objectMapper.readTree(userString);

        ObjectNode userNode = objectMapper.createObjectNode();
        userNode.put("id", userCanvasNode.get("id").asText());
        userNode.put("name", userCanvasNode.get("name").asText());

        resultNode.set("user", userNode);

        for (Submission submission : submissions) {
            ObjectNode node = objectMapper.createObjectNode();

            String submissionResponse = this.canvasApi.getCanvasCoursesApi().getSubmission(courseId, projectId, submission.getId());
            JsonNode submissionNode = objectMapper.readTree(submissionResponse);
            SubmissionAssessment assessment = gradingService.getAssessmentByProjectIdAndUserId(projectId, submission.getId());

            node.put("stringId", String.format("%s/%s", (submission.getGroupId() != null)? submission.getGroupId():"individual", submission.getId()));
            node.put("id", submission.getId());
            node.put("isGroup", (submission.getGroupId() != null));
//            node.put("name", submission.getName());
            // TODO delete this shit
            Random random = new Random();
            String stupidName = (random.nextInt(3) == 2)? "Ömer Şakar " + submission.getName(): submission.getName();
            node.put("name", stupidName);
            // TODO delete this shit
            if (assessment != null) node.put("progress", assessment.getProgress());
            else node.put("progress", 0);
            node.put("submittedAt", submissionNode.get("submitted_at").asText());
            node.put("attempt", submissionNode.get("attempt").asText());

            if (submission.getGrader() != null) {
                ObjectNode graderNode = objectMapper.createObjectNode();
                graderNode.put("id", submission.getGrader().getUserId());
                graderNode.put("name", submission.getGrader().getName());
                node.set("grader", graderNode);
            }
            arrayNode.add(node);
        }

        resultNode.set("submissions", arrayNode);

        return resultNode;
    }

    @GetMapping(value = "/{id}")
    protected JsonNode getSubmissionInfo(@PathVariable String courseId,
                                   @PathVariable String projectId,
                                   @PathVariable String id,
                                    Principal principal
//                                   @RequestParam Map<String, String> queryParameters
    ) throws JsonProcessingException {
        String projectResponse = this.canvasApi.getCanvasCoursesApi().getCourseProject(courseId, projectId);
        String courseString = this.canvasApi.getCanvasCoursesApi().getUserCourse(courseId);

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode resultNode = objectMapper.createObjectNode();

        resultNode.set("course", objectMapper.readTree(courseString));
        resultNode.set("project", objectMapper.readTree(projectResponse));

        Project project = projectService.getProjectById(courseId, projectId);
        if (project == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "project not found"
            );
        }

//        Task task = taskService.findTaskByTaskId(taskId, Boolean.parseBoolean(queryParameters.get("is_group")), project);
        Submission submission = submissionService.findSubmissionById(id, project);
        if (submission == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "task not found"
            );
        }

        String submissionResponse = this.canvasApi.getCanvasCoursesApi().getSubmission(courseId, projectId, submission.getId());
        JsonNode submissionCanvas = objectMapper.readTree(submissionResponse);

        SubmissionAssessment assessment = gradingService.getAssessmentByProjectIdAndUserId(projectId, submission.getId());
        ObjectNode node = objectMapper.createObjectNode();
        node.put("stringId", String.format("%s/%s", (submission.getGroupId() != null)? submission.getGroupId():"individual", submission.getId()));
        node.put("id", submission.getId());
        node.put("isGroup", (submission.getGroupId() != null));
        node.set("workflow_state", submissionCanvas.get("workflow_state"));
        node.set("attempt", submissionCanvas.get("attempt"));
        node.set("submitted_at", submissionCanvas.get("submitted_at"));
        node.set("attachments", submissionCanvas.get("attachments"));
        node.set("submission_comments", submissionCanvas.get("submission_comments"));
        node.put("name", submission.getName());

        if (assessment != null) node.put("progress", assessment.getProgress());
        else node.put("progress", 0);

        if (submission.getGrader() != null) {
            ObjectNode graderNode = objectMapper.createObjectNode();
            graderNode.put("name", submission.getGrader().getName());
            graderNode.put("id", submission.getGrader().getUserId());

            node.set("grader", graderNode);
        }

        if (submission.getGroupId() != null) {
            ArrayNode memberships = groupPages(objectMapper, this.canvasApi.getCanvasCoursesApi().getGroupUsers(submission.getGroupId()));
            node.set("members", memberships);
        }
        resultNode.set("submission", node);
        Grader grader = graderService.getGraderFromGraderId(principal.getName(), project);
        if (grader != null) {
            resultNode.set("user", grader.getGraderJson());
        }
        return resultNode;
    }
}