package com.group13.tcsprojectgrading.services.submissions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.group13.tcsprojectgrading.models.course.CourseParticipation;
import com.group13.tcsprojectgrading.models.grading.AssessmentLink;
import com.group13.tcsprojectgrading.models.grading.Grade;
import com.group13.tcsprojectgrading.models.permissions.PrivilegeEnum;
import com.group13.tcsprojectgrading.models.project.Project;
import com.group13.tcsprojectgrading.models.submissions.Label;
import com.group13.tcsprojectgrading.models.user.User;
import com.group13.tcsprojectgrading.models.graders.GradingParticipation;
import com.group13.tcsprojectgrading.models.grading.Assessment;
import com.group13.tcsprojectgrading.models.submissions.Submission;
import com.group13.tcsprojectgrading.repositories.course.CourseParticipationRepository;
import com.group13.tcsprojectgrading.repositories.grading.IssueRepository;
import com.group13.tcsprojectgrading.repositories.project.ProjectRepository;
import com.group13.tcsprojectgrading.repositories.submissions.SubmissionRepository;
import com.group13.tcsprojectgrading.services.user.UserService;
import com.group13.tcsprojectgrading.services.graders.GradingParticipationService;
import com.group13.tcsprojectgrading.services.grading.AssessmentService;
//import com.group13.tcsprojectgrading.services.grading.IssueService;
import com.group13.tcsprojectgrading.services.rubric.RubricService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

import static com.group13.tcsprojectgrading.models.permissions.PrivilegeEnum.*;

@Service
public class SubmissionService {
    private final SubmissionRepository submissionRepository;
    private final ProjectRepository projectRepository;
    private final GradingParticipationService gradingParticipationService;
    private final RubricService rubricService;
    private final AssessmentService assessmentService;
    private final SubmissionDetailsService submissionDetailsService;
    private final UserService userService;
    private final CourseParticipationRepository courseParticipationRepository;
    private final IssueRepository issueRepository;

    @Autowired
    public SubmissionService(SubmissionRepository submissionRepository, ProjectRepository projectRepository,
                             GradingParticipationService gradingParticipationService, RubricService rubricService,
                             @Lazy AssessmentService assessmentService, SubmissionDetailsService submissionDetailsService,
                             @Lazy UserService userService, CourseParticipationRepository courseParticipationRepository, IssueRepository issueRepository) {
        this.submissionRepository = submissionRepository;
        this.projectRepository = projectRepository;
        this.gradingParticipationService = gradingParticipationService;
        this.rubricService = rubricService;
        this.assessmentService = assessmentService;
        this.submissionDetailsService = submissionDetailsService;
        this.userService = userService;
        this.courseParticipationRepository = courseParticipationRepository;
        this.issueRepository = issueRepository;
    }

    /*
    Creates a new submission entry in the database if the submission did not exist before or updates one if it was
    already stored.
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public Submission addNewSubmission(Project project, User userId, Long groupId,
                                       Date date, String name) throws ResponseStatusException {
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
    public Submission addNewSubmission(Submission submission) throws ResponseStatusException {
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

    @Transactional(value = Transactional.TxType.MANDATORY)
    public List<Submission> findSubmissionsForGrader(Long graderId) {
        return submissionRepository.findSubmissionsByGrader_Id(graderId);
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public Submission findSubmissionsFromAssessment(Long assessmentId) {
        Set<AssessmentLink> assessmentLinks = assessmentService.findAssessmentLinksByAssessmentId(assessmentId);
        if (assessmentLinks.size() == 0) return null;
        return assessmentLinks.stream()
                .findFirst()
                .orElse(null)
                .getId().getSubmission();
    }

    /*
    Returns all submissions in the project (if the user is a grader inside that project).
     */
    @Transactional
    public List<Submission> getSubmissions(Long courseId, Long projectId, Long userId) throws ResponseStatusException {
        System.out.println("step0");
        Project project = this.projectRepository.findById(projectId).orElse(null);
        System.out.println("step1");
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
        System.out.println("step2");

        List<Submission> submissions = this.submissionRepository.findSubmissionsByProject(project);

        // link submissions' members
        for (Submission submission: submissions) {
            this.addSubmissionMembers(submission);
        }
        System.out.println("step3");

        return submissions;
    }

    /*
    Returns unassigned submissions in the project.
     */
    @Transactional
    public List<Submission> getUnassignedSubmissions(Long courseId, Long projectId, Long userId) {
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

        List<Submission> submissions = this.submissionRepository.findByProject_IdAndGraderIsNull(projectId);

        // link submissions' members
        for (Submission submission: submissions) {
            this.addSubmissionMembers(submission);
        }

        return submissions;
    }

    /*
    Returns a single submission.
     */
    @Transactional
    public Submission getSubmissionController(Long projectId, Long submissionId,
                                    List<PrivilegeEnum> privileges,
                                    Long graderId) throws JsonProcessingException, ResponseStatusException {
//        Project project = this.projectRepository.findById(projectId).orElse(null);
//
//        if (project == null) {
//            throw new ResponseStatusException(
//                    HttpStatus.NOT_FOUND, "Project not found"
//            );
//        }

        // TODO I'm not sure whether to hide the submission or not (only grading?)

        // find submission
        Submission submission = this.submissionRepository.findById(submissionId).orElse(null);

        if (submission == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Submission not found"
            );
        }

        if (privileges.contains(SUBMISSION_READ_SINGLE)) {
            GradingParticipation grader = this.gradingParticipationService.getGradingParticipationByUserAndProject(graderId, projectId);
            if (grader == null || submission.getGrader() != null ||
                    !grader.getId().getUser().getId().equals(submission.getGrader().getId())) {
                throw new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Unauthorised"
                );
            }
        }

        // link submission's members
        submission = this.addSubmissionMembers(submission);

        // link submission's assessments
        submission = this.addSubmissionAssessments(submission);

        return submission;
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public Submission getSubmission(Long submissionId) throws JsonProcessingException, ResponseStatusException {
//        Project project = this.projectRepository.findById(projectId).orElse(null);
//
//        if (project == null) {
//            throw new ResponseStatusException(
//                    HttpStatus.NOT_FOUND, "Project not found"
//            );
//        }

        // TODO I'm not sure whether to hide the submission or not (only grading?)

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
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public Submission getSubmissionWithLock(Long submissionId) throws JsonProcessingException, ResponseStatusException {
//        Project project = this.projectRepository.findById(projectId).orElse(null);
//
//        if (project == null) {
//            throw new ResponseStatusException(
//                    HttpStatus.NOT_FOUND, "Project not found"
//            );
//        }

        // TODO I'm not sure whether to hide the submission or not (only grading?)

        // find submission
        Submission submission = this.submissionRepository.findSubmissionById(submissionId).orElse(null);

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
    @Transactional
    public Submission addSubmissionAssessments(Submission submission) {
        List<Assessment> assessments = this.assessmentService.getAssessmentsBySubmission(submission);
        submission.setAssessments(assessments
                .stream()
                .peek(assessment -> assessment.setIssues(issueRepository.findIssuesByAssessmentId(assessment.getId())))
                .collect(Collectors.toList())
        );
        return submission;
    }

    /*
    Saves the list of labels for the submission.
     */
    @Transactional
    public void saveLabels(Long projectId, Long graderId, Set<Label> labels, Long submissionId,
                           List<PrivilegeEnum> privileges) throws JsonProcessingException, ResponseStatusException {
        Submission submission = this.getSubmissionWithLock(submissionId);

        if (submission == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Submission not found"
            );
        }

        if (privileges.contains(SUBMISSION_EDIT_SINGLE)) {
            GradingParticipation grader = this.gradingParticipationService.getGradingParticipationByUserAndProject(graderId, projectId);
            if (grader == null || submission.getGrader() == null ||
                    !grader.getId().getUser().getId().equals(submission.getGrader().getId())) {
                throw new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Unauthorised"
                );
            }
        }

        submission.setLabels(labels);
        this.submissionRepository.save(submission);
    }

    /*
    Moves the submissions of graders NOT in the given list of users to 'unassigned'.
     */
    @Transactional
    public void dissociateSubmissionsFromUsers(List<User> users) {

//        Obtain write locks on these submissions
        List<Submission> submissions = submissionRepository.findAllByGraderIsNotIn(users)
                .stream().peek(submission -> submission.setGrader(null)).collect(Collectors.toList());
        submissionRepository.saveAll(submissions);

//        TODO check this whether you want to use saveAll or your custom update
//        this.submissionRepository.dissociateSubmissionsFromUsersNotInList(users);
    }

    /*
    Sets the user as a grader of the submission.
    */
    @Transactional
    public void assignSubmission(Long submissionId, User grader) throws JsonProcessingException {
        Submission submission = this.getSubmissionWithLock(submissionId);

        if (submission == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Submission not found"
            );
        }

        GradingParticipation grader1 = this.gradingParticipationService
                .getGradingParticipationByUserAndProjectWithLock(grader.getId(), submission.getProject().getId());

        if (grader1 == null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Grader is not found"
            );
        }
        submission.setGrader(grader);
        this.submissionRepository.save(submission);
    }

    /*
    Sets the user as a grader of the submission.
     */
    @Transactional
    public void dissociateSubmission(Long submissionId,
                                     List<PrivilegeEnum> privileges, Long graderId) throws JsonProcessingException {

//        TODO put locks and check only self remove
        Submission submission = this.getSubmissionWithLock(submissionId);

        if (submission == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Submission not found"
            );
        }

        if (privileges.contains(MANAGE_GRADERS_SELF_EDIT)) {
            if (submission.getGrader() == null || !submission.getGrader().getId().equals(graderId)) {

                throw new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Unauthorised"
                );
            }
        }

        submission.setGrader(null);
        this.submissionRepository.save(submission);
    }


    @Transactional(rollbackOn = Exception.class)
    public void assessmentManagement(Long courseId, Long projectId, Long submissionId,
                                                 JsonNode object, List<PrivilegeEnum> privileges, Long userId) throws JsonProcessingException, ResponseStatusException {

        Submission submission = getSubmissionWithLock(submissionId);
        if (submission == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "task not found"
            );
        }

        if (privileges != null && privileges.contains(SUBMISSION_EDIT_SINGLE)) {
            if (submission.getGrader() == null || !submission.getGrader().getId().equals(userId)) {
                throw new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "submission not assigned"
                );
            }
        }

        switch (object.get("action").asText()) {
            case "new": {
                Long participantId = object.get("participantId").asLong();
                CourseParticipation participant = courseParticipationRepository.findById_User_IdAndId_Course_Id(participantId, courseId);
                if (participant == null) {
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT, "participant not found"
                    );
                }
                this.assessmentService.createNewAssessment(
                        submission,
                        participant.getId().getUser()
                );

                break;
            }
            case "clone": {
                Long source = object.get("source").asLong();
                Long participantId = object.get("participantId").asLong();
                CourseParticipation participant = courseParticipationRepository.findById_User_IdAndId_Course_Id(participantId, courseId);
                if (participant == null) {
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT, "participant not found"
                    );
                }

                Assessment sourceAssignment = this.assessmentService.getAssessment(source);
                Assessment newAssignment = this.assessmentService.createNewAssessment(
                        new Assessment(
                                sourceAssignment.getGradedCount(),
                                sourceAssignment.getFinalGrade(),
                                sourceAssignment.getFinalGradeManual(),
                                sourceAssignment.getProject(),
                                new HashSet<>())
                );
                newAssignment = assessmentService.updateAssessment(newAssignment);
                if (newAssignment == null) {
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT, "newAssignment not found"
                    );
                }

                Assessment finalNewAssignment = newAssignment;
                Assessment finalNewAssignment1 = finalNewAssignment;
                Set<Grade> grades = sourceAssignment.getGrades()
                        .stream()
                        .peek(grade -> {
                            assessmentService.saveGrade(
                                    new Grade(
                                            grade.getGrade(),
                                            grade.getDescription(),
                                            grade.getActive(),
                                            grade.getCriterionId(),
                                            finalNewAssignment1,
                                            grade.getGrader(),
                                            grade.getGradedAt()
                                    )
                            );
                        })
                        .collect(Collectors.toSet());
                finalNewAssignment.setGrades(grades);

                finalNewAssignment = assessmentService.updateAssessment(finalNewAssignment);
                if (finalNewAssignment == null) {
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT, "finalNewAssignment not found"
                    );
                }
                this.assessmentService.cloneAssessment(submission, finalNewAssignment, participant);
                break;
            }
            case "move": {
                Long source = object.get("source").asLong();
                Long destination = object.get("destination").asLong();
                Long participantId = object.get("participantId").asLong();
                Assessment sourceAssignment = assessmentService.getAssessmentWithLock(source);
                Assessment destinationAssignment = assessmentService.getAssessmentWithLock(destination);
                if (sourceAssignment.getId().equals(destinationAssignment.getId())) {
                    return;
                }

                CourseParticipation participant = courseParticipationRepository.findById_User_IdAndId_Course_Id(participantId, courseId);

                Set<AssessmentLink> linkerSrcList = assessmentService.findAssessmentLinksByAssessmentIdWithLock(source);
                Set<AssessmentLink> linkerDesList = assessmentService.findAssessmentLinksByAssessmentIdWithLock(destination);
                AssessmentLink linkerSrc = null;
                for(AssessmentLink linker : linkerSrcList) {
                    if (linker.getId().getUser().getId().equals(participant.getId().getUser().getId())) {
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
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "not found info");
                }

                assessmentService.moveAssessment(linkerSrc, destinationAssignment, linkerSrcList);

                break;
            }
            case "active": {
                Long source = object.get("source").asLong();
                Long participantId = object.get("participantId").asLong();
                CourseParticipation participant = courseParticipationRepository.findById_User_IdAndId_Course_Id(participantId, courseId);
                Set<AssessmentLink> links = assessmentService.getAssessmentsByProjectAndUserWithLock(
                        projectId,
                        participant.getId().getUser()
                );
                links.forEach(assessmentLink -> {
                    assessmentLink.setCurrent(assessmentLink.getId().getAssessment().getId().equals(source));
                });
                assessmentService.saveInfoAssessments(links);
//                return assessmentService.getAssessmentsBySubmission(submission);
                break;
            }
            case "delete": {
                Long source = object.get("source").asLong();
                Set<AssessmentLink> linkers = assessmentService.findAssessmentLinksByAssessmentIdWithLock(source);
                for (AssessmentLink linker : linkers) {
                    if (linker.getId().getUser() != null) {
                        throw new ResponseStatusException(HttpStatus.CONFLICT, "cant remove assessment that has participants");
                    }
                }
                for (AssessmentLink linker : linkers) {
                    assessmentService.deleteAssessmentLinker(linker);
                }
                Assessment assessment = assessmentService.getAssessmentWithLock(source);
                if (assessment != null) {
                    assessmentService.deleteAssessment(assessment);
                } else {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "no assessment found");
                }
                break;
            }
        }
    }

    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    public List<Assessment> getAssessmentsBySubmission(Long submissionId) throws JsonProcessingException {
        Submission submission = getSubmission(submissionId);
        return assessmentService.getAssessmentsBySubmission(submission);
    }


    @Transactional(rollbackOn = Exception.class)
    public Submission addParticipantToSubmission(Long courseId, Long projectId, Long submissionId,
                                                 Long participantId, Long assessmentId,
                                          List<PrivilegeEnum> privileges, Long userId) throws JsonProcessingException, ResponseStatusException {

        Project project = this.projectRepository.findById(projectId).orElse(null);
        if (project == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "project not found"
            );
        }
        Submission submission = getSubmissionWithLock(submissionId);
        if (submission == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "task not found"
            );
        }
        Assessment assessment = assessmentService.getAssessmentWithLock(assessmentId);
        if (assessment == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "task not found"
            );
        }

        CourseParticipation participant = courseParticipationRepository
                .findById_User_IdAndId_Course_Id(participantId, courseId);

        if (participant == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "task not found"
            );
        }
        if (privileges != null && privileges.contains(SUBMISSION_EDIT_SINGLE)) {
            if (submission.getGrader() == null || !submission.getGrader().getId().equals(userId)) {
                throw new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "submission not assigned"
                );
            }
        }
        AssessmentLink assessmentLinker = assessmentService.createNewLinkIfNotExist(new AssessmentLink(
                participant.getId().getUser(),
                submission,
                assessment,
                assessmentService.findCurrentAssessmentUser(project, participant.getId().getUser()) == null
        ));

        if (assessmentLinker == null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "link exists"
            );
        }

        return getSubmission(submissionId);
    }

    @Transactional(rollbackOn = Exception.class)
    public Object removeParticipantFromSubmission(Long courseId, Long projectId, Long submissionId, Long participantId,
                                                 List<PrivilegeEnum> privileges, Long userId, boolean returnAll) throws JsonProcessingException, ResponseStatusException {

        Project project = this.projectRepository.findById(projectId).orElse(null);
        if (project == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "project not found"
            );
        }
        Submission submission = getSubmissionWithLock(submissionId);
        if (submission == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "task not found"
            );
        }

        CourseParticipation participant = courseParticipationRepository
                .findById_User_IdAndId_Course_Id(participantId, courseId);

        if (participant == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "task not found"
            );
        }
        if (privileges != null && privileges.contains(SUBMISSION_EDIT_SINGLE)) {
            if (submission.getGrader() == null || !submission.getGrader().getId().equals(userId)) {
                throw new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "submission not assigned"
                );
            }
        }

        Set<AssessmentLink> submissionAssessmentLinker = assessmentService
                .getAssessmentLinksBySubmissionWithLock(
                        submission);

        int participantCount = 0;
        for(AssessmentLink assessmentLinker: submissionAssessmentLinker) {
            if (assessmentLinker != null && assessmentLinker.getId().getUser() != null) {
                participantCount += 1;
            }
        }

        if (participantCount <= 1) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "submission only have 0-1 participant"
            );
        }

        AssessmentLink assessmentLinker = assessmentService
                .getAssessmentLinkForUserWithLock(submission.getId(), participant.getId().getUser().getId());
        if (assessmentLinker == null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "assessment not exists"
            );
        }

        assessmentService.deleteAssessmentLinker(assessmentLinker);
        if (!returnAll) {
            return getSubmission(submissionId);
        } else {
            return getSubmissionFromParticipants(projectId, userService.findById(participantId));
        }

//        return resultNode
    }

    @Transactional
    public List<Submission> getSubmissionFromParticipants(Long projectId, User user) throws ResponseStatusException {
        Set<AssessmentLink> links = assessmentService.getAssessmentsByProjectAndUser(projectId, user);
        System.out.println(links.size());
        List<Submission> submissions = new ArrayList<>();

        links.forEach(assessmentLink -> {
            boolean flag = false;
            for(Submission submission: submissions) {
                if (submission.getId().equals(assessmentLink.getId().getSubmission().getId())) {
                    flag = true;
                    if (!submission.isContainsCurrentAssessment()
                            && assessmentLink.isCurrent()) {
                        submission.setContainsCurrentAssessment(true);
                    }
                }
            }
            if (!flag) {
                Submission submission = assessmentLink.getId().getSubmission();
                submission.setContainsCurrentAssessment(assessmentLink.isCurrent());
                submissions.add(submission);
            }
        });
        return submissions;
    }
}