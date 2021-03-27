package com.group13.tcsprojectgrading.repositories.submissions;

import com.group13.tcsprojectgrading.models.submissions.Submission;
import com.group13.tcsprojectgrading.models.submissions.SubmissionAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SubmissionAttachmentsRepository extends JpaRepository<SubmissionAttachment, UUID> {
    public List<SubmissionAttachment> findSubmissionAttachmentsBySubmission(Submission submission);
}
