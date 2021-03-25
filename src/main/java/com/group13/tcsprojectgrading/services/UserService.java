package com.group13.tcsprojectgrading.services;

import com.group13.tcsprojectgrading.models.Submission;
import com.group13.tcsprojectgrading.services.submissions.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class UserService {

    private SubmissionService submissionService;

    @Autowired
    public UserService(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @Transactional
    public List<Submission> findSubmissionsForGraderAll(String graderId) {
        return submissionService.findSubmissionsForGraderAll(graderId);
    }
}
