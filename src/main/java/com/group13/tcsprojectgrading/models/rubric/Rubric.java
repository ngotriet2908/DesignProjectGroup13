package com.group13.tcsprojectgrading.models.rubric;

import org.springframework.data.annotation.Id;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Rubric {
    @Id
    private String id;
    private List<Element> children;
    private int criterionCount;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date lastModified;

    public Rubric(String id, List<Element> children, int criterionCount) {
        this.id = id;
        this.children = children;
        this.criterionCount = criterionCount;
        this.lastModified = new Date();
    }

    public Rubric(String id) {
        this.id = id;
        this.children = new ArrayList<>();
        this.criterionCount = 0;
        this.lastModified = new Date();
    }

    public Rubric() {
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
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

    public void setCriterionCount(int criterionCount) {
        this.criterionCount = criterionCount;
    }

    public int getCriterionCount() {
        return criterionCount;
    }
}
