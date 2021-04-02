package com.group13.tcsprojectgrading.repositories.submissions;

import com.group13.tcsprojectgrading.models.submissions.Submission;
import com.group13.tcsprojectgrading.models.submissions.SubmissionComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SubmissionCommentsRepository extends JpaRepository<SubmissionComment, Long> {
    List<SubmissionComment> findSubmissionCommentsBySubmission(Submission submission);
}
