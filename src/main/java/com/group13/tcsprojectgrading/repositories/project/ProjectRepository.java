package com.group13.tcsprojectgrading.repositories.project;

import com.group13.tcsprojectgrading.models.project.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}
