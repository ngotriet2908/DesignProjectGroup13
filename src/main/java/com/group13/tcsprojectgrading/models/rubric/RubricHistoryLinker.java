package com.group13.tcsprojectgrading.models.rubric;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

@Entity
public class RubricHistoryLinker {
    @Id
    public Long id;

    @Column(columnDefinition="TEXT")
    public String rubricHistory;

    public RubricHistoryLinker(Long id, String rubricHistory) {
        this.id = id;
        this.rubricHistory = rubricHistory;
    }

    public RubricHistoryLinker(Long id) {
        this.id = id;
    }

    public RubricHistoryLinker() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRubricHistory() {
        return rubricHistory;
    }

    public void setRubricHistory(String rubricHistory) {
        this.rubricHistory = rubricHistory;
    }
}
