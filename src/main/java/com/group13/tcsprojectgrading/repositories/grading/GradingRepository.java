package com.group13.tcsprojectgrading.repositories.grading;

import com.group13.tcsprojectgrading.models.grading.SubmissionAssessment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface GradingRepository extends MongoRepository<SubmissionAssessment, SubmissionAssessment.SubmissionAssessmentKey> {
    SubmissionAssessment getById(SubmissionAssessment.SubmissionAssessmentKey id);

    List<SubmissionAssessment> findAll();
    void deleteById(SubmissionAssessment.SubmissionAssessmentKey id);
}

