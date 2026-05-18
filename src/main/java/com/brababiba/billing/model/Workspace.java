package com.brababiba.billing.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "workspaces")
@Getter
@Setter
public class Workspace {

    @Id
    private UUID id;

    private String name;

    private Instant createdAt;
}
