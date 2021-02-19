package com.group13.tcsprojectgrading.repository.project;

import com.group13.tcsprojectgrading.model.project.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}
