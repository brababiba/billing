package com.brababiba.billing.repository;

import com.brababiba.billing.model.Workspace;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WorkspaceRepository extends JpaRepository<Workspace, UUID> {

    Page<Workspace> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
