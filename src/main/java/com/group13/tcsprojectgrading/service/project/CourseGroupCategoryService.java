package com.group13.tcsprojectgrading.service.project;

import com.group13.tcsprojectgrading.model.project.CourseGroupCategory;
import com.group13.tcsprojectgrading.repository.project.CourseGroupCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CourseGroupCategoryService {

    private CourseGroupCategoryRepository repository;

    @Autowired
    public CourseGroupCategoryService(CourseGroupCategoryRepository repository) {
        this.repository = repository;
    }

    public CourseGroupCategory addNewCourseGroupCategory(CourseGroupCategory courseGroupCategory) {
        if (repository.existsById(courseGroupCategory.getId())) {
            System.out.println("Course group category" + courseGroupCategory.getName() + " is existed, updating info");
        } else {
            System.out.println("Course group category" + courseGroupCategory.getName() + " is not in the system, creating new group category");
        }
        return repository.save(courseGroupCategory);
    }

    public CourseGroupCategory findById(String id) {
        return repository.findById(id).orElse(null);
    }

}
