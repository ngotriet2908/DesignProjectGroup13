package com.group13.tcsprojectgrading.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.group13.tcsprojectgrading.canvas.api.CanvasApi;
import com.group13.tcsprojectgrading.models.course.Course;
import com.group13.tcsprojectgrading.models.course.CourseParticipation;
import com.group13.tcsprojectgrading.models.permissions.PrivilegeEnum;
import com.group13.tcsprojectgrading.models.permissions.RoleEnum;
import com.group13.tcsprojectgrading.models.user.User;
import com.group13.tcsprojectgrading.services.project.ProjectService;
import com.group13.tcsprojectgrading.services.course.CourseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import static com.group13.tcsprojectgrading.controllers.Utils.groupPages;

import java.io.IOException;
import java.security.Principal;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/courses")
class CourseController {
    private final CanvasApi canvasApi;
    private final CourseService courseService;
    private final ProjectService projectService;

    public CourseController(CanvasApi canvasApi, CourseService courseService, ProjectService projectService) {
        this.canvasApi = canvasApi;
        this.courseService = courseService;
        this.projectService = projectService;
    }

    /*
    Returns the list of courses that were imported and are available in the app's database
     */
    @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json")
    protected ResponseEntity<List<Course>> getCourses(Principal principal) throws JsonProcessingException {
        List<Course> courses = this.courseService.getCourses(Long.valueOf(principal.getName()));
        return new ResponseEntity<>(courses, HttpStatus.OK);
    }

    /*
    Imports selected courses
     */
    @RequestMapping(value = "", method = RequestMethod.POST)
    protected ResponseEntity<?> importCourses(@RequestBody ArrayNode courses,
                                              Principal principal) throws IOException, ParseException {
        // TODO check if teacher is performing this action

        // import courses
        this.courseService.importCourses(courses, Long.valueOf(principal.getName()));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /*
    Resyncs the course
     */
    @RequestMapping(value = "/{courseId}/sync", method = RequestMethod.POST)
    protected ResponseEntity<?> syncCourse(@PathVariable Long courseId) throws JsonProcessingException {
        // TODO check if teacher is performing this action

        // sync courses
        this.courseService.syncCourse(courseId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /*
    Returns all courses that the user can import (i.e. the list of courses from Canvas)
     */
    @RequestMapping(value = "/all", method = RequestMethod.GET, produces = "application/json")
    protected ResponseEntity<ArrayNode> canvasCourses() throws JsonProcessingException {
        List<String> response = this.canvasApi.getCanvasCoursesApi().getUserCourseList();

        if (response == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        ObjectMapper mapper = new ObjectMapper();
        ArrayNode courses = groupPages(response);
        ArrayNode coursesNode = mapper.createArrayNode();

        for (JsonNode course: courses) {
            RoleEnum role = RoleEnum.getRoleFromEnrolment(course.get("enrollments").get(0).get("role").asText());

            if (role.equals(RoleEnum.TEACHER) || role.equals(RoleEnum.TA)) {
                coursesNode.add(course);
            }
        }

        return new ResponseEntity<>(coursesNode, HttpStatus.OK);
    }

    /*
    Returns single course's details with user's role in the course and course's project list
     */
    @RequestMapping(value = "/{courseId}", method = RequestMethod.GET, produces = "application/json")
    protected String getCourse(@PathVariable Long courseId, Principal principal) throws IOException {
        RoleEnum roleEnum = this.courseService.getCourseRole(courseId, Long.valueOf(principal.getName()));

        if (!(roleEnum != null && !roleEnum.equals(RoleEnum.STUDENT))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
        }

        return this.courseService.getCourse(courseId, Long.valueOf(principal.getName()));
    }

    /*
    Returns all projects that the user can import (i.e. the list of course's projects from Canvas)
     */
    @RequestMapping(value = "/{courseId}/projects/all", method = RequestMethod.GET, produces = "application/json")
    protected ResponseEntity<ArrayNode> getCanvasProjects(@PathVariable Long courseId, Principal principal) throws JsonProcessingException {
        List<String> projects = this.canvasApi.getCanvasCoursesApi().getCourseProjects(courseId);

        if (projects == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        ArrayNode projectsNode = groupPages(projects);
        return new ResponseEntity<>(projectsNode, HttpStatus.OK);
    }

    /*
    Imports selected projects.
     */
    @RequestMapping(value = "/{courseId}/projects", method = RequestMethod.POST)
    protected ResponseEntity<?> importProjects(
            @PathVariable Long courseId,
            @RequestBody ArrayNode projects,
            Principal principal
    ) throws JsonProcessingException {
        RoleEnum roleEnum = this.courseService.getCourseRole(courseId, Long.valueOf(principal.getName()));
        if (!(roleEnum != null && roleEnum.equals(RoleEnum.TEACHER))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        // import projects
        this.courseService.importProjects(projects, courseId, Long.valueOf(principal.getName()));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /*
    Returns all teachers and TAs participating in the course.
     */
    @RequestMapping(value = "/{courseId}/graders", method = RequestMethod.GET, produces = "application/json")
    protected ResponseEntity<?> getCourseUsers(
            @PathVariable Long courseId,
            @RequestParam(required = false) Long id,
            Principal principal,
            @RequestParam(name = "ta", required = false) String tas
    ) {

        RoleEnum roleEnum = this.courseService.getCourseRole(courseId, Long.valueOf(principal.getName()));
        if (!(roleEnum != null &&
                ((roleEnum.equals(RoleEnum.TEACHER)) ||
                (roleEnum.equals(RoleEnum.TA_GRADING)))
        )) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        if (tas != null && tas.equals("true")) {
            List<User> graders = this.courseService.getCourseTAsAsUsers(courseId);
            return new ResponseEntity<>(graders, HttpStatus.OK);
        } else {
            List<User> graders = this.courseService.getCourseTeachersAndTAsAsUsers(courseId);
            return new ResponseEntity<>(graders, HttpStatus.OK);
        }

    }
}
