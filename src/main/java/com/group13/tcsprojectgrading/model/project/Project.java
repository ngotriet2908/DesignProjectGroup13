package com.group13.tcsprojectgrading.model.project;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.group13.tcsprojectgrading.model.course.Course;
import com.group13.tcsprojectgrading.model.project.rubric.Rubric;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Set;

@Entity
@Table(uniqueConstraints={
        @UniqueConstraint(name = "project_name_course_unique",columnNames = {"name", "course_id"})
})
public class Project {
    @Id
    private Long id;

    private String name;

    private Timestamp deadline;

    private Double pointPossible;

    private Long assignmentGroupId;

    private Long groupCategoryCanvasId;

    @Column(name = "publish_date")
    private Timestamp publishDate;

    @JsonIgnore
    @OneToMany(mappedBy = "project")
    private Set<Rubric> rubrics;

    @JsonIgnore
    @OneToMany(mappedBy = "project")
    private Set<Submission> submissions;

    @JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id", scope = Course.class)
    @JsonIdentityReference(alwaysAsId=true)
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    public Project(Long id, String name, Double pointPossible, Course course) {
        this.id = id;
        this.name = name;
        this.pointPossible = pointPossible;
        this.course = course;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Project() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Timestamp getDeadline() {
        return deadline;
    }

    public void setDeadline(Timestamp deadline) {
        this.deadline = deadline;
    }

    public Timestamp getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Timestamp publishDate) {
        this.publishDate = publishDate;
    }

    public Set<Rubric> getRubrics() {
        return rubrics;
    }

    public void setRubrics(Set<Rubric> rubrics) {
        this.rubrics = rubrics;
    }

    public Set<Submission> getSubmissions() {
        return submissions;
    }

    public void setSubmissions(Set<Submission> submissions) {
        this.submissions = submissions;
    }

    public Double getPointPossible() {
        return pointPossible;
    }

    public void setPointPossible(Double pointPossible) {
        this.pointPossible = pointPossible;
    }

    public Long getAssignmentGroupId() {
        return assignmentGroupId;
    }

    public void setAssignmentGroupId(Long assignmentGroupId) {
        this.assignmentGroupId = assignmentGroupId;
    }

    public Long getGroupCategoryCanvasId() {
        return groupCategoryCanvasId;
    }

    public void setGroupCategoryCanvasId(Long groupCategoryCanvasId) {
        this.groupCategoryCanvasId = groupCategoryCanvasId;
    }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", deadline=" + deadline +
                ", pointPossible=" + pointPossible +
                ", assignmentGroupId=" + assignmentGroupId +
                ", groupCategoryCanvasId=" + groupCategoryCanvasId +
                ", publishDate=" + publishDate +
                ", course=" + course +
                '}';
    }
}
