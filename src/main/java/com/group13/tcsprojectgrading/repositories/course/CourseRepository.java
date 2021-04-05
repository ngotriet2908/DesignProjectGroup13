package com.group13.tcsprojectgrading.repositories.course;

import com.group13.tcsprojectgrading.models.course.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import javax.persistence.LockModeType;
import java.util.Optional;


public interface CourseRepository extends JpaRepository<Course, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Course> findCourseById(Long aLong);

    @Lock(LockModeType.PESSIMISTIC_READ)
    boolean existsById(Long aLong);

    @Lock(LockModeType.PESSIMISTIC_READ)
    Optional<Course> findById(Long aLong);
}
