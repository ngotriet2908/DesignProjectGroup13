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
import com.group13.tcsprojectgrading.models.permissions.PrivilegeEnum;
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

import static com.group13.tcsprojectgrading.controllers.utils.Utils.groupPages;
import static com.group13.tcsprojectgrading.models.permissions.PrivilegeEnum.*;

/**
 * Controller handles Project Endpoints
 */
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

    /**
     * Gets the project's details.
     * this method require privilege PROJECT_READ
     * @param courseId canvas course id
     * @param projectId canvas project id
     * @param principal injected oauth2 client's information
     * @return Project wit privileges
     * @throws IOException not found exception
     * @throws ParseException parsing exception
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

    /**
     * Get project student with choice (either student with their submissions or student list)
     * this method require privilege PROJECT_READ
     * @param courseId canvas course id
     * @param projectId canvas project id
     * @param principal injected oauth2 client's information
     * @param includeSubmissions either to include student's submissions
     * @return list of Submission from database
     */
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

    /**
     * get list of final grades in project
     * this method require privilege PROJECT_READ
     * @param courseId canvas course id
     * @param projectId canvas project id
     * @param principal injected oauth2 client's information
     * @return List of final grades
     * @throws JsonProcessingException json parsing exception
     * @throws ParseException parse exception
     */
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

    /**
     * Get info on a student
     * this method require privilege PROJECT_READ
     * @param courseId canvas course id
     * @param projectId canvas project id
     * @param studentId canvas student id
     * @param principal injected oauth2 client's information
     * @return a CourseParticipation from database
     * @throws JsonProcessingException json parsing exception
     * @throws ParseException parse exception
     */
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

    /**
     * Returns the list of people who are assigned to grade submissions in the project.
     * this method require privilege PROJECT_READ
     * @param courseId canvas course id
     * @param projectId canvas project id
     * @param principal injected oauth2 client's information
     * @return List of graders from database
     * @throws IOException not found exception
     * @throws ParseException parsing exception
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

    /**
     * Saves specified users as graders in the project.
     * this method require privilege MANAGE_GRADERS_EDIT
     * @param courseId canvas course id
     * @param projectId canvas project id
     * @param graders list of graders that are active
     * @param principal injected oauth2 client's information
     * @return updated list of graders
     * @throws IOException not found exception
     * @throws ParseException parsing exception
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

    /**
     * Synchronises the project's state with (new) data obtained from Canvas (updates the user list and the submission list).
     * this method require privilege SUBMISSIONS_SYNC
     * @param courseId canvas course id
     * @param projectId canvas project id
     * @param principal injected oauth2 client's information
     * @throws JsonProcessingException json parsing exception
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

    /**
     * Get all existing labels in the project.
     * this method require privilege PROJECT_READ
     * @param courseId canvas course id
     * @param projectId canvas project id
     * @param principal injected oauth2 client's information
     * @return list of labels from database
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

    /**
     * Creates a new label and associates it with the project.
     * this method require privilege FLAG_EDIT
     * @param courseId canvas course id
     * @param projectId canvas project id
     * @param label new label from project
     * @param principal injected oauth2 client's information
     * @return created label from database
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

    /**
     * Export and download rubric in Pdf format
     * this method require privilege RUBRIC_DOWNLOAD
     * @param courseId canvas course id
     * @param projectId canvas project id
     * @param principal injected oauth2 client's information
     * @return bytes of rubric pdf
     * @throws IOException not found exception
     * @throws ParseException parsing exception
     */
    @GetMapping(
            value = "/{projectId}/downloadRubric",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE

    )
    @ResponseBody
    protected byte[] downloadRubric(@PathVariable Long courseId,
                                                     @PathVariable Long projectId,
                                                     Principal principal) throws IOException, ParseException {

        List<PrivilegeEnum> privileges = this.gradingParticipationService
                .getPrivilegesFromUserIdAndProject(Long.valueOf(principal.getName()), projectId);
        if (!(privileges != null && privileges.contains(RUBRIC_DOWNLOAD))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
        }

        return projectService.downloadRubric(courseId, projectId);
    }

    /**
     * get templates for feedback (use for sending feedback via Canvas)
     * this method require privilege FEEDBACK_OPEN
     * @param courseId canvas course id
     * @param projectId canvas project id
     * @param principal injected oauth2 client's information
     * @return list of feedback templates
     * @throws JsonProcessingException json parsing exception
     */
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

    /**
     * create feedback template
     * this method require privilege FEEDBACK_EDIT
     * @param courseId canvas course id
     * @param projectId canvas project id
     * @param objectNode template json
     * @param principal injected oauth2 client's information
     * @return created feedback template
     * @throws JsonProcessingException json parsing exception
     */
    @PostMapping(value = "/{projectId}/feedback/templates")
    protected List<FeedbackTemplate> createFeedbackTemplate(@PathVariable Long courseId,
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

    /**
     * update a feedback template
     * this method require privilege FEEDBACK_EDIT
     * @param courseId canvas course id
     * @param projectId canvas project id
     * @param templateId template id
     * @param objectNode soon to be updated template json
     * @param principal injected oauth2 client's information
     * @return updated feedback template from database
     * @throws JsonProcessingException json parsing exception
     */
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

    /**
     * delete feedback template
     * this method require privilege FEEDBACK_EDIT
     * @param courseId canvas course id
     * @param projectId canvas project id
     * @param templateId template id
     * @param principal injected oauth2 client's information
     * @return list of updated templates
     * @throws JsonProcessingException json parsing exception
     */
    @DeleteMapping(value = "/{projectId}/feedback/templates/{templateId}")
    protected List<FeedbackTemplate> deleteTemplate(@PathVariable Long courseId,
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

    /**
     * get all students that have a submission in a project
     * this method require privilege PROJECT_READ
     * @param courseId canvas course id
     * @param projectId canvas project id
     * @param principal injected oauth2 client's information
     * @return list of course participants from database
     * @throws JsonProcessingException json parsing exception
     */
    @GetMapping(value = "/{projectId}/feedback/participants/all")
    @ResponseBody
    protected List<CourseParticipation> getAllParticipant(@PathVariable Long courseId,
                                                         @PathVariable Long projectId,
                                                         Principal principal) throws JsonProcessingException {

        List<PrivilegeEnum> privileges = this.gradingParticipationService
                .getPrivilegesFromUserIdAndProject(Long.valueOf(principal.getName()), projectId);
        if (!(privileges != null && privileges.contains(PROJECT_READ))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
        }

        return projectService.allFinishedGradedUser(projectId);
    }

    /**
     * get all students that didn't receive a feedback
     * this method require privilege FEEDBACK_OPEN
     * @param courseId canvas course id
     * @param projectId canvas project id
     * @param principal injected oauth2 client's information
     * @return list of course participant from database
     * @throws JsonProcessingException json parsing exception
     */
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

    /**
     * send feedback to student with a template
     * this method require privilege FEEDBACK_OPEN
     * @param courseId canvas course id
     * @param projectId canvas project id
     * @param templateId feedback template id
     * @param isAll whether to send to all students
     * @param type delivery type ("emailPdf" or "canvasString")
     * @param principal injected oauth2 client's information
     * @return list of feedback logs
     * @throws JsonProcessingException json parsing exception
     */
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

    /**
     * retrieve rubric from database
     * this method require privilege RUBRIC_READ
     * @param projectId canvas project id
     * @param principal injected oauth2 client's information
     * @return rubric from database
     * @throws JsonProcessingException json parsing exception
     */
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

    /**
     *  Updates the rubric with update patches. Patches are applied in order they come and are stored in the database
     *  to retrieve rubric history.
     * this method require privilege RUBRIC_WRITE
     * @param patch update patch from front-end
     * @param projectId canvas project id
     * @param version version before update from front-end
     * @param principal injected oauth2 client's information
     * @return updated rubric from database
     * @throws JsonPatchApplicationException patch exception
     * @throws JsonProcessingException json parsing exception
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
//        System.out.println("Rubric version the server: " + version);
        projectService.updateRubric(projectId, patch, version);
        return projectService.getRubric(projectId);
    }

    /**
     * Download rubric as custom file for further use
     * this method require privilege RUBRIC_READ
     * @param projectId canvas project id
     * @param principal injected oauth2 client's information
     * @return bytes from custom file contains rubric
     * @throws JsonProcessingException json parsing exception
     */
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

    /**
     * upload rubric by custom file
     * this method require privilege RUBRIC_WRITE
     * @param projectId canvas project id
     * @param file soon to be update rubric
     * @param principal injected oauth2 client's information
     * @return updated rubric from database
     * @throws IOException not found exception
     */
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
        projectService.importRubric(projectId, s);
        return projectService.getRubric(projectId);
    }

    /**
     * Export project grades with comments to excel
     * this method require privilege PROJECT_READ
     * @param projectId canvas project id
     * @param principal injected oauth2 client's information
     * @return bytes from excel project grades
     */
    @GetMapping(
            value =  "/{projectId}/excel",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
    )
    public @ResponseBody byte[] getProjectExcel(@PathVariable Long projectId,
                                  Principal principal) {

        List<PrivilegeEnum> privileges = this.gradingParticipationService
                .getPrivilegesFromUserIdAndProject(Long.valueOf(principal.getName()), projectId);
        if (!(privileges != null && privileges.contains(PROJECT_READ))) {
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

    /**
     * upload completed grades to canvas
     * this method require privilege UPLOAD_GRADES
     * @param projectId canvas project id
     * @param principal injected oauth2 client's information
     * @return response from canvas
     */
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

//    @GetMapping("/{projectId}/issues")
//    public List<Issue> getIssuesForProject(
//            @PathVariable Long projectId,
//            Principal principal, @PathVariable String courseId) throws JsonProcessingException {
//
//        List<PrivilegeEnum> privileges = this.gradingParticipationService
//                .getPrivilegesFromUserIdAndProject(Long.valueOf(principal.getName()), projectId);
//        if (!(privileges != null && privileges.contains(PROJECT_READ))) {
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
//        }
//
//        return projectService.getIssuesInProject(projectId, Long.valueOf(principal.getName()));
//    }
}
