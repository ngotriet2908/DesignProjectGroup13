package com.group13.tcsprojectgrading.services.user;

import com.group13.tcsprojectgrading.models.user.Activity;
import com.group13.tcsprojectgrading.models.project.Project;
import com.group13.tcsprojectgrading.repositories.user.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Service handlers operations relating to activity
 */
@Service
public class ActivityService {
    
    private final ActivityRepository repository;

    @Autowired
    public ActivityService(ActivityRepository repository) {
        this.repository = repository;
    }

    /**
     * get user's activities
     * @param userId canvas user id
     * @return list of activities
     */
    @Transactional
    public List<Activity> getActivities(Long userId) {
        return repository.findById_User_IdOrderByLastAccessed(userId);
    }
}
