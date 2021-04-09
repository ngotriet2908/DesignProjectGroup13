package com.group13.tcsprojectgrading.models.rubric;

import com.group13.tcsprojectgrading.models.project.Project;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class RubricLinker {
    @Embeddable
    public static class Pk implements Serializable {
        @ManyToOne
        @JoinColumn(name="projectId")
        private Project project;

        public Pk() {
        }

        public Pk(Project project) {
            this.project = project;
        }

        public Project getProject() {
            return project;
        }

        public void setProject(Project project) {
            this.project = project;
        }
    }

    @EmbeddedId
    private RubricLinker.Pk id;

    @Column(columnDefinition="TEXT")
    private String rubric;

    @Version
    private Long version;

    public RubricLinker() {
    }

    public RubricLinker(Pk id, String rubric) {
        this.id = id;
        this.rubric = rubric;
    }

    public RubricLinker(Long projectId, String rubric) {
        this.id = new Pk(new Project(projectId));
        this.rubric = rubric;
    }

    public Pk getId() {
        return id;
    }

    public void setId(Pk id) {
        this.id = id;
    }

    public String getRubric() {
        return rubric;
    }

    public void setRubric(String rubric) {
        this.rubric = rubric;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
