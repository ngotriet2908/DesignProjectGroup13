package com.group13.tcsprojectgrading.model.project;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.group13.tcsprojectgrading.model.course.Course;
import com.group13.tcsprojectgrading.model.user.Account;
import com.group13.tcsprojectgrading.model.user.Grader;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Set;

@Entity
public class Submission {

    @EmbeddedId
    private SubmissionKey id;

    @ManyToOne
    @MapsId("projectId")
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne
    @MapsId("courseGroupId")
    @JoinColumn(name = "course_group_id")
    private CourseGroup courseGroup;

    private Timestamp submission_date;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "account_id", insertable = false, updatable = false),
            @JoinColumn(name = "course_id", insertable = false, updatable = false)
    })
    private Grader grader;

    @JsonIgnore
    @OneToMany(mappedBy = "submission")
    private Set<Grading> gradings;

    public Submission(SubmissionKey id, Project project, CourseGroup courseGroup, Timestamp submission_date, Grader grader, Set<Grading> gradings) {
        this.id = id;
        this.project = project;
        this.courseGroup = courseGroup;
        this.submission_date = submission_date;
        this.grader = grader;
        this.gradings = gradings;
    }

    public Submission(Project project, CourseGroup courseGroup) {
        this.project = project;
        this.courseGroup = courseGroup;
        this.id = new SubmissionKey(project.getId(), courseGroup.getId());
    }

    public Submission(Project project, CourseGroup courseGroup, Timestamp submission_date) {
        this.project = project;
        this.courseGroup = courseGroup;
        this.submission_date = submission_date;
        this.id = new SubmissionKey(project.getId(), courseGroup.getId());
    }

    public Submission() {
    }

    public SubmissionKey getId() {
        return id;
    }

    public void setId(SubmissionKey id) {
        this.id = id;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public CourseGroup getCourseGroup() {
        return courseGroup;
    }

    public void setCourseGroup(CourseGroup courseGroup) {
        this.courseGroup = courseGroup;
    }

    public Timestamp getSubmission_date() {
        return submission_date;
    }

    public void setSubmission_date(Timestamp submission_date) {
        this.submission_date = submission_date;
    }

    public Grader getGrader() {
        return grader;
    }

    public void setGrader(Grader grader) {
        this.grader = grader;
    }

    public Set<Grading> getGradings() {
        return gradings;
    }

    public void setGradings(Set<Grading> gradings) {
        this.gradings = gradings;
    }

    @Override
    public String toString() {
        return "Submission{" +
                "id=" + id +
                ", project=" + project +
                ", courseGroup=" + courseGroup +
                ", submission_date=" + submission_date +
                ", grader=" + grader +
                ", gradings=" + gradings +
                '}';
    }
}
