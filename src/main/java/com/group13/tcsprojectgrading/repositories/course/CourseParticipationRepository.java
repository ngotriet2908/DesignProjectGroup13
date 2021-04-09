package com.group13.tcsprojectgrading.repositories.course;

import com.group13.tcsprojectgrading.models.course.CourseParticipation;
import com.group13.tcsprojectgrading.models.permissions.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import javax.persistence.LockModeType;
import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CourseParticipationRepository extends JpaRepository<CourseParticipation, CourseParticipation.Pk> {

    @Lock(LockModeType.PESSIMISTIC_READ)
    Optional<CourseParticipation> findById(CourseParticipation.Pk id);

    @Lock(LockModeType.PESSIMISTIC_READ)
    CourseParticipation findById_User_IdAndId_Course_Id(Long id_user_id, Long id_course_id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    CourseParticipation findCourseParticipationById_User_IdAndId_Course_Id(Long id_user_id, Long id_course_id);

    @Lock(LockModeType.PESSIMISTIC_READ)
    List<CourseParticipation> findById_Course_IdAndRole_Name(Long id_course_id, String role_name);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<CourseParticipation> findCourseParticipationById(CourseParticipation.Pk id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    <S extends CourseParticipation> S save(S s);

    @Lock(LockModeType.PESSIMISTIC_READ)
    List<CourseParticipation> findById_Course_IdAndRole_NameIsIn(Long id_course_id, Collection<String> role_name);
}

