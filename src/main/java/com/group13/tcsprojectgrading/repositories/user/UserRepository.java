package com.group13.tcsprojectgrading.repositories.user;

import com.group13.tcsprojectgrading.models.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;
import org.springframework.data.jpa.repository.Lock;
import reactor.util.annotation.NonNullApi;

import javax.persistence.LockModeType;
import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {
//    @Query("SELECT u " +
//            "FROM User u " +
//            "JOIN FETCH u.toGrade " +
//            "WHERE u.id = (:userId)")
//    Set<User> findByIdAndFetchAssignedSubmissions(@Param("userId") Long userId);

    User save(User s);

    @Lock(LockModeType.PESSIMISTIC_READ)
    Optional<User> findById(Long aLong);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<User> findUserById(Long id);

    @Lock(LockModeType.PESSIMISTIC_READ)
    User getOne(Long aLong);

    @Lock(LockModeType.PESSIMISTIC_READ)
    boolean existsById(Long aLong);
}
