package com.group13.tcsprojectgrading.services.grading;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.group13.tcsprojectgrading.models.course.CourseParticipation;
import com.group13.tcsprojectgrading.models.graders.GradingParticipation;
import com.group13.tcsprojectgrading.models.grading.*;
import com.group13.tcsprojectgrading.models.project.Project;
import com.group13.tcsprojectgrading.models.rubric.Element;
import com.group13.tcsprojectgrading.models.rubric.Rubric;
import com.group13.tcsprojectgrading.models.user.User;
import com.group13.tcsprojectgrading.models.permissions.PrivilegeEnum;
import com.group13.tcsprojectgrading.models.submissions.Submission;
import com.group13.tcsprojectgrading.repositories.feedback.FeedbackLogRepository;
import com.group13.tcsprojectgrading.repositories.grading.*;
import com.group13.tcsprojectgrading.repositories.submissions.SubmissionRepository;
import com.group13.tcsprojectgrading.services.graders.GradingParticipationService;
import com.group13.tcsprojectgrading.services.notifications.NotificationService;
import com.group13.tcsprojectgrading.services.project.ProjectService;
import com.group13.tcsprojectgrading.services.rubric.RubricService;
import com.group13.tcsprojectgrading.services.settings.SettingsService;
import com.group13.tcsprojectgrading.services.submissions.SubmissionService;
import com.group13.tcsprojectgrading.services.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static com.group13.tcsprojectgrading.models.permissions.PrivilegeEnum.*;

/**
 * Service handlers operations relating to assessment
 */

@Service
public class AssessmentService {
    private final AssessmentRepository assessmentRepository;
    private final GradeRepository gradeRepository;
    private final AssessmentLinkRepository assessmentLinkRepository;
    private final IssueRepository issueRepository;
    private final IssueStatusRepository issueStatusRepository;
    private final SubmissionRepository submissionRepository;
    private final GradingParticipationService gradingParticipationService;


    private final UserService userService;
    private final SettingsService settingsService;
    private final ProjectService projectService;
    private final SubmissionService submissionService;
    private final NotificationService notificationService;
    private final RubricService rubricService;
    private final FeedbackLogRepository feedbackLogRepository;

    private final ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public AssessmentService(AssessmentLinkRepository assessmentLinkRepository, AssessmentRepository assessmentRepository,
                             @Lazy ProjectService projectService, @Lazy SubmissionService submissionService,
                             GradeRepository gradeRepository, @Lazy UserService userService,
                             @Lazy IssueRepository issueRepository, IssueStatusRepository issueStatusRepository,
                             SubmissionRepository submissionRepository, GradingParticipationService gradingParticipationService, SettingsService settingsService,
                             ApplicationEventPublisher applicationEventPublisher, NotificationService notificationService, RubricService rubricService, FeedbackLogRepository feedbackLogRepository) {
        this.assessmentLinkRepository = assessmentLinkRepository;
        this.assessmentRepository = assessmentRepository;
        this.gradeRepository = gradeRepository;
        this.issueRepository = issueRepository;
        this.issueStatusRepository = issueStatusRepository;

        this.projectService = projectService;
        this.submissionService = submissionService;
        this.userService = userService;
        this.submissionRepository = submissionRepository;
        this.gradingParticipationService = gradingParticipationService;
        this.notificationService = notificationService;
        this.settingsService = settingsService;

        this.applicationEventPublisher = applicationEventPublisher;
        this.rubricService = rubricService;
        this.feedbackLogRepository = feedbackLogRepository;
    }

    /**
     * Creates a new (empty) assessment for the project and links it to the submission and user.
     * @param submission submission entity
     * @param user user entity
     * @param project project entity
     * @return a created link between submission, user and a new assessment
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public AssessmentLink createNewAssessmentWithLink(Submission submission, User user, Project project) {
        Assessment assessment = new Assessment();
        assessment.setProject(project);
        this.assessmentRepository.save(assessment);

        return createNewAssessmentWithLink(submission, user, project, assessment);
    }

    /**
     * Links the assessments to the submission, user and project.
     * @param submission submission entity
     * @param user user entity
     * @param project project entity
     * @param assessment assessment entity
     * @return a created link between submission, user and assessment
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public AssessmentLink createNewAssessmentWithLink(Submission submission, User user, Project project, Assessment assessment) {
        // TODO this check can actually be simplified
        boolean currentExists = this.assessmentLinkRepository.existsById_UserAndId_Submission_ProjectAndCurrentIsTrue(user, project);

        // set as current if current assessment is not set
        AssessmentLink linker = new AssessmentLink(user, submission, assessment, !currentExists);

        this.assessmentLinkRepository.save(linker);
        return linker;
    }

    /**
     * Creates a new (empty) assessment for the project.
     * @param project project entity
     * @return a created assessment
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public Assessment createNewAssessment(Project project) {
        Assessment assessment = new Assessment();
        assessment.setProject(project);
        return this.assessmentRepository.save(assessment);
    }

    /**
     * Create a new assessment in database
     * @param assessment assessment entity
     * @return a created assessment
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public Assessment createNewAssessment(Assessment assessment) {
        Assessment assessment1 = this.assessmentRepository.findAssessmentById(assessment.getId()).orElse(null);
        if (assessment1 != null) {
            return null;
        }
        return this.assessmentRepository.save(assessment);
    }

    /**
     * update an existing assessment in database
     * @param assessment assessment entity
     * @return a updated assessment
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public Assessment updateAssessment(Assessment assessment) {
        Assessment assessment1 = this.assessmentRepository.findAssessmentById(assessment.getId()).orElse(null);
        if (assessment1 == null) {
            return null;
        }
        return this.assessmentRepository.save(assessment);
    }

    /**
     * Create a cloned assessment and move given user to that asessment
     * @param submission submission entity
     * @param assessment assessment entity that are being cloned
     * @param participation student that will be moved
     * @return a created link between submission, user and assessment
     * @throws JsonProcessingException json parsing exception
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public AssessmentLink cloneAssessment(Submission submission, Assessment assessment, CourseParticipation participation) throws JsonProcessingException {
        AssessmentLink link = getAssessmentLinkForUser(submission.getId(), participation.getId().getUser().getId());

        AssessmentLink link1 = new AssessmentLink(
                link.getId().getUser(),
                link.getId().getSubmission(),
                assessment,
                link.isCurrent()
        );

        link1.getId().setAssessment(assessment);
        assessmentLinkRepository.delete(link);
        return assessmentLinkRepository.save(link1);
    }

    /**
     * Move user from an assessment to another and create a new assessment
     * @param link current assessment link of user
     * @param assessment new assessment
     * @param assessmentLinks list of previous assessment links
     * @return a created link between submission, user and assessment
     * @throws JsonProcessingException json parsing exception
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public AssessmentLink moveAssessment(AssessmentLink link, Assessment assessment, Set<AssessmentLink> assessmentLinks) throws JsonProcessingException {

        Assessment assessment1 = link.getId().getAssessment();

        AssessmentLink link1 = new AssessmentLink(
                link.getId().getUser(),
                link.getId().getSubmission(),
                assessment,
                link.isCurrent()
        );

        feedbackLogRepository.deleteAllByLink(link);
        assessmentLinkRepository.delete(link);
        if (assessmentLinks.size() == 1) {
            deleteAssessment(assessment1);
        }
        return assessmentLinkRepository.save(link1);
    }

    /**
     * Create a new assessment in the submission and move user into it
     * @param submission submission entity
     * @param user user entity
     * @return a created link between submission, user and assessment
     * @throws JsonProcessingException json parsing exception
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public AssessmentLink createNewAssessment(Submission submission, User user) throws JsonProcessingException {
        Assessment assessment = new Assessment();
        assessment.setProject(submission.getProject());
        assessment = this.assessmentRepository.save(assessment);

        AssessmentLink link = getAssessmentLinkForUser(submission.getId(), user.getId());

        AssessmentLink link1 = new AssessmentLink(
                link.getId().getUser(),
                link.getId().getSubmission(),
                link.getId().getAssessment(),
                link.isCurrent()
        );

        link1.getId().setAssessment(assessment);

        feedbackLogRepository.deleteAllByLink(link);
        assessmentLinkRepository.delete(link);
        return assessmentLinkRepository.save(link1);
    }

    /**
     * Returns the list of people associated with a submission.
     * @param submission submission entity
     * @return list of users
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public Set<User> getSubmissionMembers(Submission submission) {
        Set<AssessmentLink> links = this.assessmentLinkRepository.findById_Submission(submission);

        Set<User> users = new HashSet<>();
        for (AssessmentLink link: links) {
            User user = link.getId().getUser();
            user.setCurrent(link.isCurrent());
            users.add(user);
        }

        return users;
    }

    /**
     * get assessments as links of a user in a project
     * @param projectId canvas project id
     * @param user user entity
     * @return a list of links between submission, user and assessment
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public Set<AssessmentLink> getAssessmentsByProjectAndUser(Long projectId, User user) {
        return this.assessmentLinkRepository.findAssessmentLinksById_Submission_Project_IdAndId_User(projectId, user);
    }

    /**
     * obtain locks on assessments as links of a user in a project
     * @param projectId canvas project id
     * @param user user entity
     * @return a list of links between submission, user and assessment
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public Set<AssessmentLink> getAssessmentsByProjectAndUserWithLock(Long projectId, User user) {
        return this.assessmentLinkRepository.findAllById_Submission_Project_IdAndId_User(projectId, user);
    }

    /**
     * get assessments as link in a submission
     * @param submission submission entity
     * @return a list of links between submission, user and assessment
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public Set<AssessmentLink> getAssessmentLinksBySubmission(Submission submission) {
        return this.assessmentLinkRepository.findById_Submission(submission);
    }

    /**
     * obtain locks on assessments as links in a submission
     * @param submission submission entity
     * @return a list of links between submission, user and assessment
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public Set<AssessmentLink> getAssessmentLinksBySubmissionWithLock(Submission submission) {
        return this.assessmentLinkRepository.findAllById_Submission(submission);
    }

    /**
     * Returns the list of assessments of a submission.
     * @param submission submission entity
     * @return list of assessments
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public List<Assessment> getAssessmentsBySubmission(Submission submission) {
        Set<AssessmentLink> links = this.assessmentLinkRepository.findById_Submission(submission);

        List<Assessment> assessments = new ArrayList<>();
        Map<Long, Assessment> idToAssessment = new HashMap<>();
        Map<Long, Set<User>> assessmentToMembers = new HashMap<>();

        for (AssessmentLink link: links) {
            Assessment assessment = link.getId().getAssessment();

            if (assessmentToMembers.containsKey(assessment.getId())) {
                assessmentToMembers.get(assessment.getId()).add(link.getId().getUser());
            } else {
                idToAssessment.put(assessment.getId(), assessment);
                assessmentToMembers.put(assessment.getId(), new HashSet<>());
                assessmentToMembers.get(assessment.getId()).add(link.getId().getUser());
            }
        }

        for (Map.Entry<Long, Set<User>> entry : assessmentToMembers.entrySet()) {
            Assessment assessment = idToAssessment.get(entry.getKey());
            assessment.setMembers(entry.getValue());
            assessments.add(assessment);
        }

        Rubric rubric = rubricService.getRubricById(submission.getProject().getId());
        for (Assessment assessment : assessments) {
            assessment.setIssues(
                    assessment.getIssues()
                            .stream()
                            .filter(issue -> issue.getStatus().equals("unresolved"))
                            .collect(Collectors.toList())
            );
            assessment.setProgress((int) (gradeRepository.findGradesByAssessmentAndIsActiveIsTrue(assessment).size()
                    *1.0/rubric.getCriterionCount()*100));
        }
        return assessments.stream().sorted(Comparator.comparingLong(Assessment::getId)).collect(Collectors.toList());
    }

    /**
     * Returns a single assessment.
     * @param id assessment id
     * @return an assessment
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public Assessment getAssessment(Long id) {
        Assessment assessment =  this.assessmentRepository.findById(id).orElse(null);

        if (assessment == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Assessment not found"
            );
        }

        return assessment;
    }

    /**
     * Returns a single assessment with details (issues, progress, final grade)
     * @param id assessment id
     * @param project project entity
     * @return an assessment
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public Assessment getAssessmentDetails(Long id, Project project) {
        Assessment assessment =  this.assessmentRepository.findById(id).orElse(null);

        if (assessment == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Assessment not found"
            );
        }

        Rubric rubric = rubricService.getRubricById(project.getId());
        List<Element> criteria = rubric.fetchAllCriteria();
        Map<String, Element> criteriaMap = new HashMap<>();
        criteria.forEach(element -> criteriaMap.put(element.getContent().getId(), element));
        List<Grade> grades = gradeRepository.findGradesByAssessmentAndIsActiveIsTrue(assessment);
        assessment.setIssues(issueRepository.findIssuesByAssessmentId(assessment.getId()));
        assessment.setProgress((int)(grades.size() *1.0/rubric.getCriterionCount()*100));

        if (assessment.getManualGrade() != null) {
            assessment.setFinalGrade(assessment.getManualGrade());
            return assessment;
        }

        assessment.setFinalGrade(
                (float) grades.stream()
                        .mapToDouble(grade ->
                                grade.getGrade()*
                                        criteriaMap.get(grade.getCriterionId())
                                                .getContent().getGrade().getWeight()
                        )
                        .reduce(0,
                                (grade, grade2) -> grade += grade2
                        )
        );
        return assessment;
    }

    /**
     * Obtain lock on an assessment
     * @param id assessment id
     * @return an assessment
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public Assessment getAssessmentWithLock(Long id) {
        Assessment assessment =  this.assessmentRepository.findAssessmentById(id).orElse(null);

        if (assessment == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Assessment not found"
            );
        }

        return assessment;
    }

    /**
     * Checks if the user has permissions to retrieve the assessment and returns it if so.
     * @param assessmentId assessment id
     * @param submissionId submission is
     * @param userId canvas user id
     * @param privileges user's privileges
     * @return an assignment
     * @throws JsonProcessingException json parsing exception
     */
    @Transactional
    public Assessment getAssessment(Long assessmentId, Long submissionId, Long userId, List<PrivilegeEnum> privileges) throws JsonProcessingException {
        Submission submission = this.submissionService.getSubmission(submissionId);

        if (submission == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Submission not found");
        }

        if (privileges != null && privileges.contains(PrivilegeEnum.GRADING_READ_SINGLE)) {
            if (submission.getGrader() == null || !submission.getGrader().getId().equals(userId)) {
                throw new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Unauthorised"
                );
            }
        }

       return getAssessmentDetails(assessmentId, submission.getProject());
    }

    /**
     * get assessment as link for user in a submission
     * @param submissionId submission id
     * @param userId canvas user id
     * @return a assessment link
     * @throws JsonProcessingException json parsing exception
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public AssessmentLink getAssessmentLinkForUser(Long submissionId, Long userId) throws JsonProcessingException {

        return assessmentLinkRepository.findAssessmentLinkById_Submission_IdAndAndId_User_Id(submissionId, userId);
    }

    /**
     * obtain a lock on assessment as link for user in a submission
     * @param submissionId submission id
     * @param userId canvas user id
     * @return a assessment link
     * @throws JsonProcessingException json parsing exception
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public AssessmentLink getAssessmentLinkForUserWithLock(Long submissionId, Long userId) throws JsonProcessingException {

        return assessmentLinkRepository.findById_Submission_IdAndAndId_User_Id(submissionId, userId);
    }

    /**
     *  Populates all fields of Grade, saves it, deactivates all existing grades for the criterion and sets the new grade
     *     as active.
     * @param submissionId submission id
     * @param assessmentId assessment id
     * @param grade grade entity from front-end
     * @param graderId grade id
     * @param privileges user's privileges
     * @return created grade
     * @throws ResponseStatusException response exception
     */
    @Transactional(rollbackOn = Exception.class)
    public Grade addGrade(Long submissionId, Long assessmentId, Grade grade, Long graderId, List<PrivilegeEnum> privileges) throws ResponseStatusException {
        Assessment assessment = getAssessmentWithLock(assessmentId);

        if (assessment == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Assessment not found");
        }

        User user = this.userService.findById(graderId);

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }


        Submission submission = submissionRepository.findById(submissionId).orElse(null);
        if (submission == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "submission not found");
        }

        if (privileges.contains(GRADING_WRITE_SINGLE)) {
            GradingParticipation grader = this.gradingParticipationService
                    .getGradingParticipationByUserAndProject(graderId, submission.getProject().getId());
            if (grader == null || submission.getGrader() == null ||
                    !grader.getId().getUser().getId().equals(submission.getGrader().getId())) {
                throw new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Unauthorised"
                );
            }
        }

        Rubric rubric = rubricService.getRubricById(submission.getProject().getId());
        List<Element> criteria = rubric.fetchAllCriteria();
        boolean isCriterionInRubric = false;
        for(Element criterion: criteria) {
            if (criterion.getContent().getId().equals(grade.getCriterionId())) {
                isCriterionInRubric = true;
                break;
            }
        }

        if (!isCriterionInRubric) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "No criterion exist"
            );
        }

        assessment = assessmentRepository.save(assessment);

        grade.setGradedAt(Date.from(Instant.now()));
        grade.setAssessment(assessment);
        grade.setGrader(user);
        this.gradeRepository.findGradesByAssessment_IdAndCriterionId(assessmentId, grade.getCriterionId());
        // deactivate all other grades and set the new one as active
        this.gradeRepository.deactivateAllGrades(assessmentId, grade.getCriterionId());
        grade.setActive(true);

        return this.gradeRepository.save(grade);
    }

    /**
     * add new grade to database
     * @param grade grade entity
     * @return a created grade
     */
    @Transactional(rollbackOn = Exception.class)
    public Grade saveGrade(Grade grade) {
        return this.gradeRepository.save(grade);
    }

    /**
     * Activates grade and deactivates all other grades for the criterion.
     * @param submissionId submission id
     * @param assessmentId assessment id
     * @param graderId grader id
     * @param gradeId grade id
     * @param privileges user's privileges
     * @return activated grade entity
     * @throws ResponseStatusException response exception
     */
    @Transactional(rollbackOn = Exception.class)
    public Grade activateGrade(Long submissionId,
                               Long assessmentId,
                               Long graderId,
                               Long gradeId,
                               List<PrivilegeEnum> privileges
    ) throws ResponseStatusException {

        Submission submission = submissionRepository.findById(submissionId).orElse(null);
        if (submission == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Submission not found");
        }

        if (privileges.contains(GRADING_WRITE_SINGLE)) {
            GradingParticipation grader = this.gradingParticipationService
                    .getGradingParticipationByUserAndProject(graderId, submission.getProject().getId());
            if (grader == null || submission.getGrader() == null ||
                    !grader.getId().getUser().getId().equals(submission.getGrader().getId())) {
                throw new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Unauthorised"
                );
            }
        }

        Grade grade = this.gradeRepository.findGradeById(gradeId).orElse(null);

        if (grade == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Grade not found");
        }

        this.gradeRepository.findGradesByAssessment_IdAndCriterionId(assessmentId, grade.getCriterionId());

        // deactivate all other grades and set the selected one as active
        this.gradeRepository.deactivateAllGrades(grade.getAssessment().getId(), grade.getCriterionId());
        grade.setActive(true);

        return this.gradeRepository.save(grade);
    }

    /**
     * Stores an issue status in the db.
     * @param status status enum
     */
    @Transactional(rollbackOn = Exception.class)
    public void saveIssueStatus(IssueStatusEnum status) {
        if (this.issueStatusRepository.findByName(status.toString()) == null)
            this.issueStatusRepository.save(new IssueStatus(status));
    }

    /**
     * Returns issues associated with the assessment.
     * @param submissionId submission id
     * @param assessmentId assessment id
     * @param graderId grader id
     * @param privileges user's privileges
     * @return a list of issues
     */
    @Transactional
    public List<Issue> getIssues(Long submissionId, Long assessmentId, Long graderId, List<PrivilegeEnum> privileges) {

        Submission submission = submissionRepository.findById(submissionId).orElse(null);
        if (submission == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Submission not found");
        }

        if (privileges.contains(SUBMISSION_READ_SINGLE)) {
            GradingParticipation grader = this.gradingParticipationService
                    .getGradingParticipationByUserAndProject(graderId, submission.getProject().getId());
            if (grader == null || submission.getGrader() == null ||
                    !grader.getId().getUser().getId().equals(submission.getGrader().getId())) {
                throw new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Unauthorised"
                );
            }
        }

        return this.issueRepository.findIssuesByAssessmentId(assessmentId);
    }

    /**
     * Creates an issue and sends an email notification to the recipient.
     * @param issue issue entity
     * @param submissionId submission id
     * @param assessmentId assessment id
     * @param userId canvas user id
     * @param privileges user's privileges
     * @return a created issue
     */
    @Transactional(rollbackOn = Exception.class)
    public Issue createIssue(Issue issue, Long submissionId, Long assessmentId, Long userId, List<PrivilegeEnum> privileges) {

        Submission submission = submissionRepository.findById(submissionId).orElse(null);
        if (submission == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Submission not found");
        }

        if (!privileges.contains(GRADING_WRITE_SINGLE) && !privileges.contains(GRADING_WRITE_ALL)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorised");
        }

        if (privileges.contains(GRADING_WRITE_SINGLE)) {
            GradingParticipation grader = this.gradingParticipationService
                    .getGradingParticipationByUserAndProject(userId, submission.getProject().getId());
            if (grader == null || submission.getGrader() == null ||
                    !grader.getId().getUser().getId().equals(submission.getGrader().getId())) {
                throw new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Unauthorised"
                );
            }
        }

        Assessment assessment = this.getAssessment(assessmentId);

        if (assessment == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Assessment not found");
        }

        User creator = this.userService.findById(userId);
        User addressee = null;
        if (issue.getAddressee() != null) {
            addressee = this.userService.findById(issue.getAddressee().getId());
        }

        issue.setSubmission(submission);
        issue.setProject(submission.getProject());
        issue.setCourse(submission.getProject().getCourse());
        issue.setAssessment(assessment);
        issue.setSolution(null);
        issue.setStatus(this.issueStatusRepository.findByName(IssueStatusEnum.OPEN.toString()));
        issue.setCreator(creator);
        issue.setAddressee(addressee);
        issue = this.issueRepository.save(issue);

        // send an email if user has notifications enabled (if transaction successful)
        if (this.settingsService.getSettings(assessment.getProject().getId(), userId).isIssuesNotificationsEnabled()) {
            this.applicationEventPublisher.publishEvent(new IssueCreatedEvent(addressee, assessment.getProject().getName()));
        }

        return issue;
    }

    /**
     * Changes the status of the issue from "open" to "resolved".
     * @param submissionId submission id
     * @param issueId issue id
     * @param graderId grader id
     * @param solution solution entity
     * @param privileges user's privileges
     * @return a resolved issue
     */
//    TODO send email
    @Transactional(rollbackOn = Exception.class)
    public Issue resolveIssue(Long submissionId, Long issueId, Long graderId, IssueSolution solution, List<PrivilegeEnum> privileges) {

        Submission submission = submissionRepository.findById(submissionId).orElse(null);
        if (submission == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Submission not found");
        }

        if (!privileges.contains(GRADING_WRITE_SINGLE) && !privileges.contains(GRADING_WRITE_ALL)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorised");
        }

        if (privileges.contains(GRADING_WRITE_SINGLE)) {
            GradingParticipation grader = this.gradingParticipationService
                    .getGradingParticipationByUserAndProject(graderId, submission.getProject().getId());
            if (grader == null || submission.getGrader() == null ||
                    !grader.getId().getUser().getId().equals(submission.getGrader().getId())) {
                throw new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Unauthorised"
                );
            }
        }

        Issue issue = this.issueRepository.findIssueById(issueId).orElse(null);

        if (issue == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Issue not found");
        }

        issue.setSolution(solution.getSolution());
        issue.setStatus(this.issueStatusRepository.findByName(IssueStatusEnum.RESOLVED.toString()));
        issue = this.issueRepository.save(issue);
        return issue;
    }

    /**
     * find current assessment as link for user in a project
     * @param project project entity
     * @param user user entity
     * @return an assessment link
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public AssessmentLink findCurrentAssessmentUser(Project project, User user) {
        return assessmentLinkRepository.findById_UserAndId_Submission_ProjectAndCurrentIsTrue(
                user,
                project
        );
    }

    /**
     * find assessment links that are linked to an assessment
     * @param id assessment id
     * @return list of assessment link
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public Set<AssessmentLink> findAssessmentLinksByAssessmentId(Long id) {
        return assessmentLinkRepository.findById_Assessment_Id(id);
    }
    @Transactional(value = Transactional.TxType.MANDATORY)
    public Set<AssessmentLink> findAssessmentLinksByAssessmentIdWithLock(Long id) {
        return assessmentLinkRepository.findAssessmentLinkById_Assessment_Id(id);
    }

    /**
     * get active grades for an assessment
     * @param assessment assessment entity
     * @return list of grades
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public List<Grade> findActiveGradesForAssignment(Assessment assessment) {
        return gradeRepository.findGradesByAssessmentAndIsActiveIsTrue(assessment);
    }


//
//    @Transactional(value = Transactional.TxType.MANDATORY)
//    public void saveAssessment(Assessment assessment) {
//        this.assessmentRepository.save(assessment);
//    }

    /**
     * remove assessment from database
     * @param assessment assessment entity
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public void deleteAssessment(Assessment assessment) {
        assessment.getGrades().forEach(gradeRepository::delete);
        this.assessmentRepository.delete(assessment);
    }

    /**
     * delete assessment linker from database and remove all logs
     * @param assessmentLink assessment link entity
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public void deleteAssessmentLinker(AssessmentLink assessmentLink) {
        feedbackLogRepository.deleteAllByLink(assessmentLink);
        this.assessmentLinkRepository.delete(assessmentLink);
    }

    /**
     * save assessment link to database
     * @param assessmentLink assessment link entity
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public void saveInfoAssessment(AssessmentLink assessmentLink) {
        this.assessmentLinkRepository.save(assessmentLink);
    }

    /**
     * save assessment links to database
     * @param links assessment link entities
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public void saveInfoAssessments(Set<AssessmentLink> links) {
        this.assessmentLinkRepository.saveAll(links);
    }

    /**
     * create new assessment link if not exist
     * @param assessmentLink assessment link entity
     * @return a created assessment link
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public AssessmentLink createNewLinkIfNotExist(AssessmentLink assessmentLink) {
        if (assessmentLinkRepository.findById(assessmentLink.getId()).isPresent()) return null;
        return this.assessmentLinkRepository.save(assessmentLink);
    }

    /**
     * get issues in an assessment
     * @param assessmentLink assessment link entity
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public void findIssuesByAssessment(AssessmentLink assessmentLink) {
        this.assessmentLinkRepository.save(assessmentLink);
    }
//    @Transactional(value = Transactional.TxType.MANDATORY)
//    public CurrentAssessment getCurrentAssessment(User user, Project project) {
//        return this.currentAssessmentRepository.findById_UserAndId_Project(user, project);
//    }
//
//    @Transactional(value = Transactional.TxType.MANDATORY)
//    public void setCurrentAssessment(User user, Project project, Assessment assessment) {
//        this.currentAssessmentRepository.save(new CurrentAssessment(user, project, assessment));
//    }


//    public int calculateFinalGrade(Rubric rubric, Assessment assessment) {
//        List<Element> criteria = rubric.fetchAllCriteria();
//        int total = 0;
//
//        for (Element criterion: criteria) {
//            CriterionGrade grades = assessment.getGrades().get(criterion.getContent().getId());
//            Grade grade = grades.getHistory().get(grades.getActive());
//            total += grade.getGrade() * criterion.getContent().getGrade().getWeight();
//        }
//
//        return total;
//    }

    /*
    TODO can be changed to catch any email-related events
     */

    /**
     * A custom even that should fire when an issue is created.
     */
    public static class IssueCreatedEvent extends ApplicationEvent {
        private final String projectName;

        public IssueCreatedEvent(Object source, String projectName) {
            super(source);
            this.projectName = projectName;
        }

        public String getProjectName() {
            return projectName;
        }
    }

    /**
     * Catches IssueCreatedEvent and sends a notification email if issue was created successfully.
     * @param event issues created event
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(IssueCreatedEvent event) {
        try {
            this.notificationService.sendIssueNotification(((User) event.getSource()).getEmail(), event.getProjectName());
            System.out.println("Sending an email.");
        } catch(Exception exception){
            System.out.println("Transaction failed, email won't be sent.");
        }
    }
}
