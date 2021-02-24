package com.group13.tcsprojectgrading.models.user;

import com.group13.tcsprojectgrading.models.course.Course;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Student extends Participant{
    private String studentAttr;

    @OneToMany(mappedBy = "student")
    private Set<GroupParticipant> groupParticipants = new HashSet<>();


    public Student(Account account, Course course) {
        super(account, course, "STUDENT");
    }

    public Student(String student_attr) {
        this.studentAttr = student_attr;
    }

    public Student() {
    }

    public String getStudentAttr() {
        return studentAttr;
    }

    public void setStudentAttr(String studentAttr) {
        this.studentAttr = studentAttr;
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
                ", student_attr='" + studentAttr + '\'' +
                '}';
    }
}
