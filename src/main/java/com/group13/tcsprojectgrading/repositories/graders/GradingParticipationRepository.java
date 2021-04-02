package com.group13.tcsprojectgrading.repositories.graders;

import com.group13.tcsprojectgrading.models.project.Project;
import com.group13.tcsprojectgrading.models.user.User;
import com.group13.tcsprojectgrading.models.graders.GradingParticipation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GradingParticipationRepository extends JpaRepository<GradingParticipation, GradingParticipation.Pk> {
    GradingParticipation findById_User_IdAndId_Project_Id(Long id_user_id, Long id_project_id);
    GradingParticipation findById_UserAndId_Project(User id_user, Project id_project);
    List<GradingParticipation> findById_Project(Project id_project);
}
