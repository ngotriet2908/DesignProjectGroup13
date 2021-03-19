package com.group13.tcsprojectgrading.models.rubric;

import org.springframework.data.annotation.Id;

import java.util.List;

public class RubricContent {
    public static final String CRITERION_TYPE = "1";
    public static final String BLOCK_TYPE = "0";

    @Id
    public String id;
    public String type;
    public String title;

    public String text;
    public RubricGrade grade;

    public RubricContent(String id, String type, String title, String text, RubricGrade grade) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.text = text;
        this.grade = grade;
    }

    public RubricContent(String id, String type, String title) {
        this.id = id;
        this.type = type;
        this.title = title;
    }

    public RubricContent() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public RubricGrade getGrade() {
        return grade;
    }

    public void setGrade(RubricGrade grade) {
        this.grade = grade;
    }
}
