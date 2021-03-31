package com.group13.tcsprojectgrading.services;

import com.group13.tcsprojectgrading.models.Activity;
import com.group13.tcsprojectgrading.models.ActivityId;
import com.group13.tcsprojectgrading.models.Project;
import com.group13.tcsprojectgrading.repositories.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class ActivityService {
    
    private final ActivityRepository repository;

    @Autowired
    public ActivityService(ActivityRepository repository) {
        this.repository = repository;
    }

    public List<Activity> getActivities(String userId) {
        return repository.findActivitiesByUserIdOrderByTimestampDesc(userId);
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public List<Activity> getActivitiesByProject(Project project) {
        return repository.findActivitiesByProject(project);
    }

    public void addOrUpdateActivity(Activity activity) {
        repository.save(activity);
    }
}
