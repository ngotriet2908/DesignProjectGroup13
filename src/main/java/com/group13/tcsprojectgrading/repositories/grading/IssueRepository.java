package com.group13.tcsprojectgrading.repositories.grading;

import com.group13.tcsprojectgrading.models.graders.Grader;
import com.group13.tcsprojectgrading.models.submissions.Issue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface IssueRepository extends JpaRepository<Issue, UUID> {
    public List<Issue> findIssuesByAssessmentId(UUID assessmentId);
    public List<Issue> findIssuesByAddressee(Grader addressee);
    public List<Issue> findIssuesByCreator(Grader creator);

}
