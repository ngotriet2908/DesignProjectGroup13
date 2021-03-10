package com.group13.tcsprojectgrading.repositories;

import com.group13.tcsprojectgrading.models.Grader;
import com.group13.tcsprojectgrading.models.Project;
import com.group13.tcsprojectgrading.models.Task;
import com.group13.tcsprojectgrading.models.TaskId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, TaskId> {
    public List<Task> findTasksByProject(Project project);
    public Task findTaskBySubmissionIdAndProject(String submissionId, Project project);
    public List<Task> findTasksByGrader_UserId(String userId);
    public List<Task> findTasksByProjectAndGrader_UserId(Project project, String userId);
}
