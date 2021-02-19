package com.group13.tcsprojectgrading.model.project;

import com.group13.tcsprojectgrading.model.user.ParticipantKey;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class GradingKey implements Serializable {

    @Column(name = "submission_id")
    private String submissionId;

    @Column(name = "criterion_id")
    private Long criterionId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass())
            return false;

        GradingKey that = (GradingKey) o;
        return Objects.equals(submissionId, that.submissionId) &&
                Objects.equals(criterionId, that.criterionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(submissionId, criterionId);
    }

    public GradingKey(String submissionId, Long criterionId) {
        this.submissionId = submissionId;
        this.criterionId = criterionId;
    }

    public GradingKey() {
    }

    public String getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(String submissionId) {
        this.submissionId = submissionId;
    }

    public Long getCriterionId() {
        return criterionId;
    }

    public void setCriterionId(Long criterionId) {
        this.criterionId = criterionId;
    }

    @Override
    public String toString() {
        return "GradingKey{" +
                "submissionId='" + submissionId + '\'' +
                ", criterionId=" + criterionId +
                '}';
    }
}