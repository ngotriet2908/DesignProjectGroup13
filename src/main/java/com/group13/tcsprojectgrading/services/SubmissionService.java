package com.group13.tcsprojectgrading.services;

import com.group13.tcsprojectgrading.models.Project;
import com.group13.tcsprojectgrading.models.Submission;
import com.group13.tcsprojectgrading.models.SubmissionId;
import com.group13.tcsprojectgrading.repositories.SubmissionRepository;
import com.group13.tcsprojectgrading.repositories.grading.GradingRepository;
import com.group13.tcsprojectgrading.services.grading.GradingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubmissionService {
    private final SubmissionRepository repository;
    private final GradingService gradingService;

    @Autowired
    public SubmissionService(SubmissionRepository repository, GradingService gradingService) {
        this.repository = repository;
        this.gradingService = gradingService;
    }

    public Submission addNewSubmission(Submission submission) {
        Submission currentSubmission = repository.findById(
                new SubmissionId(
                        submission.getId(),
                        submission.getProject().getProjectCompositeKey()
                )
        ).orElse(null);

        if (currentSubmission != null) {
            submission.setGrader(currentSubmission.getGrader());
        } else {
            gradingService.saveAssessment(submission.getProject().getProjectId(), submission.getId(), submission);
        }
        return repository.save(submission);
    }

    public List<Submission> findSubmissionWithProject(Project project) {
        return repository.findSubmissionsByProject(project);
    }

    public void deleteSubmission(Submission submission) {
        gradingService.deleteAssessment(submission.getProject().getProjectId(), submission.getId());
        repository.delete(submission);
    }

    public Submission findSubmissionById(String user_id, Project project) {
        return repository.findById(new SubmissionId(user_id, project.getProjectCompositeKey())).orElse(null);
    }

    public List<Submission> findSubmissionsForGrader(Project project, String graderId) {
        return repository.findSubmissionsByProjectAndGrader_UserId(project, graderId);
    }

    public List<Submission> findSubmissionsForGraderAll(String graderId) {
        return repository.findSubmissionsByGrader_UserId(graderId);
    }
}
