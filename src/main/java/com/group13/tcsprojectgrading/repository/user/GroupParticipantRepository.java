package com.group13.tcsprojectgrading.repository.user;

import com.group13.tcsprojectgrading.model.user.GroupParticipant;
import com.group13.tcsprojectgrading.model.user.GroupParticipantKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupParticipantRepository extends JpaRepository<GroupParticipant, GroupParticipantKey> {
}
