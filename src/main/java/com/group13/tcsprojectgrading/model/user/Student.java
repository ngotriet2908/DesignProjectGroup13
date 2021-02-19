package com.group13.tcsprojectgrading.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.group13.tcsprojectgrading.model.course.Course;
import com.group13.tcsprojectgrading.model.project.CourseGroup;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Student extends Participant{
    private String student_attr;

    @JsonIgnore
    @OneToMany(mappedBy = "student")
    private Set<GroupParticipant> groupParticipants = new HashSet<>();


    public Student(Account account, Course course) {
        super(account, course, "STUDENT");
    }

    public Student(String student_attr) {
        this.student_attr = student_attr;
    }

    public Student() {
    }

    public String getStudent_attr() {
        return student_attr;
    }

    public void setStudent_attr(String student_attr) {
        this.student_attr = student_attr;
    }

    public Set<GroupParticipant> getGroupParticipants() {
        return groupParticipants;
    }

    public void setGroupParticipants(Set<GroupParticipant> groupParticipants) {
        this.groupParticipants = groupParticipants;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", account=" + account +
                ", course=" + course +
                ", student_attr='" + student_attr + '\'' +
                '}';
    }
}
