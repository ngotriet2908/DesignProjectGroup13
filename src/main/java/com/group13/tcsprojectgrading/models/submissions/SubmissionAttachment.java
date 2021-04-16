package com.group13.tcsprojectgrading.models.submissions;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import javax.persistence.*;
import java.io.IOException;

/**
 * Model that represent submission attachment from canvas
 */
@Entity
public class SubmissionAttachment {
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE)
    private Long id;

    @JsonRawValue
    @Column(length=8192)
    private String attachment;

    @ManyToOne
    @JsonBackReference(value="submission-attachments")
    private Submission submission;


    public SubmissionAttachment() {
    }

    public SubmissionAttachment(Long id, String attachment, Submission submission) {
        this.id = id;
        this.attachment = attachment;
        this.submission = submission;
    }

    public SubmissionAttachment(String attachment) {
        this.attachment = attachment;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    /*
    Serialiser for attachment. Creates a Json object with original attachment's properties.
     */
    public static class AttachmentSerialiser extends JsonSerializer<SubmissionAttachment> {
        @Override
        public void serialize(SubmissionAttachment attachment, JsonGenerator jsonGenerator,
                              SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeRawValue(attachment.getAttachment());
        }
    }
}
