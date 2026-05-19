package com.brababiba.billing.service;

import com.brababiba.billing.dto.MyWorkspaceResponse;
import com.brababiba.billing.dto.UpdateWorkspaceRequest;
import com.brababiba.billing.exception.WorkspaceNotFoundException;
import com.brababiba.billing.model.User;
import com.brababiba.billing.model.Workspace;
import com.brababiba.billing.repository.UserRepository;
import com.brababiba.billing.repository.WorkspaceMemberRepository;
import com.brababiba.billing.repository.WorkspaceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class WorkspaceService {

    private final WorkspaceRepository repository;

    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final UserRepository userRepository;

    public WorkspaceService(WorkspaceRepository repository, WorkspaceMemberRepository workspaceMemberRepository, UserRepository userRepository) {

        this.repository = repository;

        this.workspaceMemberRepository = workspaceMemberRepository;
        this.userRepository = userRepository;
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

    public List<MyWorkspaceResponse> getMyWorkspaces(UUID userId) {
        return workspaceMemberRepository.findByIdUserId(userId)
                .stream()
                .map(member -> {
                    Workspace workspace = getById(member.getId().getWorkspaceId());

                    return new MyWorkspaceResponse(
                            workspace.getId().toString(),
                            workspace.getName(),
                            member.getRole()
                    );
                })
                .toList();
    }

    public List<MyWorkspaceResponse> getMyWorkspaces(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow();

        return getMyWorkspaces(user.getId());
    }
}
