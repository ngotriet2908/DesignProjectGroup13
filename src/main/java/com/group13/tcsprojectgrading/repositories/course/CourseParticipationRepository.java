package com.group13.tcsprojectgrading.repositories.course;

import com.group13.tcsprojectgrading.models.course.CourseParticipation;
import com.group13.tcsprojectgrading.models.permissions.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;

public interface CourseParticipationRepository extends JpaRepository<CourseParticipation, String> {
    CourseParticipation findById(CourseParticipation.Pk id);
    CourseParticipation findById_User_IdAndId_Course_Id(Long id_user_id, Long id_course_id);
    List<CourseParticipation> findById_Course_IdAndRole_Name(Long id_course_id, String role_name);

    List<CourseParticipation> findById_Course_IdAndRole_NameIsIn(Long id_course_id, Collection<String> role_name);
}
