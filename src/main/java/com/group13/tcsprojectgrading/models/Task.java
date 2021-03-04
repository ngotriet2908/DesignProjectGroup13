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
    @ManyToOne
    private Project project;

    @ManyToOne
    private Grader grader;

    private String submissionId;

    private String name;

    public Task(String id, Boolean isGroup, Project project, String submissionId, String name) {
        this.id = id;
        this.isGroup = isGroup;
        this.project = project;
        this.submissionId = submissionId;
        this.name = name;
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
    public Boolean isGroup() {
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

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id='" + id + '\'' +
                ", isGroup=" + isGroup +
                ", project=" + project +
                ", grader=" + grader +
                ", submissionId='" + submissionId + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}