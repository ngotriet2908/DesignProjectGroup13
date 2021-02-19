package com.group13.tcsprojectgrading.repository.project;

import com.group13.tcsprojectgrading.model.project.Submission;
import com.group13.tcsprojectgrading.model.project.SubmissionKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmissionRepository extends JpaRepository<Submission, SubmissionKey> {
}
