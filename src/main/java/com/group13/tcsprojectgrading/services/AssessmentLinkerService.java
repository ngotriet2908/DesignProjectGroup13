package com.group13.tcsprojectgrading.services;

import com.group13.tcsprojectgrading.models.AssessmentLinker;
import com.group13.tcsprojectgrading.models.Participant;
import com.group13.tcsprojectgrading.models.Submission;
import com.group13.tcsprojectgrading.repositories.AssessmentLinkerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssessmentLinkerService {
    private final AssessmentLinkerRepository repository;

    @Autowired
    public AssessmentLinkerService(AssessmentLinkerRepository repository) {
        this.repository = repository;
    }

    public AssessmentLinker addNewAssessment(AssessmentLinker assessmentLinker) {
        System.out.println("check point 5.5");
        List<AssessmentLinker> assessmentLinker1 = repository
                .findAssessmentsLinkersBySubmissionAndParticipant(assessmentLinker.getSubmission(), assessmentLinker.getParticipant());
        if (assessmentLinker1.size() != 0) return null;
        return repository.save(assessmentLinker);
    }

    public List<AssessmentLinker> findAssessmentLinkersForSubmission(Submission submission) {
        return repository.findAssessmentLinkersBySubmission(submission);
    }

    public List<AssessmentLinker> finaAssessmentLinkerForSubmissionAndParticipant(Submission submission, Participant participant) {
        return repository.findAssessmentsLinkersBySubmissionAndParticipant(submission, participant);
    }
}
