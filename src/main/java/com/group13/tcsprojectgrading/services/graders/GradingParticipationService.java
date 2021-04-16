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

/**
 * Service handlers operations relating graders management
 */
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

    /**
     * Save new grader to project
     * @param grader grader's participation
     * @return created grader's participation from database
     */
    public GradingParticipation addNewGradingParticipation(GradingParticipation grader) {
        return this.repository.save(grader);
    }

    /**
     * Get graders from project as participation entity
     * @param project a project from database
     * @return list of grading participation
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public List<GradingParticipation> getGradingParticipationFromProject(Project project) {
        return this.repository.findById_Project(project);
    }

    /**
     * Obtain lock on all grader participation entities
     * @param project a project from database
     * @return list of grading participation
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public List<GradingParticipation> getLocksOnAllProjectGraders(Project project) {
        return this.repository.findAllById_Project(project);
    }

    /**
     * remove grader from project
     * @param grader grader participation
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public void deleteGradingParticipation(GradingParticipation grader) {
        this.repository.delete(grader);
    }

    /**
     * Get grader as participation by params
     * @param userId canvas user id
     * @param projectId canvas project id
     * @return a grading participation
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public GradingParticipation getGradingParticipationByUserAndProject(Long userId, Long projectId) {
        return this.repository.findById_User_IdAndId_Project_Id(userId, projectId);
    }

    /**
     * Obtain lock on a grader as participation by params
     * @param userId canvas user id
     * @param projectId canvas project id
     * @return a grading participation
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public GradingParticipation getGradingParticipationByUserAndProjectWithLock(Long userId, Long projectId) {
        return this.repository.findGradingParticipationById_User_IdAndId_Project_Id(userId, projectId);
    }

    /**
     * Returns a list of graders of the project as Users
     * @param projectId canvas project id
     * @return list of graders as User entities
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

    /**
     * Removes specified graders from the project.
     * @param users list of graders
     * @param projectId canvas project id
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public void deleteGradingParticipationByUserIdsAndProject(List<User> users, Long projectId) {
        this.repository.deleteAllById_UserInAndId_ProjectId(users, projectId);
    }

    /**
     * Removes all graders from the project.
     * @param projectId canvas project id
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

    /**
     * Adds specified graders from the project.
     * @param users list of graders
     * @param project project entity
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

    /**
     * Returns user's privileges within the project.
     * @param userId canvas user id
     * @param projectId canvas project id
     * @return list of privilege enum
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
