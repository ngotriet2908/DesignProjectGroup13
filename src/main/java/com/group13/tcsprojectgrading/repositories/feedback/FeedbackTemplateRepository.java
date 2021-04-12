package com.group13.tcsprojectgrading.repositories.feedback;

import com.group13.tcsprojectgrading.models.feedback.FeedbackTemplate;
import com.group13.tcsprojectgrading.models.project.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface FeedbackTemplateRepository extends JpaRepository<FeedbackTemplate, Long> {

    @Lock(LockModeType.PESSIMISTIC_READ)
    public List<FeedbackTemplate> getFeedbackTemplatesByProjectOrderById(Project project);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<FeedbackTemplate> findFeedbackTemplateById(Long templateId);

    @Lock(LockModeType.PESSIMISTIC_READ)
    Optional<FeedbackTemplate> findById(Long id);
}
