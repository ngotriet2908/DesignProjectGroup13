package com.group13.tcsprojectgrading.repositories.course;

import com.group13.tcsprojectgrading.models.course.CourseParticipation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseParticipationRepository extends JpaRepository<CourseParticipation, String> {
    CourseParticipation findById(CourseParticipation.Pk id);
    CourseParticipation findById_User_IdAndId_Course_Id(Long id_user_id, Long id_course_id);
    List<CourseParticipation> findById_Course_IdAndRole_Name(Long id_course_id, String role_name);
}
