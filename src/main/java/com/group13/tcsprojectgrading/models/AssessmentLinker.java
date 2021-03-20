package com.group13.tcsprojectgrading.models;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
//@IdClass(AssessmentLinkerId.class)
public class AssessmentLinker {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID assessmentId;

    @ManyToOne
    private Submission submission;

    @ManyToOne
    private Participant participant;

    public AssessmentLinker(Submission submission, Participant participant, UUID assessmentId) {
        this.submission = submission;
        this.participant = participant;
        this.assessmentId = assessmentId;
    }

    public AssessmentLinker() {
    }

    public Submission getSubmission() {
        return submission;
    }

    public void setSubmission(Submission submission) {
        this.submission = submission;
    }

    public Participant getParticipant() {
        return participant;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    public UUID getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(UUID assessmentId) {
        this.assessmentId = assessmentId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    //    public List<Participant> getCurrentParticipant() {
//        return currentParticipant;
//    }
//
//    public void setCurrentParticipant(List<Participant> currentParticipant) {
//        this.currentParticipant = currentParticipant;
//    }
}
