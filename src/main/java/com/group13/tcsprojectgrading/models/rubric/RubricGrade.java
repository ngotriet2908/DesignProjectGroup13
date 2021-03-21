package com.group13.tcsprojectgrading.models.rubric;

public class RubricGrade {
    public int min;
    public int max;
    public int step;
    public double weight;

    public RubricGrade(int min, int max, int step, double weight) {
        this.min = min;
        this.max = max;
        this.step = step;
        this.weight = weight;
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

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
