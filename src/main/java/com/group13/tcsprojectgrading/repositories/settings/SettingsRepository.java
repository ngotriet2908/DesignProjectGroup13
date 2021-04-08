package com.group13.tcsprojectgrading.repositories.settings;

import com.group13.tcsprojectgrading.models.settings.Settings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import javax.persistence.LockModeType;

public interface SettingsRepository extends JpaRepository<Settings, Settings.Pk> {

    @Lock(LockModeType.PESSIMISTIC_READ)
    Settings findById_User_IdAndId_Project_Id(Long id_user_id, Long id_project_id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    <S extends Settings> S save(S s);
}

