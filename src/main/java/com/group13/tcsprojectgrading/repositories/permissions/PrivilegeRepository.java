package com.group13.tcsprojectgrading.repositories.permissions;

import com.group13.tcsprojectgrading.models.permissions.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrivilegeRepository extends JpaRepository<Privilege, Long> {
    Privilege findPrivilegeByName(String name);
}
