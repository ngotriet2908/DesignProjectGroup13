package com.group13.tcsprojectgrading.services;


import com.group13.tcsprojectgrading.models.Project;
import com.group13.tcsprojectgrading.models.ProjectId;
import com.group13.tcsprojectgrading.models.Role;
import com.group13.tcsprojectgrading.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {
    private final ProjectRepository repository;

    private ProjectRoleService projectRoleService;
    private RoleService roleService;

    @Autowired
    public ProjectService(ProjectRepository repository, ProjectRoleService projectRoleService, RoleService roleService) {
        this.repository = repository;
        this.projectRoleService = projectRoleService;
        this.roleService = roleService;
    }

    public List<Project> getProjectsByCourseId(String courseId) {
        return repository.findProjectsByCourseId(courseId);
    }

    public void addNewProject(Project project) {
        repository.save(project);
    }

    public void getOrUpdateProject(Project project) {
        repository.save(project);
    }

    public void addProjectRoles(Project project) {
        Project project1 = repository.findById(project.getProjectCompositeKey()).orElse(null);
        if (project1 == null) return;
        for(Role role: roleService.findAllRoles()) {
            projectRoleService.addNewRoleToProject(project1, role);
        }
    }

    public void deleteProject(Project project) {
        repository.delete(project);
    }

    public Project getProjectById(String courseId, String projectId) {
        return repository.findById(new ProjectId(courseId, projectId)).orElse(null);
    }
}
