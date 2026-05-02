package com.brababiba.billing.service;

import com.brababiba.billing.model.Account;
import com.brababiba.billing.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AccountService {

    private final AccountRepository repository;

    public AccountService(AccountRepository repository) {
        this.repository = repository;
    }

    public Account create(String name) {
        Account acc = new Account();
        acc.setId(UUID.randomUUID());
        acc.setName(name);
        acc.setCreatedAt(LocalDateTime.now());

        return repository.save(acc);
    }
}
