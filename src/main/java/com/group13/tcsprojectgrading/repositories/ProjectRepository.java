package com.group13.tcsprojectgrading.repositories;

import com.group13.tcsprojectgrading.models.Project;
import com.group13.tcsprojectgrading.models.ProjectId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, ProjectId> {
    List<Project> findProjectsByCourseId(String courseId);
}
