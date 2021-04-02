package com.group13.tcsprojectgrading.repositories.submissions;

import com.group13.tcsprojectgrading.models.submissions.Label;
import com.group13.tcsprojectgrading.models.project.Project;
import com.group13.tcsprojectgrading.models.submissions.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    List<Submission> findSubmissionsByProject(Project project);
    List<Submission> findSubmissionsByGrader_Id(Long grader_id);
    List<Submission> findSubmissionsByGrader_IdAndProject_Id(Long grader_id, Long project_id);
    List<Submission> findSubmissionsByLabels(Label label);
    Submission findByProject_IdAndSubmitterId_IdAndSubmittedAtAndGroupId(Long project_id, Long submitter_id, Date submittedAt, Long groupId);
}
