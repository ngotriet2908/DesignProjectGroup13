package com.group13.tcsprojectgrading.models;


import javax.persistence.*;
import java.util.List;

@Entity
@IdClass(GraderId.class)
public class Grader {
    @Id
    private String projectId;

    @Id
    private String courseId;

    @Id
    private String userId;

    private String name;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "grader")
    private List<Task> tasks;

    public Grader(String projectId, String courseId, String userId, String name, Role role) {
        this.projectId = projectId;
        this.courseId = courseId;
        this.userId = userId;
        this.name = name;
        this.role = role;
    }

    public Grader() {
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public static Role getRoleFromString(String role) {
        if (role.equals("TeacherEnrollment")) {
            return Role.TEACHER;
        } else if (role.equals("TaEnrollment")) {
            return Role.TEACHING_ASSISTANT;
        }
        return Role.STUDENT;
    }

    @Override
    public String toString() {
        return "Grader{" +
                "projectId='" + projectId + '\'' +
                ", courseId='" + courseId + '\'' +
                ", userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", role=" + role +
                '}';
    }
}
