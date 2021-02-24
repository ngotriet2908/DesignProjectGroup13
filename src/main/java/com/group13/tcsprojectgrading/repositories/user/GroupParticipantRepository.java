package com.group13.tcsprojectgrading.repositories.user;

import com.group13.tcsprojectgrading.models.user.GroupParticipant;
import com.group13.tcsprojectgrading.models.user.GroupParticipantKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupParticipantRepository extends JpaRepository<GroupParticipant, GroupParticipantKey> {
}
