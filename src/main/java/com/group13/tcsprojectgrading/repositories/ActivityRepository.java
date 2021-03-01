package com.group13.tcsprojectgrading.repositories;

import com.group13.tcsprojectgrading.models.Activity;
import com.group13.tcsprojectgrading.models.ActivityId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, ActivityId> {
    public List<Activity> findActivitiesByUserIdOrderByTimestampDesc(String userId);
}
