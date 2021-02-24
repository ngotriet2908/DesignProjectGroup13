package com.group13.tcsprojectgrading.repositories.user;

import com.group13.tcsprojectgrading.models.user.Participant;
import com.group13.tcsprojectgrading.models.user.ParticipantKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantRepository extends JpaRepository<Participant, ParticipantKey> {
}
