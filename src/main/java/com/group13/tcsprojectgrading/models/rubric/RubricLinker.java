package com.group13.tcsprojectgrading.models.rubric;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

@Entity
public class RubricLinker {

    @Id
    private String id;

//    @Lob
    @Column(columnDefinition="TEXT")
    private String rubric;

    public RubricLinker(String id, String rubric) {
        this.id = id;
        this.rubric = rubric;
    }

    public RubricLinker() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRubric() {
        return rubric;
    }

    public void setRubric(String rubric) {
        this.rubric = rubric;
    }
}
