package com.group13.tcsprojectgrading.services;


import com.group13.tcsprojectgrading.models.Project;
import com.group13.tcsprojectgrading.models.ProjectId;
import com.group13.tcsprojectgrading.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {

    private ProjectRepository repository;

    @Autowired
    public ProjectService(ProjectRepository repository) {
        this.repository = repository;
    }

    public List<Project> getProjectByCourseId(String courseId) {
        return repository.findProjectsByCourseId(courseId);
    }

    public void addNewProject(Project project) {
        repository.save(project);
    }

    public void deleteProject(Project project) {
        repository.delete(project);
    }

    public Project getProjectById(String courseId, String projectId) {
        return repository.findById(new ProjectId(courseId, projectId)).orElse(null);
    }
}
