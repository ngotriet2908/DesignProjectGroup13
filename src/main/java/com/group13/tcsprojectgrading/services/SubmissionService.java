package com.group13.tcsprojectgrading.services;

import com.group13.tcsprojectgrading.models.Project;
import com.group13.tcsprojectgrading.models.Submission;
import com.group13.tcsprojectgrading.models.SubmissionId;
import com.group13.tcsprojectgrading.repositories.SubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubmissionService {
    private final SubmissionRepository repository;

    @Autowired
    public SubmissionService(SubmissionRepository repository) {
        this.repository = repository;
    }

    public Submission addNewSubmission(Submission submission) {
        repository.findById(
                new SubmissionId(
                        submission.getId(),
                        submission.getProject().getProjectCompositeKey()
                )
        ).ifPresent(current_Submission -> submission.setGrader(current_Submission.getGrader()));
        return repository.save(submission);
    }

    public List<Submission> findSubmissionWithProject(Project project) {
        return repository.findSubmissionsByProject(project);
    }

    public void deleteSubmission(Submission submission) {
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
