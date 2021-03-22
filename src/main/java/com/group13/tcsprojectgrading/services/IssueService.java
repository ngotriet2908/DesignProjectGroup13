package com.group13.tcsprojectgrading.services;

import com.group13.tcsprojectgrading.models.Grader;
import com.group13.tcsprojectgrading.models.Issue;
import com.group13.tcsprojectgrading.repositories.IssueRepository;
import com.group13.tcsprojectgrading.services.grading.AssessmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class IssueService {
    private final AssessmentService assessmentService;
    private final IssueRepository repository;

    @Autowired
    public IssueService(AssessmentService assessmentService, IssueRepository repository) {
        this.assessmentService = assessmentService;
        this.repository = repository;
    }

    public Issue findById(UUID id) {
        return repository.findById(id).orElse(null);
    }

    public Issue saveIssue(Issue issue) {
        return repository.save(issue);
    }

    public List<Issue> findIssuesByAssessment(UUID assessmentId) {
        return repository.findIssuesByAssessmentId(assessmentId);
    }

    public List<Issue> findIssuesByCreator(Grader creator) {
        return repository.findIssuesByCreator(creator);
    }

    public List<Issue> findIssuesByAddressee(Grader addressee) {
        return repository.findIssuesByAddressee(addressee);
    }
}
