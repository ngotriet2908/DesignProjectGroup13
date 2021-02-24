package com.group13.tcsprojectgrading.services.users;

import com.group13.tcsprojectgrading.models.user.Participant;
import com.group13.tcsprojectgrading.repositories.user.ParticipantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParticipantService {
    private final ParticipantRepository participantRepository;

    @Autowired
    public ParticipantService(ParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }

    public List<Participant> findAllParticipant() {
        return participantRepository.findAll();
    }
}
