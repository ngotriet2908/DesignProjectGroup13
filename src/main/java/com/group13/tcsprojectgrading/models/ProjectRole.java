package com.group13.tcsprojectgrading.models;

import javax.persistence.*;
import java.util.Collection;

@Entity
@IdClass(ProjectRoleId.class)
public class ProjectRole {

    @Id
    @ManyToOne
    private Project project;

    @Id
    @ManyToOne
    private Role role;

    @ManyToMany(mappedBy = "projectRoles")
    private Collection<Grader> graders;

    @ManyToMany
    private Collection<Privilege> privileges;

    public ProjectRole(Project project, Role role, Collection<Privilege> privileges) {
        this.project = project;
        this.role = role;
        this.privileges = privileges;
    }

    public ProjectRole() {

    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Collection<Grader> getGraders() {
        return graders;
    }

    public void setGraders(Collection<Grader> graders) {
        this.graders = graders;
    }

    public Collection<Privilege> getPrivileges() {
        return privileges;
    }

    public void setPrivileges(Collection<Privilege> privileges) {
        this.privileges = privileges;
    }
}
