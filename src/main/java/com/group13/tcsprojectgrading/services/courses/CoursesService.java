package com.group13.tcsprojectgrading.services.courses;

import com.group13.tcsprojectgrading.models.course.Course;
import com.group13.tcsprojectgrading.repositories.course.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CoursesService {
    private final CourseRepository courseRepository;

    @Autowired
    public CoursesService(CourseRepository repository) {
        this.courseRepository = repository;
    }

    public List<Course> getCourses() {
        return courseRepository.findAll();
    }

    public Course addNewCourse(Course course) {
        if (courseRepository.existsById(course.getId())) {
            System.out.println("Course " + course.getName() + " exists, updating info.");
        } else {
            System.out.println("Course " + course.getName() + " is not in the system, creating a new course.");
        }
        return courseRepository.save(course);
    }

    public boolean existsById(String id) {
        return courseRepository.existsById(id);
    }

    public Course findCourseById(String id) {
        return courseRepository.findById(id).orElse(null);
    }
}
