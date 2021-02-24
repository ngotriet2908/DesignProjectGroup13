package com.group13.tcsprojectgrading.repositories.user;

import com.group13.tcsprojectgrading.models.user.ParticipantKey;
import com.group13.tcsprojectgrading.models.user.TeachingAssistant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeachingAssistantRepository extends JpaRepository<TeachingAssistant, ParticipantKey> {
}
