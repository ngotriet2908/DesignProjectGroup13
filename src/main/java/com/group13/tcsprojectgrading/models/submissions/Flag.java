package com.group13.tcsprojectgrading.models.submissions;

import com.group13.tcsprojectgrading.models.Project;
import com.group13.tcsprojectgrading.models.submissions.Submission;

import javax.persistence.*;
import java.util.Collection;
import java.util.UUID;

@Entity
public class Flag {
    @Id
    @GeneratedValue
    private UUID id;

    private String name;

    private String description;

    private String variant;

    @ManyToOne
    private Project project;

    @ManyToMany
    private Collection<Submission> submissions;

    public Flag(String name, String description, String variant, Project project) {
        this.name = name;
        this.description = description;
        this.variant = variant;
        this.project = project;
    }

    public Flag() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Collection<Submission> getSubmissions() {
        return submissions;
    }

    public void setSubmissions(Collection<Submission> submissions) {
        this.submissions = submissions;
    }

    public String getVariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }
}
