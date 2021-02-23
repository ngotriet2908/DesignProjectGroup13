package com.group13.tcsprojectgrading.repository.project;

import com.group13.tcsprojectgrading.model.project.CourseGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseGroupRepository extends JpaRepository<CourseGroup, Long> {
    public Optional<CourseGroup> findCourseGroupByNameContainingAndCourseGroupCategory_Id(String studentId, String categoryId);
    public Optional<CourseGroup> findCourseGroupByCanvasId(String canvasId);
}
