package com.group13.tcsprojectgrading.models.submissions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.group13.tcsprojectgrading.models.grading.Assessment;
import com.group13.tcsprojectgrading.models.grading.AssessmentLink;
import com.group13.tcsprojectgrading.models.project.Project;
import com.group13.tcsprojectgrading.models.user.User;

import javax.persistence.*;
import java.io.IOException;
import java.util.*;

/**
 * Model represent a submission that is import from canvas
 */
@Entity
public class Submission {
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE)
    private Long id;

    private Long groupId;
    private String name;

    @Column(name = "submitted_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date submittedAt;

    @ManyToOne
    @JsonSerialize(using= User.UserShortSerialiser.class)
    private User submitter;

    @ManyToOne
    @JsonSerialize(using= Project.ProjectShortSerialiser.class)
    private Project project;

    // labels
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name ="submission_label",
            joinColumns = @JoinColumn(name ="submission_id"),
            inverseJoinColumns = @JoinColumn(name ="label_id"))
    private Set<Label> labels = new HashSet<>();

    // assessments links
    @JsonIgnore
    @OneToMany(mappedBy = "id.submission")
    private Set<AssessmentLink> assessmentLinks;

    // attachments
    @OneToMany(mappedBy = "submission")
    @JsonManagedReference(value="submission-attachments")
    @JsonSerialize(contentUsing= SubmissionAttachment.AttachmentSerialiser.class)
    private Set<SubmissionAttachment> attachments;

    // comments
    @OneToMany(mappedBy = "submission")
    @JsonManagedReference(value="submission-comments")
    @JsonSerialize(contentUsing= SubmissionComment.CommentSerialiser.class)
    private Set<SubmissionComment> comments;

    // grader
    @ManyToOne
    @JsonSerialize(using= User.UserShortSerialiser.class)
    private User grader;

    // members
    @Transient
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonSerialize(contentUsing= User.UserShortSerialiser.class)
    private Set<User> members;

    // assessments
    @Transient
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Assessment> assessments;

    @Transient
    private boolean containsCurrentAssessment;

    public Submission() {
    }

    public Submission(Long id, Long groupId, String name, Project project, Set<Label> labels,
                      Set<AssessmentLink> assessmentLinks, Set<SubmissionAttachment> attachments,
                      Set<SubmissionComment> comments, User grader, User submitter) {
        this.id = id;
        this.groupId = groupId;
        this.name = name;
        this.project = project;
        this.labels = labels;
        this.assessmentLinks = assessmentLinks;
        this.attachments = attachments;
        this.comments = comments;
        this.grader = grader;
        this.submitter = submitter;
    }

    public Submission(User submitter, Long groupId, Project project, String name, Date date) {
        this.groupId = groupId;
        this.name = name;
        this.project = project;
        this.submitter = submitter;
        this.submittedAt = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(Date submittedAt) {
        this.submittedAt = submittedAt;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Set<Label> getLabels() {
        return labels;
    }

    public void setLabels(Set<Label> labels) {
        this.labels = labels;
    }

    public Set<AssessmentLink> getAssessmentLinks() {
        return assessmentLinks;
    }

    public void setAssessmentLinks(Set<AssessmentLink> assessmentLinks) {
        this.assessmentLinks = assessmentLinks;
    }

    public Set<SubmissionAttachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(Set<SubmissionAttachment> attachments) {
        this.attachments = attachments;
    }

    public Set<SubmissionComment> getComments() {
        return comments;
    }

    public void setComments(Set<SubmissionComment> comments) {
        this.comments = comments;
    }

    public User getGrader() {
        return grader;
    }

    public void setGrader(User grader) {
        this.grader = grader;
    }

    public User getSubmitter() {
        return submitter;
    }

    public void setSubmitter(User submitter) {
        this.submitter = submitter;
    }

    public Set<User> getMembers() {
        return members;
    }

    public void setMembers(Set<User> members) {
        this.members = members;
    }

    public List<Assessment> getAssessments() {
        return assessments;
    }

    public void setAssessments(List<Assessment> assessments) {
        this.assessments = assessments;
    }

    public boolean isContainsCurrentAssessment() {
        return containsCurrentAssessment;
    }

    public void setContainsCurrentAssessment(boolean containsCurrentAssessment) {
        this.containsCurrentAssessment = containsCurrentAssessment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Submission that = (Submission) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public static class SubmissionShortSerializer extends JsonSerializer<Submission> {
        @Override
        public void serialize(Submission submission, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {

            jsonGenerator.writeStartObject();
            jsonGenerator.writeNumberField("id", submission.getId());
            jsonGenerator.writeStringField("name", submission.getName());
//            jsonGenerator.writeStringField("submittedAt", submission.getSubmittedAt().toString());

//            if (submission.getGrader() != null) {
//                jsonGenerator.writeObjectField("grader", submission.getGrader());
//            }

            jsonGenerator.writeEndObject();
        }
    }
}
