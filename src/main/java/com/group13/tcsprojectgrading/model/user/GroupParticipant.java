package com.group13.tcsprojectgrading.model.user;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.group13.tcsprojectgrading.model.project.CourseGroup;
import com.group13.tcsprojectgrading.model.project.Grading;
import com.group13.tcsprojectgrading.model.project.SubmissionKey;
import org.checkerframework.checker.units.qual.C;

import javax.persistence.*;

@Entity
public class GroupParticipant {

    @EmbeddedId
    private GroupParticipantKey id;

    @JsonIgnoreProperties({"course", "groupParticipants" })
    @ManyToOne
    @MapsId("studentId")
    @JoinColumns({
            @JoinColumn(name = "account_id", insertable = false, updatable = false),
            @JoinColumn(name = "course_id", insertable = false, updatable = false)
    })
    private Student student;

    @JsonIdentityInfo(generator= ObjectIdGenerators.PropertyGenerator.class, property="id", scope = CourseGroup.class)
    @JsonIdentityReference(alwaysAsId=true)
    @ManyToOne
    @MapsId("courseGroupId")
    @JoinColumn(name = "course_group_id")
    private CourseGroup courseGroup;

    private Long canvasId;

    public GroupParticipant(Student student, CourseGroup courseGroup) {
        this.student = student;
        this.courseGroup = courseGroup;
        this.id = new GroupParticipantKey(student.getId(), courseGroup.getId());
    }

    public GroupParticipant(Student student, CourseGroup courseGroup, Long canvas_id) {
        this.student = student;
        this.courseGroup = courseGroup;
        this.canvasId = canvas_id;
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

    public Long getCanvasId() {
        return canvasId;
    }

    public void setCanvasId(Long canvasId) {
        this.canvasId = canvasId;
    }

    @Override
    public String toString() {
        return "GroupParticipant{" +
                "id=" + id +
                ", student=" + student +
                ", courseGroup=" + courseGroup +
                ", canvas_id=" + canvasId +
                '}';
    }
}
