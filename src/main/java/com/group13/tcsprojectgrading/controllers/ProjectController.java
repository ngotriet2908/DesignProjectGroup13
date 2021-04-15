package com.group13.tcsprojectgrading.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.flipkart.zjsonpatch.JsonPatchApplicationException;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.group13.tcsprojectgrading.canvas.api.CanvasApi;
import com.group13.tcsprojectgrading.models.course.CourseParticipation;
import com.group13.tcsprojectgrading.models.feedback.FeedbackLog;
import com.group13.tcsprojectgrading.models.feedback.FeedbackTemplate;
import com.group13.tcsprojectgrading.models.grading.Issue;
import com.group13.tcsprojectgrading.models.permissions.PrivilegeEnum;
import com.group13.tcsprojectgrading.models.rubric.Rubric;
import com.group13.tcsprojectgrading.models.submissions.Label;
import com.group13.tcsprojectgrading.models.user.User;
import com.group13.tcsprojectgrading.services.course.CourseService;
import com.group13.tcsprojectgrading.services.feedback.FeedbackService;
import com.group13.tcsprojectgrading.services.graders.GradingParticipationService;
import com.group13.tcsprojectgrading.services.project.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;

import static com.group13.tcsprojectgrading.controllers.Utils.groupPages;
import static com.group13.tcsprojectgrading.models.permissions.PrivilegeEnum.*;

@RestController
@RequestMapping("/api/courses/{courseId}/projects")
public class ProjectController {
    private final CanvasApi canvasApi;
    private final ProjectService projectService;
    private final GradingParticipationService gradingParticipationService;
    private final CourseService courseService;
    private final FeedbackService feedbackService;

    private final GoogleAuthorizationCodeFlow flow;

    @Autowired
    public ProjectController(CanvasApi canvasApi, ProjectService projectService, GoogleAuthorizationCodeFlow flow,
                             GradingParticipationService gradingParticipationService, CourseService courseService, FeedbackService feedbackService) {
        this.canvasApi = canvasApi;
        this.projectService = projectService;
        this.gradingParticipationService = gradingParticipationService;
        this.flow = flow;
        this.courseService = courseService;
        this.feedbackService = feedbackService;
    }

    /*
    Returns the project's details.
     */
    @RequestMapping(value = "/{projectId}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    protected String getProject(@PathVariable Long courseId,
                                @PathVariable Long projectId,
                                Principal principal) throws IOException, ParseException {

        List<PrivilegeEnum> privileges = this.gradingParticipationService
                .getPrivilegesFromUserIdAndProject(Long.valueOf(principal.getName()), projectId);
        if (!(privileges != null && privileges.contains(PROJECT_READ))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
        }

        String project = this.projectService.getProject(projectId, Long.valueOf(principal.getName()));

        if (project == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found");
        } else {
            return project;
        }
    }

//    @RequestMapping(value = "/{projectId}/students", method = RequestMethod.GET, produces = "application/json")
//    @ResponseBody
//    protected List<CourseParticipation> getProjectStudents(@PathVariable Long courseId, @PathVariable Long projectId, Principal principal) throws JsonProcessingException, ParseException {
//
//        List<PrivilegeEnum> privileges = this.gradingParticipationService
//                .getPrivilegesFromUserIdAndProject(Long.valueOf(principal.getName()), projectId);
//        if (!(privileges != null && privileges.contains(PROJECT_READ))) {
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
//        }
//
//        return this.projectService.getProjectParticipantsWithSubmissions(courseId, projectId);
//    }

    @RequestMapping(
            value = "/{projectId}/students",
            method = RequestMethod.GET,
            produces = "application/json"
    )
    @ResponseBody
    protected ResponseEntity<?> getProjectStudentsP(
            @PathVariable Long courseId,
            @PathVariable Long projectId,
            Principal principal,
            @RequestParam(name = "submissions", required = false) String includeSubmissions) {

        List<PrivilegeEnum> privileges = this.gradingParticipationService
                .getPrivilegesFromUserIdAndProject(Long.valueOf(principal.getName()), projectId);
        if (!(privileges != null && privileges.contains(PROJECT_READ))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
        }

        if (includeSubmissions != null && includeSubmissions.equals("true")) {
            List<CourseParticipation> students = this.projectService.getProjectParticipantsWithSubmissions(courseId, projectId);
            return new ResponseEntity<>(students, HttpStatus.OK);
        } else {
            List<User> students = this.courseService
                    .getCourseStudents(courseId)
                    .stream()
                    .map(participation -> participation.getId().getUser())
                    .collect(Collectors.toList());
            return new ResponseEntity<>(students, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/{projectId}/stats", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    protected List<Float> getProjectCurrentGradesDistribution(@PathVariable Long courseId,
                                                        @PathVariable Long projectId,
                                                        Principal principal) throws JsonProcessingException, ParseException {

        List<PrivilegeEnum> privileges = this.gradingParticipationService
                .getPrivilegesFromUserIdAndProject(Long.valueOf(principal.getName()), projectId);
        if (!(privileges != null && privileges.contains(PROJECT_READ))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
        }

        return this.projectService.getFinalGrades(courseId, projectId);
    }

    @RequestMapping(value = "/{projectId}/students/{studentId}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    protected CourseParticipation getProjectParticipant(@PathVariable Long courseId,
                                                        @PathVariable Long projectId,
                                                        @PathVariable Long studentId,
                                                        Principal principal) throws JsonProcessingException, ParseException {

        List<PrivilegeEnum> privileges = this.gradingParticipationService
                .getPrivilegesFromUserIdAndProject(Long.valueOf(principal.getName()), projectId);
        if (!(privileges != null && privileges.contains(PROJECT_READ))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
        }

        return this.projectService.getProjectStudent(courseId, projectId, studentId);
    }

    /*
    Returns the list of people who are


    ed to grade submissions in the project.
     */
    @RequestMapping(value = "/{projectId}/graders", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    protected List<User> getProjectGraders(
            @PathVariable Long courseId,
            @PathVariable Long projectId,
            Principal principal) throws IOException, ParseException {

        List<PrivilegeEnum> privileges = this.gradingParticipationService
                .getPrivilegesFromUserIdAndProject(Long.valueOf(principal.getName()), projectId);
        if (!(privileges != null && privileges.contains(PROJECT_READ))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
        }

        return this.projectService.getProjectGraders(projectId);
    }

    /*
    Saves specified users as graders in the project.
     */
    @RequestMapping(value = "/{projectId}/graders", method = RequestMethod.PUT, produces = "application/json")
    @ResponseBody
    protected List<User> saveProjectGraders(
            @PathVariable Long courseId,
            @PathVariable Long projectId,
            @RequestBody List<User> graders,
            Principal principal) throws IOException, ParseException {

        List<PrivilegeEnum> privileges = this.gradingParticipationService
                .getPrivilegesFromUserIdAndProject(Long.valueOf(principal.getName()), projectId);
        if (!(privileges != null && privileges.contains(MANAGE_GRADERS_EDIT))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
        }

        return this.projectService.saveProjectGraders(projectId, graders, Long.valueOf(principal.getName()));
    }

    /*
    Synchronises the project's state with (new) data obtained from Canvas (updates the user list and the submission list).
     */
    @GetMapping(value = "/{projectId}/sync")
    protected void syncWithCanvas(@PathVariable Long courseId,
                                  @PathVariable Long projectId,
                                  Principal principal) throws JsonProcessingException {

        List<PrivilegeEnum> privileges = this.gradingParticipationService
                .getPrivilegesFromUserIdAndProject(Long.valueOf(principal.getName()), projectId);
        if (!(privileges != null && privileges.contains(SUBMISSIONS_SYNC))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        // fetch data here? TODO
        List<String> submissionsString = this.canvasApi.getCanvasCoursesApi().getSubmissionsInfo(courseId, projectId);
        ArrayNode submissionsArray = groupPages(submissionsString);

        // sync course users
        this.courseService.syncCourse(courseId);

        // sync project's submissions
        this.projectService.syncProject(projectId, submissionsArray);
    }

    /*
    Returns all existing labels in the project.
     */
    @GetMapping(value = "/{projectId}/labels")
    protected List<Label> getProjectLabels(@PathVariable Long courseId,
                                             @PathVariable Long projectId,
                                             Principal principal
    ) {

        List<PrivilegeEnum> privileges = this.gradingParticipationService
                .getPrivilegesFromUserIdAndProject(Long.valueOf(principal.getName()), projectId);
        if (!(privileges != null && privileges.contains(PROJECT_READ))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        return this.projectService.getProjectLabels(projectId);
    }

    /*
    Creates a new label and associates it with the project.
     */
    @PostMapping(value = "/{projectId}/labels")
    protected Label createProjectLabel(@PathVariable Long courseId,
                                                @PathVariable Long projectId,
                                                @RequestBody Label label,
                                                Principal principal
    ) {

        List<PrivilegeEnum> privileges = this.gradingParticipationService
                .getPrivilegesFromUserIdAndProject(Long.valueOf(principal.getName()), projectId);
        if (!(privileges != null && privileges.contains(FLAG_EDIT))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        return this.projectService.saveProjectLabel(label, projectId);
    }

    @GetMapping(
            value = "/{projectId}/downloadRubric",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE

    )
    @ResponseBody
    protected byte[] sendFeedbackPdf(@PathVariable Long courseId,
                                                     @PathVariable Long projectId,
                                                     Principal principal) throws IOException, ParseException {

        List<PrivilegeEnum> privileges = this.gradingParticipationService
                .getPrivilegesFromUserIdAndProject(Long.valueOf(principal.getName()), projectId);
        if (!(privileges != null && privileges.contains(RUBRIC_DOWNLOAD))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
        }

        return projectService.downloadRubric(courseId, projectId);
    }

    @GetMapping(value = "/{projectId}/feedback/templates")
    @ResponseBody
    protected List<FeedbackTemplate> getFeedbackTemplates(@PathVariable Long courseId,
                                                         @PathVariable Long projectId,
                                                         Principal principal) throws JsonProcessingException {

        List<PrivilegeEnum> privileges = this.gradingParticipationService
                .getPrivilegesFromUserIdAndProject(Long.valueOf(principal.getName()), projectId);
        if (!(privileges != null && privileges.contains(FEEDBACK_OPEN))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
        }

        return projectService.getFeedbackTemplates(projectId);
    }

    @PostMapping(value = "/{projectId}/feedback/templates")
    protected List<FeedbackTemplate> getFeedbackParticipants(@PathVariable Long courseId,
                                                             @PathVariable Long projectId,
                                                             @RequestBody ObjectNode objectNode,
                                                             Principal principal) throws JsonProcessingException {

        List<PrivilegeEnum> privileges = this.gradingParticipationService
                .getPrivilegesFromUserIdAndProject(Long.valueOf(principal.getName()), projectId);
        if (!(privileges != null && privileges.contains(FEEDBACK_EDIT))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
        }

        return projectService.createFeedbackTemplate(projectId, objectNode);
    }

    @PutMapping(value = "/{projectId}/feedback/templates/{templateId}")
    protected List<FeedbackTemplate> updateFeedbackTemplate(@PathVariable Long courseId,
                                                             @PathVariable Long projectId,
                                                             @PathVariable Long templateId,
                                                             @RequestBody ObjectNode objectNode,
                                                             Principal principal) throws JsonProcessingException {

        List<PrivilegeEnum> privileges = this.gradingParticipationService
                .getPrivilegesFromUserIdAndProject(Long.valueOf(principal.getName()), projectId);
        if (!(privileges != null && privileges.contains(FEEDBACK_EDIT))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
        }

        return projectService.updateFeedbackTemplate(projectId, templateId, objectNode);
    }

    @DeleteMapping(value = "/{projectId}/feedback/templates/{templateId}")
    protected List<FeedbackTemplate> getFeedbackParticipants(@PathVariable Long courseId,
                                                             @PathVariable Long projectId,
                                                             @PathVariable Long templateId,
                                                             Principal principal) throws JsonProcessingException {

        List<PrivilegeEnum> privileges = this.gradingParticipationService
                .getPrivilegesFromUserIdAndProject(Long.valueOf(principal.getName()), projectId);
        if (!(privileges != null && privileges.contains(FEEDBACK_EDIT))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
        }

        return projectService.deleteUpdateTemplate(projectId, templateId);
    }

    @GetMapping(value = "/{projectId}/feedback/participants/all")
    @ResponseBody
    protected List<CourseParticipation> getAllParticipant(@PathVariable Long courseId,
                                                         @PathVariable Long projectId,
                                                         Principal principal) throws JsonProcessingException {

        List<PrivilegeEnum> privileges = this.gradingParticipationService
                .getPrivilegesFromUserIdAndProject(Long.valueOf(principal.getName()), projectId);
        if (!(privileges != null && privileges.contains(FEEDBACK_OPEN))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
        }

        return projectService.allFinishedGradedUser(projectId);
    }

    @GetMapping(value = "/{projectId}/feedback/participants/notSent")
    @ResponseBody
    protected List<CourseParticipation> getNotSentParticipant(@PathVariable Long courseId,
                                                                @PathVariable Long projectId,
                                                                Principal principal) throws JsonProcessingException {

        List<PrivilegeEnum> privileges = this.gradingParticipationService
                .getPrivilegesFromUserIdAndProject(Long.valueOf(principal.getName()), projectId);
        if (!(privileges != null && privileges.contains(FEEDBACK_OPEN))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
        }

        return projectService.allFinishedGradedUserNotSent(projectId);
    }

    @GetMapping(value = "/{projectId}/feedback/send/{templateId}")
    @ResponseBody
    protected List<FeedbackLog> sendFeedBack(@PathVariable Long courseId,
                                             @PathVariable Long projectId,
                                             @PathVariable Long templateId,
                                             @RequestParam("isAll") boolean isAll,
                                             @RequestParam("type") String type,
                                             Principal principal) throws JsonProcessingException {

        List<PrivilegeEnum> privileges = this.gradingParticipationService
                .getPrivilegesFromUserIdAndProject(Long.valueOf(principal.getName()), projectId);
        if (!(privileges != null && privileges.contains(FEEDBACK_OPEN))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
        }
        System.out.println(type);
        if (type.equals("emailPdf")) {
            return projectService.sendFeedbackEmailPdf(projectId, templateId, isAll, flow, principal);
        } else if (type.equals("canvasString")) {
            return projectService.sendFeedbackCanvasString(projectId, templateId, isAll, canvasApi, principal);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no appropriate type found");
        }
    }

    @GetMapping("/{projectId}/rubric")
    public String getRubrics(
            @PathVariable Long projectId,
            Principal principal) throws JsonProcessingException {

        List<PrivilegeEnum> privileges = this.gradingParticipationService
                .getPrivilegesFromUserIdAndProject(Long.valueOf(principal.getName()), projectId);
        if (!(privileges != null && privileges.contains(RUBRIC_READ))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        return projectService.getRubric(projectId);
    }

    /*
    Updates the rubric with update patches. Patches are applied in order they come and are stored in the database to
    retrieve rubric history.
     */
    @PatchMapping("/{projectId}/rubric")
    public String updateRubric(
            @RequestBody JsonNode patch,
            @PathVariable Long projectId,
            @RequestParam("version") Long version,
            Principal principal) throws JsonPatchApplicationException, JsonProcessingException {

        List<PrivilegeEnum> privileges = this.gradingParticipationService
                .getPrivilegesFromUserIdAndProject(Long.valueOf(principal.getName()), projectId);
        if (!(privileges != null && privileges.contains(RUBRIC_WRITE))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        System.out.println("Rubric version the server: " + version);
        projectService.updateRubric(projectId, patch, version);
        return projectService.getRubric(projectId);
    }

    @GetMapping(
            value = "/{projectId}/rubric/downloadFile",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
    )
    public byte[] getRubricFile(
            @PathVariable Long projectId,
            Principal principal) throws JsonProcessingException {

        List<PrivilegeEnum> privileges = this.gradingParticipationService
                .getPrivilegesFromUserIdAndProject(Long.valueOf(principal.getName()), projectId);
        if (!(privileges != null && privileges.contains(RUBRIC_READ))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        return projectService.getRubricFile(projectId);
    }

    @PostMapping(
            value = "/{projectId}/rubric/uploadFile",
            consumes = {"multipart/form-data"}
    )
    public String uploadRubric(
            @PathVariable Long projectId,
            @RequestParam(value = "rubric", required = false) MultipartFile file,
            Principal principal) throws IOException {

        List<PrivilegeEnum> privileges = this.gradingParticipationService
                .getPrivilegesFromUserIdAndProject(Long.valueOf(principal.getName()), projectId);
        if (!(privileges != null && privileges.contains(RUBRIC_WRITE))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        String s = new String(file.getBytes(), StandardCharsets.UTF_8);
        System.out.println(s);
//        TODO can only upload before grading starts, also would criterion id matters ?
        projectService.importRubric(projectId, s);
        return projectService.getRubric(projectId);
    }

    @GetMapping(
            value =  "/{projectId}/excel",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
    )
    public @ResponseBody byte[] getProjectExcel(@PathVariable Long projectId,
                                  Principal principal) {

        List<PrivilegeEnum> privileges = this.gradingParticipationService
                .getPrivilegesFromUserIdAndProject(Long.valueOf(principal.getName()), projectId);
        if (!(privileges != null && privileges.contains(RUBRIC_READ))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        try {
            return projectService.getProjectExcel(projectId);
        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "project not found"
            );
        }
    }

    @GetMapping(
            value =  "/{projectId}/uploadGrades"
    )
    public String uploadGrades(@PathVariable Long projectId, Principal principal) {

        List<PrivilegeEnum> privileges = this.gradingParticipationService
                .getPrivilegesFromUserIdAndProject(Long.valueOf(principal.getName()), projectId);
        if (!(privileges != null && privileges.contains(UPLOAD_GRADES))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        return projectService.uploadGradesToCanvas(projectId, canvasApi);
    }

    @GetMapping("/{projectId}/issues")
    public List<Issue> getIssuesForProject(
            @PathVariable Long projectId,
            Principal principal, @PathVariable String courseId) throws JsonProcessingException {

        List<PrivilegeEnum> privileges = this.gradingParticipationService
                .getPrivilegesFromUserIdAndProject(Long.valueOf(principal.getName()), projectId);
        if (!(privileges != null && privileges.contains(PROJECT_READ))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        return projectService.getIssuesInProject(projectId, Long.valueOf(principal.getName()));
    }
}
