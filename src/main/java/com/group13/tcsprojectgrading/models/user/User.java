package com.group13.tcsprojectgrading.models.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.group13.tcsprojectgrading.models.graders.GradingParticipation;
import com.group13.tcsprojectgrading.models.grading.AssessmentLink;
import com.group13.tcsprojectgrading.models.course.CourseParticipation;
import com.group13.tcsprojectgrading.models.submissions.Label;
import com.group13.tcsprojectgrading.models.submissions.Submission;

import javax.persistence.*;
import java.io.IOException;
import java.util.*;

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

    @JsonIgnore
    @OneToMany(mappedBy="id.user")
    private List<GradingParticipation> projects;

    // submissions to grade
//    @Transient
    @JsonProperty("submissions")
    @OneToMany(mappedBy="grader", fetch = FetchType.LAZY)
    private Set<Submission> toGrade; // = new HashSet<>();

    public User(Long id, String name, String email, String sNumber, String avatar) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.courses = new ArrayList<>();
        this.assessments = new ArrayList<>();
        this.projects = new ArrayList<>();
        this.toGrade = new HashSet<>();
        this.sNumber = sNumber;
        this.avatar = avatar;
    }

    public User(Long id, String name, String email, String sNumber, String avatar, List<CourseParticipation> courses,
                List<AssessmentLink> assessments, List<GradingParticipation> projects) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.sNumber = sNumber;
        this.avatar = avatar;
        this.courses = courses;
        this.assessments = assessments;
        this.projects = projects;
    }

    public User(Long id, String name, String email, String sNumber, String avatar, List<CourseParticipation> courses,
                List<AssessmentLink> assessments, List<GradingParticipation> projects, Set<Submission> toGrade) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.sNumber = sNumber;
        this.avatar = avatar;
        this.courses = courses;
        this.assessments = assessments;
        this.projects = projects;
        this.toGrade = toGrade;
    }

    public User(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public User(Long id, String name, Set<Submission> toGrade) {
        this.id = id;
        this.name = name;
        this.toGrade = toGrade;
    }

    public User(Long id) {
        this.id = id;
    }

    public User() { }

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

    public List<GradingParticipation> getProjects() {
        return projects;
    }

    public void setProjects(List<GradingParticipation> projects) {
        this.projects = projects;
    }

    public Set<Submission> getToGrade() {
        return toGrade;
    }

    public void setToGrade(Set<Submission> toGrade) {
        this.toGrade = toGrade;
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
            jsonGenerator.writeStringField("avatar", user.getAvatar());
            jsonGenerator.writeEndObject();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public static class UserAssessmentSerialiser extends JsonSerializer<User> {
        @Override
        public void serialize(User user, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeNumberField("id", user.getId());
            jsonGenerator.writeStringField("name", user.getName());
            jsonGenerator.writeStringField("sNumber", user.getsNumber());
            jsonGenerator.writeStringField("avatar", user.getAvatar());
            jsonGenerator.writeBooleanField("isCurrentAssessment", user.isCurrent());
            jsonGenerator.writeEndObject();
        }
    }
}
