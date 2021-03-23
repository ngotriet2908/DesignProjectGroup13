package com.group13.tcsprojectgrading.services;

import com.group13.tcsprojectgrading.models.Project;
import com.group13.tcsprojectgrading.models.Submission;
import com.group13.tcsprojectgrading.repositories.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {
    @Mock
    private ProjectRepository repository;

    @Mock
    private ProjectRoleService projectRoleService;

    @Mock
    private RoleService roleService;

    @InjectMocks
    private ProjectService projectService;

    private String courseId;
    private String projectId;
    private String name;
    private String description;
    private String createAt;
    private Project project;

    @BeforeEach
    void init() {
        courseId = "999";
        projectId = "111";
        name = "Test";
        description = "This is a test project.";
        createAt = "0";
        project = new Project(courseId, projectId, name, description, createAt);
    }

    @Test
    void progressUpdated() {
        //Starting with 0% progress, then getting one submission out of two to 50% should yield 25% total progress
        List<Submission> submissionList = new ArrayList<>();
        submissionList.add(new Submission());
        submissionList.add(new Submission());
        project.setSubmissions(submissionList);

        when(repository.save(any(Project.class))).then(returnsFirstArg());
        assertThat(projectService.updateProgress(project, 0.5).getProgress()).isEqualTo(0.25);
    }
}
