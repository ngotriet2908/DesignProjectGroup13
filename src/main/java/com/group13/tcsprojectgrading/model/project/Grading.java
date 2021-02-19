package com.group13.tcsprojectgrading.model.project;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.group13.tcsprojectgrading.model.project.rubric.Criterion;
import com.group13.tcsprojectgrading.model.project.rubric.RubricVersion;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Set;

@Entity
public class Grading {
    @EmbeddedId
    private SubmissionKey id;

    @ManyToOne
    @MapsId("submissionId")
    @JoinColumns({
            @JoinColumn(name = "project_id", insertable = false, updatable = false),
            @JoinColumn(name = "course_group_id", insertable = false, updatable = false)
    })
    private Submission submission;

    @ManyToOne
    @MapsId("criterionId")
    @JoinColumn(name = "criterion_id")
    private Criterion criterion;

    @JsonIgnore
    @OneToMany(mappedBy = "grading")
    private Set<GradingVersion> gradingVersions;

    public Grading(SubmissionKey id, Submission submission, Criterion criterion, Set<GradingVersion> gradingVersions) {
        this.id = id;
        this.submission = submission;
        this.criterion = criterion;
        this.gradingVersions = gradingVersions;
    }

    public Grading() {
    }

    public SubmissionKey getId() {
        return id;
    }

    public void setId(SubmissionKey id) {
        this.id = id;
    }

    public Submission getSubmission() {
        return submission;
    }

    public void setSubmission(Submission submission) {
        this.submission = submission;
    }

    public Criterion getCriterion() {
        return criterion;
    }

    public void setCriterion(Criterion criterion) {
        this.criterion = criterion;
    }

    public Set<GradingVersion> getGradingVersions() {
        return gradingVersions;
    }

    public void setGradingVersions(Set<GradingVersion> gradingVersions) {
        this.gradingVersions = gradingVersions;
    }

    @Override
    public String toString() {
        return "Grading{" +
                "id=" + id +
                ", submission=" + submission +
                ", criterion=" + criterion +
                ", gradingVersions=" + gradingVersions +
                '}';
    }
}
