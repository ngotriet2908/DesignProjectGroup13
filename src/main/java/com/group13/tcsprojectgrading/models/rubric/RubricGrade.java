package com.group13.tcsprojectgrading.models.rubric;

public class RubricGrade {
    public int min;
    public int max;
    public int step;

    public RubricGrade(int min, int max, int step) {
        this.min = min;
        this.max = max;
        this.step = step;
    }

    public RubricGrade() {
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }
}
