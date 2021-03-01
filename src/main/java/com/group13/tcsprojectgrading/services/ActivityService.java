package com.group13.tcsprojectgrading.services;

import com.group13.tcsprojectgrading.models.Activity;
import com.group13.tcsprojectgrading.models.ActivityId;
import com.group13.tcsprojectgrading.repositories.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityService {
    
    private ActivityRepository repository;

    @Autowired
    public ActivityService(ActivityRepository repository) {
        this.repository = repository;
    }

    public List<Activity> getActivities(String userId) {
        return repository.findActivitiesByUserIdOrderByTimestampDesc(userId);
    }

    public void addOrUpdateActivity(Activity activity) {
        repository.save(activity);
    }
}
