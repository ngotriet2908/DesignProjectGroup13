package com.group13.tcsprojectgrading.models.course;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.group13.tcsprojectgrading.models.grading.AssessmentLink;
import com.group13.tcsprojectgrading.models.submissions.Submission;
import com.group13.tcsprojectgrading.models.user.User;
import com.group13.tcsprojectgrading.models.permissions.Role;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
public class CourseParticipation {
    @Embeddable
    public static class Pk implements Serializable {
        @ManyToOne
        @JoinColumn(name="userId")
        private User user;

        @ManyToOne
        @JoinColumn(name="courseId")
        private Course course;

        public Pk(User user, Course course) {
            this.user = user;
            this.course = course;
        }

        public Pk() { }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public Course getCourse() {
            return course;
        }

        public void setCourse(Course course) {
            this.course = course;
        }
    }

    @EmbeddedId
    private Pk id;

    @ManyToOne
    private Role role;

    @Transient
//    @JsonSerialize(contentUsing= Submission.SubmissionShortSerializer.class)
    private List<Submission> submissions;

    public CourseParticipation() {
    }

    public CourseParticipation(User user, Course course, Role role) {
        this.id = new Pk(user, course);
        this.role = role;
    }

    public Pk getId() {
        return id;
    }

    public void setId(Pk id) {
        this.id = id;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public List<Submission> getSubmissions() {
        return submissions;
    }

    public void setSubmissions(List<Submission> submissions) {
        this.submissions = submissions;
    }
}
