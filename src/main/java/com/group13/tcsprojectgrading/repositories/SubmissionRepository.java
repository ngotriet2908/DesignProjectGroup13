package com.group13.tcsprojectgrading.repositories;

import com.group13.tcsprojectgrading.models.Flag;
import com.group13.tcsprojectgrading.models.Project;
import com.group13.tcsprojectgrading.models.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SubmissionRepository extends JpaRepository<Submission, UUID> {
    List<Submission> findSubmissionsByProject(Project project);
    List<Submission> findSubmissionsByGrader_UserId(String userId);
    List<Submission> findSubmissionsByProjectAndGrader_UserId(Project project, String userId);
    List<Submission> findSubmissionsByFlags(Flag flag);
    Submission findSubmissionByProjectAndUserIdAndGroupIdAndDate(Project project, String userId, String groupId, String date);
}
