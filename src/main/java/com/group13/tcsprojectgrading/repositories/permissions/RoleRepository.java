package com.group13.tcsprojectgrading.repositories.permissions;

import com.group13.tcsprojectgrading.models.permissions.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import javax.persistence.LockModeType;

public interface RoleRepository extends JpaRepository<Role, Long> {

    @Lock(LockModeType.PESSIMISTIC_READ)
    Role findByName(String name);
}
