package com.group13.tcsprojectgrading.repositories;

import com.group13.tcsprojectgrading.models.Task;
import com.group13.tcsprojectgrading.models.TaskId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, TaskId> {
    public List<Task> findTasksByCourseIdAndProjectId(String courseId, String projectId);
    public Task findTaskBySubmissionIdAndCourseIdAndProjectId(String submissionId, String courseId, String projectId);
}
