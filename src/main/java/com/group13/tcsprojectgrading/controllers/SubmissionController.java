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
import com.group13.tcsprojectgrading.models.*;
import com.group13.tcsprojectgrading.models.rubric.Element;
import com.group13.tcsprojectgrading.models.rubric.Rubric;
import com.group13.tcsprojectgrading.services.*;
import com.group13.tcsprojectgrading.services.rubric.RubricService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.*;

import static com.group13.tcsprojectgrading.controllers.Utils.groupPages;
import static com.group13.tcsprojectgrading.models.Submission.createFlagsArrayNode;

@RestController
@RequestMapping("/api/courses/{courseId}/projects/{projectId}/submissions")
public class SubmissionController {

    private final CanvasApi canvasApi;
    private final ProjectService projectService;
    private final SubmissionService submissionService;
    private final GraderService graderService;
    private final FlagService flagService;
    private final RubricService rubricService;
    private final AssessmentLinkerService assessmentLinkerService;
    private final GradingService gradingService;

    @Autowired
    public SubmissionController(CanvasApi canvasApi, ProjectService projectService, SubmissionService submissionService, GraderService graderService, FlagService flagService, RubricService rubricService, AssessmentLinkerService assessmentLinkerService) {
    public SubmissionController(CanvasApi canvasApi, ProjectService projectService, SubmissionService submissionService,
                                GraderService graderService, GradingService gradingService) {
        this.canvasApi = canvasApi;
        this.projectService = projectService;
        this.submissionService = submissionService;
        this.graderService = graderService;
        this.flagService = flagService;
        this.rubricService = rubricService;
        this.assessmentLinkerService = assessmentLinkerService;
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
        String courseString = this.canvasApi.getCanvasCoursesApi().getUserCourse(courseId);

        ObjectMapper objectMapper = new ObjectMapper();

        Project project = projectService.getProjectById(courseId, projectId);
        if (project == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "entity not found"
            );
        }

        Rubric rubric = rubricService.getRubricById(projectId);

        Grader grader = graderService.getGraderFromGraderId(principal.getName(), project);
        if (grader == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "grader not found"
            );
        }


        //sync submissions <-> canvas submissions

        //sync submissions <-> canvas submissions

        List<Submission> submissions =  submissionService.findSubmissionWithProject(project);
        ObjectNode resultNode = objectMapper.createObjectNode();

        resultNode.set("project", project.convertToJson());
        resultNode.set("course", objectMapper.readTree(courseString));

        List<Flag> yourFlags = flagService.findFlagsWithGrader(grader);
        ArrayNode yourFlagsArrayNode = createFlagsArrayNode(yourFlags);

        ObjectNode userNode = objectMapper.createObjectNode();
        userNode.put("id", grader.getUserId());
        userNode.put("name", grader.getUserId());
        userNode.set("flags", yourFlagsArrayNode);

        resultNode.set("user", userNode);

        ArrayNode arrayNode = objectMapper.createArrayNode();

        List<Integer> progresses = new ArrayList<>();
        System.out.println(submissions.size());
        for (Submission submission : submissions) {
            List<AssessmentLinker> linkers = assessmentLinkerService.findAssessmentLinkersForSubmission(submission);
            arrayNode.add(submission.convertToJson(linkers));
        }

        //TODO copy this to stats
        int finalProgress = (int) Arrays.stream(progresses.stream().mapToInt(i -> i).toArray()).average().orElse(0);

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
        String courseString = this.canvasApi.getCanvasCoursesApi().getUserCourse(courseId);

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode resultNode = objectMapper.createObjectNode();

        resultNode.set("course", objectMapper.readTree(courseString));

        Project project = projectService.getProjectById(courseId, projectId);
        if (project == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "project not found"
            );
        }
        resultNode.set("project", project.convertToJson());

//        Task task = taskService.findTaskByTaskId(taskId, Boolean.parseBoolean(queryParameters.get("is_group")), project);
        Submission submission = submissionService.findSubmissionById(id);
        if (submission == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "task not found"
            );
        }
        if (!submission.getProject().equals(project)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "submission is not in project"
            );
        }

        List<AssessmentLinker> linkers = assessmentLinkerService.findAssessmentLinkersForSubmission(submission);

        ObjectNode node = (ObjectNode) submission.convertToJsonWithDetails(linkers);
//        node.put("stringId", String.format("%s/%s", (submission.getGroupId() != null)? submission.getGroupId():"individual", submission.getId()));
//        node.put("id", submission.getId().toString());
//        node.put("isGroup", (submission.getGroupId() != null));
//        node.set("workflow_state", submissionCanvas.get("workflow_state"));
//        node.set("attempt", submissionCanvas.get("attempt"));
//        node.set("submitted_at", submissionCanvas.get("submitted_at"));
//        node.set("attachments", submissionCanvas.get("attachments"));
//        node.set("submission_comments", submissionCanvas.get("submission_comments"));
//        node.put("name", submission.getName());

        if (submission.getGrader() != null) {
            ObjectNode graderNode = objectMapper.createObjectNode();
            graderNode.put("name", submission.getGrader().getName());
            graderNode.put("id", submission.getGrader().getUserId());
            node.set("grader", graderNode);
        }
        Rubric rubric = rubricService.getRubricById(projectId);

        int progress = 0;
        if (rubric != null) {
//            SubmissionAssessment assessment = gradingService.getAssessmentByProjectIdAndUserId(projectId, submission.getId().toString());
//            if (assessment != null) {
//                progress = (int) Math.round(submissionProgress(assessment, rubric));
//            }
        }
        node.put("progress", progress);

        List<Flag> flags = (List<Flag>) submission.getFlags();
        ArrayNode submissionFlags = createFlagsArrayNode(flags);
        node.set("flags", submissionFlags);

        resultNode.set("submission", node);
        Grader grader = graderService.getGraderFromGraderId(principal.getName(), project);
        if (grader != null) {
            JsonNode graderJson = grader.getGraderJson();
            List<Flag> yourFlags = flagService.findFlagsWithGrader(grader);
            ArrayNode yourFlagsArrayNode = createFlagsArrayNode(yourFlags);
            ((ObjectNode) graderJson).set("flags", yourFlagsArrayNode);
            resultNode.set("user", graderJson);
        }
        return resultNode;
    }

//    @PostMapping(value = "/{id}/flag")
//    protected JsonNode addFlag(@PathVariable String courseId,
//                                         @PathVariable String projectId,
//                                         @PathVariable String id,
//                                         @RequestBody ObjectNode flag,
//                                         Principal principal
////                                   @RequestParam Map<String, String> queryParameters
//    ) throws JsonProcessingException {
//        String projectResponse = this.canvasApi.getCanvasCoursesApi().getCourseProject(courseId, projectId);
//        String courseString = this.canvasApi.getCanvasCoursesApi().getUserCourse(courseId);
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        ObjectNode resultNode = objectMapper.createObjectNode();
//
//        resultNode.set("course", objectMapper.readTree(courseString));
//        resultNode.set("project", objectMapper.readTree(projectResponse));
//
//        Project project = projectService.getProjectById(courseId, projectId);
//        if (project == null) {
//            throw new ResponseStatusException(
//                    HttpStatus.NOT_FOUND, "project not found"
//            );
//        }
//
////        Task task = taskService.findTaskByTaskId(taskId, Boolean.parseBoolean(queryParameters.get("is_group")), project);
//        Submission submission = submissionService.findSubmissionById(id, project);
//        if (submission == null) {
//            throw new ResponseStatusException(
//                    HttpStatus.NOT_FOUND, "task not found"
//            );
//        }
//
//        Grader grader = graderService.getGraderFromGraderId(principal.getName(), project);
//        if (grader == null) {
//            throw new ResponseStatusException(
//                    HttpStatus.NOT_FOUND, "task not found"
//            );
//        }
//        Flag flag1 = flagService.findFlagWithNameAndGrader(flag.get("name").asText(), grader);
//        if (flag1 != null) {
//            if (!submission.getFlags().contains(flag1)) submission.getFlags().add(flag1);
//            Submission submission1 = submissionService.saveFlags(submission);
//            return createFlagsArrayNode((List<Flag>) submission1.getFlags());
//        }
//        return null;
//    }
//
//    @PostMapping(value = "/{id}/flag/create")
//    protected JsonNode createFlag(@PathVariable String courseId,
//                               @PathVariable String projectId,
//                               @PathVariable String id,
//                               @RequestBody ObjectNode flag,
//                               Principal principal
////                                   @RequestParam Map<String, String> queryParameters
//    ) throws JsonProcessingException {
//        String projectResponse = this.canvasApi.getCanvasCoursesApi().getCourseProject(courseId, projectId);
//        String courseString = this.canvasApi.getCanvasCoursesApi().getUserCourse(courseId);
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        ObjectNode resultNode = objectMapper.createObjectNode();
//
//        resultNode.set("course", objectMapper.readTree(courseString));
//        resultNode.set("project", objectMapper.readTree(projectResponse));
//
//        Project project = projectService.getProjectById(courseId, projectId);
//        if (project == null) {
//            throw new ResponseStatusException(
//                    HttpStatus.NOT_FOUND, "project not found"
//            );
//        }
//
////        Task task = taskService.findTaskByTaskId(taskId, Boolean.parseBoolean(queryParameters.get("is_group")), project);
//        Submission submission = submissionService.findSubmissionById(id, project);
//        if (submission == null) {
//            throw new ResponseStatusException(
//                    HttpStatus.NOT_FOUND, "task not found"
//            );
//        }
//
//        Grader grader = graderService.getGraderFromGraderId(principal.getName(), project);
//        if (grader == null) {
//            throw new ResponseStatusException(
//                    HttpStatus.NOT_FOUND, "task not found"
//            );
//        }
//        Flag flag1 = flagService.findFlagWithNameAndGrader(flag.get("name").asText(), grader);
//        if (flag1 == null) {
//
//            flagService.saveNewFlag(new
//                    Flag(flag.get("name").asText(),
//                    flag.get("description").asText(),
//                    flag.get("variant").asText(),
//                    grader));
//
//            List<Flag> flags = flagService.findFlagsWithGrader(grader);
//            ArrayNode yourFlagsArrayNode = createFlagsArrayNode(flags);
//            ObjectNode objectNode = objectMapper.createObjectNode();
//            objectNode.set("data", yourFlagsArrayNode);
//            return objectNode;
//        } else {
//            ObjectNode objectNode = objectMapper.createObjectNode();
//            objectNode.put("error", "flag already exists");
//            return objectNode;
//        }
//    }
//
//    @DeleteMapping(value = "/{id}/flag/{flagId}")
//    protected JsonNode deleteFlag(@PathVariable String courseId,
//                               @PathVariable String projectId,
//                               @PathVariable String id,
//                               @PathVariable String flagId,
//                               Principal principal
////                                   @RequestParam Map<String, String> queryParameters
//    ) throws JsonProcessingException {
//
//        ObjectMapper objectMapper = new ObjectMapper();
//
//        Project project = projectService.getProjectById(courseId, projectId);
//        if (project == null) {
//            throw new ResponseStatusException(
//                    HttpStatus.NOT_FOUND, "project not found"
//            );
//        }
//
////        Task task = taskService.findTaskByTaskId(taskId, Boolean.parseBoolean(queryParameters.get("is_group")), project);
//        Submission submission = submissionService.findSubmissionById(id, project);
//        if (submission == null) {
//            throw new ResponseStatusException(
//                    HttpStatus.NOT_FOUND, "task not found"
//            );
//        }
//
//        Grader grader = graderService.getGraderFromGraderId(principal.getName(), project);
//        if (grader == null) {
//            throw new ResponseStatusException(
//                    HttpStatus.NOT_FOUND, "task not found"
//            );
//        }
//        Flag flag1 = flagService.findFlagWithId(Long.parseLong(flagId));
//        if (flag1 != null) {
//            submission.getFlags().remove(flag1);
//            Submission submission1 = submissionService.saveFlags(submission);
//            return createFlagsArrayNode((List<Flag>) submission1.getFlags());
//        }
//
//        return null;
//    }
//
////    private double submissionProgress(SubmissionAssessment assessment, Rubric rubric) {
////        List<Element> criteria = rubric.fetchAllCriteria();
////        return assessment.getGradedCriteria(criteria).size()*1.0/criteria.size()*100;
////    }

}
