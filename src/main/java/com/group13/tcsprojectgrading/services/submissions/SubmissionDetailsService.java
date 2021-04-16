package com.group13.tcsprojectgrading.services.submissions;

import com.group13.tcsprojectgrading.models.submissions.Submission;
import com.group13.tcsprojectgrading.models.submissions.SubmissionAttachment;
import com.group13.tcsprojectgrading.models.submissions.SubmissionComment;
import com.group13.tcsprojectgrading.repositories.submissions.SubmissionAttachmentsRepository;
import com.group13.tcsprojectgrading.repositories.submissions.SubmissionCommentsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Service handlers operations relating to Submission details
 */
@Service
public class SubmissionDetailsService {
    private final SubmissionCommentsRepository commentsRepository;
    private final SubmissionAttachmentsRepository attachmentsRepository;

    @Autowired
    public SubmissionDetailsService(SubmissionCommentsRepository commentsRepository, SubmissionAttachmentsRepository attachmentsRepository) {
        this.commentsRepository = commentsRepository;
        this.attachmentsRepository = attachmentsRepository;
    }

    /**
     * save submission attachment
     * @param submissionAttachment submission attachment entity
     * @return updated submission Attachtment
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public SubmissionAttachment saveAttachment(SubmissionAttachment submissionAttachment) {
        return attachmentsRepository.save(submissionAttachment);
    }

    /**
     * save submission comment
     * @param submissionComment submission comment entity
     * @return updated submission comment entity
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public SubmissionComment saveComment(SubmissionComment submissionComment) {
        return commentsRepository.save(submissionComment);
    }

    /**
     * get attachments in a submission
     * @param submission submission entity
     * @return list of attachments
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public List<SubmissionAttachment> getAttachments(Submission submission) {
        return attachmentsRepository.findSubmissionAttachmentsBySubmission(submission);
    }

    /**
     * get comments in a submission
     * @param submission submission entity
     * @return list of comments
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public List<SubmissionComment> getComments(Submission submission) {
        return commentsRepository.findSubmissionCommentsBySubmission(submission);
    }
}
