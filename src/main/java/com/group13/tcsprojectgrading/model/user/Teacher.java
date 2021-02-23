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
    private String teacherAttr;

    public Teacher(Account account, Course course) {
        super(account, course, "TEACHER");
    }

    public Teacher(String teacher_attr) {
        this.teacherAttr = teacher_attr;
    }


    public Teacher() {
    }

    public String getTeacherAttr() {
        return teacherAttr;
    }

    public void setTeacherAttr(String teacherAttr) {
        this.teacherAttr = teacherAttr;
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "id=" + id +
                ", account=" + account +
                ", course=" + course +
                ", role='" + role + '\'' +
                ", teacherAttr='" + teacherAttr + '\'' +
                '}';
    }
}
