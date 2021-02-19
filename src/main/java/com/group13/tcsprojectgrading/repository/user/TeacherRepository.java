package com.group13.tcsprojectgrading.repository.user;

import com.group13.tcsprojectgrading.model.user.ParticipantKey;
import com.group13.tcsprojectgrading.model.user.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeacherRepository extends JpaRepository<Teacher, ParticipantKey> {
}
