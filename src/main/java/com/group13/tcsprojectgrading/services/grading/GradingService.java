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

    public SubmissionAssessment getAssessmentByTaskId(String submissionId) {
        return repository.getBySubmissionId(submissionId);
    }
}

