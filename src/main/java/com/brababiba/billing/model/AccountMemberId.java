package com.brababiba.billing.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode
@Embeddable
public class AccountMemberId implements Serializable {

    @Column(name = "account_id")
    private UUID accountId;

    @Column(name = "user_id")
    private UUID userId;
}
