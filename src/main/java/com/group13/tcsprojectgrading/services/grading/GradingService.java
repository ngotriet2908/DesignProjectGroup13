package com.group13.tcsprojectgrading.services.grading;

import com.group13.tcsprojectgrading.models.Project;
import com.group13.tcsprojectgrading.models.Submission;
import com.group13.tcsprojectgrading.models.grading.SubmissionAssessment;
import com.group13.tcsprojectgrading.models.rubric.Rubric;
import com.group13.tcsprojectgrading.repositories.ProjectRepository;
import com.group13.tcsprojectgrading.repositories.SubmissionRepository;
import com.group13.tcsprojectgrading.repositories.grading.GradingRepository;
import com.group13.tcsprojectgrading.repositories.rubric.RubricRepository;
import com.group13.tcsprojectgrading.services.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GradingService {
    private final GradingRepository repository;
    private final RubricRepository rubricRepo;
    private final SubmissionRepository subRepo;
    private final ProjectService projectService;

    @Autowired
    public GradingService(GradingRepository repository, RubricRepository rubricRepo, SubmissionRepository subRepo,
                          ProjectService projectService) {
        this.repository = repository;
        this.rubricRepo = rubricRepo;
        this.subRepo = subRepo;
        this.projectService = projectService;
    }

    public SubmissionAssessment getAssessmentByProjectIdAndUserId(String projectId, String userId) {
        return repository.getById(new SubmissionAssessment.SubmissionAssessmentKey(projectId, userId));
    }

    public SubmissionAssessment saveAssessment(String projectId, String userId, Submission submission) {
        SubmissionAssessment assessment = new SubmissionAssessment(
                new SubmissionAssessment.SubmissionAssessmentKey(projectId, userId));
        assessment.setSubmission(submission);
        return repository.save(assessment);
    }

    public SubmissionAssessment saveAssessment(SubmissionAssessment assessment) {
        assessment.setProgress(computeProgress(assessment));
        projectService.updateProgress(assessment.getSubmission().getProject(), assessment.getProgress());
        return repository.save(assessment);
    }

    public void deleteAssessment(SubmissionAssessment assessment) {
        repository.delete(assessment);
    }

    public void deleteAssessment(String projectId, String userId) {
        repository.deleteById(new SubmissionAssessment.SubmissionAssessmentKey(projectId, userId));
    }

    public Rubric getRubric(SubmissionAssessment assessment) {
        return rubricRepo.getById(assessment.getId().getProjectId()); //Same as rubric id
    }

    public int getGradedCount(SubmissionAssessment assessment) {
        return assessment.getGrades().size();
    }

    public double computeProgress(SubmissionAssessment assessment) {
        Rubric rubric = getRubric(assessment);
        int graded = getGradedCount(assessment);
        int total = rubric.getCriterionCount();
        return graded/(double) total;
    }
}

