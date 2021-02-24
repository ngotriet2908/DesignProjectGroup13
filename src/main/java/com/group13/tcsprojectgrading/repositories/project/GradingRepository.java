package com.group13.tcsprojectgrading.repositories.project;

import com.group13.tcsprojectgrading.models.project.Grading;
import com.group13.tcsprojectgrading.models.project.GradingKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GradingRepository extends JpaRepository<Grading, GradingKey> {
}
