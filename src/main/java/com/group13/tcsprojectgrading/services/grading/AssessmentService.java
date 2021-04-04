package com.group13.tcsprojectgrading.services.grading;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.group13.tcsprojectgrading.models.grading.*;
import com.group13.tcsprojectgrading.models.project.Project;
import com.group13.tcsprojectgrading.models.user.User;
import com.group13.tcsprojectgrading.models.permissions.PrivilegeEnum;
import com.group13.tcsprojectgrading.models.submissions.Submission;
import com.group13.tcsprojectgrading.repositories.grading.*;
import com.group13.tcsprojectgrading.services.notifications.NotificationService;
import com.group13.tcsprojectgrading.services.project.ProjectService;
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

@Service
public class AssessmentService {
    private final AssessmentRepository assessmentRepository;
    private final GradeRepository gradeRepository;
    private final AssessmentLinkRepository assessmentLinkRepository;
    private final IssueRepository issueRepository;
    private final IssueStatusRepository issueStatusRepository;

    private final UserService userService;
    private final SettingsService settingsService;
    private final ProjectService projectService;
    private final SubmissionService submissionService;
    private final NotificationService notificationService;

    private final ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public AssessmentService(AssessmentLinkRepository assessmentLinkRepository, AssessmentRepository assessmentRepository,
                             @Lazy ProjectService projectService, @Lazy SubmissionService submissionService,
                             GradeRepository gradeRepository, @Lazy UserService userService,
                             @Lazy IssueRepository issueRepository, IssueStatusRepository issueStatusRepository,
                             SettingsService settingsService,
                             ApplicationEventPublisher applicationEventPublisher, NotificationService notificationService) {
        this.assessmentLinkRepository = assessmentLinkRepository;
        this.assessmentRepository = assessmentRepository;
        this.gradeRepository = gradeRepository;
        this.issueRepository = issueRepository;
        this.issueStatusRepository = issueStatusRepository;

        this.projectService = projectService;
        this.submissionService = submissionService;
        this.userService = userService;
        this.notificationService = notificationService;
        this.settingsService = settingsService;

        this.applicationEventPublisher = applicationEventPublisher;
    }

    /*
    Creates a new (empty) assessment for the project and links it to the submission and user.
     */
    public AssessmentLink createNewAssessmentWithLink(Submission submission, User user, Project project) {
        Assessment assessment = new Assessment();
        assessment.setProject(project);
        this.assessmentRepository.save(assessment);

        return createNewAssessmentWithLink(submission, user, project, assessment);
    }

    /*
    Links the assessments to the submission, user and project.
     */
    public AssessmentLink createNewAssessmentWithLink(Submission submission, User user, Project project, Assessment assessment) {
        // TODO this check can actually be simplified
        boolean currentExists = this.assessmentLinkRepository.existsById_UserAndId_Submission_ProjectAndCurrentIsTrue(user, project);

        // set as current if current assessment is not set
        AssessmentLink linker = new AssessmentLink(user, submission, assessment, !currentExists);

        this.assessmentLinkRepository.save(linker);
        return linker;
    }

    /*
    Creates a new (empty) assessment for the project.
     */
    public Assessment createNewAssessment(Project project) {
        Assessment assessment = new Assessment();
        assessment.setProject(project);
        this.assessmentRepository.save(assessment);
        return assessment;
    }

    /*
    Returns the list of people associated with a submission.
     */
    public Set<User> getSubmissionMembers(Submission submission) {
        Set<AssessmentLink> links = this.assessmentLinkRepository.findDistinctUserById_Submission(submission);

        Set<User> users = new HashSet<>();
        for (AssessmentLink link: links) {
            users.add(link.getId().getUser());
        }

        return users;
    }

    /*
    Returns the list of assessments of a submission.
     */
    public Set<Assessment> getAssessmentsBySubmission(Submission submission) {
        Set<AssessmentLink> links = this.assessmentLinkRepository.findById_Submission(submission);

        Set<Assessment> assessments = new HashSet<>();
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

        return assessments;
    }

    /*
    Returns a single assessment.
     */
    public Assessment getAssessment(Long id) {
        Assessment assessment =  this.assessmentRepository.findById(id).orElse(null);

        if (assessment == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Assessment not found"
            );
        }

        return assessment;
    }

    /*
    Checks if the user has permissions to retrieve the assessment and returns it if so.
     */
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

       return getAssessment(assessmentId);
    }

    /*
    Populates all fields of Grade, saves it, deactivates all existing grades for the criterion and set the new grade
    as active.
     */
    @Transactional
    public Grade addGrade(Long assessmentId, Grade grade, Long userId) {
        Assessment assessment = getAssessment(assessmentId);

        if (assessment == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Assessment not found");
        }

        User user = this.userService.findById(userId);

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        grade.setGradedAt(Date.from(Instant.now()));
        grade.setAssessment(assessment);
        grade.setGrader(user);

        // deactivate all other grades and set the new one as active
        this.gradeRepository.deactivateAllGrades(assessmentId, grade.getCriterionId());
        grade.setActive(true);

        return this.gradeRepository.save(grade);
    }

    /*
    Activates grade and deactivates all other grades for the criterion.
     */
    @Transactional
    public Grade activateGrade(Long gradeId) {
        Grade grade = this.gradeRepository.findById(gradeId).orElse(null);

        if (grade == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Grade not found");
        }

        // deactivate all other grades and set the selected one as active
        this.gradeRepository.deactivateAllGrades(grade.getAssessment().getId(), grade.getCriterionId());
        grade.setActive(true);

        return this.gradeRepository.save(grade);
    }

    /*
    Stores an issue status in the db.
     */
    public void saveIssueStatus(IssueStatusEnum status) {
        this.issueStatusRepository.save(new IssueStatus(status));
    }

    /*
    Returns issues associated with the assessment.
     */
    public List<Issue> getIssues(Long assessmentId) {
        return this.issueRepository.findIssuesByAssessmentId(assessmentId);
    }

    /*
    Creates an issue and sends an email notification to the recipient.
     */
    @Transactional
    public Issue createIssue(Issue issue, Long assessmentId, Long userId) {
        Assessment assessment = this.getAssessment(assessmentId);
        User creator = this.userService.findById(userId);
        User addressee = this.userService.findById(issue.getAddressee().getId());

        issue.setAssessment(assessment);
        issue.setSolution(null);
        issue.setStatus(this.issueStatusRepository.findByName(IssueStatusEnum.OPEN.toString()));
        issue.setCreator(creator);
        // TODO, can the addressee be null?
        issue.setAddressee(addressee);
        issue = this.issueRepository.save(issue);

        // send an email if user has notifications enabled (if transaction successful)
        if (this.settingsService.getSettings(assessment.getProject().getId(), userId).isIssuesNotificationsEnabled()) {
            this.applicationEventPublisher.publishEvent(new IssueCreatedEvent(addressee, assessment.getProject().getName()));
        }

        return issue;
    }

    /*
    Changes the status of the issue from "open" to "resolved".
    TODO send email
     */
    public Issue resolveIssue(Long issueId, IssueSolution solution) {
        Issue issue = this.issueRepository.findById(issueId).orElse(null);

        if (issue == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Issue not found");
        }

        issue.setSolution(solution.getSolution());
        issue.setStatus(this.issueStatusRepository.findByName(IssueStatusEnum.RESOLVED.toString()));
        issue = this.issueRepository.save(issue);
        return issue;
    }
    @Transactional(value = Transactional.TxType.MANDATORY)
    public AssessmentLink findCurrentAssessmentUser(Project project, User user) {
        return assessmentLinkRepository.findById_UserAndId_Submission_ProjectAndCurrentIsTrue(
                user,
                project
        );
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public List<Grade> findActiveGradesForAssignment(Assessment assessment) {
        return gradeRepository.findGradesByAssessmentAndIsActiveIsTrue(assessment);
    }

//    @Transactional(value = Transactional.TxType.MANDATORY)
//    public void saveAssessment(Assessment assessment) {
//        this.assessmentRepository.save(assessment);
//    }

//    @Transactional(value = Transactional.TxType.MANDATORY)
//    public void deleteAssessment(AssessmentLink assessmentLink) {
//        this.assessmentRepository.delete(assessmentLink.getId().getAssessment());
//    }

//    @Transactional(value = Transactional.TxType.MANDATORY)
//    public void deleteAssessment(Assessment assessment) {
//        this.assessmentRepository.delete(assessment);
//    }


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
    A custom even that should fire when an issue is created.
    TODO can be changed to catch any email-related events
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

    /*
    Catches IssueCreatedEvent and sends a notification email if issue was created successfully.
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
