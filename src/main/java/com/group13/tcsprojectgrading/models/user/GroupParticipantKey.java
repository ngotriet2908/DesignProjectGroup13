package com.group13.tcsprojectgrading.models.user;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class GroupParticipantKey implements Serializable {

    @Column(name = "student_id")
    private ParticipantKey student_id;

    @Column(name = "course_group_id")
    private Long courseGroupId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass())
            return false;

        GroupParticipantKey that = (GroupParticipantKey) o;
        return Objects.equals(student_id, that.student_id) &&
                Objects.equals(courseGroupId, that.courseGroupId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(student_id, courseGroupId);
    }

    public GroupParticipantKey(ParticipantKey student_id, Long courseGroupId) {
        this.student_id = student_id;
        this.courseGroupId = courseGroupId;
    }

    public GroupParticipantKey() {
    }

    public ParticipantKey getStudent_id() {
        return student_id;
    }

    public void setStudent_id(ParticipantKey student_id) {
        this.student_id = student_id;
    }

    public Long getCourseGroupId() {
        return courseGroupId;
    }

    public void setCourseGroupId(Long courseGroupId) {
        this.courseGroupId = courseGroupId;
    }

    @Override
    public String toString() {
        return "GroupMemberKey{" +
                "student_id=" + student_id +
                ", courseGroupId='" + courseGroupId + '\'' +
                '}';
    }
}
