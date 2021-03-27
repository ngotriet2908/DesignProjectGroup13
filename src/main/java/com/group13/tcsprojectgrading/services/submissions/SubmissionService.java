package com.group13.tcsprojectgrading.services.submissions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.group13.tcsprojectgrading.models.*;
import com.group13.tcsprojectgrading.models.graders.Grader;
import com.group13.tcsprojectgrading.models.grading.Assessment;
import com.group13.tcsprojectgrading.models.grading.AssessmentLinker;
import com.group13.tcsprojectgrading.models.rubric.Rubric;
import com.group13.tcsprojectgrading.models.submissions.*;
import com.group13.tcsprojectgrading.repositories.ProjectRepository;
import com.group13.tcsprojectgrading.repositories.submissions.SubmissionRepository;
import com.group13.tcsprojectgrading.services.graders.GraderService;
import com.group13.tcsprojectgrading.services.grading.IssueService;
import com.group13.tcsprojectgrading.services.ParticipantService;
import com.group13.tcsprojectgrading.services.grading.AssessmentLinkerService;
import com.group13.tcsprojectgrading.services.grading.AssessmentService;
import com.group13.tcsprojectgrading.services.rubric.RubricService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

import static com.group13.tcsprojectgrading.models.permissions.PrivilegeEnum.*;
import static com.group13.tcsprojectgrading.models.submissions.Submission.createFlagsArrayNode;

@Service
public class SubmissionService {
    private final SubmissionRepository repository;
    private final ProjectRepository projectRepository;
    private final GraderService graderService;
    private final RubricService rubricService;
    private final FlagService flagService;
    private final AssessmentLinkerService assessmentLinkerService;
    private final IssueService issueService;
    private final AssessmentService assessmentService;
    private final SubmissionDetailsService submissionDetailsService;
    private final ParticipantService participantService;

    @Autowired
    public SubmissionService(SubmissionRepository repository, ProjectRepository projectRepository, GraderService graderService, RubricService rubricService, FlagService flagService, AssessmentLinkerService assessmentLinkerService, IssueService issueService, AssessmentService assessmentService, SubmissionDetailsService submissionDetailsService, ParticipantService participantService) {
        this.repository = repository;
        this.projectRepository = projectRepository;
        this.graderService = graderService;
        this.rubricService = rubricService;
        this.flagService = flagService;
        this.assessmentLinkerService = assessmentLinkerService;
        this.issueService = issueService;
        this.assessmentService = assessmentService;
        this.submissionDetailsService = submissionDetailsService;
        this.participantService = participantService;
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public Submission addNewSubmission(Project project, String userId, String groupId,
                                       String date, String name) {
        Submission currentSubmission = repository.findSubmissionByProjectAndUserIdAndGroupIdAndDate(
                project, userId, groupId, date);

        if (currentSubmission != null) {
            return null;
        } else {
            return repository.save(new Submission(date, userId, groupId, project, name));
        }
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public Submission saveFlags(Submission submission) {
        Submission currentSubmission = repository.findSubmissionByProjectAndUserIdAndGroupIdAndDate(
                submission.getProject(),
                submission.getUserId(),
                submission.getGroupId(),
                submission.getDate()
        );
        currentSubmission.setFlags(submission.getFlags());
        return repository.save(currentSubmission);
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public Submission saveGrader(Submission submission) {
        Submission currentSubmission = repository.findSubmissionByProjectAndUserIdAndGroupIdAndDate(
                submission.getProject(),
                submission.getUserId(),
                submission.getGroupId(),
                submission.getDate()
        );
        currentSubmission.setGrader(submission.getGrader());
        return repository.save(currentSubmission);
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public List<Submission> findSubmissionWithProject(Project project) {
        return repository.findSubmissionsByProject(project);
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public Submission findSubmissionById(String id) {
//        return repository.findById(new SubmissionId(user_id, project.getProjectCompositeKey())).orElse(null);
        return repository.getOne(UUID.fromString(id));
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public List<Submission> findSubmissionsForGrader(Project project, String graderId) {
        return repository.findSubmissionsByProjectAndGrader_UserId(project, graderId);
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public List<Submission> findSubmissionsByFlags(Flag flag) {
        return repository.findSubmissionsByFlags(flag);
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public List<Submission> findSubmissionsForGraderAll(String graderId) {
        return repository.findSubmissionsByGrader_UserId(graderId);
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public List<Submission> findSubmissionsForGraderCourse(String graderId, String courseId) {
        return repository.findSubmissionsByGrader_UserIdAndProject_CourseId(graderId, courseId);
    }
    @Transactional
    public ObjectNode getSubmission(String courseId, String projectId, String userId) {
        Project project = projectRepository.findById(new ProjectId(courseId, projectId)).orElse(null);
        if (project == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "entity not found"
            );
        }
        Rubric rubric = rubricService.getRubricById(projectId);

        Grader grader = graderService.getGraderFromGraderId(userId, project);
        if (grader == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "grader not found"
            );
        }

        ObjectMapper objectMapper = new ObjectMapper();
        List<Submission> submissions = findSubmissionWithProject(project);
        ObjectNode resultNode = objectMapper.createObjectNode();

        resultNode.set("project", project.convertToJson());

        List<Flag> yourFlags = flagService.findFlagsWithProject(project);
        ArrayNode yourFlagsArrayNode = createFlagsArrayNode(yourFlags);
        ((ObjectNode) resultNode.get("project")).set("flags", yourFlagsArrayNode);

        ObjectNode userNode = objectMapper.createObjectNode();
        userNode.put("id", grader.getUserId());
        userNode.put("name", grader.getUserId());

        resultNode.set("user", userNode);

        ArrayNode arrayNode = objectMapper.createArrayNode();

        List<Integer> progresses = new ArrayList<>();

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

            for (Iterator<JsonNode> it = arrayNode.elements(); it.hasNext(); ) {
                JsonNode submissionNode = it.next();
                int progress = 0;
                if (rubric != null) {

                    for (Iterator<JsonNode> it1 = submissionNode.get("assessments").elements(); it1.hasNext(); ) {
                        JsonNode assessmentNode = it1.next();
                        Assessment assessment = assessmentService.getAssessmentById(assessmentNode.get("id").asText());
                        int assessmentProgress = (int) (assessment.getGradedCount()*1.0/rubric.getCriterionCount()*100);
                        ((ObjectNode) assessmentNode).put("progress", assessmentProgress);
                        progress += assessmentProgress;
                    }
                    progress = (submissionNode.get("assessments").size() == 0)? 0 : (int) (progress * 1.0 / submissionNode.get("assessments").size());
                }
                ((ObjectNode) submissionNode).put("progress", progress);
            }


        }

        //TODO copy this to stats
        int finalProgress = (int) Arrays.stream(progresses.stream().mapToInt(i -> i).toArray()).average().orElse(0);

        resultNode.set("submissions", arrayNode);
        return resultNode;
    }

    @Transactional
    public ObjectNode getSubmissionInfo(String courseId, String projectId, String id, String userId, List<PrivilegeEnum> privileges) throws JsonProcessingException {
        Project project = projectRepository.findById(new ProjectId(courseId, projectId)).orElse(null);
        if (project == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "project not found"
            );
        }

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode resultNode = objectMapper.createObjectNode();
        resultNode.set("project", project.convertToJson());

//        Task task = taskService.findTaskByTaskId(taskId, Boolean.parseBoolean(queryParameters.get("is_group")), project);
        Submission submission = findSubmissionById(id);
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

        if (privileges != null && privileges.contains(SUBMISSION_READ_SINGLE)) {
            if (submission.getGrader() == null || !submission.getGrader().getUserId().equals(userId)) {
                throw new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "submission not assigned"
                );
            }
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
            for (Iterator<JsonNode> it = node.get("assessments").elements(); it.hasNext(); ) {
                JsonNode assessmentNode = it.next();
                Assessment assessment = assessmentService.getAssessmentById(assessmentNode.get("id").asText());
                int assessmentProgress = (int) (assessment.getGradedCount()*1.0/rubric.getCriterionCount()*100);
                ((ObjectNode) assessmentNode).put("progress", assessmentProgress);
                progress += assessmentProgress;
            }
            progress = (node.get("assessments").size() == 0)? 0 : (int) (progress * 1.0 / node.get("assessments").size());
        }
        node.put("progress", progress);

        List<Flag> yourFlags = flagService.findFlagsWithProject(project);
        ArrayNode yourFlagsArrayNode = createFlagsArrayNode(yourFlags);
        ((ObjectNode) resultNode.get("project")).set("flags", yourFlagsArrayNode);

        resultNode.set("submission", node);
        Grader grader = graderService.getGraderFromGraderId(userId, project);
        if (grader != null) {
            JsonNode graderJson = grader.getGraderJson();
            resultNode.set("user", graderJson);
        }
        return resultNode;
    }

    @Transactional(rollbackOn = Exception.class)
    public ArrayNode assessmentManagement(String courseId, String projectId, String submissionId,
                                          JsonNode object, List<PrivilegeEnum> privileges, String userId) throws JsonProcessingException {
        Project project = projectRepository.findById(new ProjectId(courseId, projectId)).orElse(null);
        if (project == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "project not found"
            );
        }
        Submission submission = findSubmissionById(submissionId);
        if (submission == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "task not found"
            );
        }

        if (privileges != null && privileges.contains(SUBMISSION_EDIT_SINGLE)) {
            if (submission.getGrader() == null || !submission.getGrader().getUserId().equals(userId)) {
                throw new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "submission not assigned"
                );
            }
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
                Assessment newAssignment = assessmentService.addNewAssignment(
                        new Assessment(
                                UUID.randomUUID(),
                                sourceAssignment.getGrades(),
                                sourceAssignment.getGradedCount(),
                                sourceAssignment.getFinalGrade())
                );
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
                    if (participant.getCurrentAssessmentLinker().getAssessmentId().equals(linkerSrc.getAssessmentId())) {
                        participantService.saveParticipantCurrentAssessmentLinker(participant, linkerDesList.get(0));
                    }

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

        Rubric rubric = rubricService.getRubricById(projectId);
        ArrayNode assessmentsNode = (ArrayNode) submission.convertToJsonWithDetails(assessmentLinkers, null, null, issueMap).get("assessments");
        for (Iterator<JsonNode> it1 = assessmentsNode.elements(); it1.hasNext(); ) {
            JsonNode assessmentNode = it1.next();
            Assessment assessment = assessmentService.getAssessmentById(assessmentNode.get("id").asText());
            int assessmentProgress = (int) (assessment.getGradedCount()*1.0/rubric.getCriterionCount()*100);
            ((ObjectNode) assessmentNode).put("progress", assessmentProgress);
        }

        return assessmentsNode;
    }

    @Transactional(rollbackOn = Exception.class)
    public ObjectNode addParticipantToSubmission(String courseId, String projectId, String submissionId,
                                          String participantId, String assessmentId,
                                          List<PrivilegeEnum> privileges, String userId) throws JsonProcessingException {

        Project project = projectRepository.findById(new ProjectId(courseId, projectId)).orElse(null);
        if (project == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "project not found"
            );
        }
        Submission submission = findSubmissionById(submissionId);
        if (submission == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "task not found"
            );
        }
        Assessment assessment = assessmentService.findAssessment(assessmentId);
        if (assessment == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "task not found"
            );
        }
        Participant participant = participantService.findParticipantWithId(participantId, project);
        if (participant == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "task not found"
            );
        }
        if (privileges != null && privileges.contains(SUBMISSION_EDIT_SINGLE)) {
            if (submission.getGrader() == null || !submission.getGrader().getUserId().equals(userId)) {
                throw new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "submission not assigned"
                );
            }
        }
        AssessmentLinker assessmentLinker = assessmentLinkerService
                .findAssessmentsLinkerBySubmissionAndParticipantAndAssessmentId(
                        submission,
                        participant,
                        UUID.fromString(assessmentId));
        if (assessmentLinker != null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "link exists"
            );
        }

        assessmentLinkerService.addNewAssessment(new AssessmentLinker(
                submission,
                participant,
                UUID.fromString(assessmentId)
        ));

        ObjectNode objectNode = getSubmissionInfo(courseId, projectId, submissionId, userId, privileges);
        return (ObjectNode) objectNode.get("submission");
    }

    @Transactional(rollbackOn = Exception.class)
    public ObjectNode removeParticipantFromSubmission(String courseId, String projectId, String submissionId, String participantId,
                                                 List<PrivilegeEnum> privileges, String userId, boolean returnAll) throws JsonProcessingException {

        Project project = projectRepository.findById(new ProjectId(courseId, projectId)).orElse(null);
        if (project == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "project not found"
            );
        }
        Submission submission = findSubmissionById(submissionId);
        if (submission == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "task not found"
            );
        }
        Participant participant = participantService.findParticipantWithId(participantId, project);
        if (participant == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "task not found"
            );
        }
        if (privileges != null && privileges.contains(SUBMISSION_EDIT_SINGLE)) {
            if (submission.getGrader() == null || !submission.getGrader().getUserId().equals(userId)) {
                throw new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "submission not assigned"
                );
            }
        }
        List<AssessmentLinker> submissionAssessmentLinker = assessmentLinkerService
                .findAssessmentLinkersForSubmission(
                        submission);

        int participantCount = 0;
        for(AssessmentLinker assessmentLinker: submissionAssessmentLinker) {
            if (assessmentLinker != null && assessmentLinker.getParticipant() != null) {
                participantCount += 1;
            }
        }

        if (participantCount <= 1) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "submission only have 0-1 participant"
            );
        }

        List<AssessmentLinker> assessmentLinkers = assessmentLinkerService
                .findAssessmentLinkerForSubmissionAndParticipant(submission, participant);
        if (assessmentLinkers.size() != 1) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "submission and participant have: " + assessmentLinkers.size()
            );
        }

        AssessmentLinker assessmentLinker = assessmentLinkers.get(0);
        if (assessmentLinker.getAssessmentId().equals(participant.getCurrentAssessmentLinker().getAssessmentId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "cannot delete current assessment");
        }

        assessmentLinkerService.deleteAssessmentLinker(assessmentLinker);
        if (!returnAll) {
            ObjectNode objectNode = getSubmissionInfo(courseId, projectId, submissionId, userId, privileges);
            return (ObjectNode) objectNode.get("submission");
        }
        ObjectMapper objectMapper = new ObjectMapper();
        List<AssessmentLinker> assessmentLinkers1 = assessmentLinkerService.findAssessmentLinkersForParticipant(participant);
        ArrayNode submissionsNode = objectMapper.createArrayNode();
        for(AssessmentLinker linker: assessmentLinkers1) {
            Submission submission1 = linker.getSubmission();
            ObjectNode submissionNode = (ObjectNode) submission1.convertToJson();
            submissionNode.put("isCurrent", participant.getCurrentAssessmentLinker() != null
                    && participant.getCurrentAssessmentLinker().getAssessmentId().equals(linker.getAssessmentId()));
            submissionsNode.add(submissionNode);
        }
        ObjectNode resultNode = objectMapper.createObjectNode();
        resultNode.set("submissions", submissionsNode);
        return resultNode;
    }

    @Transactional(rollbackOn = Exception.class)
    public JsonNode addFlag(String courseId, String projectId, String id, ObjectNode flag, String userId, List<PrivilegeEnum> privileges) {
        ObjectMapper objectMapper = new ObjectMapper();
        Project project = projectRepository.findById(new ProjectId(courseId, projectId)).orElse(null);
        if (project == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "project not found"
            );
        }

//        Task task = taskService.findTaskByTaskId(taskId, Boolean.parseBoolean(queryParameters.get("is_group")), project);
        Submission submission = findSubmissionById(id);
        if (submission == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "submission not found"
            );
        }

        if (privileges != null && privileges.contains(SUBMISSION_EDIT_SINGLE)) {
            if (submission.getGrader() == null || !submission.getGrader().getUserId().equals(userId)) {
                throw new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "submission not assigned"
                );
            }
        }

        Grader grader = graderService.getGraderFromGraderId(userId, project);
        if (grader == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "task not found"
            );
        }
        Flag flag1 = flagService.findFlagWithNameAndProject(flag.get("name").asText(), project);
        if (flag1 != null) {
            if (!submission.getFlags().contains(flag1)) submission.getFlags().add(flag1);
            Submission submission1 = saveFlags(submission);
            ObjectNode dataNode = objectMapper.createObjectNode();
            dataNode.set("data", createFlagsArrayNode((List<Flag>) submission1.getFlags()));
            return dataNode;
        } else {
            ObjectNode errorNode = objectMapper.createObjectNode();
            errorNode.put("error", "flag not fount");
            return errorNode;
        }
    }

    @Transactional(rollbackOn = Exception.class)
    public JsonNode createFlag(String courseId, String projectId, String id, ObjectNode flag, String userId, List<PrivilegeEnum> privileges) {
        ObjectMapper objectMapper = new ObjectMapper();

        Project project = projectRepository.findById(new ProjectId(courseId, projectId)).orElse(null);
        if (project == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "project not found"
            );
        }

//        Task task = taskService.findTaskByTaskId(taskId, Boolean.parseBoolean(queryParameters.get("is_group")), project);
        Submission submission = findSubmissionById(id);
        if (submission == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "task not found"
            );
        }

        if (privileges != null && privileges.contains(SUBMISSION_EDIT_SINGLE)) {
            if (submission.getGrader() == null || !submission.getGrader().getUserId().equals(userId)) {
                throw new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "submission not assigned"
                );
            }
        }

        Grader grader = graderService.getGraderFromGraderId(userId, project);
        if (grader == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "task not found"
            );
        }
        Flag flag1 = flagService.findFlagWithNameAndProject(flag.get("name").asText(), project);
        if (flag1 == null) {

            flagService.saveNewFlag(new
                    Flag(flag.get("name").asText(),
                    flag.get("description").asText(),
                    flag.get("variant").asText(),
                    project));

            List<Flag> flags = flagService.findFlagsWithProject(project);
            ArrayNode yourFlagsArrayNode = createFlagsArrayNode(flags);
            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.set("data", yourFlagsArrayNode);
            return objectNode;
        } else {
            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("error", "flag already exists");
            return objectNode;
        }
    }

    @Transactional(rollbackOn = Exception.class)
    public JsonNode deleteFlag(String courseId, String projectId, String id, String flagId, String userId, List<PrivilegeEnum> privileges) {
        ObjectMapper objectMapper = new ObjectMapper();

        Project project = projectRepository.findById(new ProjectId(courseId, projectId)).orElse(null);
        if (project == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "project not found"
            );
        }

//        Task task = taskService.findTaskByTaskId(taskId, Boolean.parseBoolean(queryParameters.get("is_group")), project);
        Submission submission = findSubmissionById(id);
        if (submission == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "task not found"
            );
        }

        if (privileges != null && privileges.contains(SUBMISSION_EDIT_SINGLE)) {
            if (submission.getGrader() == null || !submission.getGrader().getUserId().equals(userId)) {
                throw new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "submission not assigned"
                );
            }
        }

        Grader grader = graderService.getGraderFromGraderId(userId, project);
        if (grader == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "grader not found"
            );
        }
        Flag flag1 = flagService.findFlagWithId(UUID.fromString(flagId));
        if (flag1 != null) {
            submission.getFlags().remove(flag1);
            Submission submission1 = saveFlags(submission);
            ObjectNode dataNode = objectMapper.createObjectNode();
            dataNode.set("data", createFlagsArrayNode((List<Flag>) submission1.getFlags()));
            return dataNode;
        } else {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "flag not exist"
            );
        }
    }
}