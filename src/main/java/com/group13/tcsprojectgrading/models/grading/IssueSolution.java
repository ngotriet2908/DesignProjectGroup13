package com.group13.tcsprojectgrading.models.grading;

public class IssueSolution {
    private String solution;

    public IssueSolution(String solution) {
        this.solution = solution;
    }

    public IssueSolution() {
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }
}
