package com.brababiba.billing.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    private UUID id;

    private String email;

    private String passwordHash;

    private Instant createdAt;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private Set<UserRole> roles = new HashSet<>();
}
