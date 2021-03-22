package com.group13.tcsprojectgrading.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.group13.tcsprojectgrading.canvas.api.CanvasApi;
import com.group13.tcsprojectgrading.models.*;
import com.group13.tcsprojectgrading.models.rubric.Rubric;
import com.group13.tcsprojectgrading.services.*;
import com.group13.tcsprojectgrading.services.grading.AssessmentService;
import com.group13.tcsprojectgrading.services.rubric.RubricService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

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
    private final SubmissionDetailsService submissionDetailsService;
    private final AssessmentService assessmentService;
    private final IssueService issueService;
    private final ParticipantService participantService;

    @Autowired
    public SubmissionController(CanvasApi canvasApi, ProjectService projectService, SubmissionService submissionService, GraderService graderService, FlagService flagService, RubricService rubricService, AssessmentLinkerService assessmentLinkerService, SubmissionDetailsService submissionDetailsService, AssessmentService assessmentService, IssueService issueService, ParticipantService participantService) {
        this.canvasApi = canvasApi;
        this.projectService = projectService;
        this.submissionService = submissionService;
        this.graderService = graderService;
        this.flagService = flagService;
        this.rubricService = rubricService;
        this.assessmentLinkerService = assessmentLinkerService;
        this.submissionDetailsService = submissionDetailsService;
        this.assessmentService = assessmentService;
        this.issueService = issueService;
        this.participantService = participantService;
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

        List<Submission> submissions = submissionService.findSubmissionWithProject(project);
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
            List<Assessment> assessmentList = assessmentService.getAssessmentBySubmission(submission);
            List<Issue> issues = new ArrayList<>();
            for (Assessment assessment : assessmentList) {
                issues.addAll(
                        issueService.findIssuesByAssessment(assessment.getId())
                                .stream()
                                .filter(issue -> issue.getStatus().equals("unresolved"))
                                .collect(Collectors.toList())
                );
            }

            arrayNode.add(submission.convertToJson(linkers, issues));
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
        List<SubmissionComment> comments = submissionDetailsService.getComments(submission);
        List<SubmissionAttachment> attachments = submissionDetailsService.getAttachments(submission);

        Map<UUID, List<Issue>> issueMap = new HashMap<>();
        List<Assessment> assessmentList = assessmentService.getAssessmentBySubmission(submission);
        for (Assessment assessment : assessmentList) {
            issueMap.put(
                    assessment.getId(),
                    issueService.findIssuesByAssessment(assessment.getId())
                            .stream()
                            .filter(issue -> issue.getStatus().equals("unresolved"))
                            .collect(Collectors.toList())
            );
        }

        ObjectNode node = (ObjectNode) submission.convertToJsonWithDetails(linkers, attachments, comments, issueMap);
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

    @PostMapping(value = "/{submissionId}/assessmentManagement")
    protected ArrayNode createNewAssessment(@PathVariable String courseId,
                                         @PathVariable String projectId,
                                         @PathVariable String submissionId,
                                         @RequestBody JsonNode object,
                                         Principal principal
//                                   @RequestParam Map<String, String> queryParameters
    ) throws JsonProcessingException {
        Project project = projectService.getProjectById(courseId, projectId);
        if (project == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "project not found"
            );
        }
        Submission submission = submissionService.findSubmissionById(submissionId);
        if (submission == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "task not found"
            );
        }

        switch (object.get("action").asText()) {
            case "new": {
                AssessmentLinker linker = assessmentLinkerService.addNewNullAssessment(new AssessmentLinker(
                        submission,
                        null,
                        UUID.randomUUID()
                ));
                assessmentService.saveAssessment(linker);
                break;
            }
            case "clone": {
                String source = object.get("source").asText();
                Assessment sourceAssignment = assessmentService.getAssessmentById(source);
                Assessment newAssignment = assessmentService.saveAssessment(new Assessment(UUID.randomUUID(), sourceAssignment.getGrades()));
                AssessmentLinker linker = assessmentLinkerService.addNewNullAssessment(new AssessmentLinker(
                        submission,
                        null,
                        newAssignment.getId()
                ));
                break;
            }
            case "move": {
                String source = object.get("source").asText();
                String destination = object.get("destination").asText();
                String participantId = object.get("participantId").asText();
                Assessment sourceAssignment = assessmentService.getAssessmentById(source);
                Assessment destinationAssignment = assessmentService.getAssessmentById(destination);
                Participant participant = participantService.findParticipantWithId(participantId, project);
                List<AssessmentLinker> linkerSrcList = assessmentLinkerService.findAssessmentLinkersForAssessmentId(source);
                List<AssessmentLinker> linkerDesList = assessmentLinkerService.findAssessmentLinkersForAssessmentId(destination);
                AssessmentLinker linkerSrc = null;
                for(AssessmentLinker linker : linkerSrcList) {
                    if (linker.getParticipant().getId().equals(participant.getId())) {
                        linkerSrc = linker;
                        break;
                    }
                }

                if (sourceAssignment == null ||
                        destinationAssignment == null ||
                        participant == null ||
                        linkerSrc == null ||
                        linkerSrcList.size() == 0 ||
                        linkerDesList.size() == 0) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found info");
                }



                if (linkerDesList.size() == 1 && linkerDesList.get(0).getParticipant() == null) {
                    linkerDesList.get(0).setParticipant(participant);
                    assessmentLinkerService.saveInfoAssessment(linkerDesList.get(0));
                    assessmentLinkerService.deleteAssessmentLinker(linkerSrc);
                } else {
                    linkerSrc.setAssessmentId(destinationAssignment.getId());
                    assessmentLinkerService.saveInfoAssessment(linkerSrc);
                }
                if (linkerSrcList.size() == 1) {
                    AssessmentLinker linker = assessmentLinkerService.addNewNullAssessment(new AssessmentLinker(
                            submission,
                            null,
                            sourceAssignment.getId()
                    ));
                    if (linker == null)
                        throw new ResponseStatusException(HttpStatus.CONFLICT, "conflict with assessments");
                }

                break;
            }
            case "delete": {
                String source = object.get("source").asText();
                List<AssessmentLinker> linkers = assessmentLinkerService.findAssessmentLinkersForAssessmentId(source);
                for (AssessmentLinker linker : linkers) {
                    if (linker.getParticipant() != null) {
                        throw new ResponseStatusException(HttpStatus.CONFLICT, "cant remove assessment that has participants");
                    }
                }
                for (AssessmentLinker linker : linkers) {
                    assessmentLinkerService.deleteAssessmentLinker(linker);
                }
                Assessment assessment = assessmentService.findAssessment(source);
                if (assessment != null) {
                    assessmentService.deleteAssessment(assessment);
                } else {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "no assessment found");
                }

                break;
            }
        }

        List<AssessmentLinker> assessmentLinkers = assessmentLinkerService.findAssessmentLinkersForSubmission(submission);
        Map<UUID, List<Issue>> issueMap = new HashMap<>();
        List<Assessment> assessmentList = assessmentService.getAssessmentBySubmission(submission);
        for (Assessment assessment : assessmentList) {
            issueMap.put(
                    assessment.getId(),
                    issueService.findIssuesByAssessment(assessment.getId())
                            .stream()
                            .filter(issue -> issue.getStatus().equals("unresolved"))
                            .collect(Collectors.toList())
            );
        }
        return (ArrayNode) submission.convertToJsonWithDetails(assessmentLinkers, null, null, issueMap).get("assessments");

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