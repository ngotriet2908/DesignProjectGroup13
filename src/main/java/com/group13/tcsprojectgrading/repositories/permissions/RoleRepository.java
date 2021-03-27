package com.group13.tcsprojectgrading.repositories.permissions;

import com.group13.tcsprojectgrading.models.permissions.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    public Role findRoleByName(String name);
}
