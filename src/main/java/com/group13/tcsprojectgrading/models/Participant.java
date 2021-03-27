package com.group13.tcsprojectgrading.models;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.group13.tcsprojectgrading.models.grading.AssessmentLinker;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@IdClass(ParticipantId.class)
public class Participant {

    @Id
    private String id;

    @Id
    @ManyToOne
    private Project project;

    private String name;

    private String email;

    private String sid;

    @OneToMany(mappedBy = "participant")
    private List<AssessmentLinker> assessmentLinkers = new ArrayList<>();

    public Participant(String id, Project project, String name, String email, String sid) {
        this.id = id;
        this.project = project;
        this.name = name;
        this.email = email;
        this.sid = sid;
    }

//    @ManyToOne
//    private AssessmentLinker currentAssessmentLinker;

    public Participant() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public List<AssessmentLinker> getAssessmentLinkers() {
        return assessmentLinkers;
    }

    public void setAssessmentLinkers(List<AssessmentLinker> assessmentLinkers) {
        this.assessmentLinkers = assessmentLinkers;
    }

//    public AssessmentLinker getCurrentAssessmentLinker() {
//        return currentAssessmentLinker;
//    }
//
//    public void setCurrentAssessmentLinker(AssessmentLinker currentAssessmentLinker) {
//        this.currentAssessmentLinker = currentAssessmentLinker;
//    }

    public JsonNode convertToJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("id", id);
        objectNode.put("name", name);
        objectNode.put("sid", sid);
        objectNode.put("email", email);
//        objectNode.set("project", project.convertToJson());
        return objectNode;
    }

    public JsonNode convertToJson(List<AssessmentLinker> linkers) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("id", id);
        objectNode.put("name", name);
        objectNode.put("sid", sid);
        objectNode.put("email", email);
        ArrayNode submissionsNode = objectMapper.createArrayNode();
        for(AssessmentLinker linker: linkers) {
            ObjectNode submissionNode = objectMapper.createObjectNode();
            submissionNode.put("name", linker.getSubmission().getName());
            submissionNode.put("id", linker.getSubmission().getId().toString());
            if (linker.getSubmission().getGrader() != null) {
                submissionNode.put("grader", linker.getSubmission().getGrader().getName());
            }
            submissionsNode.add(submissionNode);
        }
        objectNode.set("submissions", submissionsNode);
        return objectNode;
    }

    @Override
    public String toString() {
        return "Participant{" +
                "id='" + id + '\'' +
                ", project=" + project +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", sid='" + sid + '\'' +
                '}';
    }
}
