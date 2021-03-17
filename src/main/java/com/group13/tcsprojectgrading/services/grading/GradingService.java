package com.group13.tcsprojectgrading.services.grading;

import com.group13.tcsprojectgrading.models.grading.SubmissionAssessment;
import com.group13.tcsprojectgrading.repositories.grading.GradingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GradingService {
    private final GradingRepository repository;

    @Autowired
    public GradingService(GradingRepository repository) {
        this.repository = repository;
    }

    public SubmissionAssessment getAssessmentByProjectIdAndUserId(String projectId, String userId) {
        return repository.getById(new SubmissionAssessment.SubmissionAssessmentKey(projectId, userId));
    }

    public SubmissionAssessment saveAssessment(String projectId, String userId) {
        return repository.save(new SubmissionAssessment(new SubmissionAssessment.SubmissionAssessmentKey(projectId, userId))
        );
    }

    public SubmissionAssessment saveAssessment(SubmissionAssessment assessment) {
        return repository.save(assessment);
    }

    public void deleteAssessment(SubmissionAssessment assessment) {
        repository.delete(assessment);
    }

    public void deleteAssessment(String projectId, String userId) {
        repository.deleteById(new SubmissionAssessment.SubmissionAssessmentKey(projectId, userId));
    }
}

