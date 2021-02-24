package com.group13.tcsprojectgrading.repositories.course;

import com.group13.tcsprojectgrading.models.course.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, String> {
}
