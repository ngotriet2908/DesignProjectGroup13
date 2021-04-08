package com.group13.tcsprojectgrading.repositories.feedback;

import com.group13.tcsprojectgrading.models.feedback.FeedbackTemplate;
import com.group13.tcsprojectgrading.models.project.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackTemplateRepository extends JpaRepository<FeedbackTemplate, Long> {
    public List<FeedbackTemplate> getFeedbackTemplatesByProjectOrderById(Project project);
}
