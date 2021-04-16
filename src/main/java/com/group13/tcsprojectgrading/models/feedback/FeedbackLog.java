package com.group13.tcsprojectgrading.models.feedback;

import com.group13.tcsprojectgrading.models.grading.AssessmentLink;

import javax.persistence.*;
import java.util.Date;

@Entity
public class FeedbackLog {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "send_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date sentAt;

    @ManyToOne
    private AssessmentLink link;

    @ManyToOne
    private FeedbackTemplate template;

    public FeedbackLog(Date sentAt, AssessmentLink link, FeedbackTemplate template) {
        this.sentAt = sentAt;
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

    public Date getSentAt() {
        return sentAt;
    }

    public void setSentAt(Date sendAt) {
        this.sentAt = sendAt;
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
