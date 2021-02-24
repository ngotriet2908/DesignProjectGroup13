package com.group13.tcsprojectgrading.models.project;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class SubmissionKey implements Serializable {

    @Column(name = "project_id")
    private Long projectId;

    @Column(name = "course_group_id")
    private Long courseGroupId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass())
            return false;

        SubmissionKey that = (SubmissionKey) o;
        return Objects.equals(projectId, that.projectId) &&
                Objects.equals(courseGroupId, that.courseGroupId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectId, courseGroupId);
    }

    public SubmissionKey() {
    }

    public SubmissionKey(Long projectId, Long courseGroupId) {
        this.projectId = projectId;
        this.courseGroupId = courseGroupId;
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

    @Override
    public String toString() {
        return "SubmissionKey{" +
                "projectId='" + projectId + '\'' +
                ", courseGroupId='" + courseGroupId + '\'' +
                '}';
    }
}
