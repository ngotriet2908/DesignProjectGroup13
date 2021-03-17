package com.group13.tcsprojectgrading.services;

import com.group13.tcsprojectgrading.models.*;
import com.group13.tcsprojectgrading.repositories.ProjectRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProjectRoleService {
    private final ProjectRoleRepository repository;
    private final PrivilegeService privilegeService;
    private final RoleService roleService;

    @Autowired
    public ProjectRoleService(ProjectRoleRepository repository, PrivilegeService privilegeService, RoleService roleService) {
        this.repository = repository;
        this.privilegeService = privilegeService;
        this.roleService = roleService;
    }

    public ProjectRole addNewRoleToProject(Project project, Role role) {
        if (repository.existsById(new ProjectRoleId(role.getId(), project.getProjectCompositeKey()))) {
            return findByProjectAndRole(project, role);
        }
        List<Privilege> privilegeList = new ArrayList<>();
        for(Privilege privilege: role.getDefaultPrivileges()) {
            Privilege privilege1 = privilegeService.findPrivilegeByName(privilege.getName());
            privilegeList.add(privilege1);
        }
        return repository.save(new ProjectRole(project, role, privilegeList));
    }

    public ProjectRole findByProjectAndRole(Project project, Role role) {
        return repository.findById(new ProjectRoleId(role.getId(), project.getProjectCompositeKey())).orElse(null);
    }

    public List<Privilege> findPrivilegesByProjectAndRoleEnum(Project project, RoleEnum roleName) {

        Role role = roleService.findRoleByName(roleName.getName());
        if (role == null) return new ArrayList<>();
        ProjectRole projectRole = repository.findById(new ProjectRoleId(role.getId(), project.getProjectCompositeKey())).orElse(null);
        if (projectRole != null) {
            return (List<Privilege>) projectRole.getPrivileges();
        }
        return new ArrayList<>();
    }
}
