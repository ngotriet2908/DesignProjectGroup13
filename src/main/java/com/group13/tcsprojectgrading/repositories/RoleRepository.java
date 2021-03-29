package com.group13.tcsprojectgrading.repositories;

import com.group13.tcsprojectgrading.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findRoleByName(String name);
}
