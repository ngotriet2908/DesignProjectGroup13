package com.group13.tcsprojectgrading.models.grading;

import com.group13.tcsprojectgrading.models.Participant;
import com.group13.tcsprojectgrading.models.submissions.Submission;

import javax.persistence.*;
import java.util.List;
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

    @OneToMany(mappedBy = "currentAssessmentLinker")
    private List<Participant> currentAppliedParticipants;

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

    public List<Participant> getCurrentAppliedParticipants() {
        return currentAppliedParticipants;
    }

    public void setCurrentAppliedParticipants(List<Participant> currentAppliedParticipants) {
        this.currentAppliedParticipants = currentAppliedParticipants;
    }

    //    public List<Participant> getCurrentParticipant() {
//        return currentParticipant;
//    }
//
//    public void setCurrentParticipant(List<Participant> currentParticipant) {
//        this.currentParticipant = currentParticipant;
//    }
}
