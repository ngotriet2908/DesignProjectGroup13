package com.group13.tcsprojectgrading.models;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

public class ProjectId implements Serializable {

    @Id
    private String courseId;

    @Id
    private String projectId;

    public ProjectId(String courseId, String projectId) {
        this.courseId = courseId;
        this.projectId = projectId;
    }

    public ProjectId() {
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectId projectId1 = (ProjectId) o;
        return Objects.equals(courseId, projectId1.courseId) && Objects.equals(projectId, projectId1.projectId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseId, projectId);
    }

    @Override
    public String toString() {
        return "ProjectId{" +
                "courseId='" + courseId + '\'' +
                ", projectId='" + projectId + '\'' +
                '}';
    }
}
