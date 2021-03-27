package com.group13.tcsprojectgrading.services.submissions;

import com.group13.tcsprojectgrading.models.submissions.Flag;
import com.group13.tcsprojectgrading.models.Project;
import com.group13.tcsprojectgrading.repositories.submissions.FlagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@Service
public class FlagService {

    private final FlagRepository repository;

    @Autowired
    public FlagService(FlagRepository repository) {
        this.repository = repository;
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public Flag saveNewFlag(Flag flag) {
        Flag currentFlag = findFlagWithNameAndProject(flag.getName(), flag.getProject());
        if (currentFlag != null) {
            flag.setId(currentFlag.getId());
        }
        return this.repository.save(flag);
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public Flag findFlagWithNameAndProject(String name, Project project) {
        return repository.findFlagByNameAndProject(name, project);
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public Flag findFlagWithId(UUID id) {
        return repository.findById(id).orElse(null);
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public List<Flag> findFlagsWithProject(Project project) {
        return repository.findFlagsByProject(project);
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public void deleteFlag(Flag flag) {
        repository.delete(flag);
    }
}
