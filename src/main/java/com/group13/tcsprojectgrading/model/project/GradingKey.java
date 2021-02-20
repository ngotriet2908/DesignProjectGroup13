package com.group13.tcsprojectgrading.model.project;

import com.group13.tcsprojectgrading.model.user.ParticipantKey;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class GradingKey implements Serializable {

//    @Column(name = "submission_id")
//    private SubmissionKey submissionId;
    @Column(name = "project_id")
    private Long projectId;

    @Column(name = "course_group_id")
    private Long courseGroupId;

    @Column(name = "criterion_version_id")
    private Long criterionVersionId;

    public GradingKey(Long projectId, Long courseGroupId, Long criterionVersionId) {
        this.projectId = projectId;
        this.courseGroupId = courseGroupId;
        this.criterionVersionId = criterionVersionId;
    }

    public GradingKey() {
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getCourseGroupId() {
        return courseGroupId;
    }

    public void setCourseGroupId(Long courseGroupId) {
        this.courseGroupId = courseGroupId;
    }

    public Long getCriterionVersionId() {
        return criterionVersionId;
    }

    public void setCriterionVersionId(Long criterionVersionId) {
        this.criterionVersionId = criterionVersionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GradingKey that = (GradingKey) o;
        return Objects.equals(projectId, that.projectId) && Objects.equals(courseGroupId, that.courseGroupId) && Objects.equals(criterionVersionId, that.criterionVersionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectId, courseGroupId, criterionVersionId);
    }

    @Override
    public String toString() {
        return "GradingKey{" +
                "projectId=" + projectId +
                ", courseGroupId=" + courseGroupId +
                ", criterionVersionId=" + criterionVersionId +
                '}';
    }
}