package com.group13.tcsprojectgrading.repositories;

import com.group13.tcsprojectgrading.models.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrivilegeRepository extends JpaRepository<Privilege, Long> {
    public Privilege findPrivilegeByName(String name);
}
