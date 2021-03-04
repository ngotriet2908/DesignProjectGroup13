package com.group13.tcsprojectgrading.models;

import java.io.Serializable;
import java.util.Objects;

public class ActivityId implements Serializable {
    private ProjectId project;
    private String userId;

    public ActivityId() {
    }

    public ActivityId(ProjectId project, String userId) {
        this.project = project;
        this.userId = userId;
    }

    public ProjectId getProject() {
        return project;
    }

    public void setProject(ProjectId project) {
        this.project = project;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActivityId that = (ActivityId) o;
        return Objects.equals(project, that.project) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(project, userId);
    }

    @Override
    public String toString() {
        return "ActivityId{" +
                "projectId=" + project +
                ", userId='" + userId + '\'' +
                '}';
    }
}
