package com.group13.tcsprojectgrading.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.group13.tcsprojectgrading.model.course.Course;
import com.group13.tcsprojectgrading.model.project.Submission;

import javax.persistence.*;
import java.util.Set;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Grader extends Participant{

    @OneToMany(mappedBy = "grader")
    private Set<Submission> submissions;

//    public Grader(ParticipantKey id, Account account, Course course, Set<Submission> submissions) {
//        super(id, account, course);
//        this.submissions = submissions;
//    }

    public Grader(Account account, Course course, String role) {
        super(account, course, role);
    }

    public Grader(Set<Submission> submissions) {
        this.submissions = submissions;
    }

    public Grader() {
    }

    public Set<Submission> getSubmissions() {
        return submissions;
    }

    public void setSubmissions(Set<Submission> submissions) {
        this.submissions = submissions;
    }

    @Override
    public String toString() {
        return "Grader{" +
                "submissions=" + submissions +
                '}';
    }
}
