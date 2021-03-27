package com.group13.tcsprojectgrading.models.grading;

import com.group13.tcsprojectgrading.models.ParticipantId;
import com.group13.tcsprojectgrading.models.submissions.Submission;

import java.io.Serializable;
import java.util.Objects;

public class AssessmentLinkerId implements Serializable {
    private Submission submission;
    private ParticipantId participant;

    public AssessmentLinkerId(Submission submission, ParticipantId participant) {
        this.submission = submission;
        this.participant = participant;
    }

    public AssessmentLinkerId() {
    }

    public Submission getSubmission() {
        return submission;
    }

    public void setSubmission(Submission submission) {
        this.submission = submission;
    }

    public ParticipantId getParticipant() {
        return participant;
    }

    public void setParticipant(ParticipantId participant) {
        this.participant = participant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssessmentLinkerId that = (AssessmentLinkerId) o;
        return Objects.equals(submission, that.submission) && Objects.equals(participant, that.participant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(submission, participant);
    }
}
