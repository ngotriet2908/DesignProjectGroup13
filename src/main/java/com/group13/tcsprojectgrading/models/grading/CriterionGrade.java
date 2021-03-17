package com.group13.tcsprojectgrading.models.grading;

import java.util.List;

public class CriterionGrade {
    private int active;
    private List<Grade> history;

    public CriterionGrade() {
    }

    public CriterionGrade(int active, List<Grade> history) {
        this.active = active;
        this.history = history;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public List<Grade> getHistory() {
        return history;
    }

    public void setHistory(List<Grade> history) {
        this.history = history;
    }
}
