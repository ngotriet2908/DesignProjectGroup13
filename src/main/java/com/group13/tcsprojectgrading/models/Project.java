package com.group13.tcsprojectgrading.models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.OneToMany;
import java.util.List;

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
    private List<Task> tasks;

    public Project(String courseId, String projectId) {
        this.courseId = courseId;
        this.projectId = projectId;
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

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    @Override
    public String toString() {
        return "Project{" +
                "courseId='" + courseId + '\'' +
                ", projectId='" + projectId + '\'' +
                '}';
    }
}
