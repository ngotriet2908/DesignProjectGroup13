package com.group13.tcsprojectgrading.repositories.project;

import com.group13.tcsprojectgrading.models.project.GradingVersion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GradingVersionRepository extends JpaRepository<GradingVersion, Long> {
}
