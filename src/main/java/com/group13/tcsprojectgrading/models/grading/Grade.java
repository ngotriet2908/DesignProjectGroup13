package com.group13.tcsprojectgrading.models.grading;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.group13.tcsprojectgrading.models.user.User;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Grade {
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE)
    private Long id;

    private float grade;
    private String description;
    private boolean isActive;
    private String criterionId;

    @JsonBackReference(value="assessment-grades")
    @ManyToOne
    private Assessment assessment;

    @ManyToOne
    @JsonSerialize(using= User.UserShortSerialiser.class)
    private User grader;

    @Temporal(TemporalType.TIMESTAMP)
    private Date gradedAt;

    public Grade(Long id, Assessment assessment, String criterionId, User grader, Date gradedAt,
                 float grade, String description, boolean isActive) {
        this.id = id;
        this.assessment = assessment;
        this.criterionId = criterionId;
        this.grader = grader;
        this.gradedAt = gradedAt;
        this.grade = grade;
        this.description = description;
        this.isActive = isActive;
    }

    public Grade() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Assessment getAssessment() {
        return assessment;
    }

    public void setAssessment(Assessment assessment) {
        this.assessment = assessment;
    }

    public String getCriterionId() {
        return criterionId;
    }

    public void setCriterionId(String criterionId) {
        this.criterionId = criterionId;
    }

    public User getGrader() {
        return grader;
    }

    public void setGrader(User grader) {
        this.grader = grader;
    }

    public Date getGradedAt() {
        return gradedAt;
    }

    public void setGradedAt(Date createdAt) {
        this.gradedAt = createdAt;
    }

    public float getGrade() {
        return grade;
    }

    public void setGrade(float grade) {
        this.grade = grade;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean getActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
