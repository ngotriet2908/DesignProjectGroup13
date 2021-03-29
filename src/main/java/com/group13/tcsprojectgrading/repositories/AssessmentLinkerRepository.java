package com.group13.tcsprojectgrading.repositories;

import com.group13.tcsprojectgrading.models.AssessmentLinker;
import com.group13.tcsprojectgrading.models.AssessmentLinkerId;
import com.group13.tcsprojectgrading.models.Participant;
import com.group13.tcsprojectgrading.models.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssessmentLinkerRepository extends JpaRepository<AssessmentLinker, AssessmentLinkerId> {
    List<AssessmentLinker> findAssessmentLinkersBySubmission(Submission submission);
    List<AssessmentLinker> findAssessmentLinkersByParticipant(Participant participant);
    List<AssessmentLinker> findAssessmentsLinkersBySubmissionAndParticipant(Submission submission, Participant participant);

}
