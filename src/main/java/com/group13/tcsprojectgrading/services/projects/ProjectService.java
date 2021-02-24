package com.group13.tcsprojectgrading.services.projects;

import com.group13.tcsprojectgrading.models.project.Project;
import com.group13.tcsprojectgrading.repositories.project.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;

    @Autowired
    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public List<Project> getProjects() {
        return projectRepository.findAll();
    }

    public void addNewProject(Project project) {
        if (projectRepository.existsById(project.getId())) {
            System.out.println("Project " + project.getName() + " already exists.");
        } else {
            System.out.println("Project " + project.getName() + " does not exist, creating a new project.");
        }
        projectRepository.save(project);
    }

    public Project findProjectWithId(Long id) {
        return projectRepository.findById(id).orElse(null);
    }

}
