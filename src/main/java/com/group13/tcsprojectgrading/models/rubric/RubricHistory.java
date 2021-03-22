package com.group13.tcsprojectgrading.models.rubric;

import com.fasterxml.jackson.databind.JsonNode;
import com.flipkart.zjsonpatch.JsonPatch;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

public class RubricHistory {
    // project id
    @Id
    private String id;
    private List<RubricUpdate> history;

    public RubricHistory(String id, List<RubricUpdate> history) {
        this.id = id;
        this.history = history;
    }

    public RubricHistory(String id) {
        this.id = id;
        this.history = new ArrayList<>();
    }

    public RubricHistory() {
        this.history = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<RubricUpdate> getHistory() {
        return history;
    }

    public void setHistory(List<RubricUpdate> history) {
        this.history = history;
    }
}
