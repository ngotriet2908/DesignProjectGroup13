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

    public Assessment(UUID id, Map<String, CriterionGrade> grades) {
        this.id = id;
        this.grades = grades;
    }

    public Assessment(UUID id) {
        this.id = id;
        this.grades = new HashMap<>();
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
