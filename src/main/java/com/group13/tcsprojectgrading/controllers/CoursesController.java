package com.group13.tcsprojectgrading.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.group13.tcsprojectgrading.canvas.api.CanvasApi;
import com.group13.tcsprojectgrading.models.*;
import com.group13.tcsprojectgrading.services.ActivityService;
import com.group13.tcsprojectgrading.services.GraderService;
import com.group13.tcsprojectgrading.services.ProjectService;
import com.group13.tcsprojectgrading.services.TaskService;
import com.group13.tcsprojectgrading.services.rubric.RubricService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.group13.tcsprojectgrading.controllers.Utils.groupPages;

import java.util.*;

@RestController
@RequestMapping("/api/courses")
class CoursesController {
    private final CanvasApi canvasApi;

    private final ActivityService activityService;
    private final RubricService rubricService;
    private final GraderService graderService;
    private final TaskService taskService;
    private final ProjectService projectService;

    @Autowired
    public CoursesController(CanvasApi canvasApi, ActivityService activityService, RubricService rubricService, GraderService graderService, TaskService taskService, ProjectService projectService) {
        this.canvasApi = canvasApi;
        this.activityService = activityService;
        this.rubricService = rubricService;
        this.graderService = graderService;
        this.taskService = taskService;
        this.projectService = projectService;
    }

    @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json")
    protected ResponseEntity<String> courses() {
        List<String> response = this.canvasApi.getCanvasCoursesApi().getUserCourseList();

        if (response == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } else {
            // TODO: the following line sends back only the first batch of the list of courses!!!
            return new ResponseEntity<>(response.get(0), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/{course_id}", method = RequestMethod.GET, produces = "application/json")
    protected ResponseEntity<JsonNode> getCourse(@PathVariable String course_id) throws JsonProcessingException {
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
                            (taskService.getTasksFromId(project).size() > 0);
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
                projectService.deleteProject(project);
            }
        }

        for(String activeProjectId: editedActiveProject) {
            projectService.addNewProject(availableProjects.get(activeProjectId));
        }
    }
}
