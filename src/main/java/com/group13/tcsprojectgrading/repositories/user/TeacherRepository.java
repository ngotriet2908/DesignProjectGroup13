package com.group13.tcsprojectgrading.repositories.user;

import com.group13.tcsprojectgrading.models.user.ParticipantKey;
import com.group13.tcsprojectgrading.models.user.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeacherRepository extends JpaRepository<Teacher, ParticipantKey> {
}
