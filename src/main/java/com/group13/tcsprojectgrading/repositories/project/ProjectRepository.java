package com.group13.tcsprojectgrading.repositories.project;

import com.group13.tcsprojectgrading.models.course.Course;
import com.group13.tcsprojectgrading.models.project.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findProjectsByCourse(Course course);
    List<Project> findProjectsByCourse_Id(Long course_id);

    @Query(value = "select * " +
            "from submission s " +
            "where s.grader_id = ?1 "
            , nativeQuery = true)
    List<Project> getToDoList(Long userId);

}
