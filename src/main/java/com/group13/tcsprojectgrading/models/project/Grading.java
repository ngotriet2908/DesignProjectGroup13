package com.group13.tcsprojectgrading.models.project;

import com.fasterxml.jackson.annotation.*;
import com.group13.tcsprojectgrading.models.project.rubric.CriterionVersion;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Grading {
    @EmbeddedId
    private GradingKey id;

    @JsonIgnoreProperties({"gradings"})
    @ManyToOne
//    @MapsId("submissionId")
    @JoinColumns({
            @JoinColumn(name = "course_group_id", insertable = false, updatable = false),
            @JoinColumn(name = "project_id", insertable = false, updatable = false)
    })
    private Submission submission;

    @JsonIdentityInfo(generator= ObjectIdGenerators.PropertyGenerator.class, property="id", scope = CriterionVersion.class)
    @JsonIdentityReference(alwaysAsId=true)
    @ManyToOne
    @MapsId("criterionVersionId")
    @JoinColumn(name = "criterion_version_id")
    private CriterionVersion criterionVersion;

    @OneToMany(mappedBy = "grading")
    private Set<GradingVersion> gradingVersions;

    private Long currentGradingVersion;

    public Grading(Submission submission, CriterionVersion criterionVersion, Long current_grading_version) {
        this.submission = submission;
        this.criterionVersion = criterionVersion;
        this.currentGradingVersion = current_grading_version;
        this.id = new GradingKey(submission.getId().getProjectId(), submission.getId().getCourseGroupId(), criterionVersion.getId());
    }

    public Grading(Submission submission, CriterionVersion criterionVersion) {
        this.submission = submission;
        this.criterionVersion = criterionVersion;
        this.id = new GradingKey(submission.getId().getProjectId(), submission.getId().getCourseGroupId(), criterionVersion.getId());
    }

    public Grading() {
    }

    public GradingKey getId() {
        return id;
    }

    public void setId(GradingKey id) {
        this.id = id;
    }

    public Submission getSubmission() {
        return submission;
    }

    public void setSubmission(Submission submission) {
        this.submission = submission;
    }

    public CriterionVersion getCriterionVersion() {
        return criterionVersion;
    }

    public void setCriterionVersion(CriterionVersion criterionVersion) {
        this.criterionVersion = criterionVersion;
    }

    public Set<GradingVersion> getGradingVersions() {
        return gradingVersions;
    }

    public void setGradingVersions(Set<GradingVersion> gradingVersions) {
        this.gradingVersions = gradingVersions;
    }

    public Long getCurrentGradingVersion() {
        return currentGradingVersion;
    }

    public void setCurrentGradingVersion(Long currentGradingVersion) {
        this.currentGradingVersion = currentGradingVersion;
    }

    @Override
    public String toString() {
        return "Grading{" +
                "id=" + id +
                ", submission=" + submission +
                ", criterionVersion=" + criterionVersion +
                ", current_grading_version=" + currentGradingVersion +
                '}';
    }
}
