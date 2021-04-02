package com.group13.tcsprojectgrading.models.settings;

import com.group13.tcsprojectgrading.models.project.Project;
import com.group13.tcsprojectgrading.models.user.User;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Settings {
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
    private Settings.Pk id;

    private boolean rubricNotificationEnabled;
    private boolean issuesNotificationsEnabled;

    public Settings() {

    }

    public Settings(Pk id, boolean rubricNotificationEnabled, boolean issuesNotificationsEnabled) {
        this.id = id;
        this.rubricNotificationEnabled = rubricNotificationEnabled;
        this.issuesNotificationsEnabled = issuesNotificationsEnabled;
    }

    public Settings(Long userId, Long projectId) {
        this.id = new Pk(new User(userId), new Project(projectId));
        this.rubricNotificationEnabled = false;
        this.issuesNotificationsEnabled = false;
    }

    public Pk getId() {
        return id;
    }

    public void setId(Pk id) {
        this.id = id;
    }

    public boolean isRubricNotificationEnabled() {
        return rubricNotificationEnabled;
    }

    public void setRubricNotificationEnabled(boolean rubricNotificationEnabled) {
        this.rubricNotificationEnabled = rubricNotificationEnabled;
    }

    public boolean isIssuesNotificationsEnabled() {
        return issuesNotificationsEnabled;
    }

    public void setIssuesNotificationsEnabled(boolean issuesNotificationsEnabled) {
        this.issuesNotificationsEnabled = issuesNotificationsEnabled;
    }
}