package com.group13.tcsprojectgrading.services;

import com.group13.tcsprojectgrading.models.Grader;
import com.group13.tcsprojectgrading.models.Task;
import com.group13.tcsprojectgrading.models.TaskId;
import com.group13.tcsprojectgrading.repositories.GraderRepository;
import com.group13.tcsprojectgrading.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {
    private TaskRepository repository;

    @Autowired
    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    public Task addNewTask(Task task) {
        Task current_task = findTaskByTaskId(
                task.getId(),
                task.isGroup(),
                task.getCourseId(),
                task.getProjectId());
        if (current_task != null) {
            task.setGrader(current_task.getGrader());
        }
        return repository.save(task);
    }

    public List<Task> getTasksFromId(String courseId, String projectId) {
        return repository.findTasksByCourseIdAndProjectId(courseId, projectId);
    }

    public Task findTaskById(String submissionId, String courseId, String projectId) {
        return repository.findTaskBySubmissionIdAndCourseIdAndProjectId(submissionId, courseId, projectId);
    }

    public Task findTaskByTaskId(String id, Boolean isGroup, String courseId, String projectId) {
        return repository.findById(new TaskId(id, isGroup, courseId, projectId)).orElse(null);
    }

    public void deleteTask(Task task) {
        repository.delete(task);
    }
}
