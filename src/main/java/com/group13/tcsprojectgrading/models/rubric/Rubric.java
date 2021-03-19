package com.group13.tcsprojectgrading.models.rubric;

import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

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

    public List<Element> fetchAllCriteria() {
        Stack<Element> stack = new Stack<>();
        stack.addAll(this.children);
        List<Element> criteria = new ArrayList<>();

        while(stack.size() > 0) {
            Element element = stack.pop();
            if (element.content.type.equals(RubricContent.CRITERION_TYPE)) {
                criteria.add(element);
            } else if (element.content.type.equals(RubricContent.BLOCK_TYPE)) {
                stack.addAll(element.children);
            }
        }
        return criteria;
    }

    public void setChildren(List<Element> children) {
        this.children = children;
    }
}
