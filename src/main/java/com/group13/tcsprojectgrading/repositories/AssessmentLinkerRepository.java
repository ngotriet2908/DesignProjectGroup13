package com.group13.tcsprojectgrading.repositories;

import com.group13.tcsprojectgrading.models.AssessmentLinker;
import com.group13.tcsprojectgrading.models.AssessmentLinkerId;
import com.group13.tcsprojectgrading.models.Participant;
import com.group13.tcsprojectgrading.models.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AssessmentLinkerRepository extends JpaRepository<AssessmentLinker, UUID> {
    public List<AssessmentLinker> findAssessmentLinkersBySubmission(Submission submission);
    public List<AssessmentLinker> findAssessmentLinkersByParticipant(Participant participant);
    public List<AssessmentLinker> findAssessmentLinkersByAssessmentId(UUID assessmentId);
    public List<AssessmentLinker> findAssessmentsLinkersBySubmissionAndParticipant(Submission submission, Participant participant);

}
