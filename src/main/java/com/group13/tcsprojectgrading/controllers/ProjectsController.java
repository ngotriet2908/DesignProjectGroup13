package com.group13.tcsprojectgrading.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.flipkart.zjsonpatch.JsonPatchApplicationException;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.group13.tcsprojectgrading.canvas.api.CanvasApi;
import com.group13.tcsprojectgrading.models.*;
import com.group13.tcsprojectgrading.models.rubric.Rubric;
import com.group13.tcsprojectgrading.models.rubric.RubricHistory;
import com.group13.tcsprojectgrading.models.rubric.RubricUpdate;
import com.group13.tcsprojectgrading.services.*;
import com.group13.tcsprojectgrading.services.grading.AssessmentService;
import com.group13.tcsprojectgrading.services.rubric.RubricService;
//import com.itextpdf.text.*;
//import com.itextpdf.text.pdf.PdfWriter;
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
import java.security.GeneralSecurityException;
import java.security.Principal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.List;

import static com.group13.tcsprojectgrading.controllers.Utils.groupPages;
import static com.group13.tcsprojectgrading.models.RoleEnum.*;
import static com.group13.tcsprojectgrading.models.PrivilegeEnum.*;

@RestController
@RequestMapping("/api/courses/{courseId}/projects")
public class ProjectsController {
    private final CanvasApi canvasApi;
    private final ProjectService projectService;
    private final SecurityService securityService;

    private final GoogleAuthorizationCodeFlow flow;

    @Autowired
    public ProjectsController(CanvasApi canvasApi, ProjectService projectService, SecurityService securityService, GoogleAuthorizationCodeFlow flow) {
        this.canvasApi = canvasApi;
        this.projectService = projectService;
        this.securityService = securityService;
        this.flow = flow;
    }



    @RequestMapping(value = "/{projectId}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    protected JsonNode getProject(@PathVariable String courseId, @PathVariable String projectId, Principal principal) throws JsonProcessingException, ParseException {

        String courseResponse = this.canvasApi.getCanvasCoursesApi().getUserCourse(courseId);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode courseJson = objectMapper.readTree(courseResponse);

        //create teacher's grader object on first enter project page
        String userResponse = this.canvasApi.getCanvasCoursesApi().getCourseUser(courseId, principal.getName());
        ArrayNode enrolmentsNode = groupPages(objectMapper, this.canvasApi.getCanvasUsersApi().getEnrolments(principal.getName()));
        JsonNode userJson = objectMapper.readTree(userResponse);

        RoleEnum roleEnum = null;

        for (Iterator<JsonNode> it = enrolmentsNode.elements(); it.hasNext(); ) {
            JsonNode enrolmentNode = it.next();
            if (enrolmentNode.get("course_id").asText().equals(courseId)) {
                roleEnum = RoleEnum.getRoleFromEnrolment(enrolmentNode.get("role").asText());
            }
        }

        List<PrivilegeEnum> privileges = securityService
                .getPrivilegesFromUserIdAndProject(principal.getName(), courseId, projectId);

        if ((roleEnum != null && roleEnum.equals(TEACHER)) ||
                (privileges != null && privileges.contains(PROJECT_READ))){
            ObjectNode resultJson = projectService.getProject(courseId, projectId, roleEnum, userJson, principal.getName());
            resultJson.set("course", courseJson);
            return resultJson;
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
        }
    }

    @RequestMapping(value = "/{projectId}/participants", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    protected ObjectNode getProjectParticipants(@PathVariable String courseId, @PathVariable String projectId, Principal principal) throws JsonProcessingException, ParseException {

        List<PrivilegeEnum> privileges = securityService
                .getPrivilegesFromUserIdAndProject(principal.getName(), courseId, projectId);
        if (!(privileges != null && privileges.contains(PROJECT_READ))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
        }

        return projectService.getProjectParticipants(courseId, projectId);
    }

    @RequestMapping(value = "/{projectId}/participants/{participantId}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    protected ObjectNode getProjectParticipant(@PathVariable String courseId,
                                               @PathVariable String projectId,
                                               @PathVariable String participantId,
                                               Principal principal) throws JsonProcessingException, ParseException {

        List<PrivilegeEnum> privileges = securityService
                .getPrivilegesFromUserIdAndProject(principal.getName(), courseId, projectId);
        if (!(privileges != null && privileges.contains(PROJECT_READ))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
        }

        return projectService.getProjectParticipant(courseId, projectId, participantId);
    }

    @RequestMapping(value = "/{projectId}/graders", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    protected ArrayNode getProjectGraders(@PathVariable String courseId, @PathVariable String projectId, Principal principal) throws JsonProcessingException, ParseException {

        List<PrivilegeEnum> privileges = securityService
                .getPrivilegesFromUserIdAndProject(principal.getName(), courseId, projectId);
        if (!(privileges != null && privileges.contains(PROJECT_READ))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
        }

        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode result = objectMapper.createArrayNode();
        List<Grader> graders = projectService.getProjectsGrader(courseId, projectId);
        for(Grader grader: graders) {
            result.add(grader.getGraderJson());
        }
        return result;
    }

    @GetMapping(value = "/{projectId}/syncCanvas")
    protected void syncWithCanvas(@PathVariable String courseId,
                                  @PathVariable String projectId,
                                  Principal principal) throws JsonProcessingException {

        List<PrivilegeEnum> privileges = securityService
                .getPrivilegesFromUserIdAndProject(principal.getName(), courseId, projectId);
        if (!(privileges != null && privileges.contains(SUBMISSIONS_SYNC))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
        }

        ObjectMapper objectMapper = new ObjectMapper();
        List<String> submissionsString = this.canvasApi.getCanvasCoursesApi().getSubmissionsInfo(courseId, Long.parseLong(projectId));
        List<String> studentsString = this.canvasApi.getCanvasCoursesApi().getCourseStudents(courseId);

        ArrayNode studentArray = groupPages(objectMapper, studentsString);
        ArrayNode submissionArray = groupPages(objectMapper, submissionsString);
        projectService.syncCanvas(courseId, projectId, studentArray, submissionArray);

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

        List<PrivilegeEnum> privileges = securityService
                .getPrivilegesFromUserIdAndProject(principal.getName(), courseId, projectId);
        if (!(privileges != null && privileges.contains(FEEDBACK_SEND))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
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
        List<PrivilegeEnum> privileges = securityService
                .getPrivilegesFromUserIdAndProject(principal.getName(), courseId, projectId);
        if (!(privileges != null && privileges.contains(FEEDBACK_SEND))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
        }
//        System.out.println(Arrays.toString(byteArrayOutputStream.toByteArray()));
        return projectService.sendFeedbackPdf(courseId, projectId, feedback);
    }

    @GetMapping(value = "/{projectId}/downloadRubric")
    @ResponseBody
    protected ResponseEntity<byte[]> sendFeedbackPdf(@PathVariable String courseId,
                                                     @PathVariable String projectId,
                                                     Principal principal) throws IOException, ParseException {
        List<PrivilegeEnum> privileges = securityService
                .getPrivilegesFromUserIdAndProject(principal.getName(), courseId, projectId);
        if (!(privileges != null && privileges.contains(RUBRIC_DOWNLOAD))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
        }
        return projectService.downloadRubric(courseId, projectId);
    }

    @PostMapping(value = "/{projectId}/feedbackEmail")
    @ResponseBody
    protected String sendFeedbackEmail1(@PathVariable String courseId,
                                        @PathVariable String projectId,
                                        @RequestBody ObjectNode feedback,
                                        Principal principal) throws IOException, ParseException, GeneralSecurityException, MessagingException {

        List<PrivilegeEnum> privileges = securityService
                .getPrivilegesFromUserIdAndProject(principal.getName(), courseId, projectId);
        if (!(privileges != null && privileges.contains(FEEDBACK_SEND))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
        }

        String id = feedback.get("id").asText();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(this.canvasApi.getCanvasUsersApi().getAccountWithId(id));
        return projectService.sendFeedbackEmail(courseId, projectId,
                feedback, principal.getName(), flow,
                jsonNode.get("primary_email").asText()
        );
    }

    @GetMapping(value = "/{projectId}/feedback")
    @ResponseBody
    protected ObjectNode getFeedbackInfoPage(@PathVariable String courseId,
                                             @PathVariable String projectId,
                                             Principal principal) throws JsonProcessingException, ParseException {
        Project project = projectService.getProject(courseId, projectId);
        if (project == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "entity not found"
            );
        }

        List<PrivilegeEnum> privileges = securityService
                .getPrivilegesFromUserIdAndProject(principal.getName(), courseId, projectId);
        if (!(privileges != null && privileges.contains(FEEDBACK_OPEN))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
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

    @GetMapping("/{projectId}/rubric")
    public String getRubrics(
            @PathVariable String courseId,
            @PathVariable String projectId,
            Principal principal) throws JsonProcessingException {

        List<PrivilegeEnum> privileges = securityService
                .getPrivilegesFromUserIdAndProject(principal.getName(), courseId, projectId);
        if (!(privileges != null && privileges.contains(RUBRIC_READ))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
        }
        return projectService.getRubric(courseId, projectId);
    }

//    @PostMapping("/{projectId}/rubric")
//    public ResponseEntity<?> postRubric(
//            @RequestBody JsonNode newRubric,
//            @PathVariable String projectId) throws JsonPatchApplicationException, JsonProcessingException {
//
//        System.out.println("Updating the rubric of project " + projectId + ".");
//        Rubric rubric = this.rubricService.getRubricById(projectId);
//
//        // test diff
//        ObjectMapper objectMapper = new ObjectMapper();
//        JsonNode diffPatch = JsonDiff.asJson(
//                objectMapper.convertValue(rubric, JsonNode.class),
//                objectMapper.convertValue(newRubric, JsonNode.class));
//
//        System.out.println(diffPatch);
//
//        // apply update
//        Rubric rubricPatched = this.rubricService.applyPatchToRubric(diffPatch, rubric);
//        this.rubricService.saveRubric(rubricPatched);
//
////        RubricHistory history = this.rubricService.getHistory(projectId);
////        if (history == null) {
////            history = new RubricHistory(projectId);
////        }
////
////        // store update
////        history.getHistory().add(new RubricUpdate(patch));
////        this.rubricService.storeHistory(history);
////
////        // mark affected submissions
//////        this.rubricService.processUpdate(patch, rubric);
//
//        System.out.println("Updating the rubric of project " + projectId + " finished successfully.");
//        return ResponseEntity.ok("Rubric updated");
//    }

    /*
    Updates the rubric with update patches. Patches are applied in order they come and are stored in the database to
    retrieve rubric history.
     */
    @PatchMapping("/{projectId}/rubric")
    public String updateRubric(
            @RequestBody JsonNode patch,
            @PathVariable String courseId,
            @PathVariable String projectId,
            Principal principal) throws JsonPatchApplicationException, JsonProcessingException {

        List<PrivilegeEnum> privileges = securityService
                .getPrivilegesFromUserIdAndProject(principal.getName(), courseId, projectId);
        if (!(privileges != null && privileges.contains(RUBRIC_READ))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
        }
        return projectService.updateRubric(courseId, projectId, patch);
    }


    @DeleteMapping(value = "/{projectId}/flag/{flagId}")
    protected JsonNode deleteFlagPermanently(@PathVariable String courseId,
                                             @PathVariable String projectId,
                                             @PathVariable String flagId,
                                             Principal principal
//                                   @RequestParam Map<String, String> queryParameters
    ) throws JsonProcessingException {

        List<PrivilegeEnum> privileges = securityService
                .getPrivilegesFromUserIdAndProject(principal.getName(), courseId, projectId);
        if (!(privileges != null && privileges.contains(FLAG_DELETE))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
        }

        return projectService.deleteFlagPermanently(courseId, projectId, flagId, principal.getName());
    }

    private ArrayNode createFlagsArrayNode(List<Flag> flags) {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode arrayNode = objectMapper.createArrayNode();
        for(Flag flag2: flags) {
            ObjectNode flagNode = objectMapper.createObjectNode();
            flagNode.put("id", flag2.getId().toString());
            flagNode.put("name", flag2.getName());
            flagNode.put("variant", flag2.getVariant());
            flagNode.put("description", flag2.getDescription());
            arrayNode.add(flagNode);
        }
        return arrayNode;
    }
}
