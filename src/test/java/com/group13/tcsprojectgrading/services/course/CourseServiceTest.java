package com.group13.tcsprojectgrading.services.course;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.group13.tcsprojectgrading.canvas.api.CanvasApi;
import com.group13.tcsprojectgrading.models.course.Course;
import com.group13.tcsprojectgrading.models.course.CourseParticipation;
import com.group13.tcsprojectgrading.models.graders.GradingParticipation;
import com.group13.tcsprojectgrading.models.permissions.Role;
import com.group13.tcsprojectgrading.models.permissions.RoleEnum;
import com.group13.tcsprojectgrading.models.project.Project;
import com.group13.tcsprojectgrading.models.user.User;
import com.group13.tcsprojectgrading.repositories.course.CourseParticipationRepository;
import com.group13.tcsprojectgrading.repositories.course.CourseRepository;
import com.group13.tcsprojectgrading.repositories.graders.GradingParticipationRepository;
import com.group13.tcsprojectgrading.repositories.grading.*;
import com.group13.tcsprojectgrading.repositories.project.ProjectRepository;
import com.group13.tcsprojectgrading.repositories.submissions.SubmissionRepository;
import com.group13.tcsprojectgrading.repositories.user.UserRepository;
import com.group13.tcsprojectgrading.services.Json;
import com.group13.tcsprojectgrading.services.graders.GradingParticipationService;
import com.group13.tcsprojectgrading.services.grading.AssessmentService;
import com.group13.tcsprojectgrading.services.notifications.NotificationService;
import com.group13.tcsprojectgrading.services.permissions.ProjectRoleService;
import com.group13.tcsprojectgrading.services.permissions.RoleService;
import com.group13.tcsprojectgrading.services.project.ProjectService;
import com.group13.tcsprojectgrading.services.rubric.RubricService;
import com.group13.tcsprojectgrading.services.settings.SettingsService;
import com.group13.tcsprojectgrading.services.submissions.SubmissionService;
import com.group13.tcsprojectgrading.services.user.UserService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.stream.Collectors;

import static com.group13.tcsprojectgrading.controllers.Utils.groupPages;

@ExtendWith(MockitoExtension.class)
public class CourseServiceTest {
    @Mock
    private RubricService rubricService;
    @Mock
    private ProjectService projectService;
    @Mock
    private SettingsService settingsService;
    @Mock
    private ProjectRoleService projectRoleService;
    @Mock
    private RoleService roleService;
    @Mock
    private UserService userService;

    @Mock
    private CourseRepository courseRepository;
    @Mock
    private GradingParticipationRepository gradingParticipationRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private CourseParticipationRepository courseParticipationRepository;

    @Mock
    private CanvasApi canvasApi;

    @InjectMocks
    private CourseService courseService;

}