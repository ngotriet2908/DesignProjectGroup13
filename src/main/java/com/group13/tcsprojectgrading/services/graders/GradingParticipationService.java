package com.group13.tcsprojectgrading.services.graders;

import com.group13.tcsprojectgrading.models.project.Project;
import com.group13.tcsprojectgrading.models.graders.GradingParticipation;
import com.group13.tcsprojectgrading.models.permissions.Privilege;
import com.group13.tcsprojectgrading.models.permissions.PrivilegeEnum;
import com.group13.tcsprojectgrading.models.user.User;
import com.group13.tcsprojectgrading.repositories.graders.GradingParticipationRepository;
import com.group13.tcsprojectgrading.services.project.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GradingParticipationService {
    private final GradingParticipationRepository repository;
    private final ProjectService projectService;

    @Autowired
    public GradingParticipationService(GradingParticipationRepository repository, @Lazy ProjectService projectService) {
        this.repository = repository;
        this.projectService = projectService;
    }

    public GradingParticipation addNewGradingParticipation(GradingParticipation grader) {
        return this.repository.save(grader);
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public List<GradingParticipation> getGradingParticipationFromProject(Project project) {
        return this.repository.findById_Project(project);
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public void deleteGradingParticipation(GradingParticipation grader) {
        this.repository.delete(grader);
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public GradingParticipation getGradingParticipationByUserAndProject(Long userId, Long projectId) {
        return this.repository.findById_User_IdAndId_Project_Id(userId, projectId);
    }

    /*
    Returns a list of graders of the project.
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public List<User> getProjectUsers(Long projectId) {
        return this.repository.getProjectUsers(projectId);
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
