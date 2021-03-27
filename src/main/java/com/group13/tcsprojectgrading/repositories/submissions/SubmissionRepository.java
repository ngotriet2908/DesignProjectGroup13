package com.group13.tcsprojectgrading.repositories.submissions;

import com.group13.tcsprojectgrading.models.submissions.Flag;
import com.group13.tcsprojectgrading.models.Project;
import com.group13.tcsprojectgrading.models.submissions.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SubmissionRepository extends JpaRepository<Submission, UUID> {
    public List<Submission> findSubmissionsByProject(Project project);
    public List<Submission> findSubmissionsByGrader_UserId(String userId);
    public List<Submission> findSubmissionsByGrader_UserIdAndProject_CourseId(String userId, String courseId);
    public List<Submission> findSubmissionsByProjectAndGrader_UserId(Project project, String userId);
    public List<Submission> findSubmissionsByFlags(Flag flag);
    public Submission findSubmissionByProjectAndUserIdAndGroupIdAndDate(Project project, String userId, String groupId, String date);
}
