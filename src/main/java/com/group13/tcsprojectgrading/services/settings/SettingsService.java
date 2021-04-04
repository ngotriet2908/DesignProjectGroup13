package com.group13.tcsprojectgrading.services.settings;

import com.group13.tcsprojectgrading.models.settings.Settings;
import com.group13.tcsprojectgrading.services.user.ActivityService;
import com.group13.tcsprojectgrading.services.project.ProjectService;
import com.group13.tcsprojectgrading.services.graders.GradingParticipationService;
import com.group13.tcsprojectgrading.services.permissions.ProjectRoleService;
import com.group13.tcsprojectgrading.services.permissions.RoleService;
import com.group13.tcsprojectgrading.services.rubric.RubricService;
import com.group13.tcsprojectgrading.services.submissions.SubmissionDetailsService;
import com.group13.tcsprojectgrading.services.submissions.SubmissionService;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.group13.tcsprojectgrading.repositories.settings.SettingsRepository;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;

@Service
public class SettingsService {
    private final SettingsRepository settingsRepository;

    private final ProjectRoleService projectRoleService;
    private final RoleService roleService;
//    private final LabelService labelService;
    private final ActivityService activityService;
    private final RubricService rubricService;
    private final GradingParticipationService gradingParticipationService;
    private final SubmissionService submissionService;
    private final SubmissionDetailsService submissionDetailsService;
    private final ProjectService projectService;

    public SettingsService(SettingsRepository settingsRepository, ProjectRoleService projectRoleService,
                           RoleService roleService, ActivityService activityService,
                           RubricService rubricService, GradingParticipationService gradingParticipationService,
                           SubmissionService submissionService, SubmissionDetailsService submissionDetailsService,
                           @Lazy ProjectService projectService) {
        this.settingsRepository = settingsRepository;
        this.projectRoleService = projectRoleService;
        this.roleService = roleService;
        this.activityService = activityService;
        this.rubricService = rubricService;
        this.gradingParticipationService = gradingParticipationService;
        this.submissionService = submissionService;
        this.submissionDetailsService = submissionDetailsService;
        this.projectService = projectService;
    }

    @Transactional
    public Settings getSettings(Long projectId, Long userId) {
        if (this.projectService.getProject(projectId) == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Not found"
            );
        }

        return this.settingsRepository.findById_User_IdAndId_Project_Id(
                userId, projectId
        );
    }

    @Transactional
    public void createSettings(Long projectId, Long userId) {
        Settings settings = new Settings(userId, projectId);
        this.settingsRepository.save(settings);
    }

    @Transactional
    public Settings saveSettings(Settings settings) {
        return this.settingsRepository.save(settings);
    }
}
