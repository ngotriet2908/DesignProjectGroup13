package com.group13.tcsprojectgrading.models.feedback;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.group13.tcsprojectgrading.models.project.Project;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
public class FeedbackTemplate {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @Column(columnDefinition="TEXT")
    private String subject;

    @Column(columnDefinition="TEXT")
    private String body;

    @ManyToOne
    private Project project;

    @JsonIgnore
    @OneToMany(mappedBy="template")
    private Set<FeedbackLog> feedbackLogs;

    public FeedbackTemplate(String name, String subject, String body, Project project) {
        this.name = name;
        this.subject = subject;
        this.body = body;
        this.project = project;
    }

    public FeedbackTemplate() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Set<FeedbackLog> getFeedbackLogs() {
        return feedbackLogs;
    }

    public void setFeedbackLogs(Set<FeedbackLog> feedbackLogs) {
        this.feedbackLogs = feedbackLogs;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
