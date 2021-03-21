package com.group13.tcsprojectgrading.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.api.client.json.Json;
import org.hibernate.annotations.GeneratorType;

import javax.mail.Part;
import javax.persistence.*;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.*;

@Entity
public class Submission {

    public static final String NULL = "null";

    @Id
    @GeneratedValue
    private UUID id;

    private String date;

    private String userId;

    private String groupId;

    @ManyToOne
    private Project project;

    @OneToMany(mappedBy = "submission")
    private List<AssessmentLinker> assessmentLinkers = new ArrayList<>();

    private String name;

////    @Lob
//    @Column(length=8192)
//    private String comments;
//
////    @Lob
//    @Column(length=8192)
//    private String attachments;

    @OneToMany(mappedBy = "submission")
    private List<SubmissionAttachment> attachments;

    @OneToMany(mappedBy = "submission")
    private List<SubmissionComment> comments;

    @ManyToOne
    private Grader grader;

    @ManyToMany
    private Collection<Flag> flags = new ArrayList<>();

    public Submission(String date, String userId, String groupId, Project project, String name) {
        this.date = date;
        this.userId = userId;
        this.groupId = groupId;
        this.project = project;
        this.name = name;
    }

    public Submission() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public List<AssessmentLinker> getAssessmentLinkers() {
        return assessmentLinkers;
    }

    public void setAssessmentLinkers(List<AssessmentLinker> assessmentLinkers) {
        this.assessmentLinkers = assessmentLinkers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Grader getGrader() {
        return grader;
    }

    public void setGrader(Grader grader) {
        this.grader = grader;
    }

    public Collection<Flag> getFlags() {
        return flags;
    }

    public void setFlags(Collection<Flag> flags) {
        this.flags = flags;
    }

    public List<SubmissionAttachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<SubmissionAttachment> attachments) {
        this.attachments = attachments;
    }

    public List<SubmissionComment> getComments() {
        return comments;
    }

    public void setComments(List<SubmissionComment> comments) {
        this.comments = comments;
    }

    public JsonNode convertToJson(List<AssessmentLinker> assessmentLinkers) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();

        node.put("id", id.toString());
        node.put("isGroup", (!groupId.equals(NULL)));
        node.put("name", name);


//        // TODO delete this shit
//        Random random = new Random();
//        String stupidName = (random.nextInt(3) == 2)? "Ömer Şakar " + submission.getName(): submission.getName();
//        node.put("name", stupidName);
//        // TODO delete this shit

        int progress = 0;
//        if (rubric != null) {
//            SubmissionAssessment assessment = gradingService.getAssessmentByProjectIdAndUserId(projectId, submission.getId());
//            if (assessment != null) {
//                progress = (int) Math.round(submissionProgress(assessment, rubric));
//            }
//        }
        node.put("progress", progress);
//        progresses.add(progress);
        node.put("submittedAt", date);

        List<Flag> flags = (List<Flag>) getFlags();
        ArrayNode submissionFlags = createFlagsArrayNode(flags);
        node.set("flags", submissionFlags);

        ArrayNode participants = mapper.createArrayNode();

        for(AssessmentLinker assessmentLinker: assessmentLinkers) {
            participants.add(assessmentLinker.getParticipant().convertToJson());
        }
        node.set("participants", participants);

        Map<UUID, List<Participant>> assessmentsMap = new HashMap<>();
        for(AssessmentLinker assessmentLinker: assessmentLinkers) {
            if (!assessmentsMap.containsKey(assessmentLinker.getAssessmentId())) {
                List<Participant> participants1 = new ArrayList<>();
                participants1.add(assessmentLinker.getParticipant());
                assessmentsMap.put(assessmentLinker.getAssessmentId(), participants1);
            } else {
                assessmentsMap.get(assessmentLinker.getAssessmentId()).add(assessmentLinker.getParticipant());
            }
        }

        ArrayNode assessments = mapper.createArrayNode();
        for(Map.Entry<UUID, List<Participant>> entry: assessmentsMap.entrySet()) {
            ObjectNode assessment = mapper.createObjectNode();
            assessment.put("id", entry.getKey().toString());
            ArrayNode participantsNode = mapper.createArrayNode();
            for(Participant participant: entry.getValue()) {
                participantsNode.add(participant.convertToJson());
            }

            assessment.set("participants", participantsNode);
            assessments.add(assessment);
        }

        node.set("assessments", assessments);

        if (getGrader() != null) {
            JsonNode graderNode = mapper.createObjectNode();
            ((ObjectNode) graderNode).put("id", getGrader().getUserId());
            ((ObjectNode) graderNode).put("name", getGrader().getName());
            node.set("grader", graderNode);
        }
        return node;
    }

    public JsonNode convertToJsonWithDetails(List<AssessmentLinker> assessmentLinkers,
                                             List<SubmissionAttachment> submissionAttachments,
                                             List<SubmissionComment> submissionComments) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();

        node.put("id", id.toString());
        node.put("isGroup", (!groupId.equals(NULL)));
        node.put("name", name);


//        // TODO delete this shit
//        Random random = new Random();
//        String stupidName = (random.nextInt(3) == 2)? "Ömer Şakar " + submission.getName(): submission.getName();
//        node.put("name", stupidName);
//        // TODO delete this shit

        int progress = 0;
//        if (rubric != null) {
//            SubmissionAssessment assessment = gradingService.getAssessmentByProjectIdAndUserId(projectId, submission.getId());
//            if (assessment != null) {
//                progress = (int) Math.round(submissionProgress(assessment, rubric));
//            }
//        }
        node.put("progress", progress);
//        progresses.add(progress);
        node.put("submittedAt", date);

        ArrayNode commentsNode = mapper.createArrayNode();
        ArrayNode attachmentsNode = mapper.createArrayNode();
        for(SubmissionComment comment: submissionComments) {
            commentsNode.add(mapper.readTree(comment.getComment()));
        }
        for(SubmissionAttachment attachment: submissionAttachments) {
            attachmentsNode.add(mapper.readTree(attachment.getAttachment()));
        }

        node.set("submission_comments", commentsNode);
        node.set("attachments", attachmentsNode);

        List<Flag> flags = (List<Flag>) getFlags();
        ArrayNode submissionFlags = createFlagsArrayNode(flags);
        node.set("flags", submissionFlags);

        ArrayNode participants = mapper.createArrayNode();

        for(AssessmentLinker assessmentLinker: assessmentLinkers) {
            participants.add(assessmentLinker.getParticipant().convertToJson());
        }
        node.set("participants", participants);

        Map<UUID, List<Participant>> assessmentsMap = new HashMap<>();
        for(AssessmentLinker assessmentLinker: assessmentLinkers) {
            if (!assessmentsMap.containsKey(assessmentLinker.getAssessmentId())) {
                List<Participant> participants1 = new ArrayList<>();
                participants1.add(assessmentLinker.getParticipant());
                assessmentsMap.put(assessmentLinker.getAssessmentId(), participants1);
            } else {
                assessmentsMap.get(assessmentLinker.getAssessmentId()).add(assessmentLinker.getParticipant());
            }
        }

        ArrayNode assessments = mapper.createArrayNode();
        for(Map.Entry<UUID, List<Participant>> entry: assessmentsMap.entrySet()) {
            ObjectNode assessment = mapper.createObjectNode();
            assessment.put("id", entry.getKey().toString());
            ArrayNode participantsNode = mapper.createArrayNode();
            for(Participant participant: entry.getValue()) {
                participantsNode.add(participant.convertToJson());
            }

            assessment.set("participants", participantsNode);
            assessments.add(assessment);
        }

        node.set("assessments", assessments);

        if (getGrader() != null) {
            JsonNode graderNode = mapper.createObjectNode();
            ((ObjectNode) graderNode).put("id", getGrader().getUserId());
            ((ObjectNode) graderNode).put("name", getGrader().getName());
            node.set("grader", graderNode);
        }
        return node;
    }


    public static ArrayNode createFlagsArrayNode(List<Flag> flags) {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode arrayNode = objectMapper.createArrayNode();
        for(Flag flag2: flags) {
            ObjectNode flagNode = objectMapper.createObjectNode();
            flagNode.put("id", flag2.getId());
            flagNode.put("name", flag2.getName());
            flagNode.put("variant", flag2.getVariant());
            flagNode.put("description", flag2.getDescription());
            //TODO check flag again
//            flagNode.put("changeable", flag2.getGrader().getUserId().equals(userId));
            arrayNode.add(flagNode);
        }
        return arrayNode;
    }



    @Override
    public String toString() {
        return "Submission{" +
                "id='" + id + '\'' +
                ", project=" + project +
                ", name='" + name + '\'' +
                ", grader=" + grader +
                '}';
    }
}
