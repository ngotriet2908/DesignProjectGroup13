package com.group13.tcsprojectgrading.models.submissions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.group13.tcsprojectgrading.models.grading.Assessment;
import com.group13.tcsprojectgrading.models.grading.AssessmentLink;
import com.group13.tcsprojectgrading.models.project.Project;
import com.group13.tcsprojectgrading.models.user.User;

import javax.persistence.*;
import java.util.*;

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
    @ManyToMany
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
    private Set<Assessment> assessments;

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

    public Set<Assessment> getAssessments() {
        return assessments;
    }

    public void setAssessments(Set<Assessment> assessments) {
        this.assessments = assessments;
    }

    //    public JsonNode convertToJson() {
//        ObjectMapper mapper = new ObjectMapper();
//        ObjectNode node = mapper.createObjectNode();
//        node.put("id", id.toString());
//        node.put("isGroup", (!groupId.equals(NULL)));
//        node.put("name", name);
//        return node;
//    }

//    public JsonNode convertToJson(List<AssessmentLinker> assessmentLinkers, List<Issue> issues) {
//        ObjectMapper mapper = new ObjectMapper();
//        ObjectNode node = mapper.createObjectNode();
//
//        node.put("id", id.toString());
//        node.put("isGroup", (!groupId.equals(NULL)));
//        node.put("name", name);
//
//        int progress = 0;
//        node.put("progress", progress);
//        node.put("submittedAt", date);
//
//        List<Flag> flags = (List<Flag>) getFlags();
//        ArrayNode submissionFlags = createFlagsArrayNode(flags);
//        node.set("flags", submissionFlags);
//
//        ArrayNode participants = mapper.createArrayNode();
//
//        for(AssessmentLinker assessmentLinker: assessmentLinkers) {
////            if (assessmentLinker.getId().getUser() != null)
//                participants.add(mapper.valueToTree(assessmentLinker.getId().getUser()));
//        }
//        node.set("participants", participants);
//
//        Map<Long, List<User>> assessmentsMap = new HashMap<>();
//        for(AssessmentLinker assessmentLinker: assessmentLinkers) {
//            if (!assessmentsMap.containsKey(assessmentLinker.getId().getAssessment().getId())) {
//                List<User> participants1 = new ArrayList<>();
////                if (assessmentLinker.get() != null) {
////                    participants1.add(assessmentLinker.getParticipant());
////                }
//                participants1.add(assessmentLinker.getId().getUser());
//                assessmentsMap.put(assessmentLinker.getId().getAssessment().getId(), participants1);
//            } else {
////                if (assessmentLinker.getParticipant() != null) {
////                    assessmentsMap.get(assessmentLinker.getAssessmentId()).add(assessmentLinker.getParticipant());
////                }
//                assessmentsMap.get(assessmentLinker.getId().getAssessment().getId()).add(assessmentLinker.getId().getUser());
//            }
//        }
//
//        ArrayNode assessments = mapper.createArrayNode();
//        for(Map.Entry<Long, List<User>> entry: assessmentsMap.entrySet()) {
//            ObjectNode assessment = mapper.createObjectNode();
//            assessment.put("id", entry.getKey().toString());
//            ArrayNode participantsNode = mapper.createArrayNode();
//            for(User participant: entry.getValue()) {
//                participantsNode.add(mapper.valueToTree(participant));
//            }
//
//            assessment.set("participants", participantsNode);
//            assessments.add(assessment);
//        }
//
//        node.set("assessments", assessments);
//        node.put("issuesCount", issues.size());
//
//        if (getGrader() != null) {
//            ObjectNode graderNode = mapper.createObjectNode();
//            graderNode.put("id", getGrader().getUserId());
//            graderNode.put("name", getGrader().getName());
//            node.set("grader", graderNode);
//        }
//        return node;
//    }
//
//    public JsonNode convertToJsonWithDetails(List<AssessmentLinker> assessmentLinkers,
//                                             List<SubmissionAttachment> submissionAttachments,
//                                             List<SubmissionComment> submissionComments,
//                                             Map<Long, List<Issue>> issuesMap) throws JsonProcessingException {
//        ObjectMapper mapper = new ObjectMapper();
//        ObjectNode node = mapper.createObjectNode();
//
//        node.put("id", id.toString());
//        node.put("isGroup", (!groupId.equals(NULL)));
//        node.put("name", name);
//
//        int progress = 0;
//        node.put("progress", progress);
//        node.put("submittedAt", date);
//
//        if (submissionAttachments != null && submissionComments != null) {
//            ArrayNode commentsNode = mapper.createArrayNode();
//            ArrayNode attachmentsNode = mapper.createArrayNode();
//            for(SubmissionComment comment: submissionComments) {
//                commentsNode.add(mapper.readTree(comment.getComment()));
//            }
//            for(SubmissionAttachment attachment: submissionAttachments) {
//                attachmentsNode.add(mapper.readTree(attachment.getAttachment()));
//            }
//
//            node.set("submission_comments", commentsNode);
//            node.set("attachments", attachmentsNode);
//        }
//
//
//        List<Flag> flags = (List<Flag>) getFlags();
//        ArrayNode submissionFlags = createFlagsArrayNode(flags);
//        node.set("flags", submissionFlags);
//
//        ArrayNode participants = mapper.createArrayNode();
//
//        for(AssessmentLinker assessmentLinker: assessmentLinkers) {
//            if (assessmentLinker != null && assessmentLinker.getId().getUser() != null) {
//                participants.add(mapper.valueToTree(assessmentLinker.getId().getUser()));
//            }
//        }
//        node.set("participants", participants);
//
//        Map<Long, List<User>> assessmentsMap = new HashMap<>();
//        for(AssessmentLinker assessmentLinker: assessmentLinkers) {
//            if (!assessmentsMap.containsKey(assessmentLinker.getId().getAssessment().getId())) {
//                List<User> participants1 = new ArrayList<>();
//                if (assessmentLinker.getId().getUser() != null) {
//                    participants1.add(assessmentLinker.getId().getUser());
//                }
//                assessmentsMap.put(assessmentLinker.getId().getAssessment().getId(), participants1);
//            } else {
//                if (assessmentLinker.getId().getUser() != null) {
//                    assessmentsMap.get(assessmentLinker.getId().getAssessment().getId()).add(assessmentLinker.getId().getUser());
//                }
//            }
//        }
//
////        ArrayNode assessments = mapper.createArrayNode();
////        for(Map.Entry<Long, List<User>> entry: assessmentsMap.entrySet()) {
////            ObjectNode assessment = mapper.createObjectNode();
////            assessment.put("id", entry.getKey().toString());
////            ArrayNode participantsNode = mapper.createArrayNode();
////            for(User participant: entry.getValue()) {
////                ObjectNode participantNode = mapper.valueToTree(participant);
////                participantNode.put(
////                        "isCurrentAssessment",
////                        this.
////                        linker.getAssessmentId().equals(currentAssessmentLinker.getAssessmentId())
////                );
////                participantsNode.add(participantNode);
////            }
////
////            assessment.set("participants", participantsNode);
////            assessment.put("issuesCount", issuesMap.get(entry.getKey()).size());
////            assessments.add(assessment);
////        }
//
////        node.set("assessments", assessments);
//
//        if (getGrader() != null) {
//            ObjectNode graderNode = mapper.createObjectNode();
//            graderNode.put("id", getGrader().getUserId());
//            graderNode.put("name", getGrader().getName());
//            node.set("grader", graderNode);
//        }
//        return node;
//    }
//
//
//    public static ArrayNode createFlagsArrayNode(List<Flag> flags) {
//        ObjectMapper objectMapper = new ObjectMapper();
//        ArrayNode arrayNode = objectMapper.createArrayNode();
//        for(Flag flag2: flags) {
//            ObjectNode flagNode = objectMapper.createObjectNode();
//            flagNode.put("id", flag2.getId().toString());
//            flagNode.put("name", flag2.getName());
//            flagNode.put("variant", flag2.getVariant());
//            flagNode.put("description", flag2.getDescription());
//            arrayNode.add(flagNode);
//        }
//        return arrayNode;
//    }
}
