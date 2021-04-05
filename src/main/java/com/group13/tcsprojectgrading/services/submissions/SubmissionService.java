package com.group13.tcsprojectgrading.services.submissions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.group13.tcsprojectgrading.models.grading.AssessmentLink;
import com.group13.tcsprojectgrading.models.project.Project;
import com.group13.tcsprojectgrading.models.submissions.Label;
import com.group13.tcsprojectgrading.models.user.User;
import com.group13.tcsprojectgrading.models.graders.GradingParticipation;
import com.group13.tcsprojectgrading.models.grading.Assessment;
import com.group13.tcsprojectgrading.models.submissions.Submission;
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
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SubmissionService {
    private final SubmissionRepository submissionRepository;
    private final ProjectRepository projectRepository;
    private final GradingParticipationService gradingParticipationService;
    private final RubricService rubricService;
    private final AssessmentService assessmentService;
    private final SubmissionDetailsService submissionDetailsService;
    private final UserService userService;

    @Autowired
    public SubmissionService(SubmissionRepository submissionRepository, ProjectRepository projectRepository,
                             GradingParticipationService gradingParticipationService, RubricService rubricService,
                             @Lazy AssessmentService assessmentService, SubmissionDetailsService submissionDetailsService,
                             @Lazy UserService userService) {
        this.submissionRepository = submissionRepository;
        this.projectRepository = projectRepository;
        this.gradingParticipationService = gradingParticipationService;
        this.rubricService = rubricService;
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

    /*
    Returns all submissions in the project (if the user is a grader inside that project).
     */
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

    /*
    Saves the list of labels for the submission.
     */
    @Transactional
    public void saveLabels(Set<Label> labels, Long submissionId) throws JsonProcessingException {
        Submission submission = this.getSubmission(submissionId);

        if (submission == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Submission not found"
            );
        }

        submission.setLabels(labels);
        this.submissionRepository.save(submission);
    }

    /*
    Moves the submissions of graders NOT in the given list of users to 'unassigned'.
     */
    @Transactional
    public void dissociateSubmissionsFromUsers(List<User> users) {
        this.submissionRepository.dissociateSubmissionsFromUsersNotInList(users);
    }

    /*
    Sets the user as a grader of the submission.
     */
    @Transactional
    public void assignSubmission(Long submissionId, User grader) throws JsonProcessingException {
        Submission submission = this.getSubmission(submissionId);

        if (submission == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Submission not found"
            );
        }

        submission.setGrader(grader);
        this.submissionRepository.save(submission);
    }

    /*
    Sets the user as a grader of the submission.
     */
    @Transactional
    public void dissociateSubmission(Long submissionId) throws JsonProcessingException {
        Submission submission = this.getSubmission(submissionId);

        if (submission == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Submission not found"
            );
        }

        submission.setGrader(null);
        this.submissionRepository.save(submission);
    }
}