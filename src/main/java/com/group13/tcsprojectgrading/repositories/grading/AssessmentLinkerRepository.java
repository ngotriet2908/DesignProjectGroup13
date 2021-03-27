package com.group13.tcsprojectgrading.repositories.grading;

import com.group13.tcsprojectgrading.models.grading.AssessmentLinker;
import com.group13.tcsprojectgrading.models.Participant;
import com.group13.tcsprojectgrading.models.submissions.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AssessmentLinkerRepository extends JpaRepository<AssessmentLinker, UUID> {
    public List<AssessmentLinker> findAssessmentLinkersBySubmission(Submission submission);
    public List<AssessmentLinker> findAssessmentLinkersByParticipant(Participant participant);
    public List<AssessmentLinker> findAssessmentLinkersByAssessmentId(UUID assessmentId);
    public List<AssessmentLinker> findAssessmentsLinkersBySubmissionAndParticipant(Submission submission, Participant participant);
    public AssessmentLinker findAssessmentsLinkerBySubmissionAndParticipantAndAssessmentId(Submission submission, Participant participant, UUID assessmentId);

}
