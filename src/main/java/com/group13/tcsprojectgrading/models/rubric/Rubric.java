package com.group13.tcsprojectgrading.models.rubric;

import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

public class Rubric {
    @Id
    public String id;

    public List<Element> children;

    public Rubric(String id, List<Element> children) {
        this.id = id;
        this.children = children;
    }

    public Rubric(String id) {
        this.id = id;
        this.children = new ArrayList<>();
    }

    public Rubric() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Element> getChildren() {
        return children;
    }

    public void setChildren(List<Element> children) {
        this.children = children;
    }
}
