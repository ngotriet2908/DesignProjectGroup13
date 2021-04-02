package com.group13.tcsprojectgrading.models.submissions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.group13.tcsprojectgrading.models.project.Project;

import javax.persistence.*;
import java.util.Collection;

@Entity
public class Label {
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE)
    private Long id;

    private String name;

    private String description;

    private String variant;

    @JsonIgnore
    @ManyToOne
    private Project project;

    @JsonIgnore
    @ManyToMany
    private Collection<Submission> submissions;

    public Label(String name, String description, String variant, Project project) {
        this.name = name;
        this.description = description;
        this.variant = variant;
        this.project = project;
    }

    public Label() {
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
