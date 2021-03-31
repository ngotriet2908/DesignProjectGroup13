package com.group13.tcsprojectgrading.services;

import com.group13.tcsprojectgrading.models.AssessmentLinker;
import com.group13.tcsprojectgrading.models.Participant;
import com.group13.tcsprojectgrading.models.Submission;
import com.group13.tcsprojectgrading.repositories.AssessmentLinkerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@Service
public class AssessmentLinkerService {
    private final AssessmentLinkerRepository repository;

    @Autowired
    public AssessmentLinkerService(AssessmentLinkerRepository repository) {
        this.repository = repository;
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public AssessmentLinker addNewAssessment(AssessmentLinker assessmentLinker) {
        System.out.println("check point 5.5");
        List<AssessmentLinker> assessmentLinker1 = repository
                .findAssessmentsLinkersBySubmissionAndParticipant(assessmentLinker.getSubmission(), assessmentLinker.getParticipant());
        if (assessmentLinker1.size() != 0) return null;
        return repository.save(assessmentLinker);
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public AssessmentLinker addNewNullAssessment(AssessmentLinker assessmentLinker) {
        return repository.save(assessmentLinker);
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public AssessmentLinker saveInfoAssessment(AssessmentLinker assessmentLinker) {
        AssessmentLinker assessmentLinker1 = repository.findById(assessmentLinker.getId()).orElse(null);
//        if (assessmentLinker1 == null) return null;
        if (!assessmentLinker1.getId().equals(assessmentLinker.getId())) return null;
        return repository.save(assessmentLinker);
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public List<AssessmentLinker> findAssessmentLinkersForAssessmentId(String assessmentId) {
        return repository.findAssessmentLinkersByAssessmentId(UUID.fromString(assessmentId));
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public List<AssessmentLinker> findAssessmentLinkersForSubmission(Submission submission) {
        return repository.findAssessmentLinkersBySubmission(submission);
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public List<AssessmentLinker> findAssessmentLinkersForParticipant(Participant participant) {
        return repository.findAssessmentLinkersByParticipant(participant);
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public List<AssessmentLinker> finaAssessmentLinkerForSubmissionAndParticipant(Submission submission, Participant participant) {
        return repository.findAssessmentsLinkersBySubmissionAndParticipant(submission, participant);
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public void deleteAssessmentLinker(AssessmentLinker assessmentLinker) {
        repository.delete(assessmentLinker);
    }

}
