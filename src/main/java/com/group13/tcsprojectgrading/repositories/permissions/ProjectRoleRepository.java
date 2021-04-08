package com.group13.tcsprojectgrading.repositories.permissions;

import com.group13.tcsprojectgrading.models.project.Project;
import com.group13.tcsprojectgrading.models.permissions.ProjectRole;
import com.group13.tcsprojectgrading.models.permissions.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import javax.persistence.LockModeType;
import java.util.List;

public interface ProjectRoleRepository extends JpaRepository<ProjectRole, ProjectRole.Pk> {

    @Lock(LockModeType.PESSIMISTIC_READ)
    ProjectRole findById_ProjectAndId_Role(Project id_project, Role id_role);

    @Lock(LockModeType.PESSIMISTIC_READ)
    List<ProjectRole> findById_Project(Project project);

    @Lock(LockModeType.PESSIMISTIC_READ)
    boolean existsById_ProjectAndId_Role(Project id_project, Role id_role);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    <S extends ProjectRole> S save(S s);
}
