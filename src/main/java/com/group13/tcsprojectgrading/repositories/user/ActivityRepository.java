package com.group13.tcsprojectgrading.repositories.user;

import com.group13.tcsprojectgrading.models.user.Activity;
import com.group13.tcsprojectgrading.models.project.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Activity.Pk> {
    List<Activity> findById_User_IdOrderByLastAccessed(Long id_user_id);
    List<Activity> findActivitiesById_Project(Project project);
}
