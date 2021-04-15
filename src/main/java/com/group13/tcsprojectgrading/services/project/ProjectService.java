package com.group13.tcsprojectgrading.services.project;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.services.gmail.Gmail;
import com.group13.tcsprojectgrading.canvas.api.CanvasApi;
import com.group13.tcsprojectgrading.controllers.CanvasFeedbackUtils;
import com.group13.tcsprojectgrading.controllers.ExcelUtils;
import com.group13.tcsprojectgrading.controllers.PdfRubricUtils;
import com.group13.tcsprojectgrading.controllers.PdfUtils;
import com.group13.tcsprojectgrading.models.course.CourseParticipation;
import com.group13.tcsprojectgrading.models.feedback.FeedbackLog;
import com.group13.tcsprojectgrading.models.feedback.FeedbackTemplate;
import com.group13.tcsprojectgrading.models.grading.Assessment;
import com.group13.tcsprojectgrading.models.grading.AssessmentLink;
import com.group13.tcsprojectgrading.models.grading.Issue;
import com.group13.tcsprojectgrading.models.permissions.PrivilegeEnum;
import com.group13.tcsprojectgrading.models.permissions.RoleEnum;
import com.group13.tcsprojectgrading.models.project.Project;
import com.group13.tcsprojectgrading.models.rubric.Rubric;
import com.group13.tcsprojectgrading.models.rubric.RubricHistory;
import com.group13.tcsprojectgrading.models.rubric.RubricUpdate;
import com.group13.tcsprojectgrading.models.submissions.Label;
import com.group13.tcsprojectgrading.models.submissions.Submission;
import com.group13.tcsprojectgrading.models.submissions.SubmissionAttachment;
import com.group13.tcsprojectgrading.models.submissions.SubmissionComment;
import com.group13.tcsprojectgrading.models.user.User;
import com.group13.tcsprojectgrading.repositories.grading.IssueRepository;
import com.group13.tcsprojectgrading.repositories.project.ProjectRepository;
import com.group13.tcsprojectgrading.repositories.course.CourseParticipationRepository;
import com.group13.tcsprojectgrading.repositories.submissions.LabelRepository;
import com.group13.tcsprojectgrading.services.Json;
import com.group13.tcsprojectgrading.services.feedback.FeedbackService;
import com.group13.tcsprojectgrading.services.user.ActivityService;
import com.group13.tcsprojectgrading.services.user.UserService;
import com.group13.tcsprojectgrading.services.course.CourseService;
import com.group13.tcsprojectgrading.services.graders.GradingParticipationService;
import com.group13.tcsprojectgrading.services.grading.AssessmentService;
import com.group13.tcsprojectgrading.services.permissions.ProjectRoleService;
import com.group13.tcsprojectgrading.services.permissions.RoleService;
import com.group13.tcsprojectgrading.services.rubric.RubricService;
import com.group13.tcsprojectgrading.services.settings.SettingsService;
import com.group13.tcsprojectgrading.services.submissions.SubmissionDetailsService;
import com.group13.tcsprojectgrading.services.submissions.SubmissionService;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.stream.Collectors;

import static com.group13.tcsprojectgrading.controllers.EmailUtils.createEmailWithAttachment;
import static com.group13.tcsprojectgrading.controllers.EmailUtils.sendMessage;


@Service
public class ProjectService {
    private final ProjectRepository projectRepository;

    private final ProjectRoleService projectRoleService;
    private final RoleService roleService;
//    private final LabelService labelService;
    private final ActivityService activityService;
    private final RubricService rubricService;
    private final GradingParticipationService gradingParticipationService;
    private final UserService userService;
    private final SubmissionService submissionService;
    private final SubmissionDetailsService submissionDetailsService;
    private final AssessmentService assessmentService;
    private final SettingsService settingsService;
    private final CourseService courseService;
    private final FeedbackService feedbackService;

    private final CourseParticipationRepository courseParticipationRepository;
    private final LabelRepository labelRepository;
    private final IssueRepository issueRepository;

    public ProjectService(ProjectRepository projectRepository, ProjectRoleService projectRoleService,
                          RoleService roleService,
//                          LabelService labelService,
                          ActivityService activityService,
                          RubricService rubricService, GradingParticipationService gradingParticipationService, @Lazy UserService userService,
                          @Lazy SubmissionService submissionService, SubmissionDetailsService submissionDetailsService,
                          @Lazy AssessmentService assessmentService, @Lazy CourseService courseService,
                          @Lazy SettingsService settingsService,
                          LabelRepository labelRepository,
                          CourseParticipationRepository courseParticipationRepository, FeedbackService feedbackService, IssueRepository issueRepository) {
        this.projectRepository = projectRepository;
        this.projectRoleService = projectRoleService;
        this.roleService = roleService;
        this.activityService = activityService;
        this.rubricService = rubricService;
        this.gradingParticipationService = gradingParticipationService;
        this.userService = userService;
        this.submissionService = submissionService;
        this.submissionDetailsService = submissionDetailsService;
        this.assessmentService = assessmentService;
        this.settingsService = settingsService;
        this.courseParticipationRepository = courseParticipationRepository;
        this.courseService = courseService;
        this.labelRepository = labelRepository;
        this.feedbackService = feedbackService;
        this.issueRepository = issueRepository;
    }

    /*
    Returns a list of projects belonging to the course.
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public List<Project> getProjectsByCourseId(Long courseId) throws ResponseStatusException{
        return projectRepository.findProjectsByCourse_Id(courseId);
    }

    /*
    Returns user's to-do list (list of projects in which the user has assigned grading tasks).
     */
    // TODO, disabled
    @Transactional
    public List<Project> getToDoList(Long userId) throws ResponseStatusException {
        return this.projectRepository.getToDoList(userId);
    }

    @Transactional
    public List<CourseParticipation> getProjectParticipantsWithSubmissions(Long courseId, Long projectId) throws ResponseStatusException {
        Project project = getProject(projectId);

        if (project == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Project not found"
            );
        }

        return courseService
                .getCourseStudents(courseId)
                .stream()
                .peek(participation -> {
                    participation.setSubmissions(
                            assessmentService.getAssessmentsByProjectAndUser(
                                    projectId,
                                    participation.getId().getUser()
                            )
                                    .stream()
                                    .map(assessmentLink -> assessmentLink.getId().getSubmission())
                                    .sorted(Comparator.comparingLong(Submission::getId))
                                    .collect(Collectors.toList())
                    );
                    AssessmentLink link = assessmentService.findCurrentAssessmentUser(
                            project,
                            participation.getId().getUser()
                    );
                    if (link == null) {
                        return;
                    }
                    Assessment assessment = link.getId().getAssessment();

                    participation.setCurrentAssessment(assessmentService.getAssessmentDetails(assessment.getId(), project));
                })
//                .filter(Objects::nonNull)
                .sorted(Comparator.comparingLong(a -> a.getId().getUser().getId()))
                .collect(Collectors.toList());
    }

    @Transactional
    public CourseParticipation getProjectStudent(Long courseId, Long projectId, Long participantId) throws ResponseStatusException{
        CourseParticipation courseParticipation = courseParticipationRepository.findById_User_IdAndId_Course_Id(participantId, courseId);
        User user = userService.findById(participantId);

        if (courseParticipation == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "courseParticipation not found"
            );
        }

        if (user == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "user not found"
            );
        }

        courseParticipation.setSubmissions(this.submissionService.getSubmissionFromStudents(projectId, user));
        return courseParticipation;
    }

    @Transactional
    public List<Float> getFinalGrades(Long courseId, Long projectId) throws ResponseStatusException {
        Project project = getProject(projectId);

        if (project == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Project not found"
            );
        }

         return courseService
                .getCourseStudents(courseId)
                .stream()
                .map(participation -> {
                    AssessmentLink link = assessmentService.findCurrentAssessmentUser(
                            project,
                            participation.getId().getUser()
                    );
                    if (link == null) return null;
                    Assessment assessment = link.getId().getAssessment();
                    assessment = assessmentService.getAssessmentDetails(assessment.getId(), project);
                    if (assessment.getProgress() < 100) return null;
                    return (assessment.getManualGrade() != null)? assessment.getManualGrade() : assessment.getFinalGrade();
                })
                .filter(Objects::nonNull)
                .sorted()
                .collect(Collectors.toList());
    }

    @Transactional
    public Project getProject(Long projectId) {
        return this.projectRepository.findById(projectId).orElse(null);
    }

    @Transactional(Transactional.TxType.MANDATORY)
    public Project getProjectWithLock(Long projectId) {
        return this.projectRepository.findProjectById(projectId).orElse(null);
    }

    @Transactional
    public String getProject(Long projectId, Long userId) throws IOException, ResponseStatusException {
        Project project = this.projectRepository.findById(projectId).orElse(null);

        if (project == null) {
            return null;
        } else {
            // TODO just check if grader?
            List<PrivilegeEnum> privileges = this.gradingParticipationService.getPrivilegesFromUserIdAndProject(userId, projectId);

            if (privileges == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorised");
            }

            ObjectWriter writer = Json.getObjectWriter(Project.class)
                    .withAttribute("privileges", privileges.stream().map(PrivilegeEnum::getName).collect(Collectors.toList()));
            return writer.writeValueAsString(project);
        }
    }

    @Transactional(rollbackOn = Exception.class)
    public void syncProject(Long projectId, ArrayNode submissionsArray) throws ResponseStatusException {
        // retrieve project
        Project project = getProjectWithLock(projectId);

        // throw error if project does not exist
        if (project == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Project not found"
            );
        }

        Map<Long, List<User>> groupToUsers = new HashMap<>();
        Map<Long, Submission> groupToSubmission = new HashMap<>();
        Map<Long, List<SubmissionComment>> groupToComments = new HashMap<>();
        Map<Long, List<SubmissionAttachment>> groupToAttachments = new HashMap<>();

        // for each submission
        for (JsonNode submissionNode : submissionsArray) {
            System.out.println(submissionNode);

            // 'unsubmitted' submission
            if (submissionNode.get("workflow_state").asText().equals("unsubmitted")) {
                continue;
            }

            // get user who submitted the submission
            User submitter = this.userService.findById(submissionNode.get("user_id").asLong());

            if (submitter == null) {
                continue;
            }

            // get all submission comments
            List<SubmissionComment> submissionComments = new ArrayList<>();

            for (JsonNode node : submissionNode.get("submission_comments")) {
                submissionComments.add(new SubmissionComment(node.toString()));
            }

            // get all submission attachments
            List<SubmissionAttachment> submissionAttachments = new ArrayList<>();

            if (submissionNode.has("attachments")) {
                for (JsonNode node : submissionNode.get("attachments")) {
                    submissionAttachments.add(new SubmissionAttachment(node.toString()));
                }
            }

            // if it's not a group submission
            if (submissionNode.get("group").get("id") == null || submissionNode.get("group").get("id").asText().equals("null")) {
                Date submittedAt;
                if (!submissionNode.has("submitted_at") && !submissionNode.get("submitted_at").isNull()) {
                    TemporalAccessor accessor = DateTimeFormatter.ISO_INSTANT.parse(submissionNode.get("submitted_at").asText());
                    Instant i = Instant.from(accessor);
                    submittedAt = Date.from(i);
                } else {
                    submittedAt = null;
                }

                // create a new submission
                Submission submission = this.submissionService.addNewSubmission(
                        project,
                        submitter,
                        null,
                        submittedAt,
//                        submitter.getName() + " " + submittedAt.toString()
                        submitter.getName()
                );

                // if submission was already stored in db, continue
                if (submission == null) {
                    continue;
                }

                // save comments
                for (SubmissionComment comment : submissionComments) {
                    comment.setSubmission(submission);
                    this.submissionDetailsService.saveComment(comment);
                }

                // save attachments
                for (SubmissionAttachment attachment : submissionAttachments) {
                    attachment.setSubmission(submission);
                    this.submissionDetailsService.saveAttachment(attachment);
                }

                // create an association between the submission, user and assessment
                this.assessmentService.createNewAssessmentWithLink(submission, submitter, project);
            } else {
                if (!groupToSubmission.containsKey(submissionNode.get("group").get("id").asLong())) {
                    Date submittedAt;
                    if (!submissionNode.has("submitted_at") && !submissionNode.get("submitted_at").isNull()) {
                        TemporalAccessor accessor = DateTimeFormatter.ISO_INSTANT.parse(submissionNode.get("submitted_at").asText());
                        Instant i = Instant.from(accessor);
                        submittedAt = Date.from(i);
                    } else {
                        submittedAt = null;
                    }

                    // create a new submission
                    Submission submission = new Submission(
                            submitter,
                            submissionNode.get("group").get("id").asLong(),
                            project,
//                            submissionNode.get("group").get("name").asText() + " on " + submittedAt.toString(),
                            submissionNode.get("group").get("name").asText(),
                            submittedAt
                    );

                    groupToSubmission.put(submissionNode.get("group").get("id").asLong(), submission);

                    List<User> users = new ArrayList<>();
                    users.add(submitter);

                    // populate maps with data
                    groupToUsers.put(submissionNode.get("group").get("id").asLong(), users);
                    groupToComments.put(submissionNode.get("group").get("id").asLong(), submissionComments);
                    groupToAttachments.put(submissionNode.get("group").get("id").asLong(), submissionAttachments);
                } else {
                    groupToUsers.get(submissionNode.get("group").get("id").asLong()).add(submitter);
                }
            }
        }

        for (Map.Entry<Long, Submission> entry : groupToSubmission.entrySet()) {
            Submission submission = this.submissionService.addNewSubmission(entry.getValue());

            // skip if submission is already stored
            if (submission == null) {
                continue;
            }

            for (SubmissionComment comment : groupToComments.get(entry.getKey())) {
                comment.setSubmission(submission);
                this.submissionDetailsService.saveComment(comment);
            }

            for (SubmissionAttachment attachment : groupToAttachments.get(entry.getKey())) {
                attachment.setSubmission(submission);
                this.submissionDetailsService.saveAttachment(attachment);
            }

            Assessment assessment = this.assessmentService.createNewAssessment(project);

            for (User member : groupToUsers.get(entry.getValue().getGroupId())) {
                this.assessmentService.createNewAssessmentWithLink(submission, member, project, assessment);
            }
        }
    }

    /*
    Returns the list of people who participate in the project as graders.
     */
    @Transactional
    public List<User> getProjectGraders(Long projectId) throws IOException, ResponseStatusException {
        Project project = this.projectRepository.findById(projectId).orElse(null);

        if (project == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Project not found"
            );
        }

//        List<PrivilegeEnum> privileges = this.gradingParticipationService.getPrivilegesFromUserIdAndProject(userId, projectId);
//
//        if (privileges == null) {
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorised");
//        }

        return this.gradingParticipationService.getProjectGradersWithSubmissions(projectId);
    }

    /*
    Saves the passed list of users as graders for the project.
    (can be replaced by a more efficient version)
     */
    @Transactional
    public List<User> saveProjectGraders(Long projectId, List<User> graders, Long userId) throws IOException {

        //Obtain a write lock on project
        Project project = this.projectRepository.findProjectById(projectId).orElse(null);

        if (project == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Project not found"
            );
        }

        if  (!graders.contains(new User(userId))) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Self must be explicitly included as a grader"
            );
        }

////        Obtain a locks on all graders in the project
////        TODO should more efficient by only lock the "would be" affected only
//        this.gradingParticipationService.getLocksOnAllProjectGraders(project);

        // move all submissions of the removed graders to 'unassigned' (i.e. all graders that are not in the selected list)
        // for all submissions, if submission's grader is NOT in USERS, set grader to null
        this.submissionService.dissociateSubmissionsFromUsers(graders);

        List<User> existing = this.gradingParticipationService
                .getGradingParticipationFromProject(project)
                .stream()
//                .filter(i -> i.getRole().getName().equals(RoleEnum.TEACHER.getName()))
                .map(i -> i.getId().getUser())
                .collect(Collectors.toList());
//        System.out.println(existing.size());

        // remove all graders
        this.gradingParticipationService.deleteAllNonTeacherGradingParticipationByProject(projectId);

        // add new graders
        this.gradingParticipationService.addUsersAsGraders(
                graders.stream().filter(user -> !existing.contains(user)).collect(Collectors.toList())
                , project);

        return this.getProjectGraders(projectId);
    }

    /*
    Update the rubric with patches (patches are applied sequentially).
     */
    @Transactional(rollbackOn = Exception.class)
    public void updateRubric(Long projectId, JsonNode patch, Long version) throws JsonProcessingException, ResponseStatusException {
        Project project = getProject(projectId);
        if (project == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "project not found"
            );
        }

        System.out.println("Updating the rubric of project " + projectId + ".");
        Rubric rubric = this.rubricService.getRubricAndLock(projectId);
        if (!rubric.getVersion().equals(version)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "rubric is not up-to-date"
            );
        }
        // apply update and mark affected submissions
        Rubric rubricPatched = this.rubricService.applyUpdate(patch, rubric);

        System.out.println("Commit the rubric of project " + projectId + ".");
        this.rubricService.saveRubric(rubricPatched);

        // store update
        RubricHistory history = this.rubricService.getHistory(projectId);
        if (history == null) {
            history = new RubricHistory(projectId);
        }

        history.getHistory().add(new RubricUpdate(patch));
        this.rubricService.storeHistory(history);

        System.out.println("Updating the rubric of project " + projectId + " finished successfully.");
    }

    /*
    Returns the rubric of the project.
     */
    @Transactional
    public String getRubric(Long projectId) throws JsonProcessingException, ResponseStatusException {
        Project project = getProject(projectId);
        if (project == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "project not found"
            );
        }

        Rubric rubric = rubricService.getRubricById(projectId);

        if (rubric == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "rubric not found"
            );
        } else {
            return Json.getObjectWriter().writeValueAsString(rubric);
        }
    }

    /*
    Returns a list of labels that were created within the project.
     */
    @Transactional(rollbackOn = Exception.class)
    public String importRubric(Long projectId, String rubricJson) throws ResponseStatusException {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Rubric rubric = objectMapper.readValue(rubricJson, Rubric.class);
            rubric.setId(projectId);
            return rubricService.importRubric(rubric);
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "cant parse rubric"
            );
        }
    }

    @Transactional
    public byte[] getRubricFile(Long projectId) throws JsonProcessingException, ResponseStatusException {
        Project project = getProject(projectId);
        if (project == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "project not found"
            );
        }

        Rubric rubric = rubricService.getRubricById(projectId);

        if (rubric == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "rubric not found"
            );
        } else {
            return Json.getObjectWriter().writeValueAsString(rubric).getBytes(StandardCharsets.UTF_8);
        }
    }

    @Transactional
    public List<Label> getProjectLabels(Long projectId) throws ResponseStatusException {
        Project project = getProject(projectId);

        if (project == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Project not found"
            );
        }

        return this.labelRepository.findByProjectId(projectId);
    }

    @Transactional(rollbackOn = Exception.class)
    public Label saveProjectLabel(Label label, Long projectId) throws ResponseStatusException {
        Project project = getProjectWithLock(projectId);

        if (project == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Project not found"
            );
        }

        label.setProject(project);
        return this.labelRepository.save(label);
    }

    @Transactional
    public byte[] getProjectExcel(Long projectId) throws IOException, ResponseStatusException {
        Project project = getProject(projectId);
        if (project == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "project not found"
            );
        }

        Rubric rubric = rubricService.getRubricById(projectId);

        if (rubric == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "rubric not found"
            );
        }
        Map<User, AssessmentLink> userAssessmentLinkMap = new HashMap<>();

        courseParticipationRepository
                .findById_Course_IdAndRole_Name(
                        project.getCourse().getId(),
                        RoleEnum.STUDENT.getName()
                )
                .stream()
                .map(courseParticipation -> courseParticipation.getId().getUser())
                .forEach(user -> userAssessmentLinkMap.put(user, assessmentService.findCurrentAssessmentUser(project, user)));

//        String FILE_NAME = "src/main/resources/excel.xlsx";
//        File targetFile = new File(FILE_NAME);
//        targetFile.delete();
//        Path newFilePath = Paths.get(FILE_NAME);
//        Files.createFile(newFilePath);
//        FileOutputStream out = new FileOutputStream(targetFile);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ExcelUtils excelUtils = new ExcelUtils(userAssessmentLinkMap, rubric);
        excelUtils.addParticipantsGradePage();
//        excelUtils.getWorkbook().write(out);
//                out.close();
        excelUtils.getWorkbook().write(byteArrayOutputStream);
        byteArrayOutputStream.close();

        return byteArrayOutputStream.toByteArray();
    }

    @Transactional
    public List<FeedbackTemplate> getFeedbackTemplates(Long projectId) throws ResponseStatusException {
        Project project = getProject(projectId);
        if (project == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "project not found"
            );
        }

        return feedbackService.getTemplatesFromProject(project);
    }

    @Transactional(rollbackOn = Exception.class)
    public List<FeedbackTemplate> createFeedbackTemplate(Long projectId, ObjectNode objectNode) throws ResponseStatusException {
        Project project = getProjectWithLock(projectId);
        if (project == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "project not found"
            );
        }

        FeedbackTemplate template = new FeedbackTemplate(
                objectNode.get("name").asText(),
                objectNode.get("subject").asText(),
                objectNode.get("body").asText(),
                project
        );

        feedbackService.addTemplate(template);

        return feedbackService.getTemplatesFromProject(project);
    }

    @Transactional(rollbackOn = Exception.class)
    public List<FeedbackTemplate> updateFeedbackTemplate(Long projectId, Long templateId, ObjectNode objectNode) throws ResponseStatusException {
        Project project = getProjectWithLock(projectId);
        if (project == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "project not found"
            );
        }

        FeedbackTemplate template = new FeedbackTemplate(
                objectNode.get("name").asText(),
                objectNode.get("subject").asText(),
                objectNode.get("body").asText(),
                project
        );
        template.setId(templateId);
        feedbackService.addTemplate(template);

        return feedbackService.getTemplatesFromProject(project);
    }

    @Transactional(rollbackOn = Exception.class)
    public List<FeedbackTemplate> deleteUpdateTemplate(Long projectId, Long templateId) throws ResponseStatusException {
        Project project = getProject(projectId);
        if (project == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "project not found"
            );
        }

        feedbackService.deleteTemplate(templateId);

        return feedbackService.getTemplatesFromProject(project);
    }

    @Transactional
    public List<CourseParticipation> allFinishedGradedUser(Long projectId) throws ResponseStatusException {
        Project project = getProject(projectId);
        if (project == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "project not found"
            );
        }

        Rubric rubric = rubricService.getRubricById(projectId);
        if (rubric == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "rubric not found"
            );
        }

        return courseParticipationRepository
                .findById_Course_IdAndRole_Name(
                        project.getCourse().getId(),
                        RoleEnum.STUDENT.getName()
                )
                .stream()
                .filter(student -> {
                    AssessmentLink assessment = assessmentService
                            .findCurrentAssessmentUser(project, student.getId().getUser());
                    return (assessment != null)
                            && (assessmentService.findActiveGradesForAssignment(assessment.getId().getAssessment()).size() == rubric.getCriterionCount());

                })
                .collect(Collectors.toList());
    }

    @Transactional
    public List<CourseParticipation> allFinishedGradedUserNotSent(Long projectId) throws ResponseStatusException {
        Project project = getProject(projectId);
        if (project == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "project not found"
            );
        }

        Rubric rubric = rubricService.getRubricById(projectId);
        if (rubric == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "rubric not found"
            );
        }

        return courseParticipationRepository
                .findById_Course_IdAndRole_Name(
                        project.getCourse().getId(),
                        RoleEnum.STUDENT.getName()
                )
                .stream()
                .filter(student -> {
                    AssessmentLink assessment = assessmentService
                            .findCurrentAssessmentUser(project, student.getId().getUser());
                    return (assessment != null)
                            && (assessmentService.findActiveGradesForAssignment(assessment.getId().getAssessment()).size() == rubric.getCriterionCount())
                            && (feedbackService.findLogFromLink(assessment).size() == 0)
                            ;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public String uploadGradesToCanvas(Long projectId, CanvasApi canvasApi) throws ResponseStatusException {
        Project project = getProject(projectId);
        if (project == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "project not found"
            );
        }

        Rubric rubric = rubricService.getRubricById(projectId);
        if (rubric == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "rubric not found"
            );
        }
        List<String> queryParams = new ArrayList<>();
        courseParticipationRepository
                .findById_Course_IdAndRole_Name(
                        project.getCourse().getId(),
                        RoleEnum.STUDENT.getName()
                )
                .forEach(student -> {
                    AssessmentLink assessment = assessmentService
                            .findCurrentAssessmentUser(project, student.getId().getUser());
                    if ((assessment != null)
                            && (assessmentService.findActiveGradesForAssignment(assessment.getId().getAssessment()).size() == rubric.getCriterionCount())) {
                        Assessment finalAssessment = assessmentService.getAssessmentDetails(assessment.getId().getAssessment().getId(), project);
                        String studentId = String.valueOf(student.getId().getUser().getId());
                        String finalGrade = String.format("%.1f", (finalAssessment.getManualGrade() != null)? finalAssessment.getManualGrade() : finalAssessment.getFinalGrade());
                        String query = String.format("grade_data[%s][posted_grade]=%s", studentId, finalGrade);
                        System.out.println(query);
                        queryParams.add(query);
                    } else {
                        String studentId = String.valueOf(student.getId().getUser().getId());
                        String finalGrade = "0";
                        String query = String.format("grade_data[%s][posted_grade]=%s", studentId, finalGrade);
                        System.out.println(query);
                        queryParams.add(query);
                    }
                });
        return canvasApi.getCanvasCoursesApi().uploadGrades(project.getCourse().getId(), project.getId(),queryParams);
    }


//    @Transactional
//    public ResponseEntity<byte[]> sendFeedbackPdf(String courseId, String projectId, ObjectNode feedback) throws IOException {
//        Project project = getProjectById(courseId, projectId);
//        if (project == null) {
//            throw new ResponseStatusException(
//                    HttpStatus.NOT_FOUND, "entity not found"
//            );
//        }
//
//        String id = feedback.get("id").asText();
//        boolean isGroup = feedback.get("isGroup").asBoolean();
//        String body = feedback.get("body").asText();
//        String subject = feedback.get("subject").asText();
//
//        String fileName = id + "_" + subject + ".pdf";
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_PDF);
//        headers.setContentDispositionFormData(fileName, fileName);
//        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
////        response.setContentType("blob");
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//
//        PdfWriter pdfWriter = new PdfWriter(byteArrayOutputStream);
//        PdfDocument pdfDocument = new PdfDocument(pdfWriter);
//        Document document = new Document(pdfDocument, PageSize.A4);
//
//        document.getPdfDocument();
//
//        User participant = this.courseService.getCourseParticipant(id, courseId);
//        Submission submission = submissionService.findSubmissionById(body);
//
//        Assessment submissionAssessment = assessmentService.getAssessmentBySubmissionAndParticipant(submission, participant);
//        PdfUtils pdfUtils = new PdfUtils(document, rubricService.getRubricById(projectId), submissionAssessment
//        );
//        pdfUtils.generatePdfOfFeedback();
//        document.close();
//        return new ResponseEntity<byte[]>(byteArrayOutputStream.toByteArray(), headers, HttpStatus.OK);
//    }

    @Transactional
    public byte[] downloadRubric(Long courseId, Long projectId) throws IOException {
        Project project = getProject(projectId);
        if (project == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "entity not found"
            );
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        PdfWriter pdfWriter = new PdfWriter(byteArrayOutputStream);
        PdfDocument pdfDocument = new PdfDocument(pdfWriter);
        Document document = new Document(pdfDocument, PageSize.A4);

        document.getPdfDocument();

        PdfRubricUtils rubricUtils = new PdfRubricUtils(document, rubricService.getRubricById(projectId), project);
        rubricUtils.generateRubrics();

        return byteArrayOutputStream.toByteArray();
    }

    @Transactional
    public List<FeedbackLog> sendFeedbackEmailPdf(Long projectId, Long templateId, boolean isAll,
                                          GoogleAuthorizationCodeFlow flow, Principal principal) throws ResponseStatusException{
        Project project = getProject(projectId);
        if (project == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "project not found"
            );
        }

        FeedbackTemplate template = feedbackService.findTemplateFromId(templateId);

        if (template == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "template not found"
            );
        }

        Rubric rubric = rubricService.getRubricById(projectId);
        if (rubric == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "rubric not found"
            );
        }


        List<CourseParticipation> participations;
        if (!isAll) {
            participations = allFinishedGradedUserNotSent(projectId);
        } else {
            participations = allFinishedGradedUser(projectId);
        }
        Date date = new Date();

        participations.forEach(courseParticipation -> {
            AssessmentLink link = assessmentService.findCurrentAssessmentUser(project, courseParticipation.getId().getUser());

            try {
                if (sendFeedbackEmail(project, link, courseParticipation, template, flow, rubric, principal.getName())) {
                    feedbackService.addLog(new FeedbackLog(
                            date,
                            link,
                            template
                    ));
                }
            } catch (Exception e) {
                if (e instanceof ResponseStatusException) {
                    try {
                        throw e;
                    } catch (IOException | MessagingException ignored) { }
                }
            }
        });

        return feedbackService.getLogs(project);
    }

    @Transactional
    public List<FeedbackLog> sendFeedbackCanvasString(Long projectId, Long templateId, boolean isAll,
                                                  CanvasApi canvasApi, Principal principal) throws ResponseStatusException{
        Project project = getProject(projectId);
        if (project == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "project not found"
            );
        }

        FeedbackTemplate template = feedbackService.findTemplateFromId(templateId);

        if (template == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "template not found"
            );
        }

        Rubric rubric = rubricService.getRubricById(projectId);
        if (rubric == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "rubric not found"
            );
        }


        List<CourseParticipation> participations;
        if (!isAll) {
            participations = allFinishedGradedUserNotSent(projectId);
        } else {
            participations = allFinishedGradedUser(projectId);
        }
        Date date = new Date();

        participations.forEach(courseParticipation -> {
            AssessmentLink link = assessmentService.findCurrentAssessmentUser(project, courseParticipation.getId().getUser());
            if (sendFeedbackCanvas(project, link, courseParticipation, template, canvasApi, rubric, principal.getName())) {
                feedbackService.addLog(new FeedbackLog(
                        date,
                        link,
                        template
                ));
            }
        });

        return feedbackService.getLogs(project);
    }

    @Transactional
    public boolean sendFeedbackEmail(Project project, AssessmentLink link, CourseParticipation participation,
                                FeedbackTemplate template,
                                GoogleAuthorizationCodeFlow flow,
                                Rubric rubric,
                                String teacherId
                                    ) throws IOException, MessagingException, ResponseStatusException {
        System.out.println("getting credential for " + teacherId);
        Credential credential = flow.loadCredential(teacherId);
        if (credential == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "token not found"
            );
        }

        System.out.println("Access token of " + teacherId + ": " + credential.getAccessToken());

        Gmail service = new Gmail.Builder(flow.getTransport(), flow.getJsonFactory(), credential)
                .setApplicationName("Pro Grading")
                .build();

        String FILE_NAME = "src/main/resources/feedback.pdf";
        File targetFile = new File(FILE_NAME);
        targetFile.delete();
        Path newFilePath = Paths.get(FILE_NAME);
        Files.createFile(newFilePath);

        OutputStream out = new FileOutputStream(FILE_NAME);

        PdfWriter pdfWriter = new PdfWriter(out);
        PdfDocument pdfDocument = new PdfDocument(pdfWriter);
        Document document = new Document(pdfDocument, PageSize.A4);

        document.getPdfDocument();

        User participant = participation.getId().getUser();
        Assessment assessment = assessmentService.getAssessmentDetails(link.getId().getAssessment().getId(), project);

        PdfUtils pdfUtils = new PdfUtils(document, rubric, assessment, participation);

        pdfUtils.generatePdfOfFeedback();
        document.close();

        if (participant.getEmail() != null) {
            sendMessage(service, "me", createEmailWithAttachment(
                    participant.getEmail(),
                    "me",
                    template.getSubject(),
                    template.getBody(),
                    new File(FILE_NAME)
            ));
            return true;
        }

        return false;
    }

    @Transactional
    public boolean sendFeedbackCanvas(Project project, AssessmentLink link, CourseParticipation participation,
                                     FeedbackTemplate template,
                                     CanvasApi canvasApi,
                                     Rubric rubric,
                                     String teacherId
    ) {

        try {
            User participant = participation.getId().getUser();
            Assessment assessment = assessmentService.getAssessmentDetails(link.getId().getAssessment().getId(), project);

            String feedback = template.getBody() + "\n";
            CanvasFeedbackUtils canvasFeedbackUtils = new CanvasFeedbackUtils(feedback, rubric, assessment, participation);

            feedback = canvasFeedbackUtils.generateFeedbackString();
            System.out.println(feedback);
            canvasApi.getCanvasUsersApi().sendMessageWithId(
                    participant.getId(),
                    null,
                    template.getSubject(),
                    feedback
            );
        } catch (Exception e) {
            return false;
        }
        return true;
    }

//    @Transactional
//    public List<Issue> getIssuesInProject(Long projectId, Long graderId) throws ResponseStatusException {
//
//        User user = userService.findById(graderId);
//        if (user == null) {
//            throw new ResponseStatusException(
//                    HttpStatus.NOT_FOUND, "user not found"
//            );
//        }
//
//        return issueRepository.findIssuesByCreatorOrAddressee(user, user)
//                .stream()
//                .peek(issue -> {
//                        Submission submission = submissionService
//                                .findSubmissionsFromAssessment(
//                                        issue.getAssessment().getId());
//                        if (submission.getProject().getId().equals(projectId)) {
//                            issue.setSubmission(
//                                    submission
//                            );
//                        }
//                })
//                .filter(issue -> issue.getSubmission() != null)
//                .sorted(Comparator.comparingLong(Issue::getId))
//                .collect(Collectors.toList());
//    }
}
