package com.group13.tcsprojectgrading.repositories.grading;

import com.group13.tcsprojectgrading.models.user.User;
import com.group13.tcsprojectgrading.models.grading.Issue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface IssueRepository extends JpaRepository<Issue, UUID> {
    List<Issue> findIssuesByAssessmentId(Long assessmentId);
    List<Issue> findIssuesByAddressee(User addressee);
    List<Issue> findIssuesByCreator(User creator);

}
