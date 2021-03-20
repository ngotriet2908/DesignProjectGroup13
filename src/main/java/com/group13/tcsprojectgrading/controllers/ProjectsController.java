package com.group13.tcsprojectgrading.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.group13.tcsprojectgrading.canvas.api.CanvasApi;
import com.group13.tcsprojectgrading.models.*;
import com.group13.tcsprojectgrading.models.rubric.Rubric;
import com.group13.tcsprojectgrading.services.*;
import com.group13.tcsprojectgrading.services.grading.AssessmentService;
import com.group13.tcsprojectgrading.services.rubric.RubricService;
//import com.itextpdf.text.*;
//import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.apache.commons.codec.binary.Base64;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.Principal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import static com.group13.tcsprojectgrading.controllers.Utils.groupPages;

@RestController
@RequestMapping("/api/courses/{courseId}/projects")
public class ProjectsController {
    private final CanvasApi canvasApi;
    private final ActivityService activityService;
    private final RubricService rubricService;
    private final ProjectService projectService;
    private final RoleService roleService;
    private final GraderService graderService;
    private final ProjectRoleService projectRoleService;
    private final FlagService flagService;
    private final GoogleAuthorizationCodeFlow flow;
    private final SubmissionService submissionService;
    private final ParticipantService participantService;
    private final AssessmentLinkerService assessmentLinkerService;
    private final AssessmentService assessmentService;

    @Autowired
    public ProjectsController(CanvasApi canvasApi, ActivityService activityService, RubricService rubricService, ProjectService projectService, RoleService roleService, GraderService graderService, ProjectRoleService projectRoleService, FlagService flagService, GoogleAuthorizationCodeFlow flow, SubmissionService submissionService, ParticipantService participantService, AssessmentLinkerService assessmentLinkerService, AssessmentService assessmentService) {
        this.canvasApi = canvasApi;
        this.activityService = activityService;
        this.rubricService = rubricService;
        this.projectService = projectService;
        this.roleService = roleService;
        this.graderService = graderService;
        this.projectRoleService = projectRoleService;
        this.flagService = flagService;
        this.flow = flow;
        this.submissionService = submissionService;
        this.participantService = participantService;
        this.assessmentLinkerService = assessmentLinkerService;
        this.assessmentService = assessmentService;
    }

    @RequestMapping(value = "/{projectId}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    protected ResponseEntity<JsonNode> getProject(@PathVariable String courseId, @PathVariable String projectId, Principal principal) throws JsonProcessingException, ParseException {
        Project project = projectService.getProjectById(courseId, projectId);
        if (project == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "entity not found"
            );
        }

        String courseResponse = this.canvasApi.getCanvasCoursesApi().getUserCourse(courseId);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode courseJson = objectMapper.readTree(courseResponse);

        projectService.addProjectRoles(project);

        //create teacher's grader object on first enter project page
        String userResponse = this.canvasApi.getCanvasCoursesApi().getCourseUser(courseId, principal.getName());
        ArrayNode enrolmentsNode = groupPages(objectMapper, this.canvasApi.getCanvasUsersApi().getEnrolments(principal.getName()));
        JsonNode userJson = objectMapper.readTree(userResponse);

        RoleEnum roleEnum = null;
        Grader grader;

        for (Iterator<JsonNode> it = enrolmentsNode.elements(); it.hasNext(); ) {
            JsonNode enrolmentNode = it.next();
            if (enrolmentNode.get("course_id").asText().equals(courseId)) {
                roleEnum = RoleEnum.getRoleFromEnrolment(enrolmentNode.get("role").asText());
            }
        }

        if (roleEnum == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Enrolment not found"
            );
        }

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


        if (grader == null && !roleEnum.equals(RoleEnum.STUDENT)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Grader not found"
            );
        } else if (roleEnum.equals(RoleEnum.STUDENT)) {

            ObjectNode graderNode = objectMapper.createObjectNode();
            ArrayNode privilegesNode = objectMapper.createArrayNode();
            List<Privilege> privileges = projectRoleService.findPrivilegesByProjectAndRoleEnum(project, RoleEnum.STUDENT);
            for (Privilege privilege: privileges) {
                ObjectNode privilegeNode = objectMapper.createObjectNode();
                privilegeNode.put("name", privilege.getName());
                privilegesNode.add(privilegeNode);
            }
            graderNode.set("privileges", privilegesNode);

            ObjectNode resultJson = objectMapper.createObjectNode();
            resultJson.set("course", courseJson);
            resultJson.set("project", project.convertToJson());
            resultJson.set("grader", graderNode);

            return new ResponseEntity<>(resultJson, HttpStatus.OK);
        }

        JsonNode graderNode = grader.getGraderJson();

        // including rubric to the response
        Rubric rubric = rubricService.getRubricById(projectId);
        JsonNode rubricJson;
//        if (rubric == null) {
//            rubricJson = objectMapper.readTree("null");
//        } else {
            String rubricString = objectMapper.writeValueAsString(rubric);
            rubricJson = objectMapper.readTree(rubricString);
//        }

        ObjectNode resultJson = objectMapper.createObjectNode();
        resultJson.set("course", courseJson);
        resultJson.set("project", project.convertToJson());
        resultJson.set("rubric", rubricJson);
        resultJson.set("grader", graderNode);

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        SimpleDateFormat format = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss'Z'");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        Timestamp createdAt = new Timestamp(format.parse(project.getCreateAt()).getTime());

        Activity activity = new Activity(
                project,
                principal.getName(),
                timestamp,
                project.getName(),
                createdAt
        );

        activityService.addOrUpdateActivity(activity);

        return new ResponseEntity<>(resultJson, HttpStatus.OK);
    }

    @GetMapping(value = "/{projectId}/syncCanvas")
    protected void syncWithCanvas(@PathVariable String courseId,
                                  @PathVariable String projectId,
                                  Principal principal) throws JsonProcessingException {

        Project project = projectService.getProjectById(courseId, projectId);
        if (project == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "project not found"
            );
        }

        ObjectMapper objectMapper = new ObjectMapper();
        List<Grader> graders = graderService.getGraderFromProject(project);
        List<String> submissionsString = this.canvasApi.getCanvasCoursesApi().getSubmissionsInfo(courseId, Long.parseLong(projectId));
        List<String> studentsString = this.canvasApi.getCanvasCoursesApi().getCourseStudents(courseId);

        ArrayNode studentArray = groupPages(objectMapper, studentsString);
        for (Iterator<JsonNode> it = studentArray.elements(); it.hasNext(); ) {
            JsonNode jsonNode = it.next();

            participantService.addNewParticipant(new Participant(
                    jsonNode.get("id").asText(),
                    project,
                    jsonNode.get("name").asText(),
                    jsonNode.get("email").asText(),
                    jsonNode.get("login_id").asText()
                    )
            );
        }

        ArrayNode submissionArray = groupPages(objectMapper, submissionsString);
        Map<String, List<Participant>> groupToParticipant = new HashMap<>();
        Map<String, Submission> groupToSubmissionMap = new HashMap<>();

        for (Iterator<JsonNode> it = submissionArray.elements(); it.hasNext(); ) {
            JsonNode jsonNode = it.next();

            if (jsonNode.get("workflow_state").asText().equals("unsubmitted")) continue;
            Participant participant = participantService.findParticipantWithId(jsonNode.get("user_id").asText(), project);
            if (participant == null) continue;

            System.out.println(jsonNode.get("submission_comments").toString());
            System.out.println(jsonNode.get("attachments").toString());

            if (jsonNode.get("group").get("id") == null || jsonNode.get("group").get("id").asText().equals("null")) {
                Submission submission = submissionService.addNewSubmission(
                        project,
                        participant.getName(),
                        Submission.NULL,
                        jsonNode.get("submitted_at").asText(),
                        participant.getName() + " on " + jsonNode.get("submitted_at").asText(),
                        jsonNode.get("submission_comments").toString(),
                        jsonNode.get("attachments").toString()
                );

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
                    Submission submission = new Submission(
                            jsonNode.get("submitted_at").asText(),
                            Submission.NULL,
                            jsonNode.get("group").get("id").asText(),
                            project,
                            jsonNode.get("group").get("name").asText() + " on " + jsonNode.get("submitted_at").asText(),
                            jsonNode.get("submission_comments").toString(),
                            jsonNode.get("attachments").toString()
                    );
                    groupToSubmissionMap.put(jsonNode.get("group").get("id").asText(), submission);
                    List<Participant> participants = new ArrayList<>();
                    participants.add(participant);
                    groupToParticipant.put(jsonNode.get("group").get("id").asText(), participants);
                } else {
                    groupToParticipant.get(jsonNode.get("group").get("id").asText()).add(participant);
                }
            }
        }

        for(Map.Entry<String, Submission> entry: groupToSubmissionMap.entrySet()) {

            Submission submission = submissionService.addNewSubmission(
                    entry.getValue().getProject(),
                    entry.getValue().getUserId(),
                    entry.getValue().getGroupId(),
                    entry.getValue().getDate(),
                    entry.getValue().getName(),
                    entry.getValue().getComments(),
                    entry.getValue().getAttachments()
            );

            if (submission == null) continue;

            UUID assessmentId = UUID.randomUUID();
            System.out.println("size: " + groupToParticipant.get(entry.getValue().getGroupId()).size());
            for(Participant participant: groupToParticipant.get(entry.getValue().getGroupId())) {
                AssessmentLinker assessmentLinker = assessmentLinkerService.addNewAssessment(new AssessmentLinker(
                        submission,
                        participant,
                        assessmentId
                ));

                assessmentService.saveAssessment(assessmentLinker);
            }
        }

    }

    @PostMapping(value = "/{projectId}/feedback")
    @ResponseBody
    protected void sendFeedback(@PathVariable String courseId,
                                @PathVariable String projectId,
                                @RequestBody ObjectNode feedback,
                                Principal principal) throws JsonProcessingException, ParseException {
        Project project = projectService.getProjectById(courseId, projectId);
        if (project == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "entity not found"
            );
        }

        String id = feedback.get("id").asText();
        boolean isGroup = feedback.get("isGroup").asBoolean();
        String body = feedback.get("body").asText();
        String subject = feedback.get("subject").asText();

        this.canvasApi.getCanvasUsersApi().sendMessageWithId(
                (!isGroup)? id: null,
                (isGroup)? id: null,
                subject,
                body
        );
    }

//    @PostMapping(value = "/{projectId}/feedbackPdf", produces = "application/pdf")
    @PostMapping(value = "/{projectId}/feedbackPdf")
    @ResponseBody
    protected ResponseEntity<byte[]> sendFeedbackPdf(@PathVariable String courseId,
                                                     @PathVariable String projectId,
                                                     @RequestBody ObjectNode feedback,
                                                     Principal principal) throws IOException, ParseException {
        Project project = projectService.getProjectById(courseId, projectId);
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

//        System.out.println(Arrays.toString(byteArrayOutputStream.toByteArray()));

        return new ResponseEntity<byte[]>(byteArrayOutputStream.toByteArray(), headers, HttpStatus.OK);
    }

    @PostMapping(value = "/{projectId}/feedbackEmail")
    @ResponseBody
    protected String sendFeedbackEmail1(@PathVariable String courseId,
                                       @PathVariable String projectId,
                                       @RequestBody ObjectNode feedback,
                                       Principal principal) throws IOException, ParseException, GeneralSecurityException, MessagingException {
        Project project = projectService.getProjectById(courseId, projectId);
        if (project == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "entity not found"
            );
        }

        String id = feedback.get("id").asText();
        boolean isGroup = feedback.get("isGroup").asBoolean();
        String body = feedback.get("body").asText();
        String subject = feedback.get("subject").asText();

        System.out.println("getting credential for " + principal.getName());
        Credential credential = flow.loadCredential(principal.getName());
        if (credential == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "token not found"
            );
        }

        System.out.println("Access token of " + principal.getName() + ": " + credential.getAccessToken());
//        NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
//        JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

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

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(this.canvasApi.getCanvasUsersApi().getAccountWithId(id));
        if (jsonNode.get("primary_email") != null) {
            sendMessage(service, "me", createEmailWithAttachment(
                    jsonNode.get("primary_email").asText(),
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

    public static MimeMessage createEmailWithAttachment(String to,
                                                        String from,
                                                        String subject,
                                                        String bodyText,
                                                        File file)
            throws MessagingException, IOException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);

        email.setFrom(new InternetAddress(from));
        email.addRecipient(javax.mail.Message.RecipientType.TO,
                new InternetAddress(to));
        email.setSubject(subject);

        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(bodyText, "text/plain");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);

        mimeBodyPart = new MimeBodyPart();
        DataSource source = new FileDataSource(file);

        mimeBodyPart.setDataHandler(new DataHandler(source));
        mimeBodyPart.setFileName(file.getName());

        multipart.addBodyPart(mimeBodyPart);
        email.setContent(multipart);

        return email;
    }

    public static Message sendMessage(Gmail service,
                                      String userId,
                                      MimeMessage emailContent)
            throws MessagingException, IOException {
        Message message = createMessageWithEmail(emailContent);
        message = service.users().messages().send(userId, message).execute();

        System.out.println("Message id: " + message.getId());
        System.out.println(message.toPrettyString());
        return message;
    }

    public static MimeMessage createEmail(String to,
                                          String from,
                                          String subject,
                                          String bodyText)
            throws MessagingException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);

        email.setFrom(new InternetAddress(from));
        email.addRecipient(javax.mail.Message.RecipientType.TO,
                new InternetAddress(to));
        email.setSubject(subject);
        email.setText(bodyText);
        return email;
    }

    public static Message createMessageWithEmail(MimeMessage emailContent)
            throws MessagingException, IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        emailContent.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(bytes);
        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }

    public static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

    @GetMapping(value = "/{projectId}/feedbackPdf")
    @ResponseBody
    protected ResponseEntity<byte[]> sendFeedbackPdfTemplate(@PathVariable String courseId,
                                                     @PathVariable String projectId,
                                                     Principal principal) throws IOException, ParseException {
        Project project = projectService.getProjectById(courseId, projectId);
        if (project == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "entity not found"
            );
        }

        String fileName = "hello.pdf";
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
        PdfFont fontSubject = PdfFontFactory.createFont(FontConstants.COURIER);

        PdfFont fontBody = PdfFontFactory.createFont(FontConstants.COURIER);
        Paragraph preface = new Paragraph();
        addEmptyLine(preface, 1);

        Paragraph paragraph = new Paragraph("lalalala").setFont(fontSubject).setFontSize(22);
        preface.add(paragraph);
        addEmptyLine(preface, 2);

        paragraph = new Paragraph("lalalalalalal").setFont(fontSubject).setFontSize(13);
        preface.add(paragraph);

        document.add(preface);
        document.close();


//        System.out.println(Arrays.toString(byteArrayOutputStream.toByteArray()));

        return new ResponseEntity<byte[]>(byteArrayOutputStream.toByteArray(), headers, HttpStatus.OK);
    }

    @GetMapping(value = "/{projectId}/feedback")
    @ResponseBody
    protected ObjectNode getFeedbackInfoPage(@PathVariable String courseId,
                                       @PathVariable String projectId,
                                       Principal principal) throws JsonProcessingException, ParseException {
        Project project = projectService.getProjectById(courseId, projectId);
        if (project == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "entity not found"
            );
        }
        String projectResponse = this.canvasApi.getCanvasCoursesApi().getCourseProject(courseId, projectId);
        String courseString = this.canvasApi.getCanvasCoursesApi().getUserCourse(courseId);
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode usersNode = groupPages(objectMapper, this.canvasApi.getCanvasCoursesApi().getCourseParticipants(courseId));
        ObjectNode resultNode = objectMapper.createObjectNode();
        resultNode.set("users", usersNode);
        resultNode.set("course", objectMapper.readTree(courseString));
        resultNode.set("project", objectMapper.readTree(projectResponse));

        return resultNode;

    }

    @GetMapping(value = "/{projectId}/groups")
    @ResponseBody
    protected JsonNode getProjectGroup(@PathVariable String courseId, @PathVariable String projectId, Principal principal) throws JsonProcessingException, ParseException {
        String projectResponse = this.canvasApi.getCanvasCoursesApi().getCourseProject(courseId, projectId);
        String courseString = this.canvasApi.getCanvasCoursesApi().getUserCourse(courseId);

        Project project = projectService.getProjectById(courseId, projectId);
        if (project == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "entity not found"
            );
        }

        List<String> submissionsString = this.canvasApi.getCanvasCoursesApi().getSubmissionsInfo(courseId, Long.parseLong(projectId));
        List<String> studentsString = this.canvasApi.getCanvasCoursesApi().getCourseStudents(courseId);

//        System.out.println(submissionsString);

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode resultNode = objectMapper.createObjectNode();
        resultNode.set("project", objectMapper.readTree(projectResponse));
        resultNode.set("course", objectMapper.readTree(courseString));

        JsonNode projectJson = objectMapper.readTree(projectResponse);
        String projectCatId = projectJson.get("group_category_id").asText();
        Map<String, String> groupIdToNameMap = new HashMap<>();
        Map<String, String> userIdToGroupIdMap = new HashMap<>();
        Map<String, List<String>> groupIdToMembership = new HashMap<>();

        if (!projectCatId.equals("null")) {
            ArrayNode groupsString = groupPages(objectMapper, canvasApi.getCanvasCoursesApi().getCourseGroupCategoryGroup(projectCatId));

            for (Iterator<JsonNode> it = groupsString.elements(); it.hasNext(); ) {
                JsonNode group = it.next();
                if (group.get("members_count").asInt(0) <= 0) continue;

                List<String> members = new ArrayList<>();
                ArrayNode memberships = groupPages(objectMapper, this.canvasApi.getCanvasCoursesApi().getGroupMemberships(group.get("id").asText()));
                groupIdToNameMap.put(group.get("id").asText(), group.get("name").asText());

                for (Iterator<JsonNode> iter = memberships.elements(); iter.hasNext(); ) {
                    JsonNode membership = iter.next();
                    userIdToGroupIdMap.put(membership.get("user_id").asText(), membership.get("group_id").asText());
                    members.add(membership.get("user_id").asText());
                }
                groupIdToMembership.put(group.get("id").asText(), members);
            }
        }

        ArrayNode studentArray = groupPages(objectMapper, studentsString);
        Map<String, JsonNode> studentMap = new HashMap<>();
        for (Iterator<JsonNode> it = studentArray.elements(); it.hasNext(); ) {
            JsonNode jsonNode = it.next();
            studentMap.put(jsonNode.get("id").asText(), jsonNode);
        }

        ArrayNode submissionArray = groupPages(objectMapper, submissionsString);
        ArrayNode groupsArray = objectMapper.createArrayNode();
        for (Iterator<JsonNode> it = submissionArray.elements(); it.hasNext(); ) {
            JsonNode jsonNode = it.next();

            if (!studentMap.containsKey(jsonNode.get("user_id").asText())) continue;

            boolean isGroup = userIdToGroupIdMap.containsKey(jsonNode.get("user_id").asText());
            String id = (isGroup)? userIdToGroupIdMap.get(jsonNode.get("user_id").asText()): jsonNode.get("user_id").asText();
            String name = (isGroup)? groupIdToNameMap.get(userIdToGroupIdMap.get(jsonNode.get("user_id").asText())): studentMap.get(id).get("name").asText();

            ObjectNode entityNode = objectMapper.createObjectNode();
            entityNode.put("id", id);
            if (!isGroup) entityNode.put("sid", studentMap.get(jsonNode.get("user_id").asText()).get("login_id").asText());
            entityNode.put("name", name);
            entityNode.put("isGroup", isGroup);
            entityNode.put("status", jsonNode.get("workflow_state").asText());
            if (isGroup) {
                ArrayNode membersNode = objectMapper.createArrayNode();
                if (!groupIdToMembership.containsKey(id)) continue;
                for(String userId: groupIdToMembership.get(id)) {
                    ObjectNode memberNode = objectMapper.createObjectNode();
                    if (!studentMap.containsKey(userId)) continue;
                    memberNode.put("name", studentMap.get(userId).get("name").asText());
                    memberNode.put("sid", studentMap.get(userId).get("login_id").asText());
                    memberNode.put("sortable_name", studentMap.get(userId).get("sortable_name").asText());
                    memberNode.put("email", studentMap.get(userId).get("email").asText());
                    membersNode.add(memberNode);
                }
                entityNode.set("members", membersNode);
            }
            groupsArray.add(entityNode);
        }

        resultNode.set("groups", groupsArray);

        return resultNode;
    }

    @GetMapping("/{projectId}/rubric")
    public ResponseEntity<String> getProject(@PathVariable String projectId) throws JsonProcessingException {
        Rubric rubric = rubricService.getRubricById(projectId);

        if (rubric == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            ObjectMapper objectMapper = new ObjectMapper();
            String rubricString = objectMapper.writeValueAsString(rubric);
//            String response = "{\"rubric\":" + rubricString + "}";
//            System.out.println(rubricString);
            return new ResponseEntity<>(rubricString, HttpStatus.OK);
        }
    }

    // TODO: submit only 'children'
    @PostMapping("/{projectId}/rubric")
    public Rubric newRubric(@RequestBody Rubric newRubric) {
        System.out.println("Creating a rubric...");
        return rubricService.addNewRubric(newRubric);
    }

    // TODO temporary unsafe method
    @GetMapping("/{projectId}/submissions/sample")
    public ResponseEntity<byte[]> getSamplePdf() throws IOException {
        Path pdfPath = Paths.get("src","main", "resources","static", "testPdf.pdf");
        byte[] contents = Files.readAllBytes(pdfPath);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        String filename = "output.pdf";
        headers.setContentDispositionFormData(filename, filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        return new ResponseEntity<>(contents, headers, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{projectId}/flag/{flagId}")
    protected JsonNode deleteFlagPermanently(@PathVariable String courseId,
                                             @PathVariable String projectId,
                                             @PathVariable String flagId,
                                             Principal principal
//                                   @RequestParam Map<String, String> queryParameters
    ) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();

        Project project = projectService.getProjectById(courseId, projectId);
        if (project == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "project not found"
            );
        }

        Grader grader = graderService.getGraderFromGraderId(principal.getName(), project);
        if (grader == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "task not found"
            );
        }
        //TODO change response to errors
        Flag flag = flagService.findFlagWithId(Long.parseLong(flagId));
        ObjectNode result = objectMapper.createObjectNode();
        if (flag != null) {
            List<Submission> submissions = submissionService.findSubmissionsByFlags(flag);
            if (!flag.getGrader().getUserId().equals(principal.getName())) {
                result.put("error", "This flag is not yours");
                return result;
            }
            if (submissions.size() > 0) {
                result.put("error", "Flag is current used by some submission");
                return result;
            } else {
                flagService.deleteFlag(flag);
                result.set("data", createFlagsArrayNode(flagService.findFlagsWithGrader(grader), principal.getName()));
                return result;
            }

        }
        result.put("error", "some weird error");
        return result;
    }

    private ArrayNode createFlagsArrayNode(List<Flag> flags, String userId) {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode arrayNode = objectMapper.createArrayNode();
        for(Flag flag2: flags) {
            ObjectNode flagNode = objectMapper.createObjectNode();
            flagNode.put("id", flag2.getId());
            flagNode.put("name", flag2.getName());
            flagNode.put("variant", flag2.getVariant());
            flagNode.put("description", flag2.getDescription());
            flagNode.put("changeable", flag2.getGrader().getUserId().equals(userId));
            arrayNode.add(flagNode);
        }
        return arrayNode;
    }
}

