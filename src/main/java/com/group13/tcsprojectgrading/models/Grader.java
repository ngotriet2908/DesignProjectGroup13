package com.group13.tcsprojectgrading.models;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@IdClass(GraderId.class)
public class Grader {
    @Id
    @ManyToOne
    private Project project;

    @Id
    private String userId;

    private String name;

    @OneToMany(mappedBy = "grader")
    private List<Submission> submissions;

    @ManyToMany
    private Collection<ProjectRole> projectRoles;

    public Grader(Project project, String userId, String name, ProjectRole role) {
        this.project = project;
        this.userId = userId;
        this.name = name;
        List<ProjectRole> roles = new ArrayList<>();
        roles.add(role);
        this.projectRoles = roles;
    }

    public Grader(Project project, String userId, String name, Collection<ProjectRole> projectRoles) {
        this.project = project;
        this.userId = userId;
        this.name = name;
        this.projectRoles = projectRoles;
    }

    public Grader() {
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public List<Submission> getSubmissions() {
        return submissions;
    }

    public void setSubmissions(List<Submission> submissions) {
        this.submissions = submissions;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<ProjectRole> getProjectRoles() {
        return projectRoles;
    }

    public void setProjectRoles(Collection<ProjectRole> projectRoles) {
        this.projectRoles = projectRoles;
    }

    public ArrayNode getRolesArrayNode() {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode result = objectMapper.createArrayNode();

        for(ProjectRole projectRole: projectRoles) {
            Role role = projectRole.getRole();
            ObjectNode roleNode = objectMapper.createObjectNode();
            roleNode.put("name", role.getName());
            result.add(roleNode);
        }
        return result;
    }

    public ArrayNode getPrivilegesArrayNode() {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode result = objectMapper.createArrayNode();

        for(ProjectRole projectRole: projectRoles) {
            if (projectRole.getPrivileges() == null) continue;
            for (Privilege privilege: projectRole.getPrivileges()) {
                ObjectNode privilegeNode = objectMapper.createObjectNode();
                privilegeNode.put("name", privilege.getName());
                result.add(privilegeNode);
            }
        }
        return result;
    }

    public List<String> getRolesListString() {
        List<String> rolesString = new ArrayList<>();

        for(ProjectRole projectRole: projectRoles) {
            Role role = projectRole.getRole();
            rolesString.add(role.getName());
        }
        return rolesString;
    }

    public JsonNode getGraderJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode graderNode = objectMapper.createObjectNode();
        ((ObjectNode) graderNode).put("id", this.getUserId());
        ((ObjectNode) graderNode).put("project_id", this.getProject().getProjectId());
        ((ObjectNode) graderNode).put("name", this.getName());
        ((ObjectNode) graderNode).set("roles", this.getRolesArrayNode());
        ((ObjectNode) graderNode).set("privileges", this.getPrivilegesArrayNode());
        return graderNode;
    }

    //TODO do this
    public boolean hasPrivileges(PrivilegeEnum privilegeEnum) {
        return false;
    }


    @Override
    public String toString() {
        return "Grader{" +
                "project=" + project +
                ", userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
