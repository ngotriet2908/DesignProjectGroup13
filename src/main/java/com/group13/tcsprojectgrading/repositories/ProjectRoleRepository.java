package com.group13.tcsprojectgrading.repositories;

import com.group13.tcsprojectgrading.models.Project;
import com.group13.tcsprojectgrading.models.ProjectRole;
import com.group13.tcsprojectgrading.models.ProjectRoleId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRoleRepository extends JpaRepository<ProjectRole, ProjectRoleId> {
    public List<ProjectRole> findProjectRolesByProject(Project project);
}
