package com.group13.tcsprojectgrading.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.persistence.*;
import java.util.UUID;

@Entity
public class Issue {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID assessmentId;

    private UUID target;

    private String targetName;

    private String targetType;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    @JsonBackReference
    private Issue referentIssue;


    private String subject;

    @Column(length = 8196)
    private String description;

    @ManyToOne
    private Grader creator;

    @ManyToOne
    private Grader addressee;

    private String status;

    private String solution;

    public Issue(UUID assessmentId, UUID target, String targetType, String targetName, Issue referentIssue, String subject,
                 String description, Grader creator, String status, Grader addressee) {
        this.assessmentId = assessmentId;
        this.target = target;
        this.targetType = targetType;
        this.referentIssue = referentIssue;
        this.subject = subject;
        this.description = description;
        this.creator = creator;
        this.status = status;
        this.targetName = targetName;
        this.addressee = addressee;
    }

    public Issue() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getTarget() {
        return target;
    }

    public void setTarget(UUID target) {
        this.target = target;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public Issue getReferentIssue() {
        return referentIssue;
    }

    public void setReferentIssue(Issue referentIssue) {
        this.referentIssue = referentIssue;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Grader getCreator() {
        return creator;
    }

    public void setCreator(Grader creator) {
        this.creator = creator;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    public Grader getAddressee() {
        return addressee;
    }

    public void setAddressee(Grader addressee) {
        this.addressee = addressee;
    }

    public UUID getAssessmentId() {
        return assessmentId;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public void setAssessmentId(UUID assessmentId) {
        this.assessmentId = assessmentId;
    }

    public JsonNode convertToJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode node = objectMapper.createObjectNode();
        ((ObjectNode) node).put("id", id.toString());
        ((ObjectNode) node).put("assessmentId", assessmentId.toString());
        ((ObjectNode) node).put("target", target.toString());
        ((ObjectNode) node).put("targetName", targetName);
        ((ObjectNode) node).put("targetType", targetType);
        ((ObjectNode) node).put("subject", subject);
        ((ObjectNode) node).put("description", description);
        ((ObjectNode) node).put("status", status);
        if (solution != null)
            ((ObjectNode) node).put("solution", solution);
        ((ObjectNode) node).set("creator", creator.getGraderJson());
        if (referentIssue != null) {
            ((ObjectNode) node).set("reference", referentIssue.convertToJson());
        }
        if (addressee != null) {
            ((ObjectNode) node).set("addressee", addressee.getGraderJson());
        }
        return node;
    }
}
