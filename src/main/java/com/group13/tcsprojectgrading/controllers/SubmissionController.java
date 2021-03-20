package com.group13.tcsprojectgrading.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.group13.tcsprojectgrading.canvas.api.CanvasApi;
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

    @Autowired
    public SubmissionController(CanvasApi canvasApi, ProjectService projectService, SubmissionService submissionService, GraderService graderService, FlagService flagService, RubricService rubricService, AssessmentLinkerService assessmentLinkerService) {
        this.canvasApi = canvasApi;
        this.projectService = projectService;
        this.submissionService = submissionService;
        this.graderService = graderService;
        this.flagService = flagService;
        this.rubricService = rubricService;
        this.assessmentLinkerService = assessmentLinkerService;
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
