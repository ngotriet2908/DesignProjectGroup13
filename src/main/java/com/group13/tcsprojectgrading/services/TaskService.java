package com.group13.tcsprojectgrading.services;

import com.group13.tcsprojectgrading.models.Grader;
import com.group13.tcsprojectgrading.models.Task;
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
        return repository.save(task);
    }

//    public List<Task> getGraderFromId(String courseId, String projectId) {
//        return repository.findGraderByCourseIdAndProjectId(courseId, projectId);
//    }
}
