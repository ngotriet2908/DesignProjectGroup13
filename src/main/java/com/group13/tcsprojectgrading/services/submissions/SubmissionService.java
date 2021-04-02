package com.group13.tcsprojectgrading.services.submissions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.group13.tcsprojectgrading.models.project.Project;
import com.group13.tcsprojectgrading.models.user.User;
import com.group13.tcsprojectgrading.models.graders.GradingParticipation;
import com.group13.tcsprojectgrading.models.grading.Assessment;
import com.group13.tcsprojectgrading.models.submissions.Submission;
import com.group13.tcsprojectgrading.repositories.project.ProjectRepository;
import com.group13.tcsprojectgrading.repositories.submissions.SubmissionRepository;
import com.group13.tcsprojectgrading.services.user.UserService;
import com.group13.tcsprojectgrading.services.graders.GradingParticipationService;
import com.group13.tcsprojectgrading.services.grading.AssessmentService;
import com.group13.tcsprojectgrading.services.grading.IssueService;
import com.group13.tcsprojectgrading.services.rubric.RubricService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class SubmissionService {
    private final SubmissionRepository submissionRepository;
    private final ProjectRepository projectRepository;
    private final GradingParticipationService gradingParticipationService;
    private final RubricService rubricService;
    private final LabelService labelService;
    private final IssueService issueService;
    private final AssessmentService assessmentService;
    private final SubmissionDetailsService submissionDetailsService;
    private final UserService userService;

    @Autowired
    public SubmissionService(SubmissionRepository submissionRepository, ProjectRepository projectRepository,
                             GradingParticipationService gradingParticipationService, RubricService rubricService,
                             LabelService labelService, IssueService issueService,
                             @Lazy AssessmentService assessmentService, SubmissionDetailsService submissionDetailsService,
                             @Lazy UserService userService) {
        this.submissionRepository = submissionRepository;
        this.projectRepository = projectRepository;
        this.gradingParticipationService = gradingParticipationService;
        this.rubricService = rubricService;
        this.labelService = labelService;
        this.issueService = issueService;
        this.assessmentService = assessmentService;
        this.submissionDetailsService = submissionDetailsService;
        this.userService = userService;
    }

    /*
    Creates a new submission entry in the database if the submission did not exist before or updates one if it was
    already stored.
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public Submission addNewSubmission(Project project, User userId, Long groupId,
                                       Date date, String name) {
        Submission currentSubmission = this.submissionRepository.findByProject_IdAndSubmitterId_IdAndSubmittedAtAndGroupId(
                project.getId(), userId.getId(), date, groupId
        );

        if (currentSubmission != null) {
            return null;
        } else {
            // update submission
            return this.submissionRepository.save(new Submission(
                    userId, groupId,
                    project, name, date
            ));
        }
    }

    /*
    Creates a new submission entry in the database if the submission did not exist before or updates one if it was
    already stored.
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public Submission addNewSubmission(Submission submission) {
        Submission existingSubmission = this.submissionRepository.findByProject_IdAndSubmitterId_IdAndSubmittedAtAndGroupId(
                submission.getProject().getId(), submission.getSubmitter().getId(), submission.getSubmittedAt(), submission.getGroupId()
        );

        if (existingSubmission != null) {
            return null;
        } else {
            return this.submissionRepository.save(submission);
        }
    }

//    @Transactional(value = Transactional.TxType.MANDATORY)
//    public Submission saveFlags(Submission submission) {
//        Submission currentSubmission = submissionRepository.findSubmissionByProjectAndUserIdAndGroupIdAndDate(
//                submission.getProject(),
//                submission.getUserId(),
//                submission.getGroupId(),
//                submission.getDate()
//        );
//        currentSubmission.setFlags(submission.getFlags());
//        return submissionRepository.save(currentSubmission);
//    }
//
//    @Transactional(value = Transactional.TxType.MANDATORY)
//    public Submission saveGrader(Submission submission) {
//        Submission currentSubmission = submissionRepository.findSubmissionByProjectAndUserIdAndGroupIdAndDate(
//                submission.getProject(),
//                submission.getUserId(),
//                submission.getGroupId(),
//                submission.getDate()
//        );
//        currentSubmission.setGrader(submission.getGrader());
//        return submissionRepository.save(currentSubmission);
//    }
//
//    @Transactional(value = Transactional.TxType.MANDATORY)
//    public List<Submission> findSubmissionWithProject(Project project) {
//        return submissionRepository.findSubmissionsByProject(project);
//    }
//
//    @Transactional(value = Transactional.TxType.MANDATORY)
//    public Submission findSubmissionById(String id) {
//        return submissionRepository.getOne(UUID.fromString(id));
//    }
//
//    @Transactional(value = Transactional.TxType.MANDATORY)
//    public List<Submission> findSubmissionsForGrader(Project project, String graderId) {
//        return submissionRepository.findSubmissionsByProjectAndGrader_UserId(project, graderId);
//    }
//
//    @Transactional(value = Transactional.TxType.MANDATORY)
//    public List<Submission> findSubmissionsByLabels(Label label) {
//        return submissionRepository.findSubmissionsByFlags(label);
//    }
//
//    @Transactional(value = Transactional.TxType.MANDATORY)
//    public List<Submission> findSubmissionsForGraderAll(String graderId) {
//        return submissionRepository.findSubmissionsByGrader_UserId(graderId);
//    }
//
//    @Transactional(value = Transactional.TxType.MANDATORY)
//    public List<Submission> findSubmissionsForGraderCourse(String graderId, String courseId) {
//        return submissionRepository.findSubmissionsByGrader_UserIdAndProject_CourseId(graderId, courseId);
//    }
//


    @Transactional
    public List<Submission> getSubmissions(Long courseId, Long projectId, Long userId) {
        Project project = this.projectRepository.findById(projectId).orElse(null);

        if (project == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Project not found"
            );
        }

        GradingParticipation grader = this.gradingParticipationService.getGradingParticipationByUserAndProject(userId, projectId);
        if (grader == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Unauthorised"
            );
        }

        List<Submission> submissions = this.submissionRepository.findSubmissionsByProject(project);

        // link submissions' members
        for (Submission submission: submissions) {
            this.addSubmissionMembers(submission);
        }

        return submissions;



//        List<Label> yourLabels = labelService.findLabelsWithProject(project);
//        ArrayNode yourFlagsArrayNode = createFlagsArrayNode(yourLabels);
//        ((ObjectNode) resultNode.get("project")).set("flags", yourFlagsArrayNode);

//
//        for (Submission submission : submissions) {
//            List<AssessmentLink> linkers = this.assessmentService.findAssessmentLinkersForSubmission(submission);
//            List<Assessment> assessmentList = assessmentService.getAssessmentsBySubmission(submission);
//            List<Issue> issues = new ArrayList<>();
//            for (Assessment assessment : assessmentList) {
//                issues.addAll(issueService.findIssuesByAssessment(assessment.getId())
//                                .stream()
//                                .filter(issue -> issue.getStatus().equals("unresolved"))
//                                .collect(Collectors.toList())
//                );
//            }
//
//            arrayNode.add(submission.convertToJson(linkers, issues));
//
//            for (JsonNode submissionNode: arrayNode) {
//                int progress = 0;
//                if (rubric != null) {
//
//                    for (JsonNode assessmentNode: submissionNode.get("assessments")) {
//                        Assessment assessment = assessmentService.getAssessmentById(assessmentNode.get("id").asText());
//                        int assessmentProgress = (int) (assessment.getGradedCount() * 1.0 / rubric.getCriterionCount() * 100);
//                        ((ObjectNode) assessmentNode).put("progress", assessmentProgress);
//                        progress += assessmentProgress;
//                    }
//                    progress = (submissionNode.get("assessments").size() == 0) ? 0 : (int) (progress * 1.0 / submissionNode.get("assessments").size());
//                }
//                ((ObjectNode) submissionNode).put("progress", progress);
//            }
//        }
//
//        TODO copy this to stats
//        int finalProgress = (int) Arrays.stream(progresses.stream().mapToInt(i -> i).toArray()).average().orElse(0);
    }

    @Transactional
    public Submission getSubmission(Long submissionId) throws JsonProcessingException {
//        Project project = this.projectRepository.findById(projectId).orElse(null);
//
//        if (project == null) {
//            throw new ResponseStatusException(
//                    HttpStatus.NOT_FOUND, "Project not found"
//            );
//        }

        // TODO I'm not sure whether to hide the submission or not (only grading?)
//        GradingParticipation grader = this.gradingParticipationService.getGradingParticipationByUserAndProject(userId, projectId);
//        if (grader == null) {
//            throw new ResponseStatusException(
//                    HttpStatus.UNAUTHORIZED, "Unauthorised"
//            );
//        }

        // find submission
        Submission submission = this.submissionRepository.findById(submissionId).orElse(null);

        if (submission == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Submission not found"
            );
        }

        // link submission's members
        submission = this.addSubmissionMembers(submission);

        // link submission's assessments
        submission = this.addSubmissionAssessments(submission);

        return submission;



//        if (privileges != null && privileges.contains(SUBMISSION_READ_SINGLE)) {
//            if (submission.getGrader() == null || !submission.getGrader().getUserId().equals(userId)) {
//                throw new ResponseStatusException(
//                        HttpStatus.UNAUTHORIZED, "Submission not assigned"
//                );
//            }
//        }
//
//        List<AssessmentLink> linkers = this.assessmentService.findAssessmentLinkersForSubmission(submission);
//
//        Map<Long, List<Issue>> issueMap = new HashMap<>();
//        List<Assessment> assessmentList = this.assessmentService.getAssessmentsBySubmission(submission);
//        for (Assessment assessment : assessmentList) {
//            issueMap.put(
//                    assessment.getId(),
//                    issueService.findIssuesByAssessment(assessment.getId())
//                            .stream()
//                            .filter(issue -> issue.getStatus().equals("unresolved"))
//                            .collect(Collectors.toList())
//            );
//        }
//
//        Rubric rubric = rubricService.getRubricById(projectId);
//
//        int progress = 0;
//        if (rubric != null) {
//            for (JsonNode assessmentNode: node.get("assessments")) {
//                Assessment assessment = assessmentService.getAssessmentById(assessmentNode.get("id").asText());
//                int assessmentProgress = (int) (assessment.getGradedCount()*1.0/rubric.getCriterionCount()*100);
//                ((ObjectNode) assessmentNode).put("progress", assessmentProgress);
//                progress += assessmentProgress;
//            }
//            progress = (node.get("assessments").size() == 0)? 0 : (int) (progress * 1.0 / node.get("assessments").size());
//        }
//        node.put("progress", progress);
//
//        List<Label> yourLabels = labelService.findLabelsWithProject(project);
//        ArrayNode yourFlagsArrayNode = createFlagsArrayNode(yourLabels);
//        ((ObjectNode) resultNode.get("project")).set("flags", yourFlagsArrayNode);
//
//        resultNode.set("submission", node);
//        GradingParticipation grader = graderService.getGraderFromGraderId(userId, project);
//        if (grader != null) {
//            JsonNode graderJson = grader.getGraderJson();
//            resultNode.set("user", graderJson);
//        }
//        return resultNode;
    }

    /*
    Populates the "members" field with the list of students who are linked to the submission.
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public Submission addSubmissionMembers(Submission submission) {
        Set<User> members = this.assessmentService.getSubmissionMembers(submission);
        submission.setMembers(members);
        return submission;
    }

    /*
    Populates the "assessments" field with the list of assessments linked to the submission.
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public Submission addSubmissionAssessments(Submission submission) {
        Set<Assessment> assessments = this.assessmentService.getAssessmentsBySubmission(submission);
        submission.setAssessments(assessments);
        return submission;
    }


//    @Transactional(rollbackOn = Exception.class)
//    public ArrayNode assessmentManagement(String courseId, String projectId, String submissionId,
//                                          JsonNode object, List<PrivilegeEnum> privileges, String userId) throws JsonProcessingException {
//        Project project = projectRepository.findById(new ProjectId(courseId, projectId)).orElse(null);
//        if (project == null) {
//            throw new ResponseStatusException(
//                    HttpStatus.NOT_FOUND, "project not found"
//            );
//        }
//        Submission submission = findSubmissionById(submissionId);
//        if (submission == null) {
//            throw new ResponseStatusException(
//                    HttpStatus.NOT_FOUND, "task not found"
//            );
//        }
//
//        if (privileges != null && privileges.contains(SUBMISSION_EDIT_SINGLE)) {
//            if (submission.getGrader() == null || !submission.getGrader().getUserId().equals(userId)) {
//                throw new ResponseStatusException(
//                        HttpStatus.UNAUTHORIZED, "submission not assigned"
//                );
//            }
//        }
//
//        switch (object.get("action").asText()) {
//            case "new": {
//                this.assessmentService.createNewAssessment(
//                        submission,
//                        null,
//                        project
//                );
//
//                break;
//            }
//            case "clone": {
//                String source = object.get("source").asText();
//                Assessment sourceAssignment = this.assessmentService.getAssessmentById(source);
////                Assessment newAssignment = this.assessmentService.createNewAssessment(
////                        new Assessment(
////                                UUID.randomUUID(),
////                                sourceAssignment.getGrades(),
////                                sourceAssignment.getGradedCount(),
////                                sourceAssignment.getFinalGrade())
////                );
//
//                Assessment newAssignment = this.assessmentService.createEmptyAssessment(
//                        project
//                );
//
//                AssessmentLinker linker = this.assessmentService.createNewAssessment(submission, null, newAssignment);
//                break;
//            }
//            case "move": {
//                String source = object.get("source").asText();
//                String destination = object.get("destination").asText();
//                String participantId = object.get("participantId").asText();
//                Assessment sourceAssignment = assessmentService.getAssessmentById(source);
//                Assessment destinationAssignment = assessmentService.getAssessmentById(destination);
//                User participant = participantService.findParticipantWithId(participantId, project);
//                List<AssessmentLinker> linkerSrcList = assessmentLinkerService.findAssessmentLinkersForAssessmentId(source);
//                List<AssessmentLinker> linkerDesList = assessmentLinkerService.findAssessmentLinkersForAssessmentId(destination);
//                AssessmentLinker linkerSrc = null;
//                for(AssessmentLinker linker : linkerSrcList) {
//                    if (linker.getParticipant().getId().equals(participant.getId())) {
//                        linkerSrc = linker;
//                        break;
//                    }
//                }
//
//                if (sourceAssignment == null ||
//                        destinationAssignment == null ||
//                        participant == null ||
//                        linkerSrc == null ||
//                        linkerSrcList.size() == 0 ||
//                        linkerDesList.size() == 0) {
//                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found info");
//                }
//
//
//
//                if (linkerDesList.size() == 1 && linkerDesList.get(0).getParticipant() == null) {
//                    linkerDesList.get(0).setParticipant(participant);
//                    assessmentLinkerService.saveInfoAssessment(linkerDesList.get(0));
//                    if (participant.getCurrentAssessmentLinker().getAssessmentId().equals(linkerSrc.getAssessmentId())) {
//                        participantService.saveParticipantCurrentAssessmentLinker(participant, linkerDesList.get(0));
//                    }
//
//                    assessmentLinkerService.deleteAssessmentLinker(linkerSrc);
//                } else {
//                    linkerSrc.setAssessmentId(destinationAssignment.getId());
//                    assessmentLinkerService.saveInfoAssessment(linkerSrc);
//                }
//                if (linkerSrcList.size() == 1) {
//                    AssessmentLinker linker = assessmentLinkerService.addNewNullAssessment(new AssessmentLinker(
//                            submission,
//                            null,
//                            sourceAssignment.getId()
//                    ));
//                    if (linker == null)
//                        throw new ResponseStatusException(HttpStatus.CONFLICT, "conflict with assessments");
//                }
//
//                break;
//            }
//            case "delete": {
//                String source = object.get("source").asText();
//                List<AssessmentLinker> linkers = assessmentLinkerService.findAssessmentLinkersForAssessmentId(source);
//                for (AssessmentLinker linker : linkers) {
//                    if (linker.getParticipant() != null) {
//                        throw new ResponseStatusException(HttpStatus.CONFLICT, "cant remove assessment that has participants");
//                    }
//                }
//                for (AssessmentLinker linker : linkers) {
//                    assessmentLinkerService.deleteAssessmentLinker(linker);
//                }
//                Assessment assessment = assessmentService.findAssessment(source);
//                if (assessment != null) {
//                    assessmentService.deleteAssessment(assessment);
//                } else {
//                    throw new ResponseStatusException(HttpStatus.CONFLICT, "no assessment found");
//                }
//                break;
//            }
//        }
//
//        List<AssessmentLinker> assessmentLinkers = assessmentLinkerService.findAssessmentLinkersForSubmission(submission);
//        Map<UUID, List<Issue>> issueMap = new HashMap<>();
//        List<Assessment> assessmentList = assessmentService.getAssessmentBySubmission(submission);
//        for (Assessment assessment : assessmentList) {
//            issueMap.put(
//                    assessment.getId(),
//                    issueService.findIssuesByAssessment(assessment.getId())
//                            .stream()
//                            .filter(issue -> issue.getStatus().equals("unresolved"))
//                            .collect(Collectors.toList())
//            );
//        }
//
//        Rubric rubric = rubricService.getRubricById(projectId);
//        ArrayNode assessmentsNode = (ArrayNode) submission.convertToJsonWithDetails(assessmentLinkers, null, null, issueMap).get("assessments");
//        for (Iterator<JsonNode> it1 = assessmentsNode.elements(); it1.hasNext(); ) {
//            JsonNode assessmentNode = it1.next();
//            Assessment assessment = assessmentService.getAssessmentById(assessmentNode.get("id").asText());
//            int assessmentProgress = (int) (assessment.getGradedCount()*1.0/rubric.getCriterionCount()*100);
//            ((ObjectNode) assessmentNode).put("progress", assessmentProgress);
//        }
//
//        return assessmentsNode;
//    }

//    @Transactional(rollbackOn = Exception.class)
//    public ObjectNode addParticipantToSubmission(String courseId, String projectId, String submissionId,
//                                          String participantId, String assessmentId,
//                                          List<PrivilegeEnum> privileges, String userId) throws JsonProcessingException {
//
//        Project project = projectRepository.findById(new ProjectId(courseId, projectId)).orElse(null);
//        if (project == null) {
//            throw new ResponseStatusException(
//                    HttpStatus.NOT_FOUND, "project not found"
//            );
//        }
//        Submission submission = findSubmissionById(submissionId);
//        if (submission == null) {
//            throw new ResponseStatusException(
//                    HttpStatus.NOT_FOUND, "task not found"
//            );
//        }
//        Assessment assessment = assessmentService.findAssessment(assessmentId);
//        if (assessment == null) {
//            throw new ResponseStatusException(
//                    HttpStatus.NOT_FOUND, "task not found"
//            );
//        }
//        Participant participant = participantService.findParticipantWithId(participantId, project);
//        if (participant == null) {
//            throw new ResponseStatusException(
//                    HttpStatus.NOT_FOUND, "task not found"
//            );
//        }
//        if (privileges != null && privileges.contains(SUBMISSION_EDIT_SINGLE)) {
//            if (submission.getGrader() == null || !submission.getGrader().getUserId().equals(userId)) {
//                throw new ResponseStatusException(
//                        HttpStatus.UNAUTHORIZED, "submission not assigned"
//                );
//            }
//        }
//        AssessmentLinker assessmentLinker = assessmentLinkerService
//                .findAssessmentsLinkerBySubmissionAndParticipantAndAssessmentId(
//                        submission,
//                        participant,
//                        UUID.fromString(assessmentId));
//        if (assessmentLinker != null) {
//            throw new ResponseStatusException(
//                    HttpStatus.CONFLICT, "link exists"
//            );
//        }
//
//        assessmentLinkerService.addNewAssessment(new AssessmentLinker(
//                submission,
//                participant,
//                UUID.fromString(assessmentId)
//        ));
//
//        ObjectNode objectNode = getSubmissionInfo(courseId, projectId, submissionId, userId, privileges);
//        return (ObjectNode) objectNode.get("submission");
//    }
//
//    @Transactional(rollbackOn = Exception.class)
//    public ObjectNode removeParticipantFromSubmission(String courseId, String projectId, String submissionId, String participantId,
//                                                 List<PrivilegeEnum> privileges, String userId, boolean returnAll) throws JsonProcessingException {
//
//        Project project = projectRepository.findById(new ProjectId(courseId, projectId)).orElse(null);
//        if (project == null) {
//            throw new ResponseStatusException(
//                    HttpStatus.NOT_FOUND, "project not found"
//            );
//        }
//        Submission submission = findSubmissionById(submissionId);
//        if (submission == null) {
//            throw new ResponseStatusException(
//                    HttpStatus.NOT_FOUND, "task not found"
//            );
//        }
//        Participant participant = participantService.findParticipantWithId(participantId, project);
//        if (participant == null) {
//            throw new ResponseStatusException(
//                    HttpStatus.NOT_FOUND, "task not found"
//            );
//        }
//        if (privileges != null && privileges.contains(SUBMISSION_EDIT_SINGLE)) {
//            if (submission.getGrader() == null || !submission.getGrader().getUserId().equals(userId)) {
//                throw new ResponseStatusException(
//                        HttpStatus.UNAUTHORIZED, "submission not assigned"
//                );
//            }
//        }
//        List<AssessmentLinker> submissionAssessmentLinker = assessmentLinkerService
//                .findAssessmentLinkersForSubmission(
//                        submission);
//
//        int participantCount = 0;
//        for(AssessmentLinker assessmentLinker: submissionAssessmentLinker) {
//            if (assessmentLinker != null && assessmentLinker.getParticipant() != null) {
//                participantCount += 1;
//            }
//        }
//
//        if (participantCount <= 1) {
//            throw new ResponseStatusException(
//                    HttpStatus.CONFLICT, "submission only have 0-1 participant"
//            );
//        }
//
//        List<AssessmentLinker> assessmentLinkers = assessmentLinkerService
//                .findAssessmentLinkerForSubmissionAndParticipant(submission, participant);
//        if (assessmentLinkers.size() != 1) {
//            throw new ResponseStatusException(
//                    HttpStatus.CONFLICT, "submission and participant have: " + assessmentLinkers.size()
//            );
//        }
//
//        AssessmentLinker assessmentLinker = assessmentLinkers.get(0);
//        if (assessmentLinker.getAssessmentId().equals(participant.getCurrentAssessmentLinker().getAssessmentId())) {
//            throw new ResponseStatusException(
//                    HttpStatus.CONFLICT, "cannot delete current assessment");
//        }
//
//        assessmentLinkerService.deleteAssessmentLinker(assessmentLinker);
//        if (!returnAll) {
//            ObjectNode objectNode = getSubmissionInfo(courseId, projectId, submissionId, userId, privileges);
//            return (ObjectNode) objectNode.get("submission");
//        }
//        ObjectMapper objectMapper = new ObjectMapper();
//        List<AssessmentLinker> assessmentLinkers1 = assessmentLinkerService.findAssessmentLinkersForParticipant(participant);
//        ArrayNode submissionsNode = objectMapper.createArrayNode();
//        for(AssessmentLinker linker: assessmentLinkers1) {
//            Submission submission1 = linker.getSubmission();
//            ObjectNode submissionNode = (ObjectNode) submission1.convertToJson();
//            submissionNode.put("isCurrent", participant.getCurrentAssessmentLinker() != null
//                    && participant.getCurrentAssessmentLinker().getAssessmentId().equals(linker.getAssessmentId()));
//            submissionsNode.add(submissionNode);
//        }
//        ObjectNode resultNode = objectMapper.createObjectNode();
//        resultNode.set("submissions", submissionsNode);
//        return resultNode;
//    }
//
//    @Transactional(rollbackOn = Exception.class)
//    public JsonNode addFlag(String courseId, String projectId, String id, ObjectNode flag, String userId, List<PrivilegeEnum> privileges) {
//        ObjectMapper objectMapper = new ObjectMapper();
//        Project project = projectRepository.findById(new ProjectId(courseId, projectId)).orElse(null);
//        if (project == null) {
//            throw new ResponseStatusException(
//                    HttpStatus.NOT_FOUND, "project not found"
//            );
//        }
//
//        Submission submission = findSubmissionById(id);
//        if (submission == null) {
//            throw new ResponseStatusException(
//                    HttpStatus.NOT_FOUND, "submission not found"
//            );
//        }
//
//        if (privileges != null && privileges.contains(SUBMISSION_EDIT_SINGLE)) {
//            if (submission.getGrader() == null || !submission.getGrader().getUserId().equals(userId)) {
//                throw new ResponseStatusException(
//                        HttpStatus.UNAUTHORIZED, "submission not assigned"
//                );
//            }
//        }
//
//        GradingParticipation grader = graderService.getGraderFromGraderId(userId, project);
//        if (grader == null) {
//            throw new ResponseStatusException(
//                    HttpStatus.NOT_FOUND, "task not found"
//            );
//        }
//        Label label1 = labelService.findLabelWithNameAndProject(flag.get("name").asText(), project);
//        if (label1 != null) {
//            if (!submission.getFlags().contains(label1)) submission.getFlags().add(label1);
//            Submission submission1 = saveFlags(submission);
//            ObjectNode dataNode = objectMapper.createObjectNode();
//            dataNode.set("data", createFlagsArrayNode((List<Label>) submission1.getFlags()));
//            return dataNode;
//        } else {
//            ObjectNode errorNode = objectMapper.createObjectNode();
//            errorNode.put("error", "flag not fount");
//            return errorNode;
//        }
//    }

//    @Transactional(rollbackOn = Exception.class)
//    public JsonNode createFlag(String courseId, String projectId, String id, ObjectNode flag, String userId, List<PrivilegeEnum> privileges) {
//        ObjectMapper objectMapper = new ObjectMapper();
//
//        Project project = projectRepository.findById(new ProjectId(courseId, projectId)).orElse(null);
//        if (project == null) {
//            throw new ResponseStatusException(
//                    HttpStatus.NOT_FOUND, "project not found"
//            );
//        }
//
//        Submission submission = findSubmissionById(id);
//        if (submission == null) {
//            throw new ResponseStatusException(
//                    HttpStatus.NOT_FOUND, "task not found"
//            );
//        }
//
//        if (privileges != null && privileges.contains(SUBMISSION_EDIT_SINGLE)) {
//            if (submission.getGrader() == null || !submission.getGrader().getUserId().equals(userId)) {
//                throw new ResponseStatusException(
//                        HttpStatus.UNAUTHORIZED, "submission not assigned"
//                );
//            }
//        }
//
//        GradingParticipation grader = graderService.getGraderFromGraderId(userId, project);
//        if (grader == null) {
//            throw new ResponseStatusException(
//                    HttpStatus.NOT_FOUND, "task not found"
//            );
//        }
//        Label label1 = labelService.findLabelWithNameAndProject(flag.get("name").asText(), project);
//        if (label1 == null) {
//
//            labelService.saveNewLabel(new
//                    Label(flag.get("name").asText(),
//                    flag.get("description").asText(),
//                    flag.get("variant").asText(),
//                    project));
//
//            List<Label> labels = labelService.findLabelsWithProject(project);
//            ArrayNode yourFlagsArrayNode = createFlagsArrayNode(labels);
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
//    @Transactional(rollbackOn = Exception.class)
//    public JsonNode deleteFlag(String courseId, String projectId, String id, String flagId, String userId, List<PrivilegeEnum> privileges) {
//        ObjectMapper objectMapper = new ObjectMapper();
//
//        Project project = projectRepository.findById(new ProjectId(courseId, projectId)).orElse(null);
//        if (project == null) {
//            throw new ResponseStatusException(
//                    HttpStatus.NOT_FOUND, "project not found"
//            );
//        }
//
//        Submission submission = findSubmissionById(id);
//        if (submission == null) {
//            throw new ResponseStatusException(
//                    HttpStatus.NOT_FOUND, "task not found"
//            );
//        }
//
//        if (privileges != null && privileges.contains(SUBMISSION_EDIT_SINGLE)) {
//            if (submission.getGrader() == null || !submission.getGrader().getUserId().equals(userId)) {
//                throw new ResponseStatusException(
//                        HttpStatus.UNAUTHORIZED, "submission not assigned"
//                );
//            }
//        }
//
//        GradingParticipation grader = graderService.getGraderFromGraderId(userId, project);
//        if (grader == null) {
//            throw new ResponseStatusException(
//                    HttpStatus.NOT_FOUND, "grader not found"
//            );
//        }
//        Label label1 = labelService.findLabelWithId(UUID.fromString(flagId));
//        if (label1 != null) {
//            submission.getFlags().remove(label1);
//            Submission submission1 = saveFlags(submission);
//            ObjectNode dataNode = objectMapper.createObjectNode();
//            dataNode.set("data", createFlagsArrayNode((List<Label>) submission1.getFlags()));
//            return dataNode;
//        } else {
//            throw new ResponseStatusException(
//                    HttpStatus.CONFLICT, "flag not exist"
//            );
//        }
//    }
}