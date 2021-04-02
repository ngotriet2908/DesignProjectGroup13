package com.group13.tcsprojectgrading.services.user;

import com.group13.tcsprojectgrading.models.user.Activity;
import com.group13.tcsprojectgrading.models.project.Project;
import com.group13.tcsprojectgrading.repositories.user.ActivityRepository;
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

    public List<Activity> getActivities(Long userId) {
        return repository.findById_User_IdOrderByLastAccessed(userId);
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public List<Activity> getActivitiesByProject(Project project) {
        return repository.findActivitiesById_Project(project);
    }

    public void addOrUpdateActivity(Activity activity) {
        repository.save(activity);
    }
}
