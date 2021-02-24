package com.group13.tcsprojectgrading.models.user;


import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ParticipantKey implements Serializable {

    @Column(name = "account_id")
    private String accountId;

    @Column(name = "course_id")
    private String courseId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass())
            return false;

        ParticipantKey that = (ParticipantKey) o;
        return Objects.equals(accountId, that.accountId) &&
                Objects.equals(courseId, that.courseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId, courseId);
    }

    public ParticipantKey(String accountId, String courseId) {
        this.accountId = accountId;
        this.courseId = courseId;
    }

    public ParticipantKey() {
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    @Override
    public String toString() {
        return "ParticipantKey{" +
                "accountId='" + accountId + '\'' +
                ", courseId='" + courseId + '\'' +
                '}';
    }
}
