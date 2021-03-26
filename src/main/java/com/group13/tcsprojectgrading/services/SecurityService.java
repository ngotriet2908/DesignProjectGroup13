package com.group13.tcsprojectgrading.services;


import com.group13.tcsprojectgrading.models.*;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SecurityService {
    private final GraderService graderService;
    private final RoleService roleService;
    private final PrivilegeService privilegeService;
    private final ProjectService projectService;

    public SecurityService(GraderService graderService, RoleService roleService, PrivilegeService privilegeService, ProjectService projectService) {
        this.graderService = graderService;
        this.roleService = roleService;
        this.privilegeService = privilegeService;
        this.projectService = projectService;
    }

    @Transactional
    public List<RoleEnum> getRolesFromUserIdAndProject(String userId, String courseId, String projectId) {
        Project project = projectService.getProject(courseId, projectId);
        if (project == null) return null;
        Grader grader = graderService.getGraderFromGraderId(userId, project);
        if (grader == null) return null;
        List<ProjectRole> roleList = (List<ProjectRole>) grader.getProjectRoles();

        return roleList
                .stream()
                .map(projectRole -> RoleEnum.fromName(projectRole.getRole().getName()))
                .collect(Collectors.toList());
    }

    @Transactional
    public List<PrivilegeEnum> getPrivilegesFromUserIdAndProject(String userId, String courseId, String projectId) {
        Project project = projectService.getProject(courseId, projectId);
        if (project == null) return null;
        Grader grader = graderService.getGraderFromGraderId(userId, project);
        if (grader == null) return null;
        List<ProjectRole> roleList = (List<ProjectRole>) grader.getProjectRoles();

        return roleList
                .stream()
                .map(ProjectRole::getPrivileges)
                .flatMap(Collection::stream)
                .map(privilege -> PrivilegeEnum.fromName(privilege.getName()))
                .collect(Collectors.toList())
                ;
    }

}
