package com.group13.tcsprojectgrading.services.settings;

import com.group13.tcsprojectgrading.models.ProjectId;
import com.group13.tcsprojectgrading.models.settings.Settings;
import com.group13.tcsprojectgrading.models.settings.SettingsId;
import com.group13.tcsprojectgrading.services.ActivityService;
import com.group13.tcsprojectgrading.services.ParticipantService;
import com.group13.tcsprojectgrading.services.ProjectService;
import com.group13.tcsprojectgrading.services.graders.GraderService;
import com.group13.tcsprojectgrading.services.grading.AssessmentLinkerService;
import com.group13.tcsprojectgrading.services.grading.AssessmentService;
import com.group13.tcsprojectgrading.services.permissions.ProjectRoleService;
import com.group13.tcsprojectgrading.services.permissions.RoleService;
import com.group13.tcsprojectgrading.services.rubric.RubricService;
import com.group13.tcsprojectgrading.services.submissions.FlagService;
import com.group13.tcsprojectgrading.services.submissions.SubmissionDetailsService;
import com.group13.tcsprojectgrading.services.submissions.SubmissionService;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.group13.tcsprojectgrading.repositories.settings.SettingsRepository;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SettingsService {
    private final SettingsRepository settingsRepository;

    private final ProjectRoleService projectRoleService;
    private final RoleService roleService;
    private final FlagService flagService;
    private final ActivityService activityService;
    private final RubricService rubricService;
    private final GraderService graderService;
    private final ParticipantService participantService;
    private final AssessmentLinkerService assessmentLinkerService;
    private final SubmissionService submissionService;
    private final SubmissionDetailsService submissionDetailsService;
    private final AssessmentService assessmentService;
    private final ProjectService projectService;

    public SettingsService(SettingsRepository settingsRepository, ProjectRoleService projectRoleService,
                           RoleService roleService, FlagService flagService, ActivityService activityService,
                           RubricService rubricService, GraderService graderService,
                           ParticipantService participantService, AssessmentLinkerService assessmentLinkerService,
                           SubmissionService submissionService, SubmissionDetailsService submissionDetailsService,
                           AssessmentService assessmentService, @Lazy ProjectService projectService) {
        this.settingsRepository = settingsRepository;
        this.projectRoleService = projectRoleService;
        this.roleService = roleService;
        this.flagService = flagService;
        this.activityService = activityService;
        this.rubricService = rubricService;
        this.graderService = graderService;
        this.participantService = participantService;
        this.assessmentLinkerService = assessmentLinkerService;
        this.submissionService = submissionService;
        this.submissionDetailsService = submissionDetailsService;
        this.assessmentService = assessmentService;
        this.projectService = projectService;
    }

    // TODO: thread-unsafe (as most other methods)
    // settings for each  user should be created when a project is chosen as active
    @Transactional
    public Settings getOrCreateSettings(String courseId, String projectId, String userId) {
        if (this.projectService.getProjectById(courseId, projectId) == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Not found"
            );
        }

        Settings settings = this.settingsRepository.findByCourseIdAndProjectIdAndUserId(
                courseId, projectId, userId
        );

        if (settings == null) {
            settings = new Settings(courseId, projectId, userId);
            this.settingsRepository.save(settings);
        }

        return settings;
    }

    @Transactional
    public Settings saveSettings(Settings settings) {
        return this.settingsRepository.save(settings);
    }

//    public void updateSettings(String courseId, String projectId, String userId, Map<String, Object> updates) {
//        for (o)
//    }
}
