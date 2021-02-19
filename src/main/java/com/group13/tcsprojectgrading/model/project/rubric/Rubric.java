package com.group13.tcsprojectgrading.model.project.rubric;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.group13.tcsprojectgrading.model.course.Course;
import com.group13.tcsprojectgrading.model.project.Project;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Rubric {

    @Id
    @SequenceGenerator(
            name = "rubric_sequence",
            sequenceName = "rubric_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "rubric_sequence"
    )
    private Long id;

    @JsonIdentityInfo(generator= ObjectIdGenerators.PropertyGenerator.class, property="id", scope = Project.class)
    @JsonIdentityReference(alwaysAsId=true)
    @ManyToOne
    @JoinColumn(name="project_id")
    private Project project;

    private Long current_rubric_version_id;

    @OneToMany(mappedBy = "rubric")
    private Set<RubricVersion> rubricVersions;

    @OneToMany(mappedBy = "rubric")
    private Set<Block> blocks;

    public Rubric(Project project, Long current_rubric_version_id, Set<RubricVersion> rubricVersions) {
        this.project = project;
        this.current_rubric_version_id = current_rubric_version_id;
        this.rubricVersions = rubricVersions;
    }

    public Rubric(Project project) {
        this.project = project;
    }

    public Rubric() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Long getCurrent_rubric_version_id() {
        return current_rubric_version_id;
    }

    public void setCurrent_rubric_version_id(Long current_rubric_version_id) {
        this.current_rubric_version_id = current_rubric_version_id;
    }

    public Set<RubricVersion> getRubricVersions() {
        return rubricVersions;
    }

    public void setRubricVersions(Set<RubricVersion> rubricVersions) {
        this.rubricVersions = rubricVersions;
    }

    public Set<Block> getBlocks() {
        return blocks;
    }

    public void setBlocks(Set<Block> blocks) {
        this.blocks = blocks;
    }

    @Override
    public String toString() {
        return "Rubric{" +
                "id=" + id +
                ", project=" + project +
                ", current_rubric_version_id='" + current_rubric_version_id + '\'' +
                ", rubricVersions=" + rubricVersions +
                '}';
    }
}
