package com.group13.tcsprojectgrading.models;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@IdClass(ActivityId.class)
@JsonSerialize(using = ActivitySerializer.class)
public class Activity {

    @Id
    @ManyToOne
    private Project project;

    @Id
    private String userId;

    private Timestamp timestamp;

    private String projectName;

    private Timestamp projectCreatedAt;

    public Activity(Project project, String userId, Timestamp timestamp, String projectName, Timestamp projectCreatedAt) {
        this.project = project;
        this.userId = userId;
        this.timestamp = timestamp;
        this.projectName = projectName;
        this.projectCreatedAt = projectCreatedAt;
    }

    public Activity(Project project, String userId, Timestamp timestamp) {
        this.project = project;
        this.userId = userId;
        this.timestamp = timestamp;
    }

    public Activity() {
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Timestamp getProjectCreatedAt() {
        return projectCreatedAt;
    }

    public void setProjectCreatedAt(Timestamp projectCreatedAt) {
        this.projectCreatedAt = projectCreatedAt;
    }

    @Override
    public String toString() {
        return "Activity{" +
                "project=" + project +
                ", userId='" + userId + '\'' +
                ", timestamp=" + timestamp +
                ", projectName='" + projectName + '\'' +
                ", projectCreatedAt=" + projectCreatedAt +
                '}';
    }
}
