package com.brababiba.billing.service;

import com.brababiba.billing.dto.UpdateWorkspaceRequest;
import com.brababiba.billing.exception.WorkspaceNotFoundException;
import com.brababiba.billing.model.Workspace;
import com.brababiba.billing.repository.WorkspaceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class WorkspaceService {

    private final WorkspaceRepository repository;

    public WorkspaceService(WorkspaceRepository repository) {
        this.repository = repository;
    }

    public Workspace create(String name) {
        Workspace acc = new Workspace();
        acc.setId(UUID.randomUUID());
        acc.setName(name);
        acc.setCreatedAt(Instant.now());

        return repository.save(acc);
    }

    public Page<Workspace> getAll(String name, Pageable pageable) {

        if (name == null || name.isBlank()) {
            return repository.findAll(pageable);
        }

        return repository.findByNameContainingIgnoreCase(name, pageable);
    }

    public Workspace getById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new WorkspaceNotFoundException("Workspace not found: " + id));
    }

    public Workspace update(UUID id, UpdateWorkspaceRequest request) {
        Workspace workspace = getById(id);
        workspace.setName(request.getName());
        return repository.save(workspace);
    }

    public void delete(UUID id) {
        Workspace workspace = getById(id);
        repository.delete(workspace);
    }
}
