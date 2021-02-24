package com.group13.tcsprojectgrading.models.project;

import com.fasterxml.jackson.annotation.*;
import com.group13.tcsprojectgrading.models.user.Grader;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Set;

@Entity
public class Submission {

    @EmbeddedId
    private SubmissionKey id;

    @JsonIdentityInfo(generator= ObjectIdGenerators.PropertyGenerator.class, property="id", scope = Project.class)
    @JsonIdentityReference(alwaysAsId=true)
    @ManyToOne
    @MapsId("projectId")
    @JoinColumn(name = "project_id")
    private Project project;

    @JsonIgnoreProperties({"course", "groupParticipants" })
    @ManyToOne
    @MapsId("courseGroupId")
    @JoinColumn(name = "course_group_id")
    private CourseGroup courseGroup;

    private Timestamp submission_date;

    @JsonIgnoreProperties({"submissions","account","course","teachingassistant_attr"})
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "account_id"),
            @JoinColumn(name = "course_id")
    })
    private Grader grader;

    @OneToMany(mappedBy = "submission")
    private Set<Grading> gradings;

    @OneToMany(mappedBy = "submission")
    private Set<Attachment> attachments;

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

    public Set<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(Set<Attachment> attachments) {
        this.attachments = attachments;
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
