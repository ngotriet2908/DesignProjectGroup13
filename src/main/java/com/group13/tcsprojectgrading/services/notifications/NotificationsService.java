package com.group13.tcsprojectgrading.services.notifications;

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

import org.springframework.mail.SimpleMailMessage;

@Service
public class NotificationsService {
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
    private final EmailSender emailSender;

    public NotificationsService(SettingsRepository settingsRepository, ProjectRoleService projectRoleService,
                           RoleService roleService, FlagService flagService, ActivityService activityService,
                           RubricService rubricService, GraderService graderService,
                           ParticipantService participantService, AssessmentLinkerService assessmentLinkerService,
                           SubmissionService submissionService, SubmissionDetailsService submissionDetailsService,
                           AssessmentService assessmentService, @Lazy ProjectService projectService,
                                EmailSender emailSender) {
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
        this.emailSender = emailSender;
    }

    public void sendNotification(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("project.grader.utwente@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.getMailSender().send(message);
    }

    public void sendIssueNotification(String to, String authorName, String projectName) {
        this.sendNotification(to,
                authorName + " has mentioned you in a new issue of the " + projectName + " project",
                "You can find the issue in the " + projectName + " project"
                );
    }

    public void sendIssueNotification(String to, String projectName) {
        this.sendNotification(to,
                "New issue in " + projectName + " project",
                "You can find the issue in the " + projectName + " project"
        );
    }

    public void sendResolvedNotification(String to, String issueSubject, String projectName) {
        this.sendNotification(to,
                "The issue " + issueSubject + " in project " + projectName + " has been resolved",
                "The issue created by you in the " + projectName + " project has been resolved."
        );
    }
}

