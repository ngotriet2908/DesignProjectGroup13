package com.group13.tcsprojectgrading.repositories.user;

import com.group13.tcsprojectgrading.models.user.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, String> {
}
