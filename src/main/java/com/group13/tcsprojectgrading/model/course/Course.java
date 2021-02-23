package com.group13.tcsprojectgrading.model.course;

import com.fasterxml.jackson.annotation.*;
import com.group13.tcsprojectgrading.model.project.CourseGroup;
import com.group13.tcsprojectgrading.model.project.CourseGroupCategory;
import com.group13.tcsprojectgrading.model.project.Project;
import com.group13.tcsprojectgrading.model.user.Participant;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
public class Course {
    @Id
    private String id;
    private String name;
    private String courseCode;


    @OneToMany(mappedBy = "account")
    private Set<Participant> participants  = new HashSet<>();

//    @JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id", scope = Project.class)
//    @JsonIdentityReference(alwaysAsId=true)
    @OneToMany(mappedBy = "course")
    private Set<Project> projects  = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "course")
    private Set<CourseGroupCategory> courseGroups  = new HashSet<>();

    public Course() {
    }

    public Course(String id, String name, String course_code) {
        this.id = id;
        this.name = name;
        this.courseCode = course_code;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
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

    public Set<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(Set<Participant> participants) {
        this.participants = participants;
    }

    public Set<Project> getProjects() {
        return projects;
    }

    public void setProjects(Set<Project> projects) {
        this.projects = projects;
    }

    public Set<CourseGroupCategory> getCourseGroups() {
        return courseGroups;
    }

    public void setCourseGroups(Set<CourseGroupCategory> courseGroups) {
        this.courseGroups = courseGroups;
    }

    @Override
    public String toString() {
        return "Course{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return Objects.equals(id, course.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
