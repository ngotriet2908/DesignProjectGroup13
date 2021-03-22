package com.group13.tcsprojectgrading.models;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.OneToMany;
import java.util.List;
import java.util.Objects;

import static com.group13.tcsprojectgrading.models.Submission.createFlagsArrayNode;

@Entity
@IdClass(ProjectId.class)
public class Project {

    @Id
    private String courseId;

    @Id
    private String projectId;

    @OneToMany(mappedBy = "project")
    private List<Activity> activities;

    @OneToMany(mappedBy = "project")
    private List<Grader> graders;

    @OneToMany(mappedBy = "project")
    private List<Participant> participants;

    @OneToMany(mappedBy = "project")
    private List<Submission> submissions;

    @OneToMany(mappedBy = "project")
    private List<Flag> flags;


    private String name;

    private String description;

    private String createAt;

    public Project(String courseId, String projectId, String name, String description, String createAt) {
        this.courseId = courseId;
        this.projectId = projectId;
        this.name = name;
        this.description = description;
        this.createAt = createAt;
    }

    public Project() {
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getProjectId() {
        return projectId;
    }

    public ProjectId getProjectCompositeKey() {
        return new ProjectId(courseId, projectId);
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public List<Activity> getActivities() {
        return activities;
    }

    public void setActivities(List<Activity> activities) {
        this.activities = activities;
    }

    public List<Grader> getGraders() {
        return graders;
    }

    public void setGraders(List<Grader> graders) {
        this.graders = graders;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public List<Submission> getSubmissions() {
        return submissions;
    }

    public void setSubmissions(List<Submission> submissions) {
        this.submissions = submissions;
    }

    public List<Flag> getFlags() {
        return flags;
    }

    public void setFlags(List<Flag> flags) {
        this.flags = flags;
    }

    public JsonNode convertToJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("courseId", courseId);
        objectNode.put("id", projectId);
        objectNode.put("name", name);
        objectNode.put("createAt", createAt);
        objectNode.put("description", description);
        return objectNode;
    }

    public JsonNode convertToJsonDetails() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("courseId", courseId);
        objectNode.put("id", projectId);
        objectNode.put("name", name);
        objectNode.put("createAt", createAt);
        objectNode.put("description", description);
        objectNode.set("flags", createFlagsArrayNode(flags));
        return objectNode;
    }

    @Override
    public String toString() {
        return "Project{" +
                "courseId='" + courseId + '\'' +
                ", projectId='" + projectId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return Objects.equals(courseId, project.courseId) && Objects.equals(projectId, project.projectId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseId, projectId);
    }
}
