package com.group13.tcsprojectgrading.models.grading;

public class Grade {
    private int grade;
    private String comment;
    private String userId;
//    private boolean isActive;
    private long created;

//    public Grade(int grade, String comment, String userId, boolean isActive, long created) {
//        this.grade = grade;
//        this.comment = comment;
//        this.userId = userId;
//        this.isActive = isActive;
//        this.created = created;
//    }

//    public Grade(int grade, String userId, boolean isActive, long created) {
//        this.grade = grade;
//        this.userId = userId;
//        this.isActive = isActive;
//        this.created = created;
//    }

    public Grade() {
    }

//    public boolean getIsActive() {
//        return isActive;
//    }
//
//    public void setIsActive(boolean isActive) {
//        this.isActive = isActive;
//    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

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

    @Override
    public String toString() {
        return "Grade{" +
                "grade=" + grade +
                ", comment='" + comment + '\'' +
                ", userId='" + userId + '\'' +
//                ", isActive=" + isActive +
                ", created=" + created +
                '}';
    }
}
