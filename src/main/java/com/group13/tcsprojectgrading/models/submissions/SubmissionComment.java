package com.group13.tcsprojectgrading.models.submissions;

import javax.persistence.*;
import java.util.UUID;

@Entity
public class SubmissionComment {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(length=8192)
    private String comment;

    @ManyToOne
    private Submission submission;

    public SubmissionComment(String comment, Submission submission) {
        this.comment = comment;
        this.submission = submission;
    }

    public SubmissionComment(String comment) {
        this.comment = comment;
    }

    public SubmissionComment() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Submission getSubmission() {
        return submission;
    }

    public void setSubmission(Submission submission) {
        this.submission = submission;
    }
}
