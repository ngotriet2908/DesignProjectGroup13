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

@Service
public class FeedbackService {

    private final FeedbackLogRepository feedbackLogRepository;
    private final FeedbackTemplateRepository feedbackTemplateRepository;

    @Autowired
    public FeedbackService(FeedbackLogRepository feedbackLogRepository, FeedbackTemplateRepository feedbackTemplateRepository) {
        this.feedbackLogRepository = feedbackLogRepository;
        this.feedbackTemplateRepository = feedbackTemplateRepository;
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public List<FeedbackTemplate> getTemplatesFromProject(Project project) {
        return feedbackTemplateRepository.getFeedbackTemplatesByProjectOrderById(project);
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public FeedbackTemplate findTemplateFromId(Long id) {
        return feedbackTemplateRepository.findById(id).orElse(null);
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public void addTemplate(FeedbackTemplate template) {
        feedbackTemplateRepository.save(template);
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public void addLog(FeedbackLog log) {
        feedbackLogRepository.save(log);
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public List<FeedbackLog> getLogs(Project project) {
        return feedbackLogRepository.findFeedbackLogsByLink_Id_Submission_ProjectOrderBySendAtDesc(project);
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public void deleteTemplate(Long templateId) {
        FeedbackTemplate template = feedbackTemplateRepository.findById(templateId).orElse(null);
        if (template == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "template not found"
            );
        }

        feedbackTemplateRepository.delete(template);
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public List<FeedbackLog> findLogFromLink(AssessmentLink link) {
        return feedbackLogRepository.findFeedbackLogsByLink(link);
    }

}
