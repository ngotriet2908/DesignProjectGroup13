package com.group13.tcsprojectgrading.models;

import java.io.Serializable;
import java.util.Objects;

public class SubmissionId implements Serializable {
    private String id;
    private ProjectId project;

    public SubmissionId(String id, ProjectId project) {
        this.id = id;
        this.project = project;
    }

    public SubmissionId() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ProjectId getProject() {
        return project;
    }

    public void setProject(ProjectId project) {
        this.project = project;
    }

    @Override
    public String toString() {
        return "SubmissionId{" +
                "id='" + id + '\'' +
                ", project=" + project +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubmissionId that = (SubmissionId) o;
        return Objects.equals(id, that.id) && Objects.equals(project, that.project);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, project);
    }
}
