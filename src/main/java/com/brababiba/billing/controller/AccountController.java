package com.brababiba.billing.controller;

import com.brababiba.billing.dto.AccountResponse;
import com.brababiba.billing.dto.CreateAccountRequest;
import com.brababiba.billing.model.Account;
import com.brababiba.billing.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService service;


    public AccountController(AccountService service) {
        this.service = service;
    }

    @PostMapping
    public AccountResponse create(@RequestBody @Valid CreateAccountRequest request) {
        Account account = service.create(request.getName());
        return new AccountResponse(account.getId().toString(), account.getName(), account.getCreatedAt().toString());
    }

    @GetMapping
    public List<AccountResponse> getAll() {
        return service.getAll().stream()
                .map(AccountResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public AccountResponse getById(@PathVariable UUID id) {
        Account account = service.getById(id);
        return AccountResponse.from(account);
    }
}
