package com.group13.tcsprojectgrading.service.project;

import com.group13.tcsprojectgrading.model.course.Course;
import com.group13.tcsprojectgrading.model.project.Project;
import com.group13.tcsprojectgrading.repository.project.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {

    private ProjectRepository projectRepository;

    @Autowired
    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public List<Project> getProjects() {
        return projectRepository.findAll();
    }

    public void addNewProject(Project project) {
        if (projectRepository.existsById(project.getId())) {
            System.out.println("Project " + project.getName() + " is already existed");
        } else {
            System.out.println("Project " + project.getName() + " is not existed, creating new project");
        }
        projectRepository.save(project);
    }

    public Project findProjectWithId(Long id) {
        return projectRepository.findById(id).orElse(null);
    }

}
