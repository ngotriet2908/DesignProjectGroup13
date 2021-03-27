package com.group13.tcsprojectgrading.services.permissions;

import com.group13.tcsprojectgrading.models.*;
import com.group13.tcsprojectgrading.models.permissions.*;
import com.group13.tcsprojectgrading.repositories.permissions.ProjectRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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

    @Transactional(value = Transactional.TxType.MANDATORY)
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

    @Transactional(value = Transactional.TxType.MANDATORY)
    public ProjectRole findByProjectAndRole(Project project, Role role) {
        return repository.findById(new ProjectRoleId(role.getId(), project.getProjectCompositeKey())).orElse(null);
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public List<ProjectRole> findByProject(Project project) {
        return repository.findProjectRolesByProject(project);
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
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
