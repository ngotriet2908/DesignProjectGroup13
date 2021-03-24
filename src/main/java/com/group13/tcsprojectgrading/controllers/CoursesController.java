package com.group13.tcsprojectgrading.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.group13.tcsprojectgrading.canvas.api.CanvasApi;
import com.group13.tcsprojectgrading.models.*;
import com.group13.tcsprojectgrading.services.ProjectService;
import com.group13.tcsprojectgrading.services.*;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.util.Pair;

import org.springframework.web.server.ResponseStatusException;

import static com.group13.tcsprojectgrading.controllers.Utils.groupPages;

import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping("/api/courses")
class CoursesController {
    private final CanvasApi canvasApi;
    private final CourseServices courseServices;
    private final ProjectService projectService;

    public CoursesController(CanvasApi canvasApi, CourseServices courseServices, ProjectService projectService) {
        this.canvasApi = canvasApi;
        this.courseServices = courseServices;
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
    protected JsonNode getCourse(@PathVariable String course_id, Principal principal) throws JsonProcessingException {
        return courseServices.getCourse(course_id, this.canvasApi, principal.getName());
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

        Object[] objects = projectService.getVolatileProjectsId(course_id);
        List<String> volatileProjectsId = (List<String>) objects[0];
        Map<String, Project> projectMap = (Map<String, Project>) objects[1];
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode arrayNode = objectMapper.createArrayNode();
        for(String nodeListString: responseString) {
            JsonNode jsonNode = objectMapper.readTree(nodeListString);
            for (Iterator<JsonNode> it = jsonNode.elements(); it.hasNext(); ) {
                JsonNode node = it.next();
                Project project = projectMap.get(node.get("id").asText());
                boolean isVolatile = project != null;

                //TODO check whether needed to check the latter 2 conditions
                if (project != null) {
                    isVolatile = volatileProjectsId.contains(node.get("id").asText());
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
                                     @RequestBody ArrayNode activeProjects) throws Exception {

        List<String> responseString = this.canvasApi.getCanvasCoursesApi().getCourseProjects(course_id);
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode ProjectsArrayFromCanvas = groupPages(objectMapper, responseString);
        Map<String, Project> availableProjects = new HashMap<>();


        for (Iterator<JsonNode> it = ProjectsArrayFromCanvas.elements(); it.hasNext(); ) {
            JsonNode node = it.next();
            availableProjects.put(node.get("id").asText(), new Project(
                    course_id,
                    node.get("id").asText(),
                    node.get("name").asText(),
                    node.get("description").asText(),
                    node.get("created_at").asText()
            ));
        }

        List<String> editedActiveProject = new ArrayList<>();
        for (Iterator<JsonNode> it = activeProjects.elements(); it.hasNext(); ) {
            JsonNode project = it.next();
            editedActiveProject.add(project.get("id").asText());
        }

        projectService.processActiveProjects(editedActiveProject, availableProjects, course_id);
    }
}
