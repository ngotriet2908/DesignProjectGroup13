package com.group13.tcsprojectgrading.services;

import com.group13.tcsprojectgrading.models.Submission;
import com.group13.tcsprojectgrading.models.SubmissionAttachment;
import com.group13.tcsprojectgrading.models.SubmissionComment;
import com.group13.tcsprojectgrading.repositories.SubmissionAttachmentsRepository;
import com.group13.tcsprojectgrading.repositories.SubmissionCommentsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class SubmissionDetailsService {

    private final SubmissionCommentsRepository commentsRepository;
    private final SubmissionAttachmentsRepository attachmentsRepository;

    @Autowired
    public SubmissionDetailsService(SubmissionCommentsRepository commentsRepository, SubmissionAttachmentsRepository attachmentsRepository) {
        this.commentsRepository = commentsRepository;
        this.attachmentsRepository = attachmentsRepository;
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public SubmissionAttachment saveAttachment(SubmissionAttachment submissionAttachment) {
        return attachmentsRepository.save(submissionAttachment);
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public SubmissionComment saveComment(SubmissionComment submissionComment) {
        return commentsRepository.save(submissionComment);
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public List<SubmissionAttachment> getAttachments(Submission submission) {
        return attachmentsRepository.findSubmissionAttachmentsBySubmission(submission);
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public List<SubmissionComment> getComments(Submission submission) {
        return commentsRepository.findSubmissionCommentsBySubmission(submission);
    }
}
