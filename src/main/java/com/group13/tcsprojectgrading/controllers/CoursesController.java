package com.group13.tcsprojectgrading.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.group13.tcsprojectgrading.canvas.api.CanvasApi;
import com.group13.tcsprojectgrading.models.*;
import com.group13.tcsprojectgrading.models.rubric.Rubric;
import com.group13.tcsprojectgrading.services.ActivityService;
import com.group13.tcsprojectgrading.services.GraderService;
import com.group13.tcsprojectgrading.services.ProjectService;
import com.group13.tcsprojectgrading.services.*;
import com.group13.tcsprojectgrading.services.rubric.RubricService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.server.ResponseStatusException;

import static com.group13.tcsprojectgrading.controllers.Utils.groupPages;

import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping("/api/courses")
class CoursesController {
    private final CanvasApi canvasApi;

    private final ActivityService activityService;
    private final RubricService rubricService;
    private final GraderService graderService;
    private final SubmissionService submissionService;
    private final ProjectService projectService;

    @Autowired
    public CoursesController(CanvasApi canvasApi, ActivityService activityService, RubricService rubricService,
                             GraderService graderService, SubmissionService submissionService, ProjectService projectService) {
        this.canvasApi = canvasApi;
        this.activityService = activityService;
        this.rubricService = rubricService;
        this.graderService = graderService;
        this.submissionService = submissionService;
        this.projectService = projectService;
    }

    @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json")
    protected ResponseEntity<ArrayNode> courses() throws JsonProcessingException {
        List<String> response = this.canvasApi.getCanvasCoursesApi().getUserCourseList();

        if (response == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        ObjectMapper mapper = new ObjectMapper();
        ArrayNode courses = groupPages(mapper, response);
        ArrayNode finalCourses = mapper.createArrayNode();

        for (Iterator<JsonNode> it = courses.elements(); it.hasNext(); ) {
            JsonNode course = it.next();
            RoleEnum role = RoleEnum.getRoleFromEnrolment(course.get("enrollments").get(0).get("role").asText());
            if (role.equals(RoleEnum.TEACHER) || role.equals(RoleEnum.TA)) {
                finalCourses.add(course);
            }
        }

        return new ResponseEntity<>(courses, HttpStatus.OK);
    }

    @RequestMapping(value = "/{course_id}", method = RequestMethod.GET, produces = "application/json")
    protected ResponseEntity<JsonNode> getCourse(@PathVariable String course_id, Principal principal) throws JsonProcessingException {
        String courseString = this.canvasApi.getCanvasCoursesApi().getUserCourse(course_id);
        List<Project> projects = projectService.getProjectsByCourseId(course_id);

        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode arrayNode = objectMapper.createArrayNode();
        JsonNode jsonCourseNode = objectMapper.readTree(courseString);

        for(Project project: projects) {
//            ObjectNode projectNode = objectMapper.createObjectNode();
            String nodeString = this.canvasApi.getCanvasCoursesApi().getCourseProject(project.getCourseId(), project.getProjectId());
            JsonNode node = objectMapper.readTree(nodeString);
//            projectNode.put("id", node.get("id").asText());
//            projectNode.put("name", node.get("name").asText());
            arrayNode.add(node);
        }

        ObjectNode resultNode = objectMapper.createObjectNode();
        resultNode.set("course", jsonCourseNode);
        resultNode.set("projects", arrayNode);
//        JsonNode resultNode = objectMapper.createObjectNode();


        String userResponse = this.canvasApi.getCanvasCoursesApi().getCourseUser(course_id, principal.getName());
        ArrayNode enrolmentsNode = groupPages(objectMapper, this.canvasApi.getCanvasUsersApi().getEnrolments(principal.getName()));
        JsonNode userJson = objectMapper.readTree(userResponse);

        RoleEnum roleEnum = null;

        for (Iterator<JsonNode> it = enrolmentsNode.elements(); it.hasNext(); ) {
            JsonNode enrolmentNode = it.next();
            if (enrolmentNode.get("course_id").asText().equals(course_id)) {
                roleEnum = RoleEnum.getRoleFromEnrolment(enrolmentNode.get("role").asText());
            }
        }

        if (roleEnum == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Enrolment not found"
            );
        }

        if (roleEnum.equals(RoleEnum.TEACHER)) {
            ((ObjectNode) userJson).put("role", "teacher");
        } else if (roleEnum.equals(RoleEnum.TA)) {
            ((ObjectNode) userJson).put("role", "ta");
        }

        ((ObjectNode)resultNode).set("course", jsonCourseNode);
        ((ObjectNode)resultNode).set("projects", arrayNode);
        ((ObjectNode)resultNode).set("user", userJson);

        if (arrayNode == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } else {
            return new ResponseEntity<>(resultNode, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/{course_id}/participants", method = RequestMethod.GET, produces = "application/json")
    protected ArrayNode getCourseUser(@PathVariable String course_id, @RequestParam Map<String, String> queryParameters) throws JsonProcessingException {
        List<String> response;
        if (queryParameters.containsKey("role") && queryParameters.get("role").equals("student"))  {
            response = this.canvasApi.getCanvasCoursesApi().getCourseStudents(course_id);
        } else {
            response = this.canvasApi.getCanvasCoursesApi().getCourseParticipants(course_id);
        }

        return groupPages(new ObjectMapper(), response);
    }

    /*
    Get all projects in  the course (both active and inactive)
     */
    @RequestMapping(value = "/{course_id}/projects", method = RequestMethod.GET, produces = "application/json")
    protected ResponseEntity<ArrayNode> getCourseCanvas(@PathVariable String course_id) throws JsonProcessingException {

        List<String> responseString = this.canvasApi.getCanvasCoursesApi().getCourseProjects(course_id);

        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode arrayNode = objectMapper.createArrayNode();
        for(String nodeListString: responseString) {
            JsonNode jsonNode = objectMapper.readTree(nodeListString);
            for (Iterator<JsonNode> it = jsonNode.elements(); it.hasNext(); ) {
                JsonNode node = it.next();
                Project project = projectService.getProjectById(course_id, node.get("id").asText());
                boolean isVolatile = project != null;

                //TODO check whether needed to check the latter 2 conditions
                if (project != null) {
                    isVolatile = (activityService.getActivitiesByProject(project).size() > 0) ||
                            (graderService.getGraderFromProject(project).size() > 0) ||
                            (submissionService.findSubmissionWithProject(project).size() > 0);
                }

                ObjectNode projectNode = objectMapper.createObjectNode();
                projectNode.put("id", node.get("id").asText());
                projectNode.put("name", node.get("name").asText());
                projectNode.put("isVolatile", isVolatile);

                arrayNode.add(projectNode);
            }
        }

        if (arrayNode == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } else {
            return new ResponseEntity<>(arrayNode, HttpStatus.OK);
        }
    }

    /*
    Save active projects TODO: endpoint naming
     */
    @PostMapping(value = "/{course_id}/projects-active")
    protected void editProjects(@PathVariable String course_id,
                                     @RequestBody ArrayNode activeProjects) throws JsonProcessingException {
        List<Project> projectsDatabase = projectService.getProjectsByCourseId(course_id);

        List<String> responseString = this.canvasApi.getCanvasCoursesApi().getCourseProjects(course_id);
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode ProjectsArrayFromCanvas = groupPages(objectMapper, responseString);
        Map<String, Project> availableProjects = new HashMap<>();


        for (Iterator<JsonNode> it = ProjectsArrayFromCanvas.elements(); it.hasNext(); ) {
            JsonNode node = it.next();
            availableProjects.put(node.get("id").asText(), new Project(
                    course_id,
                    node.get("id").asText()
            ));
        }

        List<String> editedActiveProject = new ArrayList<>();
        for (Iterator<JsonNode> it = activeProjects.elements(); it.hasNext(); ) {
            JsonNode project = it.next();
            editedActiveProject.add(project.get("id").asText());
        }

        for (Project project: projectsDatabase) {
            if (!editedActiveProject.contains(project.getProjectId())) {
                rubricService.deleteRubric(project.getProjectId());
                projectService.deleteProject(project);
            }
        }

        for(String activeProjectId: editedActiveProject) {
            projectService.addNewProject(availableProjects.get(activeProjectId));
            rubricService.addNewRubric(new Rubric(activeProjectId))
;       }
    }
}
