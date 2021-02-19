package com.group13.tcsprojectgrading.repository.course;

import com.group13.tcsprojectgrading.model.course.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, String> {
}
