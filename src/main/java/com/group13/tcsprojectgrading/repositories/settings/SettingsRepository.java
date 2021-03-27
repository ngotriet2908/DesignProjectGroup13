package com.group13.tcsprojectgrading.repositories.settings;

import com.group13.tcsprojectgrading.models.Activity;
import com.group13.tcsprojectgrading.models.ActivityId;
import com.group13.tcsprojectgrading.models.Project;
import com.group13.tcsprojectgrading.models.settings.Settings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettingsRepository extends JpaRepository<Settings, String> {
    Settings findByCourseIdAndProjectIdAndUserId(String courseId, String projectId, String userId);
}

