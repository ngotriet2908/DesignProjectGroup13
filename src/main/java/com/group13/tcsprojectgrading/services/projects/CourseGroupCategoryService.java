package com.group13.tcsprojectgrading.services.projects;

import com.group13.tcsprojectgrading.models.project.CourseGroupCategory;
import com.group13.tcsprojectgrading.repositories.project.CourseGroupCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CourseGroupCategoryService {
    private final CourseGroupCategoryRepository repository;

    @Autowired
    public CourseGroupCategoryService(CourseGroupCategoryRepository repository) {
        this.repository = repository;
    }

    public CourseGroupCategory addNewCourseGroupCategory(CourseGroupCategory courseGroupCategory) {
        if (repository.existsById(courseGroupCategory.getId())) {
            System.out.println("Course group category" + courseGroupCategory.getName() + " exists, updating info.");
        } else {
            System.out.println("Course group category" + courseGroupCategory.getName() + " is not in the system, creating a new group category.");
        }
        return repository.save(courseGroupCategory);
    }

    public CourseGroupCategory findById(String id) {
        return repository.findById(id).orElse(null);
    }

}
