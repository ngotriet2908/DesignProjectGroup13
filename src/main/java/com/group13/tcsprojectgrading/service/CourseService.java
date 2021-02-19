package com.group13.tcsprojectgrading.service;

import com.group13.tcsprojectgrading.model.course.Course;
import com.group13.tcsprojectgrading.model.user.Account;
import com.group13.tcsprojectgrading.repository.course.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {

    private CourseRepository courseRepository;

    @Autowired
    public CourseService(CourseRepository repository) {
        this.courseRepository = repository;
    }

    public List<Course> getCourses() {
        return courseRepository.findAll();
    }

    public void addNewCourse(Course course) {
        courseRepository.save(course);
    }

    public boolean existsById(String id) {
        return courseRepository.existsById(id);
    }
}
