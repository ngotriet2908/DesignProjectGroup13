package com.group13.tcsprojectgrading.repositories.graders;

import com.group13.tcsprojectgrading.models.project.Project;
import com.group13.tcsprojectgrading.models.user.User;
import com.group13.tcsprojectgrading.models.graders.GradingParticipation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.LockModeType;
import java.util.Collection;
import java.util.List;

public interface GradingParticipationRepository extends JpaRepository<GradingParticipation, GradingParticipation.Pk> {

    @Lock(LockModeType.PESSIMISTIC_READ)
    GradingParticipation findById_User_IdAndId_Project_Id(Long id_user_id, Long id_project_id);

    @Lock(LockModeType.PESSIMISTIC_READ)
    GradingParticipation findById_UserAndId_Project(User id_user, Project id_project);

    @Lock(LockModeType.PESSIMISTIC_READ)
    List<GradingParticipation> findById_Project(Project id_project);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<GradingParticipation> findAllById_Project(Project id_project);

    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query(value="select new User(u.id, u.name) " +
            "from GradingParticipation g, User u " +
            "where g.id.user.id = u.id and g.id.project.id=?1")
    List<User> getProjectUsers(Long projectId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
   <S extends GradingParticipation> S save(S s);

    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query(value="select distinct u " +
            "from GradingParticipation g, User u " +
            "left join fetch u.toGrade " +
            "where g.id.user.id = u.id and g.id.project.id=?1")
    List<User> getProjectUsersAndFetchSubmissions(Long projectId);

    @Modifying(clearAutomatically=true)
    void deleteAllById_UserInAndId_ProjectId(Collection<User> id_user, Long id_project_id);

    @Modifying(clearAutomatically=true)
    void deleteAllById_Project_Id(Long id_project_id);

    @Modifying(clearAutomatically=true)
    void deleteAllById_Project_Id_AndRole_NameNot(Long id_project_id, String roleName);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<GradingParticipation> findAllById_Project_Id_AndRole_NameNot(Long id_project_id, String roleName);

//    @Modifying
//    @Query(value="INSERT INTO grading_participation (user_id, project_id, role_id) " +
//            "VALUES(1, \"A\", 19) " +
//            "ON DUPLICATE KEY UPDATE name=VALUES(name) , age=VALUES(age)",
//            nativeQuery=true)
//    void addGradingParticipant(List<User> graders);
}
