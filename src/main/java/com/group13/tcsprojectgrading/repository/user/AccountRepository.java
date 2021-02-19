package com.group13.tcsprojectgrading.repository.user;

import com.group13.tcsprojectgrading.model.user.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, String> {
}
