package com.group13.tcsprojectgrading.repositories.user;

import com.group13.tcsprojectgrading.models.user.Grader;
import com.group13.tcsprojectgrading.models.user.ParticipantKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GraderRepository extends JpaRepository<Grader, ParticipantKey> {
}
