package com.group13.tcsprojectgrading.services.graders;

import com.group13.tcsprojectgrading.models.graders.GradingParticipation;
import com.group13.tcsprojectgrading.models.permissions.Privilege;
import com.group13.tcsprojectgrading.models.permissions.PrivilegeEnum;
import com.group13.tcsprojectgrading.models.permissions.Role;
import com.group13.tcsprojectgrading.models.permissions.RoleEnum;
import com.group13.tcsprojectgrading.models.project.Project;
import com.group13.tcsprojectgrading.models.submissions.Submission;
import com.group13.tcsprojectgrading.models.user.User;
import com.group13.tcsprojectgrading.repositories.graders.GradingParticipationRepository;
import com.group13.tcsprojectgrading.services.permissions.RoleService;
import com.group13.tcsprojectgrading.services.project.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.management.relation.RoleStatus;

import java.security.PrivilegedAction;
import java.sql.Date;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GradingParticipationServiceTest {
    @Mock
    private ProjectService projectService;
    @Mock
    private RoleService roleService;

    @Mock
    private GradingParticipationRepository repository;

    @InjectMocks
    private GradingParticipationService gradingParticipationService;

    private long projectId;
    private long userId;
    private Project testProject;
    private GradingParticipation participation;

    @BeforeEach
    public void init() {
        projectId = 41L;
        userId = 72L;
        testProject = new Project(projectId);
        Collection<Privilege> privileges = List.of(new Privilege("Project_read"),
                new Privilege("Submissions_read"));
        participation = new GradingParticipation(new User(userId), testProject, new Role(
                RoleEnum.TA_GRADING.getName(), privileges
        ));

    }

    @Test
    public void getProjectGradersWithSubmissionsOk() {
        User testStudent = new User(userId);
        Submission submission = new Submission(testStudent, userId+1000, testProject, "Test Submission 1", Date.from(Instant.now()));
        submission.setId(81L);
        Submission submissionOtherProject = new Submission(testStudent, userId+1001,
                new Project(projectId+1), "Test Submission 2", Date.from(Instant.now()));
        submissionOtherProject.setId(83L);
        submission.setProject(testProject);
        submissionOtherProject.setProject(new Project(projectId + 1));
        testStudent.setToGrade(Set.of(submission, submissionOtherProject));

        when(repository.getProjectUsersAndFetchSubmissions(projectId)).thenReturn(List.of(testStudent));
        assertThat(gradingParticipationService.getProjectGradersWithSubmissions(projectId)).isEqualTo(List.of(testStudent));
        assertThat(testStudent.getToGrade()).isEqualTo(Set.of(submission));
    }

    @Test
    public void getPrivilegesNoProject() {
        when(projectService.getProject(projectId)).thenReturn(null);
        assertThat(gradingParticipationService.getPrivilegesFromUserIdAndProject(userId, projectId)).isNull();
    }

    @Test
    public void getPrivilegesNoParticipation() {
        when(projectService.getProject(projectId)).thenReturn(testProject);
        when(repository.findById_User_IdAndId_Project_Id(userId, projectId)).thenReturn(null);
        assertThat(gradingParticipationService.getPrivilegesFromUserIdAndProject(userId, projectId)).isNull();
    }

    @Test
    public void getPrivilegesOk() {
        List<PrivilegeEnum> expected = List.of(PrivilegeEnum.PROJECT_READ, PrivilegeEnum.SUBMISSIONS_READ);

        when(projectService.getProject(projectId)).thenReturn(testProject);
        when(repository.findById_User_IdAndId_Project_Id(userId, projectId)).thenReturn(participation);

        assertThat(gradingParticipationService.getPrivilegesFromUserIdAndProject(userId, projectId)).isEqualTo(expected);
    }
}
