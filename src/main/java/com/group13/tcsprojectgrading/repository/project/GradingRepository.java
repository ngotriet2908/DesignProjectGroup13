package com.group13.tcsprojectgrading.repository.project;

import com.group13.tcsprojectgrading.model.project.Grading;
import com.group13.tcsprojectgrading.model.project.GradingKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GradingRepository extends JpaRepository<Grading, GradingKey> {
}
