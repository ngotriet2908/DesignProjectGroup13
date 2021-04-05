package com.group13.tcsprojectgrading.repositories.project;

import com.group13.tcsprojectgrading.models.course.Course;
import com.group13.tcsprojectgrading.models.project.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Lock(LockModeType.PESSIMISTIC_READ)
    List<Project> findProjectsByCourse(Course course);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Project findProjectsById(Long courseId);

    List<Project> findProjectsByCourse_Id(Long course_id);

    @Query(value = "select * " +
            "from submission s " +
            "where s.grader_id = ?1 "
            , nativeQuery = true)
    List<Project> getToDoList(Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    <S extends Project> S save(S s);

    @Lock(LockModeType.PESSIMISTIC_READ)
    Optional<Project> findById(Long aLong);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Project> findProjectById(Long projectId);
}
