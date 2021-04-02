package com.group13.tcsprojectgrading.models.graders;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.group13.tcsprojectgrading.models.project.Project;
import com.group13.tcsprojectgrading.models.user.User;
import com.group13.tcsprojectgrading.models.permissions.Role;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class GradingParticipation {
    @Embeddable
    public static class Pk implements Serializable {
        @JsonIgnore
        @ManyToOne
        @JoinColumn(name="userId")
        private User user;

        @JsonIgnore
        @ManyToOne
        @JoinColumn(name="projectId")
        private Project project;

        public Pk() {
        }

        public Pk(User user, Project project) {
            this.user = user;
            this.project = project;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public Project getProject() {
            return project;
        }

        public void setProject(Project project) {
            this.project = project;
        }
    }

    @EmbeddedId
    private GradingParticipation.Pk id;

    @ManyToOne
    private Role role;

    public GradingParticipation() {
    }

    public GradingParticipation(User user, Project project, Role role) {
        this.id = new Pk(user, project);
        this.role = role;
    }

    public GradingParticipation(Pk id) {
        this.id = id;
    }

    public Pk getId() {
        return id;
    }

    public void setId(Pk id) {
        this.id = id;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    //    public Collection<ProjectRole> getProjectRoles() {
//        return projectRoles;
//    }
//
//    public void setProjectRoles(Collection<ProjectRole> projectRoles) {
//        this.projectRoles = projectRoles;
//    }

    //    public ArrayNode getRolesArrayNode() {
//        ObjectMapper objectMapper = new ObjectMapper();
//        ArrayNode result = objectMapper.createArrayNode();
//
//        for(ProjectRole projectRole: projectRoles) {
//            Role role = projectRole.getRole();
//            ObjectNode roleNode = objectMapper.createObjectNode();
//            roleNode.put("name", role.getName());
//            result.add(roleNode);
//        }
//        return result;
//    }
//
//    public ArrayNode getPrivilegesArrayNode() {
//        ObjectMapper objectMapper = new ObjectMapper();
//        ArrayNode result = objectMapper.createArrayNode();
//
//        for(ProjectRole projectRole: projectRoles) {
//            if (projectRole.getPrivileges() == null) continue;
//            for (Privilege privilege: projectRole.getPrivileges()) {
//                ObjectNode privilegeNode = objectMapper.createObjectNode();
//                privilegeNode.put("name", privilege.getName());
//                result.add(privilegeNode);
//            }
//        }
//        return result;
//    }
//
//    public List<String> getRolesListString() {
//        List<String> rolesString = new ArrayList<>();
//
//        for(ProjectRole projectRole: projectRoles) {
//            Role role = projectRole.getRole();
//            rolesString.add(role.getName());
//        }
//        return rolesString;
//    }
//
//    public JsonNode getGraderJson() {
//        ObjectMapper objectMapper = new ObjectMapper();
//        ObjectNode graderNode = objectMapper.createObjectNode();
//        graderNode.put("id", this.getUserId());
//        graderNode.put("project_id", this.getProject().getProjectId());
//        graderNode.put("name", this.getName());
//        graderNode.set("roles", this.getRolesArrayNode());
//        graderNode.set("privileges", this.getPrivilegesArrayNode());
//        return graderNode;
//    }

//    //TODO do this
//    public boolean hasPrivileges(PrivilegeEnum privilegeEnum) {
//        return false;
//    }
//
//
//    @Override
//    public String toString() {
//        return "Grader{" +
//                "project=" + project +
//                ", userId='" + userId + '\'' +
//                ", name='" + name + '\'' +
//                '}';
//    }
}
