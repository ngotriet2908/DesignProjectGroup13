package com.group13.tcsprojectgrading.model.project;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.group13.tcsprojectgrading.model.course.Course;
import com.group13.tcsprojectgrading.model.user.GroupParticipant;
import com.group13.tcsprojectgrading.model.user.Student;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
public class CourseGroup {

    @Id
    @SequenceGenerator(
            name = "course_group_sequence",
            sequenceName = "course_group_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "course_group_sequence"
    )
    private Long id;

    @Column(name = "canvas_id")
    private String canvasId;

    private String name;

    private Long maxMembership;

    private Long membersCount;

    @OneToMany(mappedBy = "courseGroup")
    private Set<GroupParticipant> groupParticipants;

    @ManyToOne
    @JoinColumn(name = "course_group_category_id")
    private CourseGroupCategory courseGroupCategory;

    public CourseGroup(String canvas_id, String name, Long max_membership, Long members_count, CourseGroupCategory courseGroupCategory) {
        this.canvasId = canvas_id;
        this.name = name;
        this.maxMembership = max_membership;
        this.courseGroupCategory = courseGroupCategory;
        this.membersCount = members_count;
    }

    public CourseGroup(String name, Long max_membership, Long members_count, CourseGroupCategory courseGroupCategory) {
        this.name = name;
        this.maxMembership = max_membership;
        this.courseGroupCategory = courseGroupCategory;
        this.membersCount = members_count;
    }

    public CourseGroup() {
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<GroupParticipant> getGroupParticipants() {
        return groupParticipants;
    }

    public void setGroupParticipants(Set<GroupParticipant> groupParticipants) {
        this.groupParticipants = groupParticipants;
    }

    public String getCanvasId() {
        return canvasId;
    }

    public void setCanvasId(String canvasId) {
        this.canvasId = canvasId;
    }

    public CourseGroupCategory getCourseGroupCategory() {
        return courseGroupCategory;
    }

    public void setCourseGroupCategory(CourseGroupCategory courseGroupCategory) {
        this.courseGroupCategory = courseGroupCategory;
    }

    public Long getMaxMembership() {
        return maxMembership;
    }

    public void setMaxMembership(Long maxMembership) {
        this.maxMembership = maxMembership;
    }

    public Long getMembersCount() {
        return membersCount;
    }

    public void setMembersCount(Long membersCount) {
        this.membersCount = membersCount;
    }

    @Override
    public String toString() {
        return "CourseGroup{" +
                "id=" + id +
                ", canvas_id='" + canvasId + '\'' +
                ", name='" + name + '\'' +
                ", max_membership=" + maxMembership +
                ", members_count=" + membersCount +
                ", groupParticipants=" + groupParticipants +
                ", courseGroupCategory=" + courseGroupCategory +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CourseGroup that = (CourseGroup) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
