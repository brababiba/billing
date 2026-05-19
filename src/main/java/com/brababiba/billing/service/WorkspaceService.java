package com.brababiba.billing.service;

import com.brababiba.billing.dto.MyWorkspaceResponse;
import com.brababiba.billing.dto.UpdateWorkspaceRequest;
import com.brababiba.billing.dto.WorkspaceResponse;
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

    private final WorkspaceRepository workspaceRepository;

    public WorkspaceService(WorkspaceRepository repository, WorkspaceMemberRepository workspaceMemberRepository,
                            WorkspaceRepository workspaceRepository, UserRepository userRepository) {

        this.repository = repository;

        this.workspaceMemberRepository = workspaceMemberRepository;
        this.userRepository = userRepository;
        this.workspaceRepository = workspaceRepository;
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

    public WorkspaceResponse getById(UUID id) {

        Workspace workspace = findById(id);
        return WorkspaceResponse.from(workspace);
    }

    public WorkspaceResponse getById(UUID id, String userEmail) {
        validateWorkspaceAccess(id, userEmail);

        Workspace workspace = findById(id);

        return WorkspaceResponse.from(workspace);
    }

    public Workspace update(UUID id, UpdateWorkspaceRequest request) {
        Workspace workspace = findById(id);
        workspace.setName(request.getName());
        return repository.save(workspace);
    }

    public void delete(UUID id) {
        Workspace workspace = findById(id);
        repository.delete(workspace);
    }

    public List<MyWorkspaceResponse> getMyWorkspaces(UUID userId) {
        return workspaceMemberRepository.findByIdUserId(userId)
                .stream()
                .map(member -> {
                    Workspace workspace = findById(member.getId().getWorkspaceId());

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

    private void validateWorkspaceAccess(UUID workspaceId, String userEmail) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow();

        boolean hasAccess = workspaceMemberRepository.existsByIdWorkspaceIdAndIdUserId(workspaceId, user.getId());

        if (!hasAccess) {
            throw new WorkspaceNotFoundException(workspaceId.toString());
        }
    }

    private Workspace findById(UUID id) {

        return workspaceRepository.findById(id)
                .orElseThrow(() -> new WorkspaceNotFoundException(id.toString()));
    }
}
