package com.group13.tcsprojectgrading.repositories.project;

import com.group13.tcsprojectgrading.models.project.CourseGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CourseGroupRepository extends JpaRepository<CourseGroup, Long> {
    public Optional<CourseGroup> findCourseGroupByNameContainingAndCourseGroupCategory_Id(String studentId, String categoryId);
    public Optional<CourseGroup> findCourseGroupByCanvasId(String canvasId);
    public Optional<CourseGroup> findCourseGroupByNameAndCourseGroupCategory_Id(String name, String categoryId);
}
