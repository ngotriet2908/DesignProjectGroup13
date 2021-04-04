package com.group13.tcsprojectgrading.repositories.grading;

import com.group13.tcsprojectgrading.models.grading.IssueStatus;
import org.springframework.data.jpa.repository.JpaRepository;


public interface IssueStatusRepository extends JpaRepository<IssueStatus, Long> {
    IssueStatus findByName(String name);
}

