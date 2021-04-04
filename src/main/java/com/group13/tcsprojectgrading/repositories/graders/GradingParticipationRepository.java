package com.group13.tcsprojectgrading.repositories.graders;

import com.group13.tcsprojectgrading.models.project.Project;
import com.group13.tcsprojectgrading.models.user.User;
import com.group13.tcsprojectgrading.models.graders.GradingParticipation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GradingParticipationRepository extends JpaRepository<GradingParticipation, GradingParticipation.Pk> {
    GradingParticipation findById_User_IdAndId_Project_Id(Long id_user_id, Long id_project_id);
    GradingParticipation findById_UserAndId_Project(User id_user, Project id_project);
    List<GradingParticipation> findById_Project(Project id_project);

    @Query(value="select new User(u.id, u.name) " +
            "from GradingParticipation g, User u " +
            "where g.id.user.id = u.id and g.id.project.id=?1")
    List<User> getProjectUsers(Long projectId);
}
