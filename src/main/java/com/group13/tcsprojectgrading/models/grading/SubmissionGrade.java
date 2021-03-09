package com.group13.tcsprojectgrading.models.grading;

import java.util.List;

public class SubmissionGrade {
    private List<Grade> grades;
    private String rubricId;
    private String projectId;
    private String submissionId;

    public SubmissionGrade(List<Grade> grades, String rubricId, String projectId, String submissionId) {
        this.grades = grades;
        this.rubricId = rubricId;
        this.projectId = projectId;
        this.submissionId = submissionId;
    }

    public SubmissionGrade() {

    }

    public List<Grade> getGrades() {
        return grades;
    }

    public void setGrades(List<Grade> grades) {
        this.grades = grades;
    }

    public String getRubricId() {
        return rubricId;
    }

    public void setRubricId(String rubricId) {
        this.rubricId = rubricId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(String submissionId) {
        this.submissionId = submissionId;
    }
}
