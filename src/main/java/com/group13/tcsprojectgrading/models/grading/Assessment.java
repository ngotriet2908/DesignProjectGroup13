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

    private Integer gradedCount = 0;
    private Integer finalGrade = null;
    private Boolean finalGradeManual = false;

    // issues
    @JsonIgnore
    @JsonManagedReference(value="assessment-issues")
    @OneToMany(mappedBy="assessment")
    private List<Issue> issues;

    @ManyToOne
    @JsonBackReference(value="project-assessments")
    private Project project;

    // members (not persisted)
    @Transient
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonSerialize(contentUsing= User.UserShortSerialiser.class)
    private Set<User> members;

    // grades
    @JsonManagedReference(value="assessment-grades")
    @OneToMany(mappedBy="assessment")
    private Set<Grade> grades;

    public Assessment() {

    }

    public Assessment(Long id) {
        this.id = id;
    }

    public Assessment(Long id, Integer gradedCount, Integer finalGrade, Boolean finalGradeManual, Project project
            , Set<Grade> grades, List<Issue> issues
    ) {
        this.id = id;
        this.gradedCount = gradedCount;
        this.finalGrade = finalGrade;
        this.finalGradeManual = finalGradeManual;
        this.project = project;
        this.grades = grades;
        this.issues = issues;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getGradedCount() {
        return gradedCount;
    }

    public void setGradedCount(Integer gradedCount) {
        this.gradedCount = gradedCount;
    }

    public Integer getFinalGrade() {
        return finalGrade;
    }

    public void setFinalGrade(Integer finalGrade) {
        this.finalGrade = finalGrade;
    }

    public Boolean getFinalGradeManual() {
        return finalGradeManual;
    }

    public void setFinalGradeManual(Boolean finalGradeManual) {
        this.finalGradeManual = finalGradeManual;
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
