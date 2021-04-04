package com.group13.tcsprojectgrading.models.feedback;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.group13.tcsprojectgrading.models.course.CourseParticipation;
import com.group13.tcsprojectgrading.models.grading.AssessmentLink;
import com.group13.tcsprojectgrading.models.project.Project;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
public class FeedbackLog {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "send_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date sendAt;

    @ManyToOne
    private AssessmentLink link;

    @ManyToOne
    private FeedbackTemplate template;

    public FeedbackLog(Date sendAt, AssessmentLink link, FeedbackTemplate template) {
        this.sendAt = sendAt;
        this.link = link;
        this.template = template;
    }

    public FeedbackLog() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getSendAt() {
        return sendAt;
    }

    public void setSendAt(Date sendAt) {
        this.sendAt = sendAt;
    }

    public AssessmentLink getLink() {
        return link;
    }

    public void setLink(AssessmentLink link) {
        this.link = link;
    }

    public FeedbackTemplate getTemplate() {
        return template;
    }

    public void setTemplate(FeedbackTemplate template) {
        this.template = template;
    }
}
