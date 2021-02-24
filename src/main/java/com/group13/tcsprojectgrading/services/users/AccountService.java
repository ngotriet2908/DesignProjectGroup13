package com.group13.tcsprojectgrading.services.users;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group13.tcsprojectgrading.canvas.api.CanvasApi;
import com.group13.tcsprojectgrading.models.course.Course;
import com.group13.tcsprojectgrading.models.user.Account;
import com.group13.tcsprojectgrading.repositories.user.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {
    private final AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository repository) {
        this.accountRepository = repository;
    }

    public List<Account> getAccounts() {
        return accountRepository.findAll();
    }

    public Account addNewAccount(Account account) {
        if (accountRepository.existsById(account.getId())) {
            System.out.println("Account " + account.getName() +" exists, updating info.");
        } else {
            System.out.println("Account " + account.getName() +" is not in the system, creating a new account.");
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
