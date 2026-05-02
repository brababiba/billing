package com.brababiba.billing.controller;

import com.brababiba.billing.model.Account;
import com.brababiba.billing.service.AccountService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService service;


    public AccountController(AccountService service) {
        this.service = service;
    }

    @PostMapping
    public Account create(@RequestParam String name) {
        return service.create(name);
    }
}
