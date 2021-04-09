package com.group13.tcsprojectgrading.models.grading;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.group13.tcsprojectgrading.models.project.Project;
import com.group13.tcsprojectgrading.models.user.User;

import javax.persistence.*;
import java.util.*;

@Entity
public class Assessment {
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE)
    private Long id;

    @Transient
    private Float finalGrade;
    private Float manualGrade;

    // issues
    @JsonIgnore
    @JsonManagedReference(value="assessment-issues")
    @OneToMany(mappedBy="assessment")
    private List<Issue> issues = new ArrayList<>();

    @ManyToOne
    @JsonBackReference(value="project-assessments")
    private Project project;

    // members (not persisted)
    @Transient
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonSerialize(contentUsing= User.UserAssessmentSerialiser.class)
    private Set<User> members;

    @Transient
    private int progress;

    // grades
    @JsonManagedReference(value="assessment-grades")
    @OneToMany(mappedBy="assessment")
    private Set<Grade> grades;

    public Assessment() {

    }

    public Assessment(Long id) {
        this.id = id;
    }

    public Assessment(Long id , Project project
            , Set<Grade> grades, List<Issue> issues
    ) {
        this.id = id;
        this.project = project;
        this.grades = grades;
        this.issues = issues;
    }

    public Assessment(Project project, Set<Grade> grades) {
        this.project = project;
        this.grades = grades;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<User> getMembers() {
        return members;
    }

    public void setMembers(Set<User> members) {
        this.members = members;
    }

    public Set<Grade> getGrades() {
        return grades;
    }

    public void setGrades(Set<Grade> grades) {
        this.grades = grades;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public List<Issue> getIssues() {
        return issues;
    }

    public void setIssues(List<Issue> issues) {
        this.issues = issues;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public Float getFinalGrade() {
        return finalGrade;
    }

    public void setFinalGrade(Float finalGrade) {
        this.finalGrade = finalGrade;
    }

    public Float getManualGrade() {
        return manualGrade;
    }

    public void setManualGrade(Float manualGrade) {
        this.manualGrade = manualGrade;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Assessment that = (Assessment) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
