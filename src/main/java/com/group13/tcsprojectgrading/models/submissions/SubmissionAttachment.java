package com.group13.tcsprojectgrading.models.submissions;

import javax.persistence.*;
import java.util.UUID;

@Entity
public class SubmissionAttachment {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(length=8192)
    private String attachment;

    @ManyToOne
    private Submission submission;

    public SubmissionAttachment() {
    }

    public SubmissionAttachment(String attachment, Submission submission) {
        this.attachment = attachment;
        this.submission = submission;
    }

    public SubmissionAttachment(String attachment) {
        this.attachment = attachment;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public Submission getSubmission() {
        return submission;
    }

    public void setSubmission(Submission submission) {
        this.submission = submission;
    }
}
