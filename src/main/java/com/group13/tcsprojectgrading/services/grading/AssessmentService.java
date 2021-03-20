package com.group13.tcsprojectgrading.services.grading;

import com.group13.tcsprojectgrading.models.Assessment;
import com.group13.tcsprojectgrading.models.AssessmentLinker;
import com.group13.tcsprojectgrading.models.Participant;
import com.group13.tcsprojectgrading.models.Submission;
import com.group13.tcsprojectgrading.repositories.grading.AssessmentRepository;
import com.group13.tcsprojectgrading.services.AssessmentLinkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AssessmentService {

    private final AssessmentRepository repository;
    private final AssessmentLinkerService service;

    @Autowired
    public AssessmentService(AssessmentRepository repository, AssessmentLinkerService service) {
        this.repository = repository;
        this.service = service;
    }

    public Assessment getAssessmentBySubmissionAndParticipant(Submission submission, Participant participant) {
        List<AssessmentLinker> linkers = service.finaAssessmentLinkerForSubmissionAndParticipant(submission, participant);
        if (linkers.size() != 1) return null;
        return repository.findById(linkers.get(0).getAssessmentId()).orElse(null);
    }

    public List<Assessment> getAssessmentBySubmission(Submission submission) {
        List<AssessmentLinker> linkers = service.findAssessmentLinkersForSubmission(submission);
        List<Assessment> assessments = new ArrayList<>();
        for(AssessmentLinker linker: linkers) {
            Assessment assessment = repository.findById(linker.getAssessmentId()).orElse(null);
            if (assessment == null) continue;
            if (!assessments.contains(assessment)) {
                assessments.add(assessment);
            }
        }

        return assessments;
    }

    public Assessment getAssessmentById(String id) {
        return repository.findById(UUID.fromString(id)).orElse(null);
    }

    public Assessment saveAssessment(Assessment assessment) {
        return repository.save(assessment);
    }

    public Assessment saveAssessment(AssessmentLinker assessmentLinker) {
        return repository.save(new Assessment(assessmentLinker.getAssessmentId()));
    }

    public void deleteAssessment(AssessmentLinker assessmentLinker) {
        repository.delete(findAssessment(assessmentLinker));
    }

    public Assessment findAssessment(AssessmentLinker assessmentLinker) {
        return repository.findById(assessmentLinker.getAssessmentId()).orElse(null);
    }
}
