package com.group13.tcsprojectgrading.services;

import com.group13.tcsprojectgrading.models.Grader;
import com.group13.tcsprojectgrading.models.Project;
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
                task.getProject());
        if (current_task != null) {
            task.setGrader(current_task.getGrader());
        }
        return repository.save(task);
    }

    public List<Task> getTasksFromId(Project project) {
        return repository.findTasksByProject(project);
    }

    public Task findTaskById(String submissionId, Project project) {
        return repository.findTaskBySubmissionIdAndProject(submissionId, project);
    }

    public Task findTaskByTaskId(String id, Boolean isGroup, Project project) {
        return repository.findById(new TaskId(id, isGroup, project.getProjectCompositeKey())).orElse(null);
    }

    public List<Task> findTaskByUserId(String userId) {
        return repository.findTasksByGrader_UserId(userId);
    }

    public List<Task> findTaskInProjectWithUserId(Project project, String userId) {
        return repository.findTasksByProjectAndGrader_UserId(project, userId);
    }

    public void deleteTask(Task task) {
        repository.delete(task);
    }
}
