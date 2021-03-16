package com.group13.tcsprojectgrading.models.grading;

import javax.persistence.Id;
import javax.persistence.IdClass;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IdClass(SubmissionAssessmentKey.class)
public class SubmissionAssessment {
//    private List<Grade> grades;

    private Map<String, List<Grade>> grades;

    @Id
    private String projectId;

    @Id
    private String userId;

    public SubmissionAssessment(Map<String, List<Grade>> grades, String projectId, String userId) {
        this.grades = grades;
//        this.rubricId = rubricId;
        this.projectId = projectId;
        this.userId = userId;
    }

    public SubmissionAssessment(String projectId, String userId) {
        this.grades = new HashMap<>();
//        this.rubricId = rubricId;
        this.projectId = projectId;
        this.userId = userId;
    }

    public SubmissionAssessment() {

    }

    public Map<String, List<Grade>> getGrades() {
        return grades;
    }

    public void setGrades(Map<String, List<Grade>> gradeDetails) {
        this.grades = gradeDetails;
    }

//    public String getRubricId() {
//        return rubricId;
//    }
//
//    public void setRubricId(String rubricId) {
//        this.rubricId = rubricId;
//    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
