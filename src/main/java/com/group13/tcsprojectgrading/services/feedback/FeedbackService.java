package com.group13.tcsprojectgrading.services.feedback;

import com.group13.tcsprojectgrading.models.feedback.FeedbackLog;
import com.group13.tcsprojectgrading.models.feedback.FeedbackTemplate;
import com.group13.tcsprojectgrading.models.grading.AssessmentLink;
import com.group13.tcsprojectgrading.models.project.Project;
import com.group13.tcsprojectgrading.repositories.feedback.FeedbackLogRepository;
import com.group13.tcsprojectgrading.repositories.feedback.FeedbackTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Service handlers operations relating to feedback
 */
@Service
public class FeedbackService {

    private final FeedbackLogRepository feedbackLogRepository;
    private final FeedbackTemplateRepository feedbackTemplateRepository;

    @Autowired
    public FeedbackService(FeedbackLogRepository feedbackLogRepository, FeedbackTemplateRepository feedbackTemplateRepository) {
        this.feedbackLogRepository = feedbackLogRepository;
        this.feedbackTemplateRepository = feedbackTemplateRepository;
    }

    /**
     * Gets feedback template from a project
     * @param project a project from database
     * @return list of feedback templates
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public List<FeedbackTemplate> getTemplatesFromProject(Project project) {
        return feedbackTemplateRepository.getFeedbackTemplatesByProjectOrderById(project);
    }

    /**
     * Gets feedback template
     * @param id template id
     * @return feedback template
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public FeedbackTemplate findTemplateFromId(Long id) {
        return feedbackTemplateRepository.findById(id).orElse(null);
    }

    /**
     * Saves feedback template to database
     * @param template feedback template
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public void addTemplate(FeedbackTemplate template) {
        feedbackTemplateRepository.save(template);
    }

    /**
     * Saves feedback log to database
     * @param log feedback log
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public void addLog(FeedbackLog log) {
        feedbackLogRepository.save(log);
    }

    /**
     * Gets feedback logs from a project
     * @param project a project from database
     * @return list of feedback logs
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public List<FeedbackLog> getLogs(Project project) {
        return feedbackLogRepository.findFeedbackLogsByLink_Id_Submission_ProjectOrderBySendAtDesc(project);
    }

    /**
     * Deletes feedback template from database
     * @param templateId feedback template id
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public void deleteTemplate(Long templateId) {
        FeedbackTemplate template = feedbackTemplateRepository.findFeedbackTemplateById(templateId).orElse(null);
        if (template == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "template not found"
            );
        }

        feedbackTemplateRepository.delete(template);
    }

    /**
     * Gets feedback logs from assessment link
     * @param link assessment link
     * @return list of feedback logs
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public List<FeedbackLog> findLogFromLink(AssessmentLink link) {
        return feedbackLogRepository.findFeedbackLogsByLink(link);
    }

}
