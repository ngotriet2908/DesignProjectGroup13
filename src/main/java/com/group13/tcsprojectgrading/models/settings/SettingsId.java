package com.group13.tcsprojectgrading.models.settings;
import com.group13.tcsprojectgrading.models.ProjectId;

import java.io.Serializable;
import java.util.Objects;

public class SettingsId implements Serializable {
    private String userId;
    private String projectId;
    private String courseId;

    public SettingsId() { }

    public SettingsId(String userId, String projectId, String courseId) {
        this.userId = userId;
        this.projectId = projectId;
        this.courseId = courseId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SettingsId that = (SettingsId) o;
        return Objects.equals(projectId, that.projectId) && Objects.equals(userId, that.userId) && Objects.equals(courseId, that.courseId);
    }
}
