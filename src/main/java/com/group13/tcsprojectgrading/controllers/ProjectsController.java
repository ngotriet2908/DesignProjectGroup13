package com.group13.tcsprojectgrading.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.flipkart.zjsonpatch.JsonPatchApplicationException;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.group13.tcsprojectgrading.canvas.api.CanvasApi;
import com.group13.tcsprojectgrading.models.*;
import com.group13.tcsprojectgrading.models.graders.Grader;
import com.group13.tcsprojectgrading.models.permissions.RoleEnum;
import com.group13.tcsprojectgrading.models.submissions.Flag;
import com.group13.tcsprojectgrading.services.*;
import com.group13.tcsprojectgrading.services.graders.GraderService;
import com.group13.tcsprojectgrading.services.grading.AssessmentLinkerService;
import com.group13.tcsprojectgrading.services.grading.AssessmentService;
import com.group13.tcsprojectgrading.services.permissions.ProjectRoleService;
import com.group13.tcsprojectgrading.services.permissions.RoleService;
import com.group13.tcsprojectgrading.services.rubric.RubricService;
//import com.itextpdf.text.*;
//import com.itextpdf.text.pdf.PdfWriter;
import com.group13.tcsprojectgrading.services.submissions.FlagService;
import com.group13.tcsprojectgrading.services.submissions.SubmissionDetailsService;
import com.group13.tcsprojectgrading.services.submissions.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.mail.MessagingException;
import java.io.*;
import java.security.GeneralSecurityException;
import java.security.Principal;
import java.text.ParseException;
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
    private final SubmissionDetailsService submissionDetailsService;

    @Autowired
    public ProjectsController(CanvasApi canvasApi, ActivityService activityService,
                              RubricService rubricService, ProjectService projectService,
                              RoleService roleService, GraderService graderService,
                              ProjectRoleService projectRoleService,
                              FlagService flagService,
                              GoogleAuthorizationCodeFlow flow,
                              SubmissionService submissionService, ParticipantService participantService,
                              AssessmentLinkerService assessmentLinkerService, AssessmentService assessmentService, SubmissionDetailsService submissionDetailsService) {
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
        this.submissionDetailsService = submissionDetailsService;
    }

    @RequestMapping(value = "/{projectId}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    protected JsonNode getProject(@PathVariable String courseId, @PathVariable String projectId, Principal principal) throws JsonProcessingException, ParseException {

        String courseResponse = this.canvasApi.getCanvasCoursesApi().getUserCourse(courseId);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode courseJson = objectMapper.readTree(courseResponse);

        // create teacher's grader object on first enter project page
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

        ObjectNode resultJson = projectService.getProject(courseId, projectId, roleEnum, userJson, principal.getName());
        resultJson.set("course", courseJson);

        return resultJson;
    }

    @RequestMapping(value = "/{projectId}/participants", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    protected ObjectNode getProjectParticipants(@PathVariable String courseId, @PathVariable String projectId, Principal principal) throws JsonProcessingException, ParseException {

        return projectService.getProjectParticipants(courseId, projectId);
    }

    @RequestMapping(value = "/{projectId}/graders", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    protected ArrayNode getProjectGraders(@PathVariable String courseId, @PathVariable String projectId, Principal principal) throws JsonProcessingException, ParseException {
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


//        System.out.println(Arrays.toString(byteArrayOutputStream.toByteArray()));
        return projectService.sendFeedbackPdf(courseId, projectId, feedback);
    }

    @GetMapping(value = "/{projectId}/downloadRubric")
    @ResponseBody
    protected ResponseEntity<byte[]> sendFeedbackPdf(@PathVariable String courseId,
                                                     @PathVariable String projectId,
                                                     Principal principal) throws IOException, ParseException {
        return projectService.downloadRubric(courseId, projectId);
    }

    @PostMapping(value = "/{projectId}/feedbackEmail")
    @ResponseBody
    protected String sendFeedbackEmail1(@PathVariable String courseId,
                                        @PathVariable String projectId,
                                        @RequestBody ObjectNode feedback,
                                        Principal principal) throws IOException, ParseException, GeneralSecurityException, MessagingException {

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
                                             @PathVariable String projectId) throws JsonProcessingException {
        Project project = projectService.getProject(courseId, projectId);
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

//    @GetMapping(value = "/{projectId}/groups")
//    @ResponseBody
//    protected JsonNode getProjectGroup(@PathVariable String courseId, @PathVariable String projectId, Principal principal) throws JsonProcessingException, ParseException {
//        String projectResponse = this.canvasApi.getCanvasCoursesApi().getCourseProject(courseId, projectId);
//        String courseString = this.canvasApi.getCanvasCoursesApi().getUserCourse(courseId);
//
//        Project project = projectService.getProjectById(courseId, projectId);
//        if (project == null) {
//            throw new ResponseStatusException(
//                    HttpStatus.NOT_FOUND, "entity not found"
//            );
//        }
//
//        List<String> submissionsString = this.canvasApi.getCanvasCoursesApi().getSubmissionsInfo(courseId, Long.parseLong(projectId));
//        List<String> studentsString = this.canvasApi.getCanvasCoursesApi().getCourseStudents(courseId);
//
////        System.out.println(submissionsString);
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        ObjectNode resultNode = objectMapper.createObjectNode();
//        resultNode.set("project", objectMapper.readTree(projectResponse));
//        resultNode.set("course", objectMapper.readTree(courseString));
//
//        JsonNode projectJson = objectMapper.readTree(projectResponse);
//        String projectCatId = projectJson.get("group_category_id").asText();
//        Map<String, String> groupIdToNameMap = new HashMap<>();
//        Map<String, String> userIdToGroupIdMap = new HashMap<>();
//        Map<String, List<String>> groupIdToMembership = new HashMap<>();
//
//        if (!projectCatId.equals("null")) {
//            ArrayNode groupsString = groupPages(objectMapper, canvasApi.getCanvasCoursesApi().getCourseGroupCategoryGroup(projectCatId));
//
//            for (Iterator<JsonNode> it = groupsString.elements(); it.hasNext(); ) {
//                JsonNode group = it.next();
//                if (group.get("members_count").asInt(0) <= 0) continue;
//
//                List<String> members = new ArrayList<>();
//                ArrayNode memberships = groupPages(objectMapper, this.canvasApi.getCanvasCoursesApi().getGroupMemberships(group.get("id").asText()));
//                groupIdToNameMap.put(group.get("id").asText(), group.get("name").asText());
//
//                for (Iterator<JsonNode> iter = memberships.elements(); iter.hasNext(); ) {
//                    JsonNode membership = iter.next();
//                    userIdToGroupIdMap.put(membership.get("user_id").asText(), membership.get("group_id").asText());
//                    members.add(membership.get("user_id").asText());
//                }
//                groupIdToMembership.put(group.get("id").asText(), members);
//            }
//        }
//
//        ArrayNode studentArray = groupPages(objectMapper, studentsString);
//        Map<String, JsonNode> studentMap = new HashMap<>();
//        for (Iterator<JsonNode> it = studentArray.elements(); it.hasNext(); ) {
//            JsonNode jsonNode = it.next();
//            studentMap.put(jsonNode.get("id").asText(), jsonNode);
//        }
//
//        ArrayNode submissionArray = groupPages(objectMapper, submissionsString);
//        ArrayNode groupsArray = objectMapper.createArrayNode();
//        for (Iterator<JsonNode> it = submissionArray.elements(); it.hasNext(); ) {
//            JsonNode jsonNode = it.next();
//
//            if (!studentMap.containsKey(jsonNode.get("user_id").asText())) continue;
//
//            boolean isGroup = userIdToGroupIdMap.containsKey(jsonNode.get("user_id").asText());
//            String id = (isGroup)? userIdToGroupIdMap.get(jsonNode.get("user_id").asText()): jsonNode.get("user_id").asText();
//            String name = (isGroup)? groupIdToNameMap.get(userIdToGroupIdMap.get(jsonNode.get("user_id").asText())): studentMap.get(id).get("name").asText();
//
//            ObjectNode entityNode = objectMapper.createObjectNode();
//            entityNode.put("id", id);
//            if (!isGroup) entityNode.put("sid", studentMap.get(jsonNode.get("user_id").asText()).get("login_id").asText());
//            entityNode.put("name", name);
//            entityNode.put("isGroup", isGroup);
//            entityNode.put("status", jsonNode.get("workflow_state").asText());
//            if (isGroup) {
//                ArrayNode membersNode = objectMapper.createArrayNode();
//                if (!groupIdToMembership.containsKey(id)) continue;
//                for(String userId: groupIdToMembership.get(id)) {
//                    ObjectNode memberNode = objectMapper.createObjectNode();
//                    if (!studentMap.containsKey(userId)) continue;
//                    memberNode.put("name", studentMap.get(userId).get("name").asText());
//                    memberNode.put("sid", studentMap.get(userId).get("login_id").asText());
//                    memberNode.put("sortable_name", studentMap.get(userId).get("sortable_name").asText());
//                    memberNode.put("email", studentMap.get(userId).get("email").asText());
//                    membersNode.add(memberNode);
//                }
//                entityNode.set("members", membersNode);
//            }
//            groupsArray.add(entityNode);
//        }
//
//        resultNode.set("groups", groupsArray);
//
//        return resultNode;
//    }

    @GetMapping("/{projectId}/rubric")
    public String getRubrics(
            @PathVariable String courseId,
            @PathVariable String projectId) throws JsonProcessingException {
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
            @PathVariable String projectId) throws JsonPatchApplicationException, JsonProcessingException {
        return projectService.updateRubric(courseId, projectId, patch);
    }


    @DeleteMapping(value = "/{projectId}/flag/{flagId}")
    protected JsonNode deleteFlagPermanently(@PathVariable String courseId,
                                             @PathVariable String projectId,
                                             @PathVariable String flagId,
                                             Principal principal
//                                   @RequestParam Map<String, String> queryParameters
    ) throws JsonProcessingException {

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
