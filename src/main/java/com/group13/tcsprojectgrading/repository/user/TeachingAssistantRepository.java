package com.group13.tcsprojectgrading.repository.user;

import com.group13.tcsprojectgrading.model.user.ParticipantKey;
import com.group13.tcsprojectgrading.model.user.TeachingAssistant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeachingAssistantRepository extends JpaRepository<TeachingAssistant, ParticipantKey> {
}
