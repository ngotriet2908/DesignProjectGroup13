package com.group13.tcsprojectgrading.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group13.tcsprojectgrading.models.grading.CriterionGrade;
import com.group13.tcsprojectgrading.models.grading.Grade;
import com.group13.tcsprojectgrading.models.rubric.Element;
import com.group13.tcsprojectgrading.models.rubric.Rubric;

import javax.persistence.Id;
import java.util.*;

public class Assessment {
    @Id
    private UUID id;
    private Map<String, CriterionGrade> grades;
    private int gradedCount = 0;
    private int finalGrade;
    private boolean finalGradeManual = false;

    public Assessment(UUID id, Map<String, CriterionGrade> grades, int gradedCount, int finalGrade) {
        this.id = id;
        this.grades = grades;
        this.gradedCount = gradedCount;
        this.finalGrade = finalGrade;
    }

    public Assessment(UUID id, Map<String, CriterionGrade> grades, int gradedCount, int finalGrade, boolean finalGradeManual) {
        this.id = id;
        this.grades = grades;
        this.gradedCount = gradedCount;
        this.finalGrade = finalGrade;
        this.finalGradeManual = finalGradeManual;
    }

    public Assessment(UUID id, Map<String, CriterionGrade> grades) {
        this.id = id;
        this.grades = grades;
        this.gradedCount = 0;
        this.finalGradeManual = false;
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

    public int getGradedCount() {
        return gradedCount;
    }

    public void setGradedCount(int gradedCount) {
        this.gradedCount = gradedCount;
    }

    public int getFinalGrade() {
        return finalGrade;
    }

    public void setFinalGrade(int finalGrade) {
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

    public static void main(String[] args) throws JsonProcessingException {
        String json = "{\n" +
                "    \"id\": \"ae057613-87d7-4ee3-94d2-45a4b8665b80\",\n" +
                "    \"grades\": {\n" +
                "      \"5da62319-bbbc-4691-b9bd-c76835633ab6\": {\n" +
                "        \"active\": 0,\n" +
                "        \"history\": [\n" +
                "          {\n" +
                "            \"grade\": 2,\n" +
                "            \"comment\": \"dasdasdas\",\n" +
                "            \"userId\": \"160\",\n" +
                "            \"created\": 1616588332050\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      \"9d9943c6-6884-40b4-91c6-6de00e0ef890\": {\n" +
                "        \"active\": 0,\n" +
                "        \"history\": [\n" +
                "          {\n" +
                "            \"grade\": 3,\n" +
                "            \"comment\": \"dasdasasd\",\n" +
                "            \"userId\": \"160\",\n" +
                "            \"created\": 1616588338149\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      \"d8a75022-73ee-4c5b-9b40-2ef59acef73d\": {\n" +
                "        \"active\": 0,\n" +
                "        \"history\": [\n" +
                "          {\n" +
                "            \"grade\": 4,\n" +
                "            \"comment\": \"adassadas\",\n" +
                "            \"userId\": \"160\",\n" +
                "            \"created\": 1616588343928\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      \"a7667e09-25f9-4321-a370-168e679d4b1c\": {\n" +
                "        \"active\": 0,\n" +
                "        \"history\": [\n" +
                "          {\n" +
                "            \"grade\": 5,\n" +
                "            \"comment\": \"dasdsa\",\n" +
                "            \"userId\": \"160\",\n" +
                "            \"created\": 1616588347976\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      \"e576cdfe-0cad-4986-a948-8d0dde4c958e\": {\n" +
                "        \"active\": 0,\n" +
                "        \"history\": [\n" +
                "          {\n" +
                "            \"grade\": 4,\n" +
                "            \"comment\": \"dasdsadas\",\n" +
                "            \"userId\": \"160\",\n" +
                "            \"created\": 1616588355330\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      \"841c68a9-d7de-46ea-bff3-a40564408a88\": {\n" +
                "        \"active\": 0,\n" +
                "        \"history\": [\n" +
                "          {\n" +
                "            \"grade\": 4,\n" +
                "            \"comment\": \"dasdsaadd\",\n" +
                "            \"userId\": \"160\",\n" +
                "            \"created\": 1616588364647\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      \"6c13e719-bfb3-4731-a877-3c2701c14482\": {\n" +
                "        \"active\": 0,\n" +
                "        \"history\": [\n" +
                "          {\n" +
                "            \"grade\": 4,\n" +
                "            \"comment\": \"fasfasfsafa\",\n" +
                "            \"userId\": \"160\",\n" +
                "            \"created\": 1616588399220\n" +
                "          }\n" +
                "        ]\n" +
                "      }\n" +
                "    },\n" +
                "    \"finalGrade\": 0,\n" +
                "    \"finalGradeManual\": false,\n" +
                "    \"gradedCount\": 7\n" +
                "  }";

        ObjectMapper objectMapper = new ObjectMapper();
        Assessment rubric = objectMapper.readValue(json, Assessment.class);
        System.out.println(rubric);
        System.out.println(objectMapper.writeValueAsString(rubric));
    }
}
