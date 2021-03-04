package com.group13.tcsprojectgrading.models;

import java.io.Serializable;
import java.util.Objects;

public class GraderId implements Serializable {
    private String userId;
    private ProjectId project;

    public GraderId(String userId, ProjectId project) {
        this.userId = userId;
        this.project = project;
    }

    public GraderId() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public ProjectId getProject() {
        return project;
    }

    public void setProject(ProjectId project) {
        this.project = project;
    }

    @Override
    public String toString() {
        return "GraderId{" +
                "userId='" + userId + '\'' +
                ", project=" + project +
                '}';
    }
}
