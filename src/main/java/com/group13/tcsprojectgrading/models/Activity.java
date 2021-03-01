package com.group13.tcsprojectgrading.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.sql.Timestamp;

@Entity
@IdClass(ActivityId.class)
public class Activity {

    @Id
    private String projectId;

    @Id
    private String courseId;

    @Id
    private String userId;

    private Timestamp timestamp;

    private String projectName;

    private Timestamp projectCreatedAt;

    public Activity(String projectId, String courseId, String userId, Timestamp timestamp, String projectName, Timestamp projectCreatedAt) {
        this.projectId = projectId;
        this.courseId = courseId;
        this.userId = userId;
        this.timestamp = timestamp;
        this.projectName = projectName;
        this.projectCreatedAt = projectCreatedAt;
    }

    public Activity(String projectId, String courseId, String userId, Timestamp timestamp) {
        this.projectId = projectId;
        this.courseId = courseId;
        this.userId = userId;
        this.timestamp = timestamp;
    }

    public Activity() {
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

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    @Override
    public String toString() {
        return "Activity{" +
                "projectId='" + projectId + '\'' +
                ", courseId='" + courseId + '\'' +
                ", userId='" + userId + '\'' +
                ", timestamp=" + timestamp +
                ", projectName='" + projectName + '\'' +
                ", projectCreatedAt=" + projectCreatedAt +
                '}';
    }
}
