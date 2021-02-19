package com.group13.tcsprojectgrading.service;

import com.group13.tcsprojectgrading.model.user.Participant;
import com.group13.tcsprojectgrading.model.user.ParticipantKey;
import com.group13.tcsprojectgrading.model.user.Student;
import com.group13.tcsprojectgrading.repository.user.ParticipantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParticipantService {

    private ParticipantRepository participantRepository;

    @Autowired
    private StudentService studentService;

    @Autowired
    public ParticipantService(ParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }

    public List<Participant> findAllParticipant() {
        return participantRepository.findAll();
    }
}
