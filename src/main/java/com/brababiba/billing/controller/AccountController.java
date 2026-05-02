package com.brababiba.billing.controller;

import com.brababiba.billing.dto.AccountResponse;
import com.brababiba.billing.dto.CreateAccountRequest;
import com.brababiba.billing.model.Account;
import com.brababiba.billing.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

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
}
