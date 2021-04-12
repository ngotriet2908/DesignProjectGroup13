package com.group13.tcsprojectgrading.repositories.grading;

import com.group13.tcsprojectgrading.models.grading.IssueStatus;
import com.group13.tcsprojectgrading.models.user.User;
import com.group13.tcsprojectgrading.models.grading.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IssueRepository extends JpaRepository<Issue, Long> {
    @Lock(LockModeType.PESSIMISTIC_READ)
    List<Issue> findIssuesByAssessmentId(Long assessmentId);

    @Lock(LockModeType.PESSIMISTIC_READ)
    List<Issue> findIssuesByAssessmentIdAndStatus_Name(Long assessmentId, String statusName);

    @Lock(LockModeType.PESSIMISTIC_READ)
    Optional<Issue> findById(Long aLong);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Issue> findIssueById(Long aLong);

    @Lock(LockModeType.PESSIMISTIC_READ)
    List<Issue> findIssuesByAddressee(User addressee);

    @Lock(LockModeType.PESSIMISTIC_READ)
    List<Issue> findIssuesByCreator(User creator);

    @Lock(LockModeType.PESSIMISTIC_READ)
    List<Issue> findIssuesByCreatorOrAddressee(User creator, User addressee);
}
