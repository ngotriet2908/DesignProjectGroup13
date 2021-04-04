package com.group13.tcsprojectgrading.models.submissions;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;

import javax.persistence.*;
import java.io.IOException;
import java.util.UUID;

@Entity
public class SubmissionComment {
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE)
    private Long id;

    @JsonRawValue
    @Column(length=8192)
    private String comment;

    @ManyToOne
    @JsonBackReference(value="submission-comments")
    private Submission submission;

    public SubmissionComment() {
    }

    public SubmissionComment(String comment) {
        this.comment = comment;
    }

    public SubmissionComment(Long id, String comment, Submission submission) {
        this.id = id;
        this.comment = comment;
        this.submission = submission;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    /*
    Serialiser for comment. Creates a Json object with original comment's properties.
     */
    public static class CommentSerialiser extends JsonSerializer<SubmissionComment> {
        @Override
        public void serialize(SubmissionComment comment, JsonGenerator jsonGenerator,
                              SerializerProvider serializerProvider) throws IOException {
            ObjectMapper objectMapper = new ObjectMapper();
            jsonGenerator.writeObject(objectMapper.readTree(comment.getComment()));
        }
    }
}
