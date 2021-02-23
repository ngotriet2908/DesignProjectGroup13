package com.group13.tcsprojectgrading.model.project;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.group13.tcsprojectgrading.model.project.rubric.Rubric;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
public class GradingVersion {
    @Id
    @SequenceGenerator(
            name = "grading_version_sequence",
            sequenceName = "grading_version_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "grading_version_sequence"
    )
    private Long id;

    private String name;

    private Timestamp created_date;

    private String created_by; //TODO: make this into relation with Grader ?

    private Long previous_grading_version_id;

    @JsonIgnoreProperties({"gradingVersions"})
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "course_group_id"),
            @JoinColumn(name = "criterion_version_id"),
            @JoinColumn(name = "project_id")
    })
    private Grading grading;

    private Double grade;
    private String comment;

    public GradingVersion(String name, Timestamp created_date, String created_by, Long previous_grading_version_id, Grading grading, Double grade, String comment) {
        this.name = name;
        this.created_date = created_date;
        this.created_by = created_by;
        this.previous_grading_version_id = previous_grading_version_id;
        this.grading = grading;
        this.grade = grade;
        this.comment = comment;
    }

    public GradingVersion() {
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

    public Timestamp getCreated_date() {
        return created_date;
    }

    public void setCreated_date(Timestamp created_date) {
        this.created_date = created_date;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public Long getPrevious_grading_version_id() {
        return previous_grading_version_id;
    }

    public void setPrevious_grading_version_id(Long previous_grading_version_id) {
        this.previous_grading_version_id = previous_grading_version_id;
    }

    public Grading getGrading() {
        return grading;
    }

    public void setGrading(Grading grading) {
        this.grading = grading;
    }

    public Double getGrade() {
        return grade;
    }

    public void setGrade(Double grade) {
        this.grade = grade;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "GradingVersion{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", created_date=" + created_date +
                ", created_by='" + created_by + '\'' +
                ", previous_rubric_version_id='" + previous_grading_version_id + '\'' +
                ", grading=" + grading +
                ", grade=" + grade +
                ", comment='" + comment + '\'' +
                '}';
    }
}
