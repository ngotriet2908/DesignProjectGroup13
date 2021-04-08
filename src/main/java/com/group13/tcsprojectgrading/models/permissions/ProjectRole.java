package com.group13.tcsprojectgrading.models.permissions;

import com.group13.tcsprojectgrading.models.project.Project;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class ProjectRole {
    @Embeddable
    public static class Pk implements Serializable {
        @ManyToOne
        @JoinColumn(name="roleId")
        private Role role;

        @ManyToOne
        @JoinColumn(name="projectId")
        private Project project;

        public Pk() {
        }

        public Pk(Role role, Project project) {
            this.role = role;
            this.project = project;
        }

        public Role getRole() {
            return role;
        }

        public void setRole(Role role) {
            this.role = role;
        }

        public Project getProject() {
            return project;
        }

        public void setProject(Project project) {
            this.project = project;
        }
    }

    @EmbeddedId
    private ProjectRole.Pk id;

//    @ManyToMany(mappedBy = "projectRoles")
//    @JsonBackReference
//    private Collection<GradingParticipation> graders;

//    @ManyToMany
//    @JsonManagedReference
//    private Collection<Privilege> privileges;

    public ProjectRole() {

    }

    public ProjectRole(Pk id) {
        this.id = id;
    }

    public ProjectRole(Role role, Project project) {
        this.id = new Pk(role, project);
    }

    public Pk getId() {
        return id;
    }

    public void setId(Pk id) {
        this.id = id;
    }

    //    public Collection<GradingParticipation> getGraders() {
//        return graders;
//    }
//
//    public void setGraders(Collection<GradingParticipation> graders) {
//        this.graders = graders;
//    }
}
