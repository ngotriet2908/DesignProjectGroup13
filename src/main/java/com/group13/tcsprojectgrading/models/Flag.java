package com.group13.tcsprojectgrading.models;

import javax.persistence.*;
import java.util.Collection;

@Entity
public class Flag {
    //TODO should flag be visible to its other creator
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private String description;

    private String variant;

    @ManyToOne
    private Grader grader;

    @ManyToMany
    private Collection<Submission> submissions;

    public Flag(String name, String description, String variant, Grader grader) {
        this.name = name;
        this.description = description;
        this.variant = variant;
        this.grader = grader;
    }

    public Flag() {
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

    public Grader getGrader() {
        return grader;
    }

    public void setGrader(Grader grader) {
        this.grader = grader;
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
