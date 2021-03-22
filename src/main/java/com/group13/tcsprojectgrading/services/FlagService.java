package com.group13.tcsprojectgrading.services;

import com.group13.tcsprojectgrading.models.Flag;
import com.group13.tcsprojectgrading.models.Grader;
import com.group13.tcsprojectgrading.models.Project;
import com.group13.tcsprojectgrading.repositories.FlagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class FlagService {

    private final FlagRepository repository;

    @Autowired
    public FlagService(FlagRepository repository) {
        this.repository = repository;
    }

    public Flag saveNewFlag(Flag flag) {
        Flag currentFlag = findFlagWithNameAndProject(flag.getName(), flag.getProject());
        if (currentFlag != null) {
            flag.setId(currentFlag.getId());
        }
        return this.repository.save(flag);
    }

    public Flag findFlagWithNameAndProject(String name, Project project) {
        return repository.findFlagByNameAndProject(name, project);
    }

    public Flag findFlagWithId(UUID id) {
        return repository.findById(id).orElse(null);
    }

    public List<Flag> findFlagsWithProject(Project project) {
        return repository.findFlagsByProject(project);
    }

    public void deleteFlag(Flag flag) {
        repository.delete(flag);
    }
}
