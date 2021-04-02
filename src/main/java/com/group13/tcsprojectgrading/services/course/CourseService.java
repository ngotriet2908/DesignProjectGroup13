package com.group13.tcsprojectgrading.services.course;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.group13.tcsprojectgrading.canvas.api.CanvasApi;
import com.group13.tcsprojectgrading.models.course.Course;
import com.group13.tcsprojectgrading.models.project.Project;
import com.group13.tcsprojectgrading.models.user.User;
import com.group13.tcsprojectgrading.models.graders.GradingParticipation;
import com.group13.tcsprojectgrading.models.course.CourseParticipation;
import com.group13.tcsprojectgrading.models.permissions.Role;
import com.group13.tcsprojectgrading.models.permissions.RoleEnum;
import com.group13.tcsprojectgrading.repositories.course.CourseRepository;
import com.group13.tcsprojectgrading.repositories.project.ProjectRepository;
import com.group13.tcsprojectgrading.repositories.user.UserRepository;
import com.group13.tcsprojectgrading.repositories.graders.GradingParticipationRepository;
import com.group13.tcsprojectgrading.repositories.course.CourseParticipationRepository;
import com.group13.tcsprojectgrading.services.Json;
import com.group13.tcsprojectgrading.services.permissions.ProjectRoleService;
import com.group13.tcsprojectgrading.services.permissions.RoleService;
import com.group13.tcsprojectgrading.services.project.ProjectService;
import com.group13.tcsprojectgrading.services.rubric.RubricService;
import com.group13.tcsprojectgrading.services.settings.SettingsService;
import com.group13.tcsprojectgrading.services.submissions.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.io.IOException;
import java.text.ParseException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.*;

import static com.group13.tcsprojectgrading.controllers.Utils.groupPages;

@Service
public class CourseService {
    private final SubmissionService submissionService;
    private final ProjectService projectService;
    private final RubricService rubricService;
    private final SettingsService settingsService;
    private final RoleService roleService;
    private final ProjectRoleService projectRoleService;

    private final CourseRepository courseRepository;
    private final GradingParticipationRepository gradingParticipationRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final CourseParticipationRepository courseParticipationRepository;

    private final CanvasApi canvasApi;

    @Autowired
    public CourseService(CanvasApi canvasApi, SubmissionService submissionService,
                         CourseRepository courseRepository, UserRepository userRepository,
                         CourseParticipationRepository courseParticipationRepository,
                         ProjectRepository projectRepository, RubricService rubricService,
                         SettingsService settingsService, RoleService roleService,
                         GradingParticipationRepository gradingParticipationRepository,
                         ProjectRoleService projectRoleService, ProjectService projectService
                         ) {
        this.submissionService = submissionService;
        this.courseRepository = courseRepository;
        this.canvasApi = canvasApi;
        this.userRepository = userRepository;
        this.courseParticipationRepository = courseParticipationRepository;
        this.projectRepository = projectRepository;
        this.rubricService = rubricService;
        this.settingsService = settingsService;
        this.roleService = roleService;
        this.gradingParticipationRepository = gradingParticipationRepository;
        this.projectRoleService = projectRoleService;
        this.projectService = projectService;
    }

    /*
    Imports selected courses into the app
     */
    @Transactional
    public void importCourses(ArrayNode courses) throws IOException, ParseException {
        // for each course that was added
        for (JsonNode courseToImport: courses) {
            // TODO: users should submit only course ids, rest info should be fetched here directly
            long id = courseToImport.get("id").asLong();

            String name = courseToImport.get("name").asText();
            String date = courseToImport.get("start_at").asText();

            TemporalAccessor accessor = DateTimeFormatter.ISO_INSTANT.parse(date);
            Instant i = Instant.from(accessor);
            Date startAt = Date.from(i);

            // create course
            Course course = new Course(id, name, startAt);
            this.courseRepository.save(course);

            // fetch users and for each create a) User b) Participation
            ArrayNode usersArray = groupPages(this.canvasApi.getCanvasCoursesApi().getCourseParticipantsWithAvatars(id));

            for (JsonNode userToCreate: usersArray) {
                JsonNode email = userToCreate.get("email");
                JsonNode sid = userToCreate.get("sis_user_id");
                JsonNode avatar = userToCreate.get("avatar_url");

                // save user to db
                User user = new User(
                        userToCreate.get("id").asLong(),
                        userToCreate.get("name").asText(),
                        email != null ? email.asText() : null,
                        sid != null ? sid.asText() : null,
                        avatar != null ? avatar.asText() : null
                );

                // find user's status in the course
                JsonNode enrolmentsNode = userToCreate.get("enrollments");

                RoleEnum role = null;

                for (JsonNode enrolment: enrolmentsNode) {
                    if (enrolment.get("course_id").asLong() == id) {
                        role = RoleEnum.getRoleFromEnrolment(enrolment.get("role").asText());
                    }
                }

                if (role == null) {
                    continue;
                }

                this.userRepository.save(user);

                // save user's participation to db
                CourseParticipation participation = new CourseParticipation(user, course, this.roleService.findRoleByName(role.toString()));
                this.courseParticipationRepository.save(participation);
            }
        }
    }

    @Transactional
    public void syncCourse(Long courseId) throws JsonProcessingException {
        // get course (can also be used to update it if required)
        Course course = this.courseRepository.findById(courseId).orElse(null);

        if (course == null) {
             throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Course not found"
            );
        }

        // fetch participants anew
        ArrayNode usersArray = groupPages(this.canvasApi.getCanvasCoursesApi().getCourseParticipantsWithAvatars(courseId));

        for (JsonNode userToCreate : usersArray) {
            JsonNode email = userToCreate.get("email");
            JsonNode sid = userToCreate.get("sis_user_id");
            JsonNode avatar = userToCreate.get("avatar_url");

            // save user to db
            User user = new User(
                    userToCreate.get("id").asLong(),
                    userToCreate.get("name").asText(),
                    email != null ? email.asText() : null,
                    sid != null ? sid.asText() : null,
                    avatar != null ? avatar.asText() : null
            );

            // find user's status in the course
            JsonNode enrolmentsNode = userToCreate.get("enrollments");

            RoleEnum role = null;

            for (JsonNode enrolment : enrolmentsNode) {
                if (enrolment.get("course_id").asLong() == courseId) {
                    role = RoleEnum.getRoleFromEnrolment(enrolment.get("role").asText());
                }
            }

            if (role == null) {
                continue;
            }

            this.userRepository.save(user);

            // save user's participation to db
            CourseParticipation participation = new CourseParticipation(user, course, this.roleService.findRoleByName(role.toString()));
            this.courseParticipationRepository.save(participation);
        }
    }

    /*
    Returns all imported courses that the user participates in.
     */
    @Transactional
    public List<Course> getCourses(Long userId) throws JsonProcessingException {
        Optional<User> user = this.userRepository.findById(userId);

        List<Course> courses = new ArrayList<>();
        if (user.isPresent()) {
            List<CourseParticipation> userCourseParticipations = user.get().getCourses();

            for (CourseParticipation course: userCourseParticipations) {
                courses.add(course.getId().getCourse());
            }

            return courses;
        }

        return courses;
    }

    @Transactional
    public String getCourse(Long courseId, Long userId) throws IOException {
        Course course = this.courseRepository.findById(courseId).orElse(null);

        if (course == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Course not found"
            );
        }

        RoleEnum role = getCourseRole(courseId, userId);

        ObjectWriter writer = Json.getObjectWriter(Course.class).withAttribute("role", role.toString());
        return writer.writeValueAsString(course);
    }

    /*
    Imports selected projects into the app
     */
    @Transactional
    public void importProjects(ArrayNode projects, Long courseId, Long userId) throws JsonProcessingException {
        Optional<Course> courseOptional = this.courseRepository.findById(courseId);

        if (courseOptional.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Course not found"
            );
        }

        Course course = courseOptional.get();

        ObjectMapper mapper = new ObjectMapper();

        // for each project that was added
        for (JsonNode projectToImport: projects) {
            Long projectId = projectToImport.get("id").asLong();
            String projectCanvas = this.canvasApi.getCanvasCoursesApi().getCourseProject(courseId, projectId);
            JsonNode projectCanvasNode = mapper.readTree(projectCanvas);

            // create project
            Project project = new Project(
                    projectId,
                    course,
                    projectCanvasNode.get("name").asText(),
                    projectCanvasNode.get("created_at").asText()
            );

            // TODO fetch submissions here (i.e. do the initial sync)

            // save project
            this.projectRepository.save(project);

            // create rubric
            this.rubricService.addNewRubric(projectId);

            // for each participant, create settings
            List<CourseParticipation> courseParticipationList = course.getUsers();

            for (CourseParticipation courseParticipation : courseParticipationList) {
                User user = courseParticipation.getId().getUser();

                if (!courseParticipation.getRole().getName().equals(RoleEnum.STUDENT.toString())) {
                    this.settingsService.createSettings(projectId, user.getId());
                }
            }

            // create default project roles
            this.projectRoleService.addDefaultRolesToProject(project);
            Role teacherRole = this.roleService.findRoleByName(RoleEnum.TEACHER.toString());

            // add teachers as graders
            for (CourseParticipation user: getCourseTeachers(courseId)) {
                this.gradingParticipationRepository.save(
                        new GradingParticipation(
                                user.getId().getUser(),
                                project,
                                teacherRole
                        )
                );
            }

            // fetch project's submissions
            List<String> submissions = this.canvasApi.getCanvasCoursesApi().getSubmissionsInfo(courseId, projectId);
            ArrayNode submissionsArray = groupPages(submissions);
            this.projectService.syncProject(projectId, submissionsArray);
        }
    }

    public List<User> getCourseParticipants(Long courseId) {
        Optional<Course> courseOptional = this.courseRepository.findById(courseId);

        if (courseOptional.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Course not found"
            );
        }

        Course course = courseOptional.get();

        List<CourseParticipation> courseParticipationList = course.getUsers();
        List<User> users = new ArrayList<>();
        for (CourseParticipation courseParticipation : courseParticipationList) {
            users.add(courseParticipation.getId().getUser());
        }

        return users;
    }

    public List<CourseParticipation> getCourseTeachers(Long courseId) {
        Optional<Course> courseOptional = this.courseRepository.findById(courseId);

        if (courseOptional.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Course not found"
            );
        }

        return this.courseParticipationRepository.findById_Course_IdAndRole_Name(courseId, RoleEnum.TEACHER.toString());
    }

    public List<User> getCourseTeachersUsers(Long courseId) {
        Optional<Course> courseOptional = this.courseRepository.findById(courseId);

        if (courseOptional.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Course not found"
            );
        }

        List<CourseParticipation> courseParticipationList =
                this.courseParticipationRepository.findById_Course_IdAndRole_Name(courseId, RoleEnum.TEACHER.toString());
        List<User> users = new ArrayList<>();
        for (CourseParticipation courseParticipation : courseParticipationList) {
            users.add(courseParticipation.getId().getUser());
        }

        return users;
    }

    public User getCourseParticipant(Long courseId, Long userId) {
        Optional<Course> courseOptional = this.courseRepository.findById(courseId);

        if (courseOptional.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Course not found"
            );
        }

        Course course = courseOptional.get();

        CourseParticipation courseParticipation = this.courseParticipationRepository.findById(
                new CourseParticipation.Pk(new User(userId), course)
        );

        return courseParticipation.getId().getUser();
    }

    public RoleEnum getCourseRole(Long courseId, Long userId) {
        if (!(this.userRepository.existsById(userId) && this.courseRepository.existsById(courseId))) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Course or user not found"
            );
        }

        CourseParticipation participation = this.courseParticipationRepository.findById_User_IdAndId_Course_Id(userId, courseId);

        if (participation == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "User does not participate in the course"
            );
        }

        return RoleEnum.fromName(participation.getRole().getName());
    }
}