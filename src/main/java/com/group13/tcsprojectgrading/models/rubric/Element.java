package com.group13.tcsprojectgrading.models.rubric;

import org.springframework.data.annotation.Id;

import java.util.List;

public class Element {
    public RubricContent content;
    public List<Element> children;

    public Element(RubricContent content, List<Element> children) {
        this.content = content;
        this.children = children;
    }

    public Element(RubricContent content) {
        this.content = content;
    }

    public Element() {
    }

    public RubricContent getContent() {
        return content;
    }

    public void setContent(RubricContent content) {
        this.content = content;
    }

    public List<Element> getChildren() {
        return children;
    }

    public void setChildren(List<Element> children) {
        this.children = children;
    }
}
