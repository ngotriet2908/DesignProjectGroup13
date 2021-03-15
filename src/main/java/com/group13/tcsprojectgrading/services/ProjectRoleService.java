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

    @Autowired
    public ProjectRoleService(ProjectRoleRepository repository, PrivilegeService privilegeService) {
        this.repository = repository;
        this.privilegeService = privilegeService;
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
}
