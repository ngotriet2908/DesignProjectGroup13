package com.group13.tcsprojectgrading.models.rubric;

import org.springframework.data.annotation.Id;

import java.util.List;

public class Rubric {
    @Id
    public String id;
    public String projectId;

    public List<Element> children;

    public Rubric(String id, String projectId, List<Element> children) {
        this.id = id;
        this.projectId = projectId;
        this.children = children;
    }

    public Rubric(String id, List<Element> children) {
        this.id = id;
        this.children = children;
    }

    public Rubric() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public List<Element> getChildren() {
        return children;
    }

    public void setChildren(List<Element> children) {
        this.children = children;
    }
}
