package com.group13.tcsprojectgrading.repositories.user;

import com.group13.tcsprojectgrading.models.user.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, Long> {

}
