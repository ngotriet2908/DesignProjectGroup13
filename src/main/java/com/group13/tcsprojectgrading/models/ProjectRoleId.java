package com.group13.tcsprojectgrading.models;

import java.io.Serializable;

public class ProjectRoleId implements Serializable {
    private Long role;
    private ProjectId project;

    public ProjectRoleId(Long role, ProjectId project) {
        this.role = role;
        this.project = project;
    }

    public ProjectRoleId() {
    }

    public Long getRole() {
        return role;
    }

    public void setRole(Long role) {
        this.role = role;
    }

    public ProjectId getProject() {
        return project;
    }

    public void setProject(ProjectId project) {
        this.project = project;
    }
}
