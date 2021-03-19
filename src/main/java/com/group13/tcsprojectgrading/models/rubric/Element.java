package com.group13.tcsprojectgrading.models.rubric;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.annotation.Id;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
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

    public int getCriterionCount() {
        int total = 0;
        if (children != null) {
            for (Element child : children) {
                total += child.getCriterionCount();
            }
        }
        if (content.getGrade() != null) { //Only criteria have grades
            total++;
        }
        return total;
    }
}
