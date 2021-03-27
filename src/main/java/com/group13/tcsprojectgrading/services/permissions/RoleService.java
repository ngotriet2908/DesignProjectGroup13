package com.group13.tcsprojectgrading.services.permissions;

import com.group13.tcsprojectgrading.models.permissions.Privilege;
import com.group13.tcsprojectgrading.models.permissions.Role;
import com.group13.tcsprojectgrading.repositories.permissions.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class RoleService {

    private final RoleRepository repository;

    @Autowired
    public RoleService(RoleRepository repository) {
        this.repository = repository;
    }

    public Role findRoleByName(String name) {
        return repository.findRoleByName(name);
    }

    public Role addRoleIfNotExist(String name, List<Privilege> privileges) {
        Role role = repository.findRoleByName(name);
        if (role == null) {
            role = new Role(name, privileges);
            repository.save(role);
        }
        return role;
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public List<Role> findAllRoles() {
        return repository.findAll();
    }
}
