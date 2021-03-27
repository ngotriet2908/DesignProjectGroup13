package com.group13.tcsprojectgrading.models.settings;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.group13.tcsprojectgrading.models.Project;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;

@Entity
@IdClass(SettingsId.class)
//@JsonSerialize(using = ActivitySerializer.class)
public class Settings {
    @Id
    private String courseId;

    @Id
    private String projectId;

    @Id
    private String userId;

    private boolean notificationsEnabled;

    public Settings() {

    }

    public Settings(String courseId, String projectId, String userId) {
        this.courseId = courseId;
        this.projectId = projectId;
        this.userId = userId;
        this.notificationsEnabled = false;
    }

    public Settings(String courseId, String projectId, String userId, boolean notificationsEnabled) {
        this.courseId = courseId;
        this.projectId = projectId;
        this.userId = userId;
        this.notificationsEnabled = notificationsEnabled;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isNotificationsEnabled() {
        return notificationsEnabled;
    }

    public void setNotificationsEnabled(boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }
}