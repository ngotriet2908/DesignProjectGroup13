package com.group13.tcsprojectgrading.models.grading;

import java.io.Serializable;

public class SubmissionAssessmentKey implements Serializable {
    private String projectId;
    private String submissionId;

    public SubmissionAssessmentKey(String projectId, String submissionId) {
        this.projectId = projectId;
        this.submissionId = submissionId;
    }

    public SubmissionAssessmentKey() { }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(String submissionId) {
        this.submissionId = submissionId;
    }

}
