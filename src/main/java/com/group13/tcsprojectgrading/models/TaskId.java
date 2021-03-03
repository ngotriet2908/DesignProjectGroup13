package com.group13.tcsprojectgrading.models;

import java.io.Serializable;
import java.util.Objects;

public class TaskId implements Serializable {
    private String id;
    private Boolean isGroup;
    private String courseId;
    private String projectId;

    public TaskId(String id, Boolean isGroup, String courseId, String projectId) {
        this.id = id;
        this.isGroup = isGroup;
        this.courseId = courseId;
        this.projectId = projectId;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskId taskId = (TaskId) o;
        return Objects.equals(id, taskId.id) && Objects.equals(isGroup, taskId.isGroup) && Objects.equals(courseId, taskId.courseId) && Objects.equals(projectId, taskId.projectId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, isGroup, courseId, projectId);
    }

    @Override
    public String toString() {
        return "TaskId{" +
                "id='" + id + '\'' +
                ", isGroup=" + isGroup +
                ", courseId='" + courseId + '\'' +
                ", projectId='" + projectId + '\'' +
                '}';
    }
}
