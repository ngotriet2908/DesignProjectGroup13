package com.group13.tcsprojectgrading.models.project;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.group13.tcsprojectgrading.models.feedback.FeedbackLog;
import com.group13.tcsprojectgrading.models.feedback.FeedbackTemplate;
import com.group13.tcsprojectgrading.models.user.Activity;
import com.group13.tcsprojectgrading.models.course.Course;
import com.group13.tcsprojectgrading.models.graders.GradingParticipation;
import com.group13.tcsprojectgrading.models.submissions.Label;
import com.group13.tcsprojectgrading.models.submissions.Submission;

import javax.persistence.*;
import java.io.IOException;
import java.util.List;

@JsonAppend(attrs = {
        @JsonAppend.Attr(value = "privileges")
})
@Entity
public class Project {
    @Id
    @JsonProperty("id")
    private Long id;

    private String name;
    private String createdAt;

    @ManyToOne
    @JsonSerialize(using= Course.CourseShortSerialiser.class)
    private Course course;

    @JsonIgnore
    @OneToMany(mappedBy = "id.project")
    private List<Activity> activities;

    @JsonIgnore
    @OneToMany(mappedBy = "id.project")
    private List<GradingParticipation> graders;

    @JsonIgnore
    @OneToMany(mappedBy = "project")
    private List<Submission> submissions;

    @JsonIgnore
    @OneToMany(mappedBy = "project")
    private List<Label> labels;

    @JsonIgnore
    @OneToMany(mappedBy = "project")
    private List<FeedbackTemplate> feedbackTemplates;

    public Project() {
    }

    public Project(Long id) {
        this.id = id;
    }

    public Project(Long id, Course course, String name, String createdAt) {
        this.id = id;
        this.course = course;
        this.name = name;
        this.createdAt = createdAt;
    }

    public Project(Long id, String name, String createdAt, List<Activity> activities, List<GradingParticipation> graders, List<Submission> submissions, List<Label> labels) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.activities = activities;
        this.graders = graders;
        this.submissions = submissions;
        this.labels = labels;
    }

    public Project(Long id, String name, String createdAt, Course course, List<Activity> activities, List<GradingParticipation> graders, List<Submission> submissions, List<Label> labels) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.course = course;
        this.activities = activities;
        this.graders = graders;
        this.submissions = submissions;
        this.labels = labels;
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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public List<Activity> getActivities() {
        return activities;
    }

    public void setActivities(List<Activity> activities) {
        this.activities = activities;
    }

    public List<GradingParticipation> getGraders() {
        return graders;
    }

    public void setGraders(List<GradingParticipation> graders) {
        this.graders = graders;
    }

    public List<Submission> getSubmissions() {
        return submissions;
    }

    public void setSubmissions(List<Submission> submissions) {
        this.submissions = submissions;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public List<Label> getLabels() {
        return labels;
    }

    public void setLabels(List<Label> labels) {
        this.labels = labels;
    }

    public List<FeedbackTemplate> getFeedbackTemplates() {
        return feedbackTemplates;
    }

    public void setFeedbackTemplates(List<FeedbackTemplate> feedbackTemplates) {
        this.feedbackTemplates = feedbackTemplates;
    }

    /*
       Serialiser for the main information of the course (without details).
        */
    public static class ProjectShortSerialiser extends JsonSerializer<Project> {
        @Override
        public void serialize(Project project, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeNumberField("id", project.getId());
            jsonGenerator.writeStringField("name", project.getName());
            jsonGenerator.writeStringField("createdAt", project.getCreatedAt());
            jsonGenerator.writeEndObject();
        }
    }
}
