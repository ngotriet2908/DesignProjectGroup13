package com.group13.tcsprojectgrading.repositories.permissions;

import com.group13.tcsprojectgrading.models.project.Project;
import com.group13.tcsprojectgrading.models.permissions.ProjectRole;
import com.group13.tcsprojectgrading.models.permissions.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRoleRepository extends JpaRepository<ProjectRole, ProjectRole.Pk> {
    ProjectRole findById_ProjectAndId_Role(Project id_project, Role id_role);
    List<ProjectRole> findById_Project(Project project);
    boolean existsById_ProjectAndId_Role(Project id_project, Role id_role);
}
