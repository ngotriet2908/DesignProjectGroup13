package com.group13.tcsprojectgrading.model.user;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.group13.tcsprojectgrading.model.course.Course;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Participant {

    @EmbeddedId
    protected ParticipantKey id;

    @ManyToOne
    @MapsId("accountId")
    @JoinColumn(name = "account_id")
    protected Account account;

    @ManyToOne
    @MapsId("courseId")
    @JoinColumn(name = "course_id")
    protected Course course;

    protected String role;

    public Participant(Account account, Course course, String role) {
        this.account = account;
        this.course = course;
        this.role = role;
        this.id = new ParticipantKey(account.getId(), course.getId());
    }

    public Participant() {
    }

    public ParticipantKey getId() {
        return id;
    }

    public void setId(ParticipantKey id) {
        this.id = id;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "Participant{" +
                "id=" + id +
                ", account=" + account +
                ", course=" + course +
                ", role='" + role + '\'' +
                '}';
    }
}
