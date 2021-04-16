package com.group13.tcsprojectgrading.services.permissions;

import com.group13.tcsprojectgrading.models.permissions.Privilege;
import com.group13.tcsprojectgrading.models.permissions.Role;
import com.group13.tcsprojectgrading.repositories.permissions.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Service handlers operations relating to role
 */
@Service
public class RoleService {

    private final RoleRepository repository;

    @Autowired
    public RoleService(RoleRepository repository) {
        this.repository = repository;
    }

    /**
     * get role by name
     * @param name name of the role
     * @return role entity
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public Role findRoleByName(String name) {
        return repository.findByName(name);
    }

    /**
     * create role if not exist
     * @param name name of new role
     * @param privileges role's privileges
     * @return created role
     */
    @Transactional(rollbackOn = Exception.class)
    public Role addRoleIfNotExist(String name, List<Privilege> privileges) {
        Role role = repository.findByName(name);
        if (role == null) {
            role = new Role(name, privileges);
        } else {
            role.setPrivileges(privileges);
        }
        repository.save(role);
        return role;
    }

    /**
     * Get all roles in application
     * @return list of roles
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public List<Role> findAllRoles() {
        return repository.findAll();
    }
}
