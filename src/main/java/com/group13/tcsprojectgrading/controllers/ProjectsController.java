package com.group13.tcsprojectgrading.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.group13.tcsprojectgrading.canvas.api.CanvasApi;
import com.group13.tcsprojectgrading.models.Activity;
import com.group13.tcsprojectgrading.models.Grader;
import com.group13.tcsprojectgrading.models.Project;
import com.group13.tcsprojectgrading.models.RoleEnum;
import com.group13.tcsprojectgrading.models.rubric.Rubric;
import com.group13.tcsprojectgrading.services.*;
import com.group13.tcsprojectgrading.services.rubric.RubricService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    public ProjectsController(CanvasApi canvasApi, ActivityService activityService, RubricService rubricService, ProjectService projectService, RoleService roleService, GraderService graderService, ProjectRoleService projectRoleService) {
        this.canvasApi = canvasApi;
        this.activityService = activityService;
        this.rubricService = rubricService;
        this.projectService = projectService;
        this.roleService = roleService;
        this.graderService = graderService;
        this.projectRoleService = projectRoleService;
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

        String projectResponse = this.canvasApi.getCanvasCoursesApi().getCourseProject(courseId, projectId);
        String courseResponse = this.canvasApi.getCanvasCoursesApi().getUserCourse(courseId);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode projectJson = objectMapper.readTree(projectResponse);
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

        if (grader == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Grader not found"
            );
        }

        JsonNode graderNode = grader.getGraderJson();

        // including rubric to the response
        Rubric rubric = rubricService.getRubricByProjectId(projectId);
        JsonNode rubricJson;
//        if (rubric == null) {
//            rubricJson = objectMapper.readTree("null");
//        } else {
            String rubricString = objectMapper.writeValueAsString(rubric);
            rubricJson = objectMapper.readTree(rubricString);
//        }

        ObjectNode resultJson = objectMapper.createObjectNode();
        resultJson.set("course", courseJson);
        resultJson.set("project", projectJson);
        resultJson.set("rubric", rubricJson);
        resultJson.set("grader", graderNode);

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        SimpleDateFormat format = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss'Z'");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        Timestamp createdAt = new Timestamp(format.parse(projectJson.get("created_at").asText()).getTime());

        Activity activity = new Activity(
                project,
                principal.getName(),
                timestamp,
                projectJson.get("name").asText(),
                createdAt
        );

        activityService.addOrUpdateActivity(activity);

        if (projectResponse == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } else {
            return new ResponseEntity<>(resultJson, HttpStatus.OK);
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
                                                     Principal principal) throws IOException, ParseException, DocumentException {
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

        Document document = new Document();
        PdfWriter.getInstance(document, byteArrayOutputStream);

        document.open();
        Font fontSubject = FontFactory.getFont(FontFactory.COURIER, 22, BaseColor.BLACK);

        Font fontBody = FontFactory.getFont(FontFactory.COURIER, 13, BaseColor.BLACK);
        Paragraph preface = new Paragraph();
        addEmptyLine(preface, 1);

        Paragraph paragraph = new Paragraph(subject, fontSubject);
        preface.add(paragraph);
        addEmptyLine(preface, 2);

        paragraph = new Paragraph(body, fontBody);
        preface.add(paragraph);

        document.add(preface);
        document.close();


        System.out.println(Arrays.toString(byteArrayOutputStream.toByteArray()));

        return new ResponseEntity<byte[]>(byteArrayOutputStream.toByteArray(), headers, HttpStatus.OK);
    }

    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

    @GetMapping(value = "/{projectId}/feedbackPdf")
    @ResponseBody
    protected ResponseEntity<byte[]> sendFeedbackPdfTemplate(@PathVariable String courseId,
                                                     @PathVariable String projectId,
                                                     Principal principal) throws IOException, ParseException, DocumentException {
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

        Document document = new Document();
        PdfWriter.getInstance(document, byteArrayOutputStream);

        document.open();
        Font font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
        Chunk subjectChunk = new Chunk("subject", font);
        Chunk bodyChunk = new Chunk("bodyyyyy", font);

        document.add(subjectChunk);
        document.add(bodyChunk);
        document.close();


        System.out.println(Arrays.toString(byteArrayOutputStream.toByteArray()));

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

        System.out.println(submissionsString);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode resultNode = objectMapper.createObjectNode();
        ((ObjectNode) resultNode).set("project", objectMapper.readTree(projectResponse));
        ((ObjectNode) resultNode).set("course", objectMapper.readTree(courseString));

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

            JsonNode entityNode = objectMapper.createObjectNode();
            ((ObjectNode) entityNode).put("id", id);
            if (!isGroup) ((ObjectNode) entityNode).put("sid", studentMap.get(jsonNode.get("user_id").asText()).get("login_id").asText());
            ((ObjectNode) entityNode).put("name", name);
            ((ObjectNode) entityNode).put("isGroup", isGroup);
            ((ObjectNode) entityNode).put("status", jsonNode.get("workflow_state").asText());
            if (isGroup) {
                ArrayNode membersNode = objectMapper.createArrayNode();
                if (!groupIdToMembership.containsKey(id)) continue;
                for(String userId: groupIdToMembership.get(id)) {
                    JsonNode memberNode = objectMapper.createObjectNode();
                    if (!studentMap.containsKey(userId)) continue;
                    ((ObjectNode) memberNode).put("name", studentMap.get(userId).get("name").asText());
                    ((ObjectNode) memberNode).put("sid", studentMap.get(userId).get("login_id").asText());
                    ((ObjectNode) memberNode).put("sortable_name", studentMap.get(userId).get("sortable_name").asText());
                    ((ObjectNode) memberNode).put("email", studentMap.get(userId).get("email").asText());
                    membersNode.add(memberNode);
                }
                ((ObjectNode) entityNode).set("members", membersNode);
            }
            groupsArray.add(entityNode);
        }

        ((ObjectNode) resultNode).set("groups", groupsArray);

        return resultNode;
    }

    @GetMapping("/{projectId}/rubric")
    public ResponseEntity<String> getProject(@PathVariable String projectId) throws JsonProcessingException {
        Rubric rubric = rubricService.getRubricByProjectId(projectId);

        if (rubric == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            ObjectMapper objectMapper = new ObjectMapper();
            String rubricString = objectMapper.writeValueAsString(rubric);
            String response = "{\"rubric\":" + rubricString + "}";
            System.out.println(response);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    @PostMapping("/{projectId}/rubric")
    public Rubric newRubric(@RequestBody Rubric newRubric) {
        System.out.println("Creating a rubric...");
        return rubricService.addNewRubric(newRubric);
    }

    @DeleteMapping("{projectId}/rubric")
    public void deleteRubric(@PathVariable String projectId) {
        System.out.println("Deleting the rubric...");
        rubricService.deleteRubric(projectId);
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
        ResponseEntity<byte[]> response = new ResponseEntity<>(contents, headers, HttpStatus.OK);
        return response;
    }
}

