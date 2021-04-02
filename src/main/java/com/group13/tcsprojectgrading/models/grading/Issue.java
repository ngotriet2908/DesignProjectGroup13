package com.group13.tcsprojectgrading.models.grading;

import com.group13.tcsprojectgrading.models.user.User;

import javax.persistence.*;

@Entity
public class Issue {
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    private Assessment assessment;

//    private UUID target;
//    private String targetName;
//    private String targetType;

    @ManyToOne
    private Issue referentIssue;

    private String subject;

    @Column(length = 8196)
    private String description;

    @ManyToOne
    private User creator;

    @ManyToOne
    private User addressee;

    @ManyToOne
    private IssueStatus status;

    private String solution;

    public Issue() {
    }

    public Issue(Long id, Assessment assessment, Issue referentIssue, String subject, String description, User creator,
                 User addressee, IssueStatus status, String solution) {
        this.id = id;
        this.assessment = assessment;
        this.referentIssue = referentIssue;
        this.subject = subject;
        this.description = description;
        this.creator = creator;
        this.addressee = addressee;
        this.status = status;
        this.solution = solution;
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

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public User getAddressee() {
        return addressee;
    }

    public void setAddressee(User addressee) {
        this.addressee = addressee;
    }

    public IssueStatus getStatus() {
        return status;
    }

    public void setStatus(IssueStatus status) {
        this.status = status;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    //    public JsonNode convertToJson() {
//        ObjectMapper objectMapper = new ObjectMapper();
//        ObjectNode node = objectMapper.createObjectNode();
//        node.put("id", id.toString());
//        node.put("assessmentId", assessmentId.toString());
//        node.put("target", target.toString());
//        node.put("targetName", targetName);
//        node.put("targetType", targetType);
//        node.put("subject", subject);
//        node.put("description", description);
//        node.put("status", status);
//        if (solution != null)
//            node.put("solution", solution);
//        node.set("creator", creator.getGraderJson());
//        if (referentIssue != null) {
//            node.set("reference", referentIssue.convertToJson());
//        }
//        if (addressee != null) {
//            node.set("addressee", addressee.getGraderJson());
//        }
//        return node;
//    }
}
