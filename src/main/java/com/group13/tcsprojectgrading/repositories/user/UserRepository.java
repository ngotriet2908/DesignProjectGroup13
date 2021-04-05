package com.group13.tcsprojectgrading.repositories.user;

import com.group13.tcsprojectgrading.models.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;


public interface UserRepository extends JpaRepository<User, Long> {
//    @Query("SELECT u " +
//            "FROM User u " +
//            "JOIN FETCH u.toGrade " +
//            "WHERE u.id = (:userId)")
//    Set<User> findByIdAndFetchAssignedSubmissions(@Param("userId") Long userId);
}
