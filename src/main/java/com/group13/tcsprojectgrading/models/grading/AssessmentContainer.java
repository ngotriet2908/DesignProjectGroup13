package com.group13.tcsprojectgrading.models.grading;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

@Entity
public class AssessmentContainer {

    @Id
    private UUID id;

    @Column(columnDefinition="TEXT")
    private String assessment;

    public AssessmentContainer(UUID id) {
        this.id = id;
    }

    public AssessmentContainer() {
    }

    public AssessmentContainer(UUID id, String assessment) {
        this.id = id;
        this.assessment = assessment;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getAssessment() {
        return assessment;
    }

    public void setAssessment(String assessment) {
        this.assessment = assessment;
    }
}
