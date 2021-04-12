package com.group13.tcsprojectgrading.services.graders;

import com.group13.tcsprojectgrading.models.permissions.RoleEnum;
import com.group13.tcsprojectgrading.models.project.Project;
import com.group13.tcsprojectgrading.models.graders.GradingParticipation;
import com.group13.tcsprojectgrading.models.permissions.Privilege;
import com.group13.tcsprojectgrading.models.permissions.PrivilegeEnum;
import com.group13.tcsprojectgrading.models.user.User;
import com.group13.tcsprojectgrading.repositories.graders.GradingParticipationRepository;
import com.group13.tcsprojectgrading.services.permissions.RoleService;
import com.group13.tcsprojectgrading.services.project.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GradingParticipationService {
    private final GradingParticipationRepository repository;
    private final ProjectService projectService;
    private final RoleService roleService;

    @Autowired
    public GradingParticipationService(GradingParticipationRepository repository,
                                       @Lazy ProjectService projectService,
                                       RoleService roleService) {
        this.repository = repository;
        this.projectService = projectService;
        this.roleService = roleService;
    }

    public GradingParticipation addNewGradingParticipation(GradingParticipation grader) {
        return this.repository.save(grader);
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public List<GradingParticipation> getGradingParticipationFromProject(Project project) {
        return this.repository.findById_Project(project);
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public List<GradingParticipation> getLocksOnAllProjectGraders(Project project) {
        return this.repository.findAllById_Project(project);
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public void deleteGradingParticipation(GradingParticipation grader) {
        this.repository.delete(grader);
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public GradingParticipation getGradingParticipationByUserAndProject(Long userId, Long projectId) {
        return this.repository.findById_User_IdAndId_Project_Id(userId, projectId);
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public GradingParticipation getGradingParticipationByUserAndProjectWithLock(Long userId, Long projectId) {
        return this.repository.findGradingParticipationById_User_IdAndId_Project_Id(userId, projectId);
    }


    /*
    Returns a list of graders of the project.
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public List<User> getProjectGradersWithSubmissions(Long projectId) {
        return this.repository.getProjectUsersAndFetchSubmissions(projectId)
                .stream()
                .peek(
                        user -> user.setToGrade(user.getToGrade()
                                .stream()
                                .filter(submission -> submission.getProject().getId().equals(projectId))
                                .collect(Collectors.toSet())
                        )
                ).collect(Collectors.toList());
    }

    /*
    Removes specified graders from the project.
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public void deleteGradingParticipationByUserIdsAndProject(List<User> users, Long projectId) {
        this.repository.deleteAllById_UserInAndId_ProjectId(users, projectId);
    }

    /*
    Removes all graders from the project.
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public void deleteAllGradingParticipationByProject(Long projectId) {
        this.repository.deleteAllById_Project_Id(projectId);
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public void deleteAllNonTeacherGradingParticipationByProject(Long projectId) {
        this.repository.findAllById_Project_Id_AndRole_NameNot(projectId, RoleEnum.TEACHER.getName());
        this.repository.deleteAllById_Project_Id_AndRole_NameNot(projectId, RoleEnum.TEACHER.getName());
    }

    /*
   Adds specified graders from the project.
    */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public void addUsersAsGraders(List<User> users, Project project) {
        System.out.println(users.size());
        Set<GradingParticipation> participations = new HashSet<>();
        for (User user: users) {
            participations.add(new GradingParticipation(user, project,
                    this.roleService.findRoleByName(RoleEnum.TA_GRADING.toString()))
            );
        }

        this.repository.saveAll(participations);
    }

    /*
    Returns user's privileges within the project.
     */
    @Transactional
    public List<PrivilegeEnum> getPrivilegesFromUserIdAndProject(Long userId, Long projectId) {
        Project project = this.projectService.getProject(projectId);

        if (project == null) {
            return null;
        }

        GradingParticipation gradingParticipation = getGradingParticipationByUserAndProject(userId, projectId);

        if (gradingParticipation == null) {
            return null;
        }

        Collection<Privilege> privileges = gradingParticipation.getRole().getPrivileges();

        return privileges
                .stream()
                .map(privilege -> PrivilegeEnum.fromName(privilege.getName()))
                .collect(Collectors.toList())
                ;
    }
}
