package com.group13.tcsprojectgrading.repository.user;

import com.group13.tcsprojectgrading.model.user.Participant;
import com.group13.tcsprojectgrading.model.user.ParticipantKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantRepository extends JpaRepository<Participant, ParticipantKey> {
}
