package com.group13.tcsprojectgrading.repositories.grading;

import com.group13.tcsprojectgrading.models.grading.SubmissionAssessment;
import com.group13.tcsprojectgrading.models.grading.SubmissionAssessmentKey;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GradingRepository extends MongoRepository<SubmissionAssessment, SubmissionAssessmentKey> {
    SubmissionAssessment getByProjectIdAndUserId(String projectId, String userId);
    void deleteByProjectIdAndUserId(String projectId, String userId);
}

