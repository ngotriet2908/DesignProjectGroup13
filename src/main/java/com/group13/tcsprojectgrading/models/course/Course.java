package com.group13.tcsprojectgrading.models.course;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.group13.tcsprojectgrading.models.project.Project;

import javax.persistence.*;
import java.io.IOException;
import java.util.*;

@JsonAppend(attrs = {
        @JsonAppend.Attr(value = "role")
})
@Entity
public class Course {
    @Id
    private Long id;
    private String name;

    @Column(name = "start_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startAt;

    @JsonIgnore
    @OneToMany(mappedBy="id.course")
    private List<CourseParticipation> users;

    @JsonSerialize(contentUsing= Project.ProjectShortSerialiser.class)
    @OneToMany(mappedBy="course")
    private Set<Project> projects;

    public Course(Long id, String name, Date startAt) {
        this.id = id;
        this.name = name;
        this.startAt = startAt;
        users = new ArrayList<>();
        projects = new HashSet<>();
    }

    public Course() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStartAt() {
        return startAt;
    }

    public void setStartAt(Date startAt) {
        this.startAt = startAt;
    }

    @JsonIgnore
    public List<CourseParticipation> getUsers() {
        return users;
    }

    public void setUsers(List<CourseParticipation> users) {
        this.users = users;
    }

    public Set<Project> getProjects() {
        return projects;
    }

    public void setProjects(Set<Project> projects) {
        this.projects = projects;
    }

    /*
    Serialiser for the main information of the course (without details).
     */
    public static class CourseShortSerialiser extends JsonSerializer<Course> {
        @Override
        public void serialize(Course course, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeNumberField("id", course.getId());
            jsonGenerator.writeStringField("name", course.getName());
            jsonGenerator.writeEndObject();
        }
    }
}
