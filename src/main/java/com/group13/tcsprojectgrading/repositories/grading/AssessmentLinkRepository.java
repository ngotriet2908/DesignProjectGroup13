package com.group13.tcsprojectgrading.repositories.grading;

import com.group13.tcsprojectgrading.models.project.Project;
import com.group13.tcsprojectgrading.models.user.User;
import com.group13.tcsprojectgrading.models.grading.AssessmentLink;
import com.group13.tcsprojectgrading.models.submissions.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import javax.persistence.LockModeType;
import javax.transaction.Transactional;
import java.util.Set;

@Transactional(value = Transactional.TxType.MANDATORY)
public interface AssessmentLinkRepository extends JpaRepository<AssessmentLink, AssessmentLink.Pk> {
    // get all user's assessments in the project
    Set<AssessmentLink> findById_UserAndId_Submission_Project(User id_user, Project id_submission_project);

    // get members
    Set<AssessmentLink> findDistinctUserById_Submission(Submission id_submission);

    AssessmentLink findAssessmentLinkById_Submission_IdAndAndId_User_Id(Long submissionId, Long userId);

    @Lock(LockModeType.PESSIMISTIC_READ)
    Set<AssessmentLink> findAssessmentLinksById_Submission_Project_IdAndId_User(Long projectId, User user);

    // get current assessment
    AssessmentLink findById_UserAndId_Submission_ProjectAndCurrentIsTrue(User id_user, Project id_submission_project);

    // check if there is already a current assessment for the user
    boolean existsById_UserAndId_Submission_ProjectAndCurrentIsTrue(User id_user, Project id_submission_project);

    // get all assessments of the submission
    Set<AssessmentLink> findById_Submission(Submission id_submission);

    Set<AssessmentLink> findById_Assessment_Id(Long assessmentId);


//    @Query(value = "SELECT * " +
//            "FROM assessment_link l " +
//            "WHERE l.submission_id = ?1 " +
//            "GROUP BY l.assessment_id",
//            nativeQuery = true)
//    Set<AssessmentLink> findAllAssessmentsBySubmissionId(Long submissionId);
}