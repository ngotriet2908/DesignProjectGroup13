package com.group13.tcsprojectgrading.service;

import com.group13.tcsprojectgrading.model.user.Account;
import com.group13.tcsprojectgrading.repository.user.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class AccountService {

    private AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository repository) {
        this.accountRepository = repository;
    }

    public List<Account> getAccounts() {
        return accountRepository.findAll();
    }

    public void addNewAccount(Account account) {
        accountRepository.save(account);
    }

    public boolean existsById(String id) {
        return accountRepository.existsById(id);
    }
}
