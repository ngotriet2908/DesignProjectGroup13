package com.group13.tcsprojectgrading.services;

import com.group13.tcsprojectgrading.models.Participant;
import com.group13.tcsprojectgrading.models.ParticipantId;
import com.group13.tcsprojectgrading.models.Project;
import com.group13.tcsprojectgrading.repositories.ParticipantRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class ParticipantService {

    private ParticipantRepository repository;

    public ParticipantService(ParticipantRepository repository) {
        this.repository = repository;
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public Participant addNewParticipant(Participant participant) {
        if (repository.existsById(new ParticipantId(participant.getId(), participant.getProject().getProjectCompositeKey()))) {
            return repository.getOne(new ParticipantId(participant.getId(), participant.getProject().getProjectCompositeKey()));
        } else {
            return repository.save(participant);
        }
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public List<Participant> findParticipantsWithProject(Project project) {
        return repository.findParticipantsByProject(project);
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public Participant findParticipantWithId(String id, Project project) {
        return repository.findById(new ParticipantId(id, project.getProjectCompositeKey())).orElse(null);
    }
}