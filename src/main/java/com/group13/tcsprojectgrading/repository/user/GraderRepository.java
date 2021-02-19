package com.group13.tcsprojectgrading.repository.user;

import com.group13.tcsprojectgrading.model.user.Grader;
import com.group13.tcsprojectgrading.model.user.ParticipantKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GraderRepository extends JpaRepository<Grader, ParticipantKey> {
}
