package com.group13.tcsprojectgrading.models.rubric;

import org.springframework.data.annotation.Id;

import java.util.List;

public class Block {
    @Id
    private String id;
    private String title;
    private List<Criterion> criteria;

    public Block(String id, String title, List<Criterion> criteria) {
        this.id = id;
        this.title = title;
        this.criteria = criteria;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Criterion> getCriteria() {
        return criteria;
    }

    public void setCriteria(List<Criterion> criteria) {
        this.criteria = criteria;
    }
}
