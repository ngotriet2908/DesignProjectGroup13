package com.group13.tcsprojectgrading.model.user;

import com.group13.tcsprojectgrading.model.project.CourseGroup;
import com.group13.tcsprojectgrading.model.project.SubmissionKey;

import javax.persistence.*;

@Entity
public class GroupParticipant {

    @EmbeddedId
    private GroupParticipantKey id;

    @ManyToOne
    @MapsId("studentId")
    @JoinColumns({
            @JoinColumn(name = "account_id", insertable = false, updatable = false),
            @JoinColumn(name = "course_id", insertable = false, updatable = false)
    })
    private Student student;

    @ManyToOne
    @MapsId("courseGroupId")
    @JoinColumn(name = "course_group_id")
    private CourseGroup courseGroup;

    public GroupParticipant(Student student, CourseGroup courseGroup) {
        this.student = student;
        this.courseGroup = courseGroup;
        this.id = new GroupParticipantKey(student.getId(), courseGroup.getId());
    }

    public GroupParticipant() {
    }

    public GroupParticipantKey getId() {
        return id;
    }

    public void setId(GroupParticipantKey id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public CourseGroup getCourseGroup() {
        return courseGroup;
    }

    public void setCourseGroup(CourseGroup courseGroup) {
        this.courseGroup = courseGroup;
    }

    @Override
    public String toString() {
        return "GroupParticipant{" +
                "id=" + id +
                ", student=" + student +
                ", courseGroup=" + courseGroup +
                '}';
    }
}
