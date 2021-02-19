package com.group13.tcsprojectgrading.service;

import com.group13.tcsprojectgrading.model.project.CourseGroup;
import com.group13.tcsprojectgrading.model.user.Account;
import com.group13.tcsprojectgrading.model.user.GroupParticipant;
import com.group13.tcsprojectgrading.repository.project.CourseGroupRepository;
import com.group13.tcsprojectgrading.repository.user.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseGroupService {
    private CourseGroupRepository repository;

    public CourseGroupService(CourseGroupRepository repository) {
        this.repository = repository;
    }

    public List<CourseGroup> getCourseGroups() {
        return repository.findAll();
    }

    public void addCourseGroup(CourseGroup courseGroup) {
        repository.save(courseGroup);
    }

    public boolean existsById(Long id) {
        return repository.existsById(id);
    }

    public void addAllCourseGroups(List<CourseGroup> courseGroups) {
        repository.saveAll(courseGroups);
    }
}
