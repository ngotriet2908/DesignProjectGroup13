package com.group13.tcsprojectgrading.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.services.gmail.Gmail;
import com.group13.tcsprojectgrading.controllers.PdfRubricUtils;
import com.group13.tcsprojectgrading.controllers.PdfUtils;
import com.group13.tcsprojectgrading.models.*;
import com.group13.tcsprojectgrading.models.graders.Grader;
import com.group13.tcsprojectgrading.models.grading.Assessment;
import com.group13.tcsprojectgrading.models.grading.AssessmentLinker;
import com.group13.tcsprojectgrading.models.permissions.Privilege;
import com.group13.tcsprojectgrading.models.permissions.Role;
import com.group13.tcsprojectgrading.models.permissions.RoleEnum;
import com.group13.tcsprojectgrading.models.rubric.Rubric;
import com.group13.tcsprojectgrading.models.rubric.RubricHistory;
import com.group13.tcsprojectgrading.models.rubric.RubricUpdate;
import com.group13.tcsprojectgrading.models.settings.Settings;
import com.group13.tcsprojectgrading.models.submissions.Flag;
import com.group13.tcsprojectgrading.models.submissions.Submission;
import com.group13.tcsprojectgrading.models.submissions.SubmissionAttachment;
import com.group13.tcsprojectgrading.models.submissions.SubmissionComment;
import com.group13.tcsprojectgrading.repositories.ProjectRepository;
import com.group13.tcsprojectgrading.services.graders.GraderService;
import com.group13.tcsprojectgrading.services.grading.AssessmentLinkerService;
import com.group13.tcsprojectgrading.services.grading.AssessmentService;
import com.group13.tcsprojectgrading.services.permissions.ProjectRoleService;
import com.group13.tcsprojectgrading.services.permissions.RoleService;
import com.group13.tcsprojectgrading.services.rubric.RubricService;
import com.group13.tcsprojectgrading.services.settings.SettingsService;
import com.group13.tcsprojectgrading.services.submissions.FlagService;
import com.group13.tcsprojectgrading.services.submissions.SubmissionDetailsService;
import com.group13.tcsprojectgrading.services.submissions.SubmissionService;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.mail.MessagingException;
import javax.persistence.EntityExistsException;
import javax.transaction.Transactional;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.*;

import static com.group13.tcsprojectgrading.controllers.EmailUtils.createEmailWithAttachment;
import static com.group13.tcsprojectgrading.controllers.EmailUtils.sendMessage;
import static com.group13.tcsprojectgrading.models.submissions.Submission.createFlagsArrayNode;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;

    private final ProjectRoleService projectRoleService;
    private final RoleService roleService;
    private final FlagService flagService;
    private final ActivityService activityService;
    private final RubricService rubricService;
    private final GraderService graderService;
    private final ParticipantService participantService;
    private final AssessmentLinkerService assessmentLinkerService;
    private final SubmissionService submissionService;
    private final SubmissionDetailsService submissionDetailsService;
    private final AssessmentService assessmentService;
    private final SettingsService settingsService;

    public ProjectService(ProjectRepository projectRepository, ProjectRoleService projectRoleService,
                          RoleService roleService, FlagService flagService, ActivityService activityService,
                          RubricService rubricService, GraderService graderService, ParticipantService participantService,
                          AssessmentLinkerService assessmentLinkerService, SubmissionService submissionService,
                          SubmissionDetailsService submissionDetailsService, AssessmentService assessmentService,
                          SettingsService settingsService) {
        this.projectRepository = projectRepository;
        this.projectRoleService = projectRoleService;
        this.roleService = roleService;
        this.flagService = flagService;
        this.activityService = activityService;
        this.rubricService = rubricService;
        this.graderService = graderService;
        this.participantService = participantService;
        this.assessmentLinkerService = assessmentLinkerService;
        this.submissionService = submissionService;
        this.submissionDetailsService = submissionDetailsService;
        this.assessmentService = assessmentService;
        this.settingsService = settingsService;
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public List<Project> getProjectsByCourseId(String courseId) {
        return projectRepository.findProjectsByCourseId(courseId);
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public void deleteProject(Project project) {
        List<Flag> flags = flagService.findFlagsWithProject(project);
        for (Flag flag : flags) {
            flagService.deleteFlag(flag);
        }
        projectRepository.delete(project);
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public Project getProjectById(String courseId, String projectId) {
        return projectRepository.findById(new ProjectId(courseId, projectId)).orElse(null);
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public void addNewProject(Project project) {
        if (projectRepository.existsById(project.getProjectCompositeKey())) {
            throw new EntityExistsException("Exists");
        }

        Project project1 = projectRepository.save(project);
        flagService.saveNewFlag(new Flag(
                "Requires attention",
                "This project requires attention", "red", project1
        ));
    }

    @Transactional
    public Object[] getVolatileProjectsId(String courseId) {
        List<String> projectsId = new ArrayList<>();
        List<Project> projects = projectRepository.findProjectsByCourseId(courseId);
        Map<String, Project> projectMap = new HashMap<>();
        for (Project project : projects) {
            if (project != null) {
                project.setVolatile(activityService.getActivitiesByProject(project).size() > 0);
                if (project.isVolatile()) {
                    projectsId.add(project.getProjectId());
                }
                projectMap.put(project.getProjectId(), project);
            }
        }
        Object[] objects = new Object[2];
        objects[0] = projectsId;
        objects[1] = projectMap;

        return objects;
    }

    @Transactional(rollbackOn = Exception.class)
    public void processActiveProjects(List<String> postActiveProjectIds,
                                      Map<String, Project> canvasProjects,
                                      String courseId) throws Exception {
        List<Project> activeProjects = projectRepository.findProjectsByCourseId(courseId);
        for (Project project : activeProjects) {
            if (!postActiveProjectIds.contains(project.getProjectId())) {
//                Random random = new Random();
//                if (random.nextInt(100) < 30) {
//                    throw new Exception("weird exception");
//                }
                rubricService.deleteRubric(project.getProjectId());
                deleteProject(project);
            }
        }
        for (String activeProjectId : postActiveProjectIds) {
            boolean existed = false;
            for (Project project : activeProjects) {
                if (project.getProjectId().equals(activeProjectId)) {
                    existed = true;
                    break;
                }
            }

            if (existed) continue;

            addNewProject(canvasProjects.get(activeProjectId));
            rubricService.addNewRubric(new Rubric(activeProjectId));
        }
    }

    @Transactional
    public List<Grader> getProjectsGrader(String courseId, String projectId) throws ResponseStatusException {
        Project project = projectRepository.findById(new ProjectId(courseId, projectId)).orElse(null);
        if (project == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "project not found"
            );
        }
        return graderService.getGraderFromProject(project);
    }

    @Transactional
    public ObjectNode getProjectParticipants(String courseId, String projectId) throws ResponseStatusException {
        Project project = projectRepository.findById(new ProjectId(courseId, projectId)).orElse(null);
        if (project == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "project not found"
            );
        }

        List<Participant> participants = participantService.findParticipantsWithProject(project);
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode participantsNode = objectMapper.createArrayNode();
        for (Participant participant : participants) {
            List<AssessmentLinker> assessmentLinkers = assessmentLinkerService.findAssessmentLinkersForParticipant(participant);
            participantsNode.add(participant.convertToJson(assessmentLinkers));
        }

        ObjectNode resultNode = objectMapper.createObjectNode();
        resultNode.set("project", project.convertToJson());
        resultNode.set("participants", participantsNode);

        return resultNode;
    }

    @Transactional
    public ObjectNode getProject(String courseId, String projectId, RoleEnum roleEnum, JsonNode userJson, String userId) throws ResponseStatusException, JsonProcessingException, ParseException {
        Project project = projectRepository.findById(new ProjectId(courseId, projectId)).orElse(null);
        if (project == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "entity not found"
            );
        }
        if (projectRoleService.findByProject(project).size() <= 0) {
            addProjectRoles(project);
        }

        if (roleEnum == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Enrolment not found"
            );
        }

        Grader grader;
        if (roleEnum.equals(RoleEnum.TEACHER)) {
            grader = graderService.addNewGrader(new Grader(
                    project,
                    userJson.get("id").asText(),
                    userJson.get("name").asText(),
                    projectRoleService.findByProjectAndRole(project, roleService.findRoleByName(roleEnum.toString()))
            ));
        } else {
            grader = graderService.getGraderFromGraderId(userJson.get("id").asText(), project);
        }

        ObjectMapper objectMapper = new ObjectMapper();

        if (grader == null && !roleEnum.equals(RoleEnum.STUDENT)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Grader not found"
            );
        } else if (roleEnum.equals(RoleEnum.STUDENT)) {

            ObjectNode graderNode = objectMapper.createObjectNode();
            ArrayNode privilegesNode = objectMapper.createArrayNode();
            List<Privilege> privileges = projectRoleService.findPrivilegesByProjectAndRoleEnum(project, RoleEnum.STUDENT);
            for (Privilege privilege : privileges) {
                ObjectNode privilegeNode = objectMapper.createObjectNode();
                privilegeNode.put("name", privilege.getName());
                privilegesNode.add(privilegeNode);
            }
            graderNode.set("privileges", privilegesNode);

            ObjectNode resultJson = objectMapper.createObjectNode();
            resultJson.set("project", project.convertToJson());
            resultJson.set("grader", graderNode);

            return resultJson;
        }

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        SimpleDateFormat format = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss'Z'");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        Timestamp createdAt = new Timestamp(format.parse(project.getCreateAt()).getTime());

        Activity activity = new Activity(
                project,
                userId,
                timestamp,
                project.getName(),
                createdAt
        );

        activityService.addOrUpdateActivity(activity);

        JsonNode graderNode = grader.getGraderJson();

        Rubric rubric = rubricService.getRubricById(projectId);
        JsonNode rubricJson;
        String rubricString = objectMapper.writeValueAsString(rubric);
        rubricJson = objectMapper.readTree(rubricString);


        ObjectNode resultJson = objectMapper.createObjectNode();
        resultJson.set("project", project.convertToJson());
        resultJson.set("rubric", rubricJson);
        resultJson.set("grader", graderNode);

        return resultJson;
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public void addProjectRoles(Project project) {
        Project project1 = projectRepository.findById(project.getProjectCompositeKey()).orElse(null);
        if (project1 == null) return;
        for (Role role : roleService.findAllRoles()) {
            projectRoleService.addNewRoleToProject(project1, role);
        }
    }

    @Transactional
    public void syncCanvas(String courseId, String projectId, ArrayNode studentArray, ArrayNode submissionArray) throws ResponseStatusException {
        Project project = getProjectById(courseId, projectId);
        if (project == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "project not found"
            );
        }
        for (Iterator<JsonNode> it = studentArray.elements(); it.hasNext(); ) {
            JsonNode jsonNode = it.next();

            // create new participant
            participantService.addNewParticipant(new Participant(
                            jsonNode.get("id").asText(),
                            project,
                            jsonNode.get("name").asText(),
                            jsonNode.get("email").asText(),
                            jsonNode.get("login_id").asText()
                    )
            );
        }

        Map<String, List<Participant>> groupToParticipant = new HashMap<>();
        Map<String, Submission> groupToSubmissionMap = new HashMap<>();
        Map<String, List<SubmissionComment>> groupToComments = new HashMap<>();
        Map<String, List<SubmissionAttachment>> groupToAttachments = new HashMap<>();

        for (Iterator<JsonNode> it = submissionArray.elements(); it.hasNext(); ) {
            JsonNode jsonNode = it.next();

            if (jsonNode.get("workflow_state").asText().equals("unsubmitted")) continue;
            Participant participant = participantService.findParticipantWithId(jsonNode.get("user_id").asText(), project);
            if (participant == null) continue;

            List<SubmissionComment> submissionComments = new ArrayList<>();
            for (Iterator<JsonNode> iter = jsonNode.get("submission_comments").elements(); iter.hasNext(); ) {
                JsonNode node = iter.next();
                submissionComments.add(new SubmissionComment(node.toString()));
            }
            List<SubmissionAttachment> submissionAttachments = new ArrayList<>();
            for (Iterator<JsonNode> iter = jsonNode.get("attachments").elements(); iter.hasNext(); ) {
                JsonNode node = iter.next();
                submissionAttachments.add(new SubmissionAttachment(node.toString()));
            }

            if (jsonNode.get("group").get("id") == null || jsonNode.get("group").get("id").asText().equals("null")) {
                TemporalAccessor accessor = DateTimeFormatter.ISO_INSTANT.parse(jsonNode.get("submitted_at").asText());
                Instant instant = Instant.from(accessor);
                LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.of(ZoneId.SHORT_IDS.get("ECT")));
                String date = localDateTime.format(DateTimeFormatter.ofPattern("E MMMM d  uuuu HH:mm:ss"));

                Submission submission = submissionService.addNewSubmission(
                        project,
                        participant.getName(),
                        Submission.NULL,
                        jsonNode.get("submitted_at").asText(),
                        participant.getName() + " on " + date);

                for (SubmissionComment comment : submissionComments) {
                    comment.setSubmission(submission);
                    submissionDetailsService.saveComment(comment);
                }
                for (SubmissionAttachment attachment : submissionAttachments) {
                    attachment.setSubmission(submission);
                    submissionDetailsService.saveAttachment(attachment);
                }

                if (submission == null) continue;

                UUID assessmentId = UUID.randomUUID();
                AssessmentLinker assessmentLinker = assessmentLinkerService.addNewAssessment(
                        new AssessmentLinker(
                                submission,
                                participant,
                                assessmentId
                        )
                );
                assessmentService.saveAssessment(assessmentLinker);
            } else {
                if (!groupToSubmissionMap.containsKey(jsonNode.get("group").get("id").asText())) {
                    TemporalAccessor accessor = DateTimeFormatter.ISO_INSTANT.parse(jsonNode.get("submitted_at").asText());
                    Instant instant = Instant.from(accessor);
                    LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.of(ZoneId.SHORT_IDS.get("ECT")));
                    String date = localDateTime.format(DateTimeFormatter.ofPattern("E MMMM d  uuuu HH:mm:ss"));

                    Submission submission = new Submission(
                            jsonNode.get("submitted_at").asText(),
                            Submission.NULL,
                            jsonNode.get("group").get("id").asText(),
                            project,
                            jsonNode.get("group").get("name").asText() + " on " + date
                    );
                    groupToSubmissionMap.put(jsonNode.get("group").get("id").asText(), submission);
                    List<Participant> participants = new ArrayList<>();
                    participants.add(participant);
                    groupToParticipant.put(jsonNode.get("group").get("id").asText(), participants);
                    groupToComments.put(jsonNode.get("group").get("id").asText(), submissionComments);
                    groupToAttachments.put(jsonNode.get("group").get("id").asText(), submissionAttachments);
                } else {
                    groupToParticipant.get(jsonNode.get("group").get("id").asText()).add(participant);
                }
            }
        }

        for (Map.Entry<String, Submission> entry : groupToSubmissionMap.entrySet()) {
            Submission submission = submissionService.addNewSubmission(
                    entry.getValue().getProject(),
                    entry.getValue().getUserId(),
                    entry.getValue().getGroupId(),
                    entry.getValue().getDate(),
                    entry.getValue().getName()
            );

            if (submission == null) continue;

            for (SubmissionComment comment : groupToComments.get(entry.getKey())) {
                comment.setSubmission(submission);
                submissionDetailsService.saveComment(comment);
            }
            for (SubmissionAttachment attachment : groupToAttachments.get(entry.getKey())) {
                attachment.setSubmission(submission);
                submissionDetailsService.saveAttachment(attachment);
            }

            UUID assessmentId = UUID.randomUUID();
            System.out.println("size: " + groupToParticipant.get(entry.getValue().getGroupId()).size());
            for (Participant participant : groupToParticipant.get(entry.getValue().getGroupId())) {
                AssessmentLinker assessmentLinker = assessmentLinkerService.addNewAssessment(new AssessmentLinker(
                        submission,
                        participant,
                        assessmentId
                ));

                assessmentService.saveAssessment(assessmentLinker);
            }
        }
    }

    @Transactional
    public JsonNode deleteFlagPermanently(String courseId, String projectId, String flagId, String userId) {

        ObjectMapper objectMapper = new ObjectMapper();
        Project project = getProjectById(courseId, projectId);
        if (project == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "project not found"
            );
        }

        Grader grader = graderService.getGraderFromGraderId(userId, project);
        if (grader == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "task not found"
            );
        }
        //TODO change response to errors
        Flag flag = flagService.findFlagWithId(UUID.fromString(flagId));
        ObjectNode result = objectMapper.createObjectNode();
        if (flag != null) {
            List<Submission> submissions = submissionService.findSubmissionsByFlags(flag);
            if (submissions.size() > 0) {
                result.put("error", "Flag is current used by some submission");
            } else {
                flagService.deleteFlag(flag);
                result.set("data", createFlagsArrayNode(flagService.findFlagsWithProject(project)));
            }
            return result;

        }
        result.put("error", "some weird error");
        return result;
    }

    @Transactional(rollbackOn = Exception.class)
    public String updateRubric(String courseId, String projectId, JsonNode patch) throws JsonProcessingException {
        Project project = getProjectById(courseId, projectId);
        if (project == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "project not found"
            );
        }

        System.out.println("Updating the rubric of project " + projectId + ".");
        Rubric rubric = this.rubricService.getRubricById(projectId);

        // apply update and mark affected submissions
        Rubric rubricPatched = this.rubricService.applyUpdate(patch, rubric);
        this.rubricService.saveRubric(rubricPatched);

        // store update
        RubricHistory history = this.rubricService.getHistory(projectId);
        if (history == null) {
            history = new RubricHistory(projectId);
        }

        history.getHistory().add(new RubricUpdate(patch));
        this.rubricService.storeHistory(history);

        System.out.println("Updating the rubric of project " + projectId + " finished successfully.");
        return "Rubric updated";
    }

    @Transactional
    public String getRubric(String courseId, String projectId) throws JsonProcessingException {
        Project project = getProjectById(courseId, projectId);
        if (project == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "project not found"
            );
        }

        Rubric rubric = rubricService.getRubricById(projectId);

        if (rubric == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "rubric not found"
            );
        } else {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(rubric);
        }
    }

    @Transactional
    public ResponseEntity<byte[]> sendFeedbackPdf(String courseId, String projectId, ObjectNode feedback) throws IOException {
        Project project = getProjectById(courseId, projectId);
        if (project == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "entity not found"
            );
        }

        String id = feedback.get("id").asText();
        boolean isGroup = feedback.get("isGroup").asBoolean();
        String body = feedback.get("body").asText();
        String subject = feedback.get("subject").asText();

        String fileName = id + "_" + subject + ".pdf";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData(fileName, fileName);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
//        response.setContentType("blob");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        PdfWriter pdfWriter = new PdfWriter(byteArrayOutputStream);
        PdfDocument pdfDocument = new PdfDocument(pdfWriter);
        Document document = new Document(pdfDocument, PageSize.A4);

        document.getPdfDocument();

        Participant participant = participantService.findParticipantWithId(id, project);
        Submission submission = submissionService.findSubmissionById(body);

        Assessment submissionAssessment = assessmentService.getAssessmentBySubmissionAndParticipant(submission, participant);
        PdfUtils pdfUtils = new PdfUtils(document, rubricService.getRubricById(projectId), submissionAssessment
        );
        pdfUtils.generatePdfOfFeedback();
        document.close();
        return new ResponseEntity<byte[]>(byteArrayOutputStream.toByteArray(), headers, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<byte[]> downloadRubric(String courseId, String projectId) throws IOException {
        Project project = getProjectById(courseId, projectId);
        if (project == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "entity not found"
            );
        }

        String fileName = project.getName() + " rubric.pdf";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData(fileName, fileName);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
//        response.setContentType("blob");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        PdfWriter pdfWriter = new PdfWriter(byteArrayOutputStream);
        PdfDocument pdfDocument = new PdfDocument(pdfWriter);
        Document document = new Document(pdfDocument, PageSize.A4);

        document.getPdfDocument();

        PdfRubricUtils rubricUtils = new PdfRubricUtils(document, rubricService.getRubricById(projectId));
        rubricUtils.generateRubrics();
//        System.out.println(Arrays.toString(byteArrayOutputStream.toByteArray()));

        return new ResponseEntity<byte[]>(byteArrayOutputStream.toByteArray(), headers, HttpStatus.OK);
    }

    @Transactional
    public String sendFeedbackEmail(String courseId, String projectId,
                                    ObjectNode feedback, String userId,
                                    GoogleAuthorizationCodeFlow flow, String email
                                    ) throws IOException, MessagingException {
        Project project = getProjectById(courseId, projectId);
        if (project == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "entity not found"
            );
        }

        String id = feedback.get("id").asText();
        boolean isGroup = feedback.get("isGroup").asBoolean();
        String body = feedback.get("body").asText();
        String subject = feedback.get("subject").asText();

        System.out.println("getting credential for " + userId);
        Credential credential = flow.loadCredential(userId);
        if (credential == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "token not found"
            );
        }

        System.out.println("Access token of " + userId + ": " + credential.getAccessToken());

        Gmail service = new Gmail.Builder(flow.getTransport(), flow.getJsonFactory(), credential)
                .setApplicationName("Pro Grading")
                .build();

        String FILE_NAME = "src/main/resources/fileToCreate.pdf";
        File targetFile = new File(FILE_NAME);
        targetFile.delete();
        Path newFilePath = Paths.get(FILE_NAME);
        Files.createFile(newFilePath);

        OutputStream out = new FileOutputStream(FILE_NAME);

        PdfWriter pdfWriter = new PdfWriter(out);
        PdfDocument pdfDocument = new PdfDocument(pdfWriter);
        Document document = new Document(pdfDocument, PageSize.A4);

        document.getPdfDocument();

        Participant participant = participantService.findParticipantWithId(id, project);
        Submission submission = submissionService.findSubmissionById(body);

        Assessment submissionAssessment = assessmentService.getAssessmentBySubmissionAndParticipant(submission, participant);
        PdfUtils pdfUtils = new PdfUtils(document, rubricService.getRubricById(projectId), submissionAssessment
        );
        pdfUtils.generatePdfOfFeedback();
        document.close();

        if (email != null) {
            sendMessage(service, "me", createEmailWithAttachment(
                    email,
                    "me",
                    subject,
                    body,
                    new File(FILE_NAME)
            ));
            return "ok";
        }
//        System.out.println(Arrays.toString(byteArrayOutputStream.toByteArray()));

        return "something is wrong";
    }

    @Transactional
    public Project getProject(String courseId, String projectId) {
        return projectRepository.findById(new ProjectId(courseId, projectId)).orElse(null);
    }
}
