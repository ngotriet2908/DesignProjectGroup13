package com.group13.tcsprojectgrading.models;

import java.io.Serializable;
import java.util.Objects;

public class TaskId implements Serializable {
    private String id;
    private Boolean isGroup;
    private ProjectId project;

    public TaskId(String id, Boolean isGroup, ProjectId project) {
        this.id = id;
        this.isGroup = isGroup;
        this.project = project;
    }

    public TaskId() {

    }

    public ProjectId getProject() {
        return project;
    }

    public void setProject(ProjectId project) {
        this.project = project;
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

    @Override
    public String toString() {
        return "TaskId{" +
                "id='" + id + '\'' +
                ", isGroup=" + isGroup +
                ", project=" + project +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskId taskId = (TaskId) o;
        return Objects.equals(id, taskId.id) && Objects.equals(isGroup, taskId.isGroup) && Objects.equals(project, taskId.project);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, isGroup, project);
    }
}
