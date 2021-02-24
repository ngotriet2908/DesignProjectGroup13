package com.group13.tcsprojectgrading.models.user;

import com.group13.tcsprojectgrading.models.course.Course;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class TeachingAssistant extends Grader{
    private String teachingAssistantAttr;


    public TeachingAssistant(Account account, Course course) {
        super(account, course, "TA");
    }

    public TeachingAssistant(String teachingassistant_attr) {
        this.teachingAssistantAttr = teachingassistant_attr;
    }

    public TeachingAssistant() {
    }

    public String getTeachingAssistantAttr() {
        return teachingAssistantAttr;
    }

    public void setTeachingAssistantAttr(String teachingAssistantAttr) {
        this.teachingAssistantAttr = teachingAssistantAttr;
    }

    @Override
    public String toString() {
        return "TeachingAssistant{" +
                "id=" + id +
                ", account=" + account +
                ", course=" + course +
                ", teachingassistant_attr='" + teachingAssistantAttr + '\'' +
                '}';
    }
}
