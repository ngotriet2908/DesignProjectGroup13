package com.group13.tcsprojectgrading.services.user;

import com.group13.tcsprojectgrading.models.project.Project;
import com.group13.tcsprojectgrading.models.user.User;
import com.group13.tcsprojectgrading.repositories.graders.GradingParticipationRepository;
import com.group13.tcsprojectgrading.repositories.user.UserRepository;
import com.group13.tcsprojectgrading.services.submissions.SubmissionService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.*;

@Service
public class UserService {
    private final SubmissionService submissionService;
    private final UserRepository userRepository;
    private final GradingParticipationRepository gradingParticipationRepository;

    public UserService(SubmissionService submissionService, UserRepository userRepository, GradingParticipationRepository gradingParticipationRepository) {
        this.submissionService = submissionService;
        this.userRepository = userRepository;
        this.gradingParticipationRepository = gradingParticipationRepository;
    }

    @Transactional(rollbackOn = Exception.class)
    public void saveUser(User user) {
        //Obtain write lock on user
        User user1 = userRepository.findUserById(user.getId()).orElse(null);

        this.userRepository.save(user);
    }

    @Transactional
    public User findById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Transactional
    public User getOne(Long userId) {
        return userRepository.getOne(userId);
    }

    @Transactional
    public Collection<Project> getTodoForUser(Long userId) throws ResponseStatusException{
        User user = userRepository.getOne(userId);
        if (user == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "user not found"
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
                                        System.out.println(assessment.getProgress() + "%");
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
}
