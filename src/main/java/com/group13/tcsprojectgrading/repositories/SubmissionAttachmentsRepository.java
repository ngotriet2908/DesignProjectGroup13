package com.group13.tcsprojectgrading.repositories;

import com.group13.tcsprojectgrading.models.Submission;
import com.group13.tcsprojectgrading.models.SubmissionAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SubmissionAttachmentsRepository extends JpaRepository<SubmissionAttachment, UUID> {
    List<SubmissionAttachment> findSubmissionAttachmentsBySubmission(Submission submission);
}
