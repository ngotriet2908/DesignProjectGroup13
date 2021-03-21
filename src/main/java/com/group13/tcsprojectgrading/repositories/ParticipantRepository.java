package com.group13.tcsprojectgrading.repositories;

import com.group13.tcsprojectgrading.models.Participant;
import com.group13.tcsprojectgrading.models.ParticipantId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantRepository extends JpaRepository<Participant, ParticipantId> {

}
