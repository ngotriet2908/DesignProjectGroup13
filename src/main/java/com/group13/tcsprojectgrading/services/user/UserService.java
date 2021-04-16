package com.group13.tcsprojectgrading.services.user;

import com.group13.tcsprojectgrading.models.grading.Issue;
import com.group13.tcsprojectgrading.models.project.Project;
import com.group13.tcsprojectgrading.models.user.User;
import com.group13.tcsprojectgrading.repositories.graders.GradingParticipationRepository;
import com.group13.tcsprojectgrading.repositories.grading.IssueRepository;
import com.group13.tcsprojectgrading.repositories.user.UserRepository;
import com.group13.tcsprojectgrading.services.grading.AssessmentService;
import com.group13.tcsprojectgrading.services.submissions.SubmissionService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.*;

/**
 * Service handlers operations relating to user
 */
@Service
public class UserService {
    private final SubmissionService submissionService;
    private final UserRepository userRepository;
    private final GradingParticipationRepository gradingParticipationRepository;
    private final IssueRepository issueRepository;

    public UserService(SubmissionService submissionService, UserRepository userRepository,
                       GradingParticipationRepository gradingParticipationRepository,
                       IssueRepository issueRepository) {
        this.submissionService = submissionService;
        this.userRepository = userRepository;
        this.gradingParticipationRepository = gradingParticipationRepository;

        this.issueRepository = issueRepository;
    }

    /**
     * save user in database
     * @param user user entity
     */
    @Transactional(rollbackOn = Exception.class)
    public void saveUser(User user) {
        //Obtain write lock on user
        User user1 = userRepository.findUserById(user.getId()).orElse(null);

        this.userRepository.save(user);
    }

    /**
     * find user by id
     * @param userId canvas user id
     * @return user entity
     */
    @Transactional
    public User findById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Transactional
    public User getOne(Long userId) {
        return userRepository.getOne(userId);
    }

    /**
     * Returns a list of projects in which the user has submissions to grade.
     * @param userId canvas user id
     * @return list of projects
     * @throws ResponseStatusException response exception
     */
    @Transactional
    public Collection<Project> getTodoForUser(Long userId) throws ResponseStatusException{
        User user = userRepository.getOne(userId);
        if (user == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "User not found"
            );
        }

        Map<Long, Project> projectMap = new HashMap<>();
        submissionService.findSubmissionsForGrader(userId)
                .forEach(submission -> {
                    submission = submissionService.addTodoSubmissionAssessments(submission);
                    System.out.println(submission.getAssessments().size());
                    if (!projectMap.containsKey(submission.getProject().getId())) {
                        if (submission.getProject().getGradingTasks() == null) {
                            if (
                                    !submission.getAssessments().stream()
                                    .map(assessment -> {
                                        return assessment.getProgress() == 100;
                                    })
                                    .reduce(true, (value, value1) -> value = value & value1)
                            ) {
                                submission.getProject().setGradingTasks(1L);
                            } else {
                                submission.getProject().setGradingTasks(0L);
                            }
                        }
                        if (submission.getProject().getIssues() == null) {
                            Integer issuesNum = submission.getAssessments()
                                    .stream()
                                    .map(assessment -> assessment.getIssues().size())
                                    .reduce((integer, integer2) -> integer += integer2)
                                    .orElse(null);
                            if (issuesNum == null) {
                                submission.getProject().setIssues(0L);
                            } else {
                                submission.getProject().setIssues(Long.valueOf(issuesNum));
                            }
                        }
                        projectMap.put(submission.getProject().getId(), submission.getProject());
                    } else {
                        Project project = projectMap.get(submission.getProject().getId());
                        if (
                                !submission.getAssessments().stream()
                                        .map(assessment -> assessment.getProgress() == 100)
                                        .reduce(true, (value, value1) -> value = value & value1)
                        ) {
                            project.setGradingTasks(project.getGradingTasks() + 1);
                        }

                        submission.getAssessments()
                                .stream()
                                .map(assessment -> assessment.getIssues().size())
                                .reduce((integer, integer2) -> integer += integer2)
                                .ifPresent(issuesNum -> project.setIssues(project.getIssues() + Long.valueOf(issuesNum)));
                    }
                });
        return projectMap.values();
    }

    /**
     * Returns a list of issues that have the user as the author or addressee.
     * @param userId canvas user id
     * @return list of issue
     * @throws ResponseStatusException response exception
     */
    @Transactional
    public Collection<Issue> getIssuesForUser(Long userId) throws ResponseStatusException{
        User user = userRepository.getOne(userId);

        if (user == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "User not found"
            );
        }

        return this.issueRepository.findIssuesByCreatorOrAddressee(user, user);
    }
}
