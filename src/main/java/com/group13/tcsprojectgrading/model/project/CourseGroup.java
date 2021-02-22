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

    private String canvas_id;

    private String name;

    private Long max_membership;

    private Long members_count;

    @OneToMany(mappedBy = "courseGroup")
    private Set<GroupParticipant> groupParticipants;

    @JsonIdentityInfo(generator= ObjectIdGenerators.PropertyGenerator.class, property="id", scope = CourseGroupCategory.class)
    @JsonIdentityReference(alwaysAsId=true)
    @ManyToOne
    @JoinColumn(name = "course_group_category_id")
    private CourseGroupCategory courseGroupCategory;

    public CourseGroup(String canvas_id, String name, Long max_membership, Long members_count, CourseGroupCategory courseGroupCategory) {
        this.canvas_id = canvas_id;
        this.name = name;
        this.max_membership = max_membership;
        this.courseGroupCategory = courseGroupCategory;
        this.members_count = members_count;
    }

    public CourseGroup() {
    }

    public Long getMax_membership() {
        return max_membership;
    }

    public void setMax_membership(Long max_membership) {
        this.max_membership = max_membership;
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

    public String getCanvas_id() {
        return canvas_id;
    }

    public void setCanvas_id(String canvas_id) {
        this.canvas_id = canvas_id;
    }

    public CourseGroupCategory getCourseGroupCategory() {
        return courseGroupCategory;
    }

    public void setCourseGroupCategory(CourseGroupCategory courseGroupCategory) {
        this.courseGroupCategory = courseGroupCategory;
    }

    public Long getMembers_count() {
        return members_count;
    }

    public void setMembers_count(Long members_count) {
        this.members_count = members_count;
    }

    @Override
    public String toString() {
        return "CourseGroup{" +
                "id=" + id +
                ", canvas_id='" + canvas_id + '\'' +
                ", name='" + name + '\'' +
                ", max_membership=" + max_membership +
                ", members_count=" + members_count +
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
