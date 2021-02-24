package com.group13.tcsprojectgrading.models.project;

import com.group13.tcsprojectgrading.models.course.Course;

import javax.persistence.*;
import java.util.Set;

@Entity
public class CourseGroupCategory {
    @Id
    private String id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @OneToMany(mappedBy = "courseGroupCategory")
    private Set<CourseGroup> courseGroups;

    public CourseGroupCategory(String id, String name, Course course) {
        this.id = id;
        this.name = name;
        this.course = course;
    }

    public CourseGroupCategory() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Set<CourseGroup> getCourseGroups() {
        return courseGroups;
    }

    public void setCourseGroups(Set<CourseGroup> courseGroups) {
        this.courseGroups = courseGroups;
    }

    @Override
    public String toString() {
        return "CourseGroupCategory{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", course=" + course +
                '}';
    }
}
