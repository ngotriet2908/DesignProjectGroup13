package com.group13.tcsprojectgrading.models;

import java.io.Serializable;
import java.util.Objects;

public class ActivityId implements Serializable {
    private String projectId;
    private String courseId;
    private String userId;

    public ActivityId() {
    }

    public ActivityId(String projectId, String courseId, String userId) {
        this.projectId = projectId;
        this.courseId = courseId;
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActivityId that = (ActivityId) o;
        return Objects.equals(projectId, that.projectId) && Objects.equals(courseId, that.courseId) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectId, courseId, userId);
    }
}
