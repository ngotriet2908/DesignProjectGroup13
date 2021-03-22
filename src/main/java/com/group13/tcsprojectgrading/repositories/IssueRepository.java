package com.group13.tcsprojectgrading.repositories;

import com.group13.tcsprojectgrading.models.Grader;
import com.group13.tcsprojectgrading.models.Issue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface IssueRepository extends JpaRepository<Issue, UUID> {
    public List<Issue> findIssuesByAssessmentId(UUID assessmentId);
    public List<Issue> findIssuesByAddressee(Grader addressee);
    public List<Issue> findIssuesByCreator(Grader creator);

}
