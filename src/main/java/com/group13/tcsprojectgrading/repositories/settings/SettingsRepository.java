package com.group13.tcsprojectgrading.repositories.settings;

import com.group13.tcsprojectgrading.models.settings.Settings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettingsRepository extends JpaRepository<Settings, Settings.Pk> {
    Settings findById_User_IdAndId_Project_Id(Long id_user_id, Long id_project_id);
}

