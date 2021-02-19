package com.group13.tcsprojectgrading.model.user;

import com.group13.tcsprojectgrading.model.course.Course;
import com.group13.tcsprojectgrading.model.project.Submission;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import java.util.Set;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Teacher extends Grader{
    private String teacher_attr;

    public Teacher(Account account, Course course) {
        super(account, course, "TEACHER");
    }

    public Teacher(String teacher_attr) {
        this.teacher_attr = teacher_attr;
    }


    public Teacher() {
    }

    public String getTeacher_attr() {
        return teacher_attr;
    }

    public void setTeacher_attr(String teacher_attr) {
        this.teacher_attr = teacher_attr;
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "id=" + id +
                ", account=" + account +
                ", course=" + course +
                ", teacher_attr='" + teacher_attr + '\'' +
                '}';
    }
}
