package com.group13.tcsprojectgrading.models;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.api.client.json.Json;

import javax.mail.Part;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
