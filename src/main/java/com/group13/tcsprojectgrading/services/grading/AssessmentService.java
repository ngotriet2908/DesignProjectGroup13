package com.group13.tcsprojectgrading.services.grading;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.group13.tcsprojectgrading.models.project.Project;
import com.group13.tcsprojectgrading.models.user.User;
import com.group13.tcsprojectgrading.models.grading.Assessment;
import com.group13.tcsprojectgrading.models.grading.AssessmentLink;
import com.group13.tcsprojectgrading.models.grading.Grade;
import com.group13.tcsprojectgrading.models.permissions.PrivilegeEnum;
import com.group13.tcsprojectgrading.models.submissions.Submission;
import com.group13.tcsprojectgrading.repositories.grading.AssessmentLinkRepository;
import com.group13.tcsprojectgrading.repositories.grading.AssessmentRepository;
import com.group13.tcsprojectgrading.repositories.grading.GradeRepository;
import com.group13.tcsprojectgrading.services.project.ProjectService;
import com.group13.tcsprojectgrading.services.submissions.SubmissionService;
import com.group13.tcsprojectgrading.services.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.*;

@Service
public class AssessmentService {
    private final AssessmentRepository assessmentRepository;
    private final GradeRepository gradeRepository;
    private final AssessmentLinkRepository assessmentLinkRepository;
    private final UserService userService;

    private final ProjectService projectService;
    private final SubmissionService submissionService;

    @Autowired
    public AssessmentService(AssessmentLinkRepository assessmentLinkRepository, AssessmentRepository assessmentRepository,
                             @Lazy ProjectService projectService, @Lazy SubmissionService submissionService,
                             GradeRepository gradeRepository, @Lazy UserService userService) {
        this.assessmentLinkRepository = assessmentLinkRepository;
        this.assessmentRepository = assessmentRepository;
        this.gradeRepository = gradeRepository;

        this.projectService = projectService;
        this.submissionService = submissionService;
        this.userService = userService;
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
}
