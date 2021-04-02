package com.group13.tcsprojectgrading.models.grading;


import com.group13.tcsprojectgrading.models.user.User;
import com.group13.tcsprojectgrading.models.submissions.Submission;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
public class AssessmentLink {
    @Embeddable
    public static class Pk implements Serializable {
        @ManyToOne
        @JoinColumn(name="userId")
        private User user;

        @ManyToOne
        @JoinColumn(name="submissionId")
        private Submission submission;

        @ManyToOne
        @JoinColumn(name="assessmentId")
        private Assessment assessment;

        public Pk() {
        }

        public Pk(User user, Submission submission, Assessment assessment) {
            this.user = user;
            this.submission = submission;
            this.assessment = assessment;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public Submission getSubmission() {
            return submission;
        }

        public void setSubmission(Submission submission) {
            this.submission = submission;
        }

        public Assessment getAssessment() {
            return assessment;
        }

        public void setAssessment(Assessment assessment) {
            this.assessment = assessment;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Pk other = (Pk) o;
            return Objects.equals(user.getId(), other.getUser().getId())
                    && Objects.equals(submission.getId(), other.getSubmission().getId()) &&
                    Objects.equals(assessment.getId(), other.getAssessment().getId());
        }
    }

    @EmbeddedId
    private AssessmentLink.Pk id;

    private boolean current = false;

    public AssessmentLink() {
    }

    public AssessmentLink(User user, Submission submission, Assessment assessment, boolean current) {
        this.id = new Pk(user, submission, assessment);
        this.current = current;
    }

    public Pk getId() {
        return id;
    }

    public void setId(Pk id) {
        this.id = id;
    }

    public boolean isCurrent() {
        return current;
    }

    public void setCurrent(boolean active) {
        this.current = active;
    }

//    /*
//    Serialises
//    */
//    public static class MembersSerialiser extends JsonSerializer<AssessmentLink> {
//        @Override
//        public void serialize(AssessmentLink link, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
//            jsonGenerator.writeStartObject();
//
////            jsonGenerator.writeNumberField("id", course.getId());
////            jsonGenerator.writeStringField("name", course.getName());
//            jsonGenerator.writeEndObject();
//        }
//    }
}
