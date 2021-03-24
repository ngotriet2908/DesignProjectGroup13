package com.group13.tcsprojectgrading.repositories;


import com.group13.tcsprojectgrading.models.AssessmentContainer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AssessmentContainerRepository extends JpaRepository<AssessmentContainer, UUID> {
}
