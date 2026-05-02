package com.brababiba.billing.dto;

public class AccountResponse {
    private String id;
    private String name;
    private String createdAt;

    public AccountResponse(String id, String name, String createdAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
