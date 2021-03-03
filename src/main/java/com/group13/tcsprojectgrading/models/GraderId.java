package com.group13.tcsprojectgrading.models;

import java.io.Serializable;
import java.util.Objects;

public class GraderId implements Serializable {
    private String userId;
    private String courseId;
    private String projectId;

    public GraderId(String userId, String courseId, String projectId) {
        this.userId = userId;
        this.courseId = courseId;
        this.projectId = projectId;
    }

    public GraderId() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GraderId graderId = (GraderId) o;
        return Objects.equals(userId, graderId.userId) && Objects.equals(courseId, graderId.courseId) && Objects.equals(projectId, graderId.projectId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, courseId, projectId);
    }

    @Override
    public String toString() {
        return "GraderId{" +
                "userId='" + userId + '\'' +
                ", courseId='" + courseId + '\'' +
                ", projectId='" + projectId + '\'' +
                '}';
    }
}
