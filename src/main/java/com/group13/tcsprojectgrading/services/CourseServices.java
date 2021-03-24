package com.group13.tcsprojectgrading.services;

import com.group13.tcsprojectgrading.models.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class CourseServices {
    private final ProjectService projectService;

    @Autowired
    public CourseServices(ProjectService projectService) {
        this.projectService = projectService;
    }

    @Transactional
    public List<Project> getProjectsInCourse(String courseId) {
        return projectService.getProjectsByCourseId(courseId);
    }
}
