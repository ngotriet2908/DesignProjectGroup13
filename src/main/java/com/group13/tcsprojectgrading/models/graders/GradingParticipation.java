package com.group13.tcsprojectgrading.models.graders;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.group13.tcsprojectgrading.models.project.Project;
import com.group13.tcsprojectgrading.models.user.User;
import com.group13.tcsprojectgrading.models.permissions.Role;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class GradingParticipation {
    @Embeddable
    public static class Pk implements Serializable {
        @JsonIgnore
        @ManyToOne
        @JoinColumn(name="userId")
        private User user;

        @JsonIgnore
        @ManyToOne
        @JoinColumn(name="projectId")
        private Project project;

        public Pk() {
        }

        public Pk(User user, Project project) {
            this.user = user;
            this.project = project;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public Project getProject() {
            return project;
        }

        public void setProject(Project project) {
            this.project = project;
        }
    }

    @EmbeddedId
    private GradingParticipation.Pk id;

    @ManyToOne
    private Role role;

    public GradingParticipation() {
    }

    public GradingParticipation(User user, Project project, Role role) {
        this.id = new Pk(user, project);
        this.role = role;
    }

    public GradingParticipation(Pk id) {
        this.id = id;
    }

    public Pk getId() {
        return id;
    }

    public void setId(Pk id) {
        this.id = id;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
