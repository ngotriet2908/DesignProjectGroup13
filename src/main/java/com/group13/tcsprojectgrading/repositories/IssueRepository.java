package com.group13.tcsprojectgrading.repositories;

import com.group13.tcsprojectgrading.models.Grader;
import com.group13.tcsprojectgrading.models.Issue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface IssueRepository extends JpaRepository<Issue, UUID> {
    List<Issue> findIssuesByAssessmentId(UUID assessmentId);
    List<Issue> findIssuesByAddressee(Grader addressee);
    List<Issue> findIssuesByCreator(Grader creator);

}
