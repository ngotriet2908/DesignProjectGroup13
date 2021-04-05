package com.group13.tcsprojectgrading.models.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.group13.tcsprojectgrading.models.grading.AssessmentLink;
import com.group13.tcsprojectgrading.models.course.CourseParticipation;

import javax.persistence.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(name = "user", schema = "public")
public class User {
    @Id
    private Long id;

    private String name;
    private String email;
    private String sNumber;
    private String avatar;

    @Transient
    private boolean isCurrent;

    @JsonIgnore
    @OneToMany(mappedBy="id.user")
    private List<CourseParticipation> courses;

    @JsonIgnore
    @OneToMany(mappedBy="id.user")
    private List<AssessmentLink> assessments;

    public User(Long id, String name, String email, String sNumber, String avatar) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.courses = new ArrayList<>();
        this.assessments = new ArrayList<>();
        this.sNumber = sNumber;
        this.avatar = avatar;
    }

    public User(Long id, String name, String email, String sNumber, List<CourseParticipation> courses, List<AssessmentLink> assessments) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.sNumber = sNumber;
        this.courses = courses;
        this.assessments = assessments;
    }

    public User(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public User(Long id) {
        this.id = id;
    }

    public User() {
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<CourseParticipation> getCourses() {
        return courses;
    }

    public void setCourses(List<CourseParticipation> courses) {
        this.courses = courses;
    }

    public List<AssessmentLink> getAssessments() {
        return assessments;
    }

    public void setAssessments(List<AssessmentLink> assessments) {
        this.assessments = assessments;
    }

    public String getsNumber() {
        return sNumber;
    }

    public void setsNumber(String sNumber) {
        this.sNumber = sNumber;
    }

    public boolean isCurrent() {
        return isCurrent;
    }

    public void setCurrent(boolean current) {
        isCurrent = current;
    }

    /*
                Serialiser for the main information of the user (without details).
                 */
    public static class UserShortSerialiser extends JsonSerializer<User> {
        @Override
        public void serialize(User user, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeNumberField("id", user.getId());
            jsonGenerator.writeStringField("name", user.getName());
            jsonGenerator.writeStringField("sNumber", user.getsNumber());
            jsonGenerator.writeEndObject();
        }
    }

    public static class UserAssessmentSerialiser extends JsonSerializer<User> {
        @Override
        public void serialize(User user, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeNumberField("id", user.getId());
            jsonGenerator.writeStringField("name", user.getName());
            jsonGenerator.writeStringField("sNumber", user.getsNumber());
            jsonGenerator.writeBooleanField("isCurrentAssessment", user.isCurrent());
            jsonGenerator.writeEndObject();
        }
    }
}
