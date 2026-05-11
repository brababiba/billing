package com.brababiba.billing.repository;

import com.brababiba.billing.model.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {

    Page<Account> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
