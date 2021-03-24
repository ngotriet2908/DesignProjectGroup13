package com.group13.tcsprojectgrading.repositories;

import com.group13.tcsprojectgrading.models.Participant;
import com.group13.tcsprojectgrading.models.ParticipantId;
import com.group13.tcsprojectgrading.models.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParticipantRepository extends JpaRepository<Participant, ParticipantId> {
    public List<Participant> findParticipantsByProject(Project project);
}
