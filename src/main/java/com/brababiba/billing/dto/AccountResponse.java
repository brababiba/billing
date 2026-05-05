package com.brababiba.billing.dto;

import com.brababiba.billing.model.Account;

public record AccountResponse(String id, String name, String createdAt) {

    public static AccountResponse from(Account account) {
        return new AccountResponse(
                account.getId().toString(),
                account.getName(),
                account.getCreatedAt().toString()
        );
    }
}
