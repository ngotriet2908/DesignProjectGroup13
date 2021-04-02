package com.group13.tcsprojectgrading.services.grading;

import com.group13.tcsprojectgrading.models.user.User;
import com.group13.tcsprojectgrading.models.grading.Issue;
import com.group13.tcsprojectgrading.repositories.grading.IssueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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

    @Transactional(value = Transactional.TxType.MANDATORY)
    public Issue findById(UUID id) {
        return repository.findById(id).orElse(null);
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public Issue saveIssue(Issue issue) {
        return repository.save(issue);
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public List<Issue> findIssuesByAssessment(Long assessmentId) {
        return repository.findIssuesByAssessmentId(assessmentId);
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public List<Issue> findIssuesByCreator(User creator) {
        return repository.findIssuesByCreator(creator);
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public List<Issue> findIssuesByAddressee(User addressee) {
        return repository.findIssuesByAddressee(addressee);
    }
}
