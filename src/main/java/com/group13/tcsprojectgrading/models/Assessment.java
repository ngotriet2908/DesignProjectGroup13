package com.group13.tcsprojectgrading.models;

import com.group13.tcsprojectgrading.models.grading.CriterionGrade;
import com.group13.tcsprojectgrading.models.grading.Grade;
import com.group13.tcsprojectgrading.models.rubric.Element;

import javax.persistence.Id;
import java.util.*;

public class Assessment {
    @Id
    private UUID id;
    private Map<String, CriterionGrade> grades;
    private int gradedCount = 0;
    private double progress;
    private double finalGrade;
    private boolean finalGradeManual = false;

    public Assessment(UUID id, Map<String, CriterionGrade> grades, int gradedCount, int finalGrade) {
        this.id = id;
        this.grades = grades;
        this.gradedCount = gradedCount;
        this.finalGrade = finalGrade;
        this.progress = 0;
    }

    public Assessment(UUID id, Map<String, CriterionGrade> grades, int gradedCount, int finalGrade, boolean finalGradeManual) {
        this.id = id;
        this.grades = grades;
        this.gradedCount = gradedCount;
        this.finalGrade = finalGrade;
        this.finalGradeManual = finalGradeManual;
        this.progress = 0;
    }

    public Assessment(UUID id, Map<String, CriterionGrade> grades) {
        this.id = id;
        this.grades = grades;
        this.gradedCount = 0;
        this.finalGradeManual = false;
        this.progress = 0;
    }

    public Assessment(UUID id) {
        this.id = id;
        this.grades = new HashMap<>();
        this.gradedCount = 0;
        this.progress = 0;
    }

    public Assessment() {
    }

    public Map<Element, Grade> getGradedCriteria(List<Element> criteria) {
        Map<Element, Grade> map = new HashMap<>();
        for(Element criterion: criteria) {
            if (grades.containsKey(criterion.getContent().getId())) {
                CriterionGrade criterionGrade = grades.get(criterion.getContent().getId());
                if (criterionGrade.getActive() < criterionGrade.getHistory().size()) {
                    map.put(criterion, criterionGrade.getHistory().get(criterionGrade.getActive()));
                }
            }
        }
        return map;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Map<String, CriterionGrade> getGrades() {
        return grades;
    }

    public void setGrades(Map<String, CriterionGrade> grades) {
        this.grades = grades;
    }

    public int getGradedCount() {
        return gradedCount;
    }

    public void setGradedCount(int gradedCount) {
        this.gradedCount = gradedCount;
    }

    public double getFinalGrade() {
        return finalGrade;
    }

    public void setFinalGrade(double finalGrade) {
        this.finalGrade = finalGrade;
    }

    public void incrementGradedCount() {
        this.gradedCount += 1;
    }

    public void increaseGradedCount(int n) {
        this.gradedCount += n;
    }

    public boolean isFinalGradeManual() {
        return finalGradeManual;
    }

    public void setFinalGradeManual(boolean finalGradeManual) {
        this.finalGradeManual = finalGradeManual;
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Assessment that = (Assessment) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
