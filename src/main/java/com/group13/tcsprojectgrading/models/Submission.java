package com.group13.tcsprojectgrading.models;

import javax.persistence.*;
import java.util.Collection;

@Entity
@IdClass(SubmissionId.class)
public class Submission {

    @Id
    private String id;

    @Id
    @ManyToOne
    private Project project;

    private String name;

    private String groupId;

    @ManyToOne
    private Grader grader;

    @ManyToMany
    private Collection<Flag> flags;

    public Submission(String id, Project project, String name, String groupId) {
        this.id = id;
        this.project = project;
        this.name = name;
        this.groupId = groupId;
    }

    public Submission() {
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

    public Grader getGrader() {
        return grader;
    }

    public void setGrader(Grader grader) {
        this.grader = grader;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Collection<Flag> getFlags() {
        return flags;
    }

    public void setFlags(Collection<Flag> flags) {
        this.flags = flags;
    }

    @Override
    public String toString() {
        return "Submission{" +
                "id='" + id + '\'' +
                ", project=" + project +
                ", name='" + name + '\'' +
                ", grader=" + grader +
                '}';
    }
}
