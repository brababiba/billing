package com.brababiba.billing.dto;

import com.brababiba.billing.model.Workspace;

public record WorkspaceResponse(String id, String name, String createdAt) {

    public static WorkspaceResponse from(Workspace workspace) {
        return new WorkspaceResponse(
                workspace.getId().toString(),
                workspace.getName(),
                workspace.getCreatedAt().toString()
        );
    }
}
