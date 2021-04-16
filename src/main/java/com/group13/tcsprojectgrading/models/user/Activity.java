package com.group13.tcsprojectgrading.models.user;

import com.group13.tcsprojectgrading.models.project.Project;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Model that record user's interaction with certain projects
 */
@Entity
public class Activity {
    @Embeddable
    public static class Pk implements Serializable {
        @ManyToOne
        @JoinColumn(name="userId")
        private User user;

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
    private Activity.Pk id;

    private Timestamp lastAccessed;

    public Activity() {
    }

    public Activity(Pk id, Timestamp lastAccessed) {
        this.id = id;
        this.lastAccessed = lastAccessed;
    }

    public Activity(Project project, User user, Timestamp lastAccessed) {
        this.id = new Activity.Pk(user, project);
        this.lastAccessed = lastAccessed;
    }

    public Pk getId() {
        return id;
    }

    public void setId(Pk id) {
        this.id = id;
    }

    public Timestamp getLastAccessed() {
        return lastAccessed;
    }

    public void setLastAccessed(Timestamp lastAccessed) {
        this.lastAccessed = lastAccessed;
    }
}
