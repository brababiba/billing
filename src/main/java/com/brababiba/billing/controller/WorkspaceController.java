package com.brababiba.billing.controller;

import com.brababiba.billing.dto.*;
import com.brababiba.billing.model.Workspace;
import com.brababiba.billing.service.WorkspaceService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/workspaces")
public class WorkspaceController {

    private final WorkspaceService workspaceService;


    public WorkspaceController(WorkspaceService service) {
        this.workspaceService = service;
    }

    @PostMapping
    public WorkspaceResponse create(@RequestBody @Valid CreateWorkspaceRequest request) {
        Workspace workspace = workspaceService.create(request.getName());
        return new WorkspaceResponse(workspace.getId().toString(), workspace.getName(), workspace.getCreatedAt().toString());
    }

    @GetMapping
    public Page<WorkspaceResponse> getAll(@RequestParam(required = false) String name, Pageable pageable) {
        return workspaceService.getAll(name, pageable)
                .map(WorkspaceResponse::from);
    }

    @GetMapping("/{id}")
    public WorkspaceResponse getById(@PathVariable UUID id, Authentication authentication) {

        return workspaceService.getById(id, authentication.getName());
    }

    @PutMapping("/{id}")
    public WorkspaceResponse update(@PathVariable UUID id,
                                    @RequestBody @Valid UpdateWorkspaceRequest request,
                                    Authentication authentication) {
        return workspaceService.update(id, request, authentication.getName());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id, Authentication authentication) {

        workspaceService.delete(id, authentication.getName());
    }

    @GetMapping("/my")
    public List<MyWorkspaceResponse> getMyWorkspaces(
            Authentication authentication
    ) {

        String email = authentication.getName();

        return workspaceService.getMyWorkspaces(email);
    }

    @GetMapping("/{id}/members")
    public List<WorkspaceMemberResponse> getMembers(
            @PathVariable UUID id, Authentication authentication
    ) {
        return workspaceService.getMembers(id, authentication.getName());
    }
}
