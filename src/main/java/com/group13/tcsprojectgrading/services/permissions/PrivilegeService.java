package com.group13.tcsprojectgrading.services.permissions;

import com.group13.tcsprojectgrading.models.permissions.Privilege;
import com.group13.tcsprojectgrading.repositories.permissions.PrivilegeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * Service handlers operations relating to privileges
 */
@Service
public class PrivilegeService {
    private final PrivilegeRepository repository;

    @Autowired
    public PrivilegeService(PrivilegeRepository repository) {
        this.repository = repository;
    }

    /**
     * get privileges by name
     * @param name privilege name
     * @return a privilege entity
     */
    @Transactional(value = Transactional.TxType.MANDATORY)
    public Privilege findPrivilegeByName(String name) {
        return repository.findPrivilegeByName(name);
    }

    /**
     * create new privileges if not exist
     * @param name privilege name
     * @return a created privileges
     */
    @Transactional(rollbackOn = Exception.class)
    public Privilege addPrivilegeIfNotExist(String name) {
        Privilege privilege = repository.findPrivilegeByName(name);
        if (privilege == null) {
            privilege = new Privilege(name);
            repository.save(privilege);
        }
        return privilege;
    }
}
