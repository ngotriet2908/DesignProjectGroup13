package com.group13.tcsprojectgrading.service.user;

import com.group13.tcsprojectgrading.model.user.Account;
import com.group13.tcsprojectgrading.repository.user.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
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

    public Account addNewAccount(Account account) {
        if (accountRepository.existsById(account.getId())) {
            System.out.println("Account " + account.getName() +" is existed, updating info");
        } else {
            System.out.println("Account " + account.getName() +" is not in the system, creating new account");
        }
        return accountRepository.save(account);
    }

    public Account findAccountById(String id) {
        return accountRepository.findById(id).orElse(null);
    }

    public boolean existsById(String id) {
        return accountRepository.existsById(id);
    }
}
