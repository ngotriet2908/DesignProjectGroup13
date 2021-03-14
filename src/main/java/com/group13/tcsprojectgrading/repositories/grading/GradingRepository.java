package com.group13.tcsprojectgrading.repositories.grading;


import com.group13.tcsprojectgrading.models.grading.SubmissionAssessment;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GradingRepository extends MongoRepository<SubmissionAssessment, String> {
    SubmissionAssessment getBySubmissionId(String submissionId);
}

