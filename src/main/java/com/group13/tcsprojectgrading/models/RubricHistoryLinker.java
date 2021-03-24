package com.group13.tcsprojectgrading.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

@Entity
public class RubricHistoryLinker {

    @Id
    public String id;

//    @Lob
    @Column(columnDefinition="TEXT")
    public String rubricHistory;

    public RubricHistoryLinker(String id, String rubricHistory) {
        this.id = id;
        this.rubricHistory = rubricHistory;
    }

    public RubricHistoryLinker(String id) {
        this.id = id;
    }

    public RubricHistoryLinker() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRubricHistory() {
        return rubricHistory;
    }

    public void setRubricHistory(String rubricHistory) {
        this.rubricHistory = rubricHistory;
    }
}
