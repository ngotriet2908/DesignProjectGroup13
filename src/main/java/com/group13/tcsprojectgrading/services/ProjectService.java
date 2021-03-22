package com.group13.tcsprojectgrading.services;


import com.group13.tcsprojectgrading.models.Flag;
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

    private final ProjectRoleService projectRoleService;
    private final RoleService roleService;
    private final FlagService flagService;

    @Autowired
    public ProjectService(ProjectRepository repository, ProjectRoleService projectRoleService, RoleService roleService, FlagService flagService) {
        this.repository = repository;
        this.projectRoleService = projectRoleService;
        this.roleService = roleService;
        this.flagService = flagService;
    }

    public List<Project> getProjectsByCourseId(String courseId) {
        return repository.findProjectsByCourseId(courseId);
    }

    public void addNewProject(Project project) {
        Project project1 = repository.save(project);
        flagService.saveNewFlag(new Flag("Required Attention", "for some godforsaken reason, this submission need a flag", "primary", project1));
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
        List<Flag> flags = flagService.findFlagsWithProject(project);
        for(Flag flag: flags) {
            flagService.deleteFlag(flag);
        }
        repository.delete(project);
    }

    public Project getProjectById(String courseId, String projectId) {
        return repository.findById(new ProjectId(courseId, projectId)).orElse(null);
    }
}
