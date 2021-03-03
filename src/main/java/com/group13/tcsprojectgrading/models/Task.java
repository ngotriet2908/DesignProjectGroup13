package com.group13.tcsprojectgrading.models;

import javax.persistence.*;

@Entity
@IdClass(TaskId.class)
public class Task {
    @Id
    private String id;

    @Id
    private Boolean isGroup;

    @Id
    private String courseId;

    @Id
    private String projectId;

    @ManyToOne
    private Grader grader;

    private String submissionId;

    public Task(String id, Boolean isGroup, String courseId, String projectId, String submissionId) {
        this.id = id;
        this.isGroup = isGroup;
        this.courseId = courseId;
        this.projectId = projectId;
        this.submissionId = submissionId;
    }

    public Task() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getGroup() {
        return isGroup;
    }

    public void setGroup(Boolean group) {
        isGroup = group;
    }

    public Grader getGrader() {
        return grader;
    }

    public void setGrader(Grader grader) {
        this.grader = grader;
    }

    public String getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(String submissionId) {
        this.submissionId = submissionId;
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
    public String toString() {
        return "Task{" +
                "id='" + id + '\'' +
                ", isGroup=" + isGroup +
                ", courseId='" + courseId + '\'' +
                ", projectId='" + projectId + '\'' +
                ", grader=" + grader +
                ", submissionId='" + submissionId + '\'' +
                '}';
    }
}
