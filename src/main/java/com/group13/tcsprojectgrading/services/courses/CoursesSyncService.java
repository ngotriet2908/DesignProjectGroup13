package com.group13.tcsprojectgrading.services.courses;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group13.tcsprojectgrading.canvas.api.CanvasApi;
import com.group13.tcsprojectgrading.canvas.api.CanvasEndpoints;
import com.group13.tcsprojectgrading.canvas.api.CanvasRoles;
import com.group13.tcsprojectgrading.models.course.Course;
import com.group13.tcsprojectgrading.models.project.*;
import com.group13.tcsprojectgrading.models.user.Account;
import com.group13.tcsprojectgrading.models.user.Student;
import com.group13.tcsprojectgrading.services.projects.*;
import com.group13.tcsprojectgrading.services.users.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

@Service
public class CoursesSyncService {
    private final AccountService accountService;
    private final CoursesService courseService;
    private final TeacherService teacherService;
    private final TeachingAssistantService teachingAssistantService;
    private final StudentService studentService;
    private final CourseGroupCategoryService courseGroupCategoryService;
    private final CourseGroupService courseGroupService;
    private final GroupParticipantService groupParticipantService;
    private final ProjectService projectService;
    private final SubmissionService submissionService;
    private final AttachmentService attachmentService;

    private final CanvasApi canvasApi;

    @Autowired
    public CoursesSyncService(AccountService accountService,
                               CoursesService courseService,
                               TeacherService teacherService,
                               TeachingAssistantService teachingAssistantService,
                               StudentService studentService,
                               CourseGroupCategoryService courseGroupCategoryService,
                               CourseGroupService courseGroupService,
                               GroupParticipantService groupParticipantService,
                               ProjectService projectService,
                               SubmissionService submissionService,
                               AttachmentService attachmentService,
                               CanvasApi canvasApi) {
        this.accountService = accountService;
        this.courseService = courseService;
        this.teacherService = teacherService;
        this.teachingAssistantService = teachingAssistantService;
        this.studentService = studentService;
        this.courseGroupCategoryService = courseGroupCategoryService;
        this.courseGroupService = courseGroupService;
        this.groupParticipantService = groupParticipantService;
        this.projectService = projectService;
        this.submissionService = submissionService;
        this.attachmentService = attachmentService;

        this.canvasApi = canvasApi;
    }

    public void selfSyncCourseAndUser(String userId) throws JsonProcessingException {
        List<String> coursesList = canvasApi.getCanvasCoursesApi().getUserCourseList();

        for (String coursesArray: coursesList) {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(coursesArray);

            for (Iterator<JsonNode> it = jsonNode.elements(); it.hasNext(); ) {
                JsonNode node = it.next();
                Course course = courseService.addNewCourse(new Course(
                        node.get("id").asText(),
                        node.get("name").asText(),
                        node.get("course_code").asText()));

                JsonNode enrollmentsNode = node.get("enrollments");

                for (Iterator<JsonNode> iter = enrollmentsNode.elements(); iter.hasNext(); ) {
                    JsonNode enroll = iter.next();
                    if (enroll.get("user_id").asText().equals(userId)) {
                        //TODO add the other two roles

                        if (enroll.get("role").asText().equals(CanvasRoles.TEACHER_ROLE)) {
                            Account account = accountService.findAccountById(userId);
                            if (account != null) {
                                teacherService.addNewTeacher(course, account);
                            }
                        } else if (enroll.get("role").asText().equals(CanvasRoles.TA_ROLE)) {
                            Account account = accountService.findAccountById(userId);
                            if (account != null) {
                                teachingAssistantService.addNewTA(course, account);
                            }
                        } else {
                            System.out.println("Invalid role for signed in user: " + node.get("name") + " -> "+ enroll.get("role"));
                        }
                    }
                }
            }
        }
    }

    public void syncCourseProjects(String courseId) throws JsonProcessingException {
        List<String> jsonNodeStringList = canvasApi.getCanvasCoursesApi().getCourseProjects(courseId);
        Course course = courseService.findCourseById(courseId);

        for (String jsonNodeString: jsonNodeStringList) {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonNodeString);

            for (Iterator<JsonNode> it = jsonNode.elements(); it.hasNext(); ) {
                JsonNode node = it.next();

                Project project = new Project(
                        node.get("id").asLong(),
                        node.get("name").asText(),
                        node.get("points_possible").asDouble(),
                        course
                );

                if (node.get("group_category_id") != null && !node.get("group_category_id").asText().equals("null"))
                    project.setGroupCategoryCanvasId(node.get("group_category_id").asLong());

                if (node.get("due_at") != null && !node.get("due_at").asText().equals("null")) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                    dateFormat.setTimeZone(TimeZone.getTimeZone(ZoneId.of("UTC")));
                    try {
                        Date parsedDate = dateFormat.parse(node.get("due_at").asText());
                        Timestamp timestamp = new Timestamp(parsedDate.getTime());
                        project.setDeadline(timestamp);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                projectService.addNewProject(project);
            }
        }
    }

    public void syncParticipants(String courseId) throws JsonProcessingException {
        List<String> jsonNodeStringList = canvasApi.getCanvasCoursesApi().getCourseParticipants(courseId);
        Course course = courseService.findCourseById(courseId);

        for(String jsonNodeString: jsonNodeStringList) {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonNodeString);

            for (Iterator<JsonNode> it = jsonNode.elements(); it.hasNext(); ) {
                JsonNode node = it.next();

                Account account = accountService.addNewAccount(new Account(
                        node.get("id").asText(),
                        node.get("name").asText(),
                        node.get("login_id").asText(),
                        node.get("short_name").asText(),
                        node.get("sortable_name").asText(),
                        node.get("email").asText()
                ));

                JsonNode enrollmentsNode = node.get("enrollments");

                label:
                for (Iterator<JsonNode> iter = enrollmentsNode.elements(); iter.hasNext(); ) {
                    JsonNode enroll = iter.next();
                    if (enroll.get("user_id").asText().equals(account.getId()) &&
                            enroll.get("course_id").asText().equals(course.getId())) {
                        switch (enroll.get("type").asText()) {
                            case CanvasRoles.TEACHER_ROLE:
                                teacherService.addNewTeacher(course, account);
                                break label;
                            case CanvasRoles.STUDENT_ROLE:
                                studentService.addNewStudent(course, account);
                                break label;
                            case CanvasRoles.TA_ROLE:
                                teachingAssistantService.addNewTA(course, account);
                                break label;
                            default:
                                System.out.println("Unknown type: " + enroll.get("type").asText());
                                break label;
                        }
                    }
                }
            }
        }
    }

    public void syncSingleCourseGroup(String courseId) {
        //create single group category
        Course course = courseService.findCourseById(courseId);

        courseGroupCategoryService.addNewCourseGroupCategory(new CourseGroupCategory(
                (CourseGroupService.SINGLE_GROUP_MARK + courseId),
                CourseGroupService.SINGLE_GROUP_NAME_PREFIX + course.getName(),
                course));

        //sync single group
        List<Student> students = studentService.findStudentByCourse(course);
        for (Student student: students) {
            CourseGroup courseGroup = courseGroupService.addCourseGroup(new CourseGroup(
//                    student.getAccount().getId(),
                    CourseGroupService.SINGLE_GROUP_NAME_PREFIX + student.getAccount().getName() + "(" + student.getAccount().getId() + ")",
                    1L,
                    1L,
                    courseGroupCategoryService.findById(CourseGroupService.SINGLE_GROUP_MARK + course.getId())));

            groupParticipantService.addSingleGroupParticipant(student, courseGroup);
        }

    }

    public void syncCourseGroupCategory(String courseId) throws JsonProcessingException {
        //fetch and create group categories from canvas
        Course course = courseService.findCourseById(courseId);

        List<String> jsonNodeStringList = canvasApi.getCanvasCoursesApi().getCourseGroupCategories(courseId);

        for (String jsonNodeString: jsonNodeStringList) {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonNodeString);

            for (Iterator<JsonNode> it = jsonNode.elements(); it.hasNext(); ) {
                JsonNode node = it.next();

                courseGroupCategoryService.addNewCourseGroupCategory(new CourseGroupCategory(
                        node.get("id").asText(),
                        node.get("name").asText(),
                        course));
            }
        }

    }

    public void syncCourseGroups(String courseId) throws JsonProcessingException {
        //fetch and sync groups from canvas
        List<String> jsonNodeStringList = canvasApi.getCanvasCoursesApi().getCourseGroups(courseId);

        for (String jsonNodeString: jsonNodeStringList) {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonNodeString);

            for (Iterator<JsonNode> it = jsonNode.elements(); it.hasNext(); ) {
                JsonNode node = it.next();

                CourseGroupCategory category = courseGroupCategoryService.findById(node.get("group_category_id").asText());
//                System.out.println(category);

                courseGroupService.addCourseGroup(new CourseGroup(
                        node.get("id").asText(),
                        node.get("name").asText(),
                        node.get("max_membership").asLong(),
                        node.get("members_count").asLong(),
                        category));
            }
        }

    }

    public void syncCourseGroupParticipants(String courseId) throws JsonProcessingException {
        List<CourseGroup> courseGroups = courseGroupService.getCourseGroups();
        Course course = courseService.findCourseById(courseId);

        for(CourseGroup courseGroup: courseGroups) {
            if (courseGroup.getCourseGroupCategory().getId().contains(CourseGroupService.SINGLE_GROUP_MARK)) continue;
            if (courseGroup.getMembersCount() == 0) continue;

            List<String> jsonNodeStringList = canvasApi.getCanvasCoursesApi().getGroupMemberships(courseGroup.getCanvasId());

            for (String jsonNodeString: jsonNodeStringList) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(jsonNodeString);

                for (Iterator<JsonNode> it = jsonNode.elements(); it.hasNext(); ) {
                    JsonNode node = it.next();
                    Account account = accountService.findAccountById(node.get("user_id").asText());
                    Student student = studentService.findStudentByAccountAndCourse(account, course);
                    System.out.println("ID: " + student);
                    groupParticipantService.addGroupParticipant(
                            student,
                            courseGroup,
                            node.get("id").asLong()
                    );
                }
            }
        }
    }

    public void syncSubmission(Long assignmentId) throws JsonProcessingException {
        Project project = projectService.findProjectWithId(assignmentId);
        Course course = project.getCourse();

        List<String> jsonNodeStringList = canvasApi.getCanvasCoursesApi().getSubmissions(course.getId(), assignmentId);

        for (String jsonNodeString: jsonNodeStringList) {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonNodeString);

            for (Iterator<JsonNode> it = jsonNode.elements(); it.hasNext(); ) {
                JsonNode node = it.next();
                if (!node.get("workflow_state").asText().equals("submitted")) continue;

                JsonNode groupNode = node.get("group");
                CourseGroup courseGroup;

                if (!groupNode.get("id").asText().equals("null")) {
                    courseGroup = courseGroupService.findCanvasGroup(groupNode.get("id").asText());
                } else {
                    Student student = studentService.findStudentByAccountAndCourse(
                            accountService.findAccountById(node.get("user_id").asText()),
                            course);
                    if (student == null) continue;
                    courseGroup = courseGroupService.findSingleCourseGroup(student);
                }

                Submission submission = new Submission(
                        project,
                        courseGroup);

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                dateFormat.setTimeZone(TimeZone.getTimeZone(ZoneId.of("UTC")));
                try {
                    Date parsedDate = dateFormat.parse(node.get("submitted_at").asText());
                    Timestamp timestamp = new Timestamp(parsedDate.getTime());
                    submission.setSubmission_date(timestamp);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                submissionService.addNewSubmission(submission);

                JsonNode attachmentsNode = node.get("attachments");
                for (Iterator<JsonNode> iter = attachmentsNode.elements(); iter.hasNext(); ) {
                    JsonNode attachmentNode = iter.next();
                    Attachment attachment = new Attachment(
                            attachmentNode.get("id").asLong(),
                            attachmentNode.get("uuid").asText(),
                            attachmentNode.get("display_name").asText(),
                            attachmentNode.get("filename").asText(),
                            attachmentNode.get("content-type").asText(),
                            attachmentNode.get("url").asText(),
                            attachmentNode.get("size").asLong(),
                            submission
                            );
                    attachmentService.addNewAttachment(attachment);
                }
            }
        }
    }
}
