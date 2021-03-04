package com.group13.tcsprojectgrading.models;


import javax.persistence.*;
import java.util.List;

@Entity
@IdClass(GraderId.class)
public class Grader {
    @Id
    @ManyToOne
    private Project project;

    @Id
    private String userId;

    private String name;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "grader")
    private List<Task> tasks;

    public Grader(Project project, String userId, String name, Role role) {
        this.project = project;
        this.userId = userId;
        this.name = name;
        this.role = role;
    }

    public Grader() {
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
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
                "project=" + project +
                ", userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", role=" + role +
                '}';
    }
}
