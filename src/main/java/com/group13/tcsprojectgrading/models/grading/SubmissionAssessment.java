package com.group13.tcsprojectgrading.models.grading;

import javax.persistence.Id;
import javax.persistence.IdClass;
import java.util.List;

@IdClass(SubmissionAssessmentKey.class)
public class SubmissionAssessment {
    private List<Grade> grades;
    private String rubricId;

    @Id
    private String courseId;
    @Id
    private String projectId;
    @Id
    private String submissionId;

    public SubmissionAssessment(List<Grade> grades, String rubricId, String courseId, String projectId, String taskId) {
        this.grades = grades;
        this.rubricId = rubricId;
        this.courseId = courseId;
        this.projectId = projectId;
        this.submissionId = taskId;
    }

    public SubmissionAssessment() {

    }

    public List<Grade> getGrades() {
        return grades;
    }

    public void setGrades(List<Grade> gradeDetails) {
        this.grades = gradeDetails;
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

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(String submissionId) {
        this.submissionId = submissionId;
    }
}
