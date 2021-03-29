package com.group13.tcsprojectgrading.repositories;

import com.group13.tcsprojectgrading.models.Activity;
import com.group13.tcsprojectgrading.models.ActivityId;
import com.group13.tcsprojectgrading.models.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, ActivityId> {
    List<Activity> findActivitiesByUserIdOrderByTimestampDesc(String userId);
    List<Activity> findActivitiesByProject(Project project);
}
