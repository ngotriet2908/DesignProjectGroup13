package com.group13.tcsprojectgrading.repositories;

import com.group13.tcsprojectgrading.models.Flag;
import com.group13.tcsprojectgrading.models.Grader;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FlagRepository extends JpaRepository<Flag, Long> {

    public Flag findFlagByNameAndGrader(String name, Grader grader);
    public List<Flag> findFlagsByGrader(Grader grader);
}
