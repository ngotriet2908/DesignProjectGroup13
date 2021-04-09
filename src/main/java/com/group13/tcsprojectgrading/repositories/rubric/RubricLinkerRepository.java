package com.group13.tcsprojectgrading.repositories.rubric;

import com.group13.tcsprojectgrading.models.rubric.RubricLinker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.Repository;

import javax.persistence.LockModeType;
import javax.transaction.Transactional;
import java.util.Optional;

@Transactional(value = Transactional.TxType.MANDATORY)
public interface RubricLinkerRepository extends JpaRepository<RubricLinker, RubricLinker.Pk> {

    @Lock(LockModeType.PESSIMISTIC_READ)
    Optional<RubricLinker> findById(RubricLinker.Pk pk);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<RubricLinker> findRubricLinkerById(RubricLinker.Pk pk);

    @Lock(LockModeType.PESSIMISTIC_FORCE_INCREMENT)
    <S extends RubricLinker> S save(S s);
}
