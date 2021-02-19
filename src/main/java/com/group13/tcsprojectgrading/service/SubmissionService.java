package com.group13.tcsprojectgrading.service;

import com.group13.tcsprojectgrading.model.project.CourseGroup;
import com.group13.tcsprojectgrading.model.project.Project;
import com.group13.tcsprojectgrading.model.project.Submission;
import com.group13.tcsprojectgrading.repository.project.SubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class SubmissionService {

    private SubmissionRepository repository;

    @Autowired
    public SubmissionService(SubmissionRepository repository) {
        this.repository = repository;
    }

    public List<Submission> getSubmissions() {
        return repository.findAll();
    }

    public void addNewSubmission(Project project, CourseGroup courseGroup) {
        if (!project.getCourse().equals(courseGroup.getCourse())) {
            throw new IllegalArgumentException("project course and group course are not the same" + project + ";" + courseGroup);
        }
        repository.save(new Submission(project, courseGroup));
    }

    public void addNewSubmissionWithSubmissionDate(Project project, CourseGroup courseGroup, Timestamp submissionDate) {
        if (!project.getCourse().equals(courseGroup.getCourse())) {
            throw new IllegalArgumentException("project course and group course are not the same" + project + ";" + courseGroup);
        }
        repository.save(new Submission(project, courseGroup, submissionDate));
    }
}
