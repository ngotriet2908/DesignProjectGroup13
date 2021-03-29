package com.group13.tcsprojectgrading.repositories;

import com.group13.tcsprojectgrading.models.Submission;
import com.group13.tcsprojectgrading.models.SubmissionComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SubmissionCommentsRepository extends JpaRepository<SubmissionComment, UUID> {
    List<SubmissionComment> findSubmissionCommentsBySubmission(Submission submission);
}
