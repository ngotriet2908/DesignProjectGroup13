package com.group13.tcsprojectgrading.repositories.project;

import com.group13.tcsprojectgrading.models.project.Submission;
import com.group13.tcsprojectgrading.models.project.SubmissionKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmissionRepository extends JpaRepository<Submission, SubmissionKey> {
}
