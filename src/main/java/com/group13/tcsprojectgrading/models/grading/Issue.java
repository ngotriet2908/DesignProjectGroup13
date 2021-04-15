package com.group13.tcsprojectgrading.models.grading;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.group13.tcsprojectgrading.models.course.Course;
import com.group13.tcsprojectgrading.models.project.Project;
import com.group13.tcsprojectgrading.models.submissions.Submission;
import com.group13.tcsprojectgrading.models.user.User;

import javax.persistence.*;
import java.io.IOException;

@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Issue {
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @JsonIdentityInfo(generator= ObjectIdGenerators.PropertyGenerator.class, property="id")
    @JsonIdentityReference(alwaysAsId = true)
    private Assessment assessment;

    private String target;

    @ManyToOne
    @JsonSerialize(using = Issue.IssueShortSerialiser.class)
    private Issue referentIssue;

    private String subject;

    @Column(columnDefinition="TEXT")
    private String description;

    @ManyToOne
    @JsonSerialize(using = User.UserShortSerialiser.class)
    private User creator;

    @ManyToOne
    @JsonSerialize(using = User.UserShortSerialiser.class)
    private User addressee;

    @ManyToOne
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "name")
    @JsonIdentityReference(alwaysAsId = true)
    private IssueStatus status;

    private String solution;

    @ManyToOne
    @JsonSerialize(using = Submission.SubmissionShortSerializer.class)
    private Submission submission;

    @ManyToOne
    @JsonSerialize(using = Course.CourseShortSerialiser.class)
    private Course course;

    @ManyToOne
    @JsonSerialize(using = Project.ProjectShortSerialiser.class)
    private Project project;

    public Issue() { }

    public Issue(Assessment assessment, String target, Issue referentIssue, String subject, String description,
                 User creator, User addressee, IssueStatus status, String solution, Submission submission,
                 Course course, Project project) {
        this.assessment = assessment;
        this.target = target;
        this.referentIssue = referentIssue;
        this.subject = subject;
        this.description = description;
        this.creator = creator;
        this.addressee = addressee;
        this.status = status;
        this.solution = solution;
        this.submission = submission;
        this.course = course;
        this.project = project;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Assessment getAssessment() {
        return assessment;
    }

    public void setAssessment(Assessment assessment) {
        this.assessment = assessment;
    }

    public Issue getReferentIssue() {
        return referentIssue;
    }

    public void setReferentIssue(Issue referentIssue) {
        this.referentIssue = referentIssue;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public User getAddressee() {
        return addressee;
    }

    public void setAddressee(User addressee) {
        this.addressee = addressee;
    }

    public IssueStatus getStatus() {
        return status;
    }

    public void setStatus(IssueStatus status) {
        this.status = status;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public Submission getSubmission() {
        return submission;
    }

    public void setSubmission(Submission submission) {
        this.submission = submission;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    /*
                Serialiser for the brief information about the issue.
                 */
    public static class IssueShortSerialiser extends JsonSerializer<Issue> {
        @Override
        public void serialize(Issue issue, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeNumberField("id", issue.getId());
            jsonGenerator.writeStringField("subject", issue.getSubject());
            jsonGenerator.writeEndObject();
        }
    }
}
