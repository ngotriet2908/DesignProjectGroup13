package com.group13.tcsprojectgrading.models.grading;

public class Grade {
    private int grade;
    private String comment;
//    private String criterionId;
    private String userId;

    public Grade(int grade, String comment, String userId) {
        this.grade = grade;
        this.comment = comment;
//        this.criterionId = criterionId;
        this.userId = userId;
    }

    public Grade(int grade, String userId) {
        this.grade = grade;
//        this.criterionId = criterionId;
        this.userId = userId;
    }

    public Grade() {
    }

//    public String getCriterionId() {
//        return criterionId;
//    }
//
//    public void setCriterionId(String criterionId) {
//        this.criterionId = criterionId;
//    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
