package com.group13.tcsprojectgrading.models.submissions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.group13.tcsprojectgrading.models.project.Project;

import javax.persistence.*;
import java.util.Collection;
import java.util.Objects;

@Entity
public class Label {
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE)
    private Long id;

    private String name;
    private String description;

    private String color;

    @JsonIgnore
    @ManyToOne
    private Project project;

    @JsonIgnore
    @ManyToMany(mappedBy="labels")
    private Collection<Submission> submissions;

    public Label(String name, String description, String color, Project project) {
        this.name = name;
        this.description = description;
        this.color = color;
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

    public String getColor() {
        return color;
    }

    public void setColor(String variant) {
        this.color = variant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Label label = (Label) o;
        return id.equals(label.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
