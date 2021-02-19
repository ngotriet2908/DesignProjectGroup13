package com.group13.tcsprojectgrading.model.project;

import com.group13.tcsprojectgrading.model.course.Course;
import com.group13.tcsprojectgrading.model.user.GroupParticipant;
import com.group13.tcsprojectgrading.model.user.Student;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(uniqueConstraints={
        @UniqueConstraint(name = "group_name_course_unique",columnNames = {"name", "course_id"})
})
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

    private String name;

    @OneToMany(mappedBy = "courseGroup")
    private Set<GroupParticipant> groupParticipants;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    public CourseGroup(String name, Course course) {
        this.name = name;
        this.course = course;
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

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    @Override
    public String toString() {
        return "CourseGroup{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", course=" + course +
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
