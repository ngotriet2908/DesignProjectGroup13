package com.group13.tcsprojectgrading.services;

import com.group13.tcsprojectgrading.models.Flag;
import com.group13.tcsprojectgrading.models.Grader;
import com.group13.tcsprojectgrading.repositories.FlagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FlagService {

    private final FlagRepository repository;

    @Autowired
    public FlagService(FlagRepository repository) {
        this.repository = repository;
    }

    public Flag saveNewFlag(Flag flag) {
        Flag currentFlag = findFlagWithNameAndGrader(flag.getName(), flag.getGrader());
        if (currentFlag != null) {
            flag.setId(currentFlag.getId());
        }
        return this.repository.save(flag);
    }

    public Flag findFlagWithNameAndGrader(String name, Grader grader) {
        return repository.findFlagByNameAndGrader(name, grader);
    }

    public Flag findFlagWithId(Long id) {
        return repository.findById(id).orElse(null);
    }

    public List<Flag> findFlagsWithGrader(Grader grader) {
        return repository.findFlagsByGrader(grader);
    }

    public void deleteFlag(Flag flag) {
        repository.delete(flag);
    }
}
