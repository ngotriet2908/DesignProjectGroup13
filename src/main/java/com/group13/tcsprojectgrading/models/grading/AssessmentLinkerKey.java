package com.group13.tcsprojectgrading.models.grading;

import com.group13.tcsprojectgrading.models.ParticipantId;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class AssessmentLinkerKey implements Serializable {
    private ParticipantId participantId;
    private UUID submissionId;

    public AssessmentLinkerKey(ParticipantId participantId, UUID submissionId) {
        this.participantId = participantId;
        this.submissionId = submissionId;
    }

    public AssessmentLinkerKey() {
    }

    public ParticipantId getParticipantId() {
        return participantId;
    }

    public void setParticipantId(ParticipantId participantId) {
        this.participantId = participantId;
    }

    public UUID getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(UUID submissionId) {
        this.submissionId = submissionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssessmentLinkerKey that = (AssessmentLinkerKey) o;
        return Objects.equals(participantId, that.participantId) && Objects.equals(submissionId, that.submissionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(participantId, submissionId);
    }
}
