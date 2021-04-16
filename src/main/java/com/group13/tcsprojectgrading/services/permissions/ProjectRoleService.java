package com.group13.tcsprojectgrading.services.permissions;

import com.group13.tcsprojectgrading.models.permissions.*;
import com.group13.tcsprojectgrading.models.project.Project;
import com.group13.tcsprojectgrading.repositories.permissions.ProjectRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Service handlers operations relating to project role
 */
@Service
public class ProjectRoleService {
    private final ProjectRoleRepository projectRoleRepository;
    private final PrivilegeService privilegeService;
    private final RoleService roleService;

    @Autowired
    public ProjectRoleService(ProjectRoleRepository projectRoleRepository, PrivilegeService privilegeService, RoleService roleService) {
        this.projectRoleRepository = projectRoleRepository;
        this.privilegeService = privilegeService;
        this.roleService = roleService;
    }

    /**
     * add all default roles to project
     * @param project project entity
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public void addDefaultRolesToProject(Project project) {
        Role teacher = this.roleService.findRoleByName(RoleEnum.TEACHER.toString());
        Role taGrading = this.roleService.findRoleByName(RoleEnum.TA_GRADING.toString());

        this.projectRoleRepository.save(new ProjectRole(teacher, project));
        this.projectRoleRepository.save(new ProjectRole(taGrading, project));
    }

    /**
     * add a role to project
     * @param project project entity
     * @param role role entity
     * @return created project role
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public ProjectRole addRoleToProject(Project project, Role role) {
        if (this.projectRoleRepository.existsById_ProjectAndId_Role(project, role)) {
            return findByProjectAndRole(project, role);
        }

//        List<Privilege> privilegeList = new ArrayList<>();
//        for(Privilege privilege: role.getDefaultPrivileges()) {
//            Privilege privilege1 = privilegeService.findPrivilegeByName(privilege.getName());
//            privilegeList.add(privilege1);
//        }

        return this.projectRoleRepository.save(new ProjectRole(role, project));
    }

    /**
     * get project role entity in a project and role
     * @param project project entity
     * @param role role entity
     * @return project role entity
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public ProjectRole findByProjectAndRole(Project project, Role role) {
        return this.projectRoleRepository.findById_ProjectAndId_Role(project, role);
    }

    /**
     * get project roles in a project
     * @param project project entity
     * @return list of project role
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public List<ProjectRole> findByProject(Project project) {
        return this.projectRoleRepository.findById_Project(project);
    }

//    @Transactional(value = Transactional.TxType.MANDATORY)
//    public List<Privilege> getPrivilegesByProjectAndRoleEnum(Project project, RoleEnum roleName) {
//        Role role = this.roleService.findRoleByName(roleName.getName());
//
//        if (role == null) {
//            return new ArrayList<>();
//        }
//
//        ProjectRole projectRole = this.projectRoleRepository.findById_ProjectAndId_Role(project, role);
//
//        if (projectRole != null) {
//            return (List<Privilege>) projectRole.getId().getRole().g
//        }
//
//        return new ArrayList<>();
//    }
}
