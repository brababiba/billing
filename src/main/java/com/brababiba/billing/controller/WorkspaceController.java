package com.brababiba.billing.controller;

import com.brababiba.billing.dto.CreateWorkspaceRequest;
import com.brababiba.billing.dto.UpdateWorkspaceRequest;
import com.brababiba.billing.dto.WorkspaceResponse;
import com.brababiba.billing.model.Workspace;
import com.brababiba.billing.service.WorkspaceService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/workspaces")
public class WorkspaceController {

    private final WorkspaceService service;


    public WorkspaceController(WorkspaceService service) {
        this.service = service;
    }

    @PostMapping
    public WorkspaceResponse create(@RequestBody @Valid CreateWorkspaceRequest request) {
        Workspace workspace = service.create(request.getName());
        return new WorkspaceResponse(workspace.getId().toString(), workspace.getName(), workspace.getCreatedAt().toString());
    }

    @GetMapping
    public Page<WorkspaceResponse> getAll(@RequestParam(required = false) String name, Pageable pageable) {
        return service.getAll(name, pageable)
                .map(WorkspaceResponse::from);
    }

    @GetMapping("/{id}")
    public WorkspaceResponse getById(@PathVariable UUID id) {
        Workspace workspace = service.getById(id);
        return WorkspaceResponse.from(workspace);
    }

    @PutMapping("/{id}")
    public WorkspaceResponse update(@PathVariable UUID id, @RequestBody @Valid UpdateWorkspaceRequest request) {
        Workspace workspace = service.update(id, request);
        return WorkspaceResponse.from(workspace);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
