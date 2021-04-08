package com.group13.tcsprojectgrading.repositories.feedback;

import com.group13.tcsprojectgrading.models.feedback.FeedbackLog;
import com.group13.tcsprojectgrading.models.grading.AssessmentLink;
import com.group13.tcsprojectgrading.models.project.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackLogRepository extends JpaRepository<FeedbackLog, Long> {
    public List<FeedbackLog> findFeedbackLogsByLink(AssessmentLink link);
    public List<FeedbackLog> findFeedbackLogsByLink_Id_Submission_ProjectOrderBySendAtDesc(Project project);
}
