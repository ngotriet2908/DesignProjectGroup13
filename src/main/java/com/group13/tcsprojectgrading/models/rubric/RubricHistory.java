package com.group13.tcsprojectgrading.models.rubric;

import com.fasterxml.jackson.databind.JsonNode;
import com.flipkart.zjsonpatch.JsonPatch;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

public class RubricHistory {
    // project id
    @Id
    private Long id;
    private List<RubricUpdate> history;

    public RubricHistory(Long id, List<RubricUpdate> history) {
        this.id = id;
        this.history = history;
    }

    public RubricHistory(Long id) {
        this.id = id;
        this.history = new ArrayList<>();
    }

    public RubricHistory() {
        this.history = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<RubricUpdate> getHistory() {
        return history;
    }

    public void setHistory(List<RubricUpdate> history) {
        this.history = history;
    }
}
