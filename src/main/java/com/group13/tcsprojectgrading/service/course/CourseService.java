package com.group13.tcsprojectgrading.service.course;

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

    public Course addNewCourse(Course course) {
        if (courseRepository.existsById(course.getId())) {
            System.out.println("Course " + course.getName() + " is existed, updating info");
        } else {
            System.out.println("Course " + course.getName() + " is not in the system, creating new course");
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
