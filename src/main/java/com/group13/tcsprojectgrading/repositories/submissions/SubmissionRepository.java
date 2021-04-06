package com.group13.tcsprojectgrading.repositories.submissions;

import com.group13.tcsprojectgrading.models.submissions.Label;
import com.group13.tcsprojectgrading.models.project.Project;
import com.group13.tcsprojectgrading.models.submissions.Submission;
import com.group13.tcsprojectgrading.models.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.LockModeType;
import java.util.Date;
import java.util.List;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    @Lock(LockModeType.PESSIMISTIC_READ)
    List<Submission> findSubmissionsByProject(Project project);

    @Lock(LockModeType.PESSIMISTIC_READ)
    List<Submission> findByProject_IdAndGraderIsNull(Long project_id);

    List<Submission> findSubmissionsByGrader_Id(Long grader_id);

    @Lock(LockModeType.PESSIMISTIC_READ)
    List<Submission> findSubmissionsByGrader_IdAndProject_Id(Long grader_id, Long project_id);

    @Lock(LockModeType.PESSIMISTIC_READ)
    List<Submission> findSubmissionsByLabels(Label label);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Submission findByProject_IdAndSubmitterId_IdAndSubmittedAtAndGroupId(Long project_id, Long submitter_id, Date submittedAt, Long groupId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    <S extends Submission> S save(S s);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Modifying
    @Query(value="UPDATE Submission s " +
        "SET s.grader=null " +
        "WHERE s.grader NOT IN ?1")
    void dissociateSubmissionsFromUsersNotInList(List<User> graders);
}
