package com.group13.tcsprojectgrading.services.course;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
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
import com.group13.tcsprojectgrading.services.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.stream.Collectors;

import static com.group13.tcsprojectgrading.controllers.utils.Utils.groupPages;
/**
 * Service handlers operations relating to courses
 */
@Service
public class CourseService {
    private final ProjectService projectService;
    private final RubricService rubricService;
    private final SettingsService settingsService;
    private final RoleService roleService;
    private final ProjectRoleService projectRoleService;

    private final CourseRepository courseRepository;
    private final GradingParticipationRepository gradingParticipationRepository;
    private final ProjectRepository projectRepository;
    private final CourseParticipationRepository courseParticipationRepository;
    private final UserService userService;

    private final CanvasApi canvasApi;

    @Autowired
    public CourseService(CanvasApi canvasApi,
                         CourseRepository courseRepository, UserRepository userRepository,
                         CourseParticipationRepository courseParticipationRepository,
                         ProjectRepository projectRepository, RubricService rubricService,
                         SettingsService settingsService, RoleService roleService,
                         GradingParticipationRepository gradingParticipationRepository,
                         ProjectRoleService projectRoleService, ProjectService projectService,
                         UserService userService) {
        this.courseRepository = courseRepository;
        this.canvasApi = canvasApi;
        this.courseParticipationRepository = courseParticipationRepository;
        this.projectRepository = projectRepository;
        this.rubricService = rubricService;
        this.settingsService = settingsService;
        this.roleService = roleService;
        this.gradingParticipationRepository = gradingParticipationRepository;
        this.projectRoleService = projectRoleService;
        this.projectService = projectService;
        this.userService = userService;
    }

    /**
     * Imports selected courses into the app
     * @param courses courses from canvas
     * @param userId canvas user id
     * @throws IOException not found exception
     * @throws ResponseStatusException response exception
     */
    @Transactional(rollbackOn = Exception.class)
    public void importCourses(ArrayNode courses, Long userId) throws IOException, ResponseStatusException {
        // for each course that was added
        for (JsonNode courseToImport: courses) {
            long id = courseToImport.get("id").asLong();

            // TODO what if no such course exists?
            // should be checked what Canvas returns in such a case, then catch it and return an error
            JsonNode courseCanvas = Json.getObjectReader().readTree(this.canvasApi.getCanvasCoursesApi().getUserCourse(id));

            String name = courseCanvas.get("name").asText();
            String date = courseCanvas.get("start_at").asText();

            TemporalAccessor accessor = DateTimeFormatter.ISO_INSTANT.parse(date);
            Instant i = Instant.from(accessor);
            Date startAt = Date.from(i);

            // create course
            Course course = new Course(id, name, startAt);
            if (courseRepository.existsById(course.getId())) {
                // TODO
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT, "Course already exists"
                );
            }
            this.courseRepository.save(course);

            // check if user that requested action is teacher
            for (JsonNode enrolment: courseCanvas.get("enrollments")) {
                if (enrolment.get("user_id").asLong() == userId &&
                        !RoleEnum.getRoleFromEnrolment(enrolment.get("role").asText()).equals(RoleEnum.TEACHER)) {
                    throw new ResponseStatusException(
                            HttpStatus.UNAUTHORIZED, "You are not a teacher in course " + course.getName());
                }
            }

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

                this.userService.saveUser(user);

                // save user's participation to db
                CourseParticipation participation = new CourseParticipation(user, course, this.roleService.findRoleByName(role.toString()));
                this.courseParticipationRepository.save(participation);
            }
        }
    }

    /**
     * Obtain a lock on a course
     * @param courseId canvas course id
     * @return a course from database
     * @throws ResponseStatusException response exception
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public Course getCourseWithLock(Long courseId) throws ResponseStatusException {
        return this.courseRepository.findCourseById(courseId).orElse(null);
    }

    /**
     * re-synchronise course from canvas
     * @param courseId canvas course id
     * @throws JsonProcessingException json parsing exception
     * @throws ResponseStatusException response exception
     */
    @Transactional(rollbackOn = Exception.class)
    public void syncCourse(Long courseId) throws JsonProcessingException, ResponseStatusException{
        // get course (can also be used to update it if required)
        Course course = getCourseWithLock(courseId);
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

            this.userService.saveUser(user);

            // save user's participation to db
            CourseParticipation participation = new CourseParticipation(user, course, this.roleService.findRoleByName(role.toString()));
            CourseParticipation courseParticipation = courseParticipationRepository.findCourseParticipationById_User_IdAndId_Course_Id(user.getId(), course.getId());
            if (courseParticipation != null) {
                courseParticipation.setRole(this.roleService.findRoleByName(role.toString()));
                this.courseParticipationRepository.save(courseParticipation);
            } else {
                this.courseParticipationRepository.save(participation);
            }
        }
    }

    /**
     * Returns all imported courses that the user participates in.
     * @param userId canvas user id
     * @return list of courses from database
     * @throws JsonProcessingException json parsing exception
     * @throws ResponseStatusException response exception
     */
    @Transactional
    public List<Course> getCourses(Long userId) throws JsonProcessingException, ResponseStatusException{
        User user = this.userService.findById(userId);

        List<Course> courses = new ArrayList<>();
        if (user != null) {
            List<CourseParticipation> userCourseParticipations = user.getCourses();

            for (CourseParticipation course: userCourseParticipations) {
                courses.add(course.getId().getCourse());
            }

            return courses;
        }

        return courses;
    }

    /**
     * Get course with user role
     * @param courseId canvas course id
     * @param userId canvas user id
     * @return a course with user role from database
     * @throws IOException not found exception
     * @throws ResponseStatusException response exception
     */
    @Transactional
    public String getCourse(Long courseId, Long userId) throws IOException, ResponseStatusException{
        Course course = this.courseRepository.findById(courseId).orElse(null);

        if (course == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Course not found"
            );
        }

        RoleEnum role = getCourseRole(courseId, userId);
        if (!role.equals(RoleEnum.TEACHER)) {
            course.setProjects(course.getProjects()
                    .stream()
                    .filter(project -> gradingParticipationRepository
                            .findById_User_IdAndId_Project_Id(userId, project.getId()) != null)
                    .collect(Collectors.toSet()))
            ;
        }

        ObjectWriter writer = Json.getObjectWriter(Course.class).withAttribute("role", role.toString());
        return writer.writeValueAsString(course);
    }

    /**
     * Imports selected projects into the app
     * @param projects projects from canvas
     * @param courseId canvas course id
     * @param userId canvas user id
     * @throws JsonProcessingException json parsing exception
     * @throws ResponseStatusException response exception
     */
    @Transactional(rollbackOn = Exception.class)
    public void importProjects(ArrayNode projects, Long courseId, Long userId) throws JsonProcessingException, ResponseStatusException {
        Course course = getCourseWithLock(courseId);

        if (course == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Course not found"
            );
        }

        // for each project that was added
        for (JsonNode projectToImport: projects) {
            Long projectId = projectToImport.get("id").asLong();
            String projectCanvas = this.canvasApi.getCanvasCoursesApi().getCourseProject(courseId, projectId);
            JsonNode projectCanvasNode = Json.getObjectReader().readTree(projectCanvas);

            // create project
            Project project = new Project(
                    projectId,
                    course,
                    projectCanvasNode.get("name").asText(),
                    projectCanvasNode.get("created_at").asText()
            );

            // TODO fetch submissions here (i.e. do the initial sync)

            // save project
            Project project1 = projectRepository.findProjectsById(projectId);
            if (project1 != null) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT, "Project already exists"
                );
            }
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

    /**
     * get users from course
     * @param courseId canvas course id
     * @return list of users from database
     * @throws ResponseStatusException response exception
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public List<User> getCourseUsers(Long courseId) throws ResponseStatusException {
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

    /**
     * get students from course
     * @param courseId canvas course id
     * @return list of course participant of students from database
     * @throws ResponseStatusException response exception
     */
    @Transactional
    public List<CourseParticipation> getCourseStudents(Long courseId) throws ResponseStatusException {
        Optional<Course> courseOptional = this.courseRepository.findById(courseId);

        if (courseOptional.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Course not found"
            );
        }

        return this.courseParticipationRepository.findById_Course_IdAndRole_Name(courseId, RoleEnum.STUDENT.toString());
    }

    /**
     * get teachers from course
     * @param courseId canvas course id
     * @return list of course participant of teachers from database
     * @throws ResponseStatusException response exception
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public List<CourseParticipation> getCourseTeachers(Long courseId) throws ResponseStatusException {
        Optional<Course> courseOptional = this.courseRepository.findById(courseId);

        if (courseOptional.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Course not found"
            );
        }

        return this.courseParticipationRepository.findById_Course_IdAndRole_Name(courseId, RoleEnum.TEACHER.toString());
    }

    /**
     * get teachers and teaching assistants from course
     * @param courseId canvas course id
     * @return list of course participant of teachers and tas from database
     * @throws ResponseStatusException response exception
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public List<CourseParticipation> getCourseTeachersAndTAs(Long courseId) throws ResponseStatusException {
        Optional<Course> courseOptional = this.courseRepository.findById(courseId);

        if (courseOptional.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Course not found"
            );
        }

        Collection<String> roles = new HashSet<>();
        roles.add(RoleEnum.TEACHER.toString());
        roles.add(RoleEnum.TA.toString());

        return this.courseParticipationRepository.findById_Course_IdAndRole_NameIsIn(courseId, roles);
    }

    /**
     * get teachers and tas from course as user
     * @param courseId canvas course id
     * @return list of users from database
     * @throws ResponseStatusException response exception
     */
    @Transactional
    public List<User> getCourseTeachersAsUsers(Long courseId) {
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

    /**
     * get tas from course as user
     * @param courseId canvas course id
     * @return list of users from database
     * @throws ResponseStatusException response exception
     */
    @Transactional
    public List<User> getCourseTAsAsUsers(Long courseId) {
        Course courseOptional = this.courseRepository.findById(courseId).orElse(null);

        if (courseOptional == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Course not found"
            );
        }

        List<CourseParticipation> courseParticipationList =
                this.courseParticipationRepository.findById_Course_IdAndRole_Name(courseId, RoleEnum.TA.toString());
        List<User> users = new ArrayList<>();
        for (CourseParticipation courseParticipation : courseParticipationList) {
            users.add(courseParticipation.getId().getUser());
        }

        return users;
    }

    /**
     * get teachers and tas from course as user
     * @param courseId canvas course id
     * @return list of users from database
     * @throws ResponseStatusException response exception
     */
    @Transactional
    public List<User> getCourseTeachersAndTAsAsUsers(Long courseId) {
        Course course = this.courseRepository.findById(courseId).orElse(null);

        if (course == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Course not found"
            );
        }

        Collection<String> roles = new HashSet<>();
        roles.add(RoleEnum.TEACHER.toString());
        roles.add(RoleEnum.TA.toString());

        List<CourseParticipation> courseParticipationList = this.courseParticipationRepository.findById_Course_IdAndRole_NameIsIn(courseId, roles);

        List<User> users = new ArrayList<>();
        for (CourseParticipation courseParticipation : courseParticipationList) {
            users.add(courseParticipation.getId().getUser());
        }

        return users;
    }

    /**
     * get info on a user in a course
     * @param courseId canvas course id
     * @param userId canvas user id
     * @return get user from database
     * @throws ResponseStatusException response exception
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public User getCourseUser(Long courseId, Long userId) throws ResponseStatusException {
        Optional<Course> courseOptional = this.courseRepository.findById(courseId);

        if (courseOptional.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Course not found"
            );
        }

        Course course = courseOptional.get();

        CourseParticipation courseParticipation = this.courseParticipationRepository.findById(
                new CourseParticipation.Pk(new User(userId), course)
        ).orElse(null);

        if (courseParticipation == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Course not found"
            );
        }
        return courseParticipation.getId().getUser();
    }

    /**
     * get role of a user in a course
     * @param courseId canvas course id
     * @param userId canvas user id
     * @return get role enum from database
     * @throws ResponseStatusException response exception
     */
    @Transactional
    public RoleEnum getCourseRole(Long courseId, Long userId) throws ResponseStatusException {
        if (!((this.userService.findById(userId) != null) && this.courseRepository.existsById(courseId))) {
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
