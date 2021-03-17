package com.group13.tcsprojectgrading.models.grading;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SubmissionAssessment {
    private Map<String, CriterionGrade> grades;

    @Id
    private SubmissionAssessmentKey id;

    public static class SubmissionAssessmentKey implements Serializable {
        private String projectId;
        private String userId;

        public SubmissionAssessmentKey(String projectId, String userId) {
            this.projectId = projectId;
            this.userId = userId;
        }

        public SubmissionAssessmentKey() { }

        public String getProjectId() {
            return projectId;
        }

        public void setProjectId(String projectId) {
            this.projectId = projectId;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String submissionId) {
            this.userId = submissionId;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }

            if (!(obj instanceof SubmissionAssessmentKey)) {
                return false;
            }

            SubmissionAssessmentKey id = (SubmissionAssessmentKey) obj;

//            System.out.println(id.projectId);
//            System.out.println(id.userId);
//
//            System.out.println(this.projectId);
//            System.out.println(this.userId);

            return id.projectId.equals(this.projectId) && id.userId.equals(this.userId);
        }
    }

    public SubmissionAssessment(SubmissionAssessmentKey id, Map<String, CriterionGrade> grades) {
        this.grades = grades;
        this.id = id;
    }

    public SubmissionAssessment(SubmissionAssessmentKey id) {
        this.grades = new HashMap<>();
        this.id = id;
    }

    public SubmissionAssessment() {

    }

    public SubmissionAssessmentKey getId() {
        return id;
    }

    public void setId(SubmissionAssessmentKey id) {
        this.id = id;
    }

    public Map<String, CriterionGrade> getGrades() {
        return grades;
    }

    public void setGrades(Map<String, CriterionGrade> gradeDetails) {
        this.grades = gradeDetails;
    }
}
