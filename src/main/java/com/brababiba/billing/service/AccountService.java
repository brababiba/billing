package com.brababiba.billing.service;

import com.brababiba.billing.dto.UpdateAccountRequest;
import com.brababiba.billing.exception.AccountNotFoundException;
import com.brababiba.billing.model.Account;
import com.brababiba.billing.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
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
        acc.setCreatedAt(Instant.now());

        return repository.save(acc);
    }

    public List<Account> getAll() {
        return repository.findAll();
    }

    public Account getById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + id));
    }

    public Account update(UUID id, UpdateAccountRequest request) {
        Account account = getById(id);
        account.setName(request.getName());
        return repository.save(account);
    }

    public void delete(UUID id) {
        Account account = getById(id);
        repository.delete(account);
    }
}
