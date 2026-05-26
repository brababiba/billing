package com.brababiba.billing.security;

import java.util.Set;

public enum WorkspaceRole {

    OWNER(Set.of(
            WorkspacePermission.WORKSPACE_READ,
            WorkspacePermission.WORKSPACE_UPDATE,
            WorkspacePermission.WORKSPACE_DELETE,
            WorkspacePermission.MEMBER_INVITE,
            WorkspacePermission.MEMBER_REMOVE,
            WorkspacePermission.BILLING_VIEW,
            WorkspacePermission.BILLING_MANAGE
    )),

    ADMIN(Set.of(
            WorkspacePermission.WORKSPACE_READ,
            WorkspacePermission.WORKSPACE_UPDATE,
            WorkspacePermission.MEMBER_INVITE,
            WorkspacePermission.MEMBER_REMOVE,
            WorkspacePermission.BILLING_VIEW
    )),

    MEMBER(Set.of(
            WorkspacePermission.WORKSPACE_READ
    )),

    BILLING_MANAGER(Set.of(
            WorkspacePermission.WORKSPACE_READ,
            WorkspacePermission.BILLING_VIEW,
            WorkspacePermission.BILLING_MANAGE
    ));

    private final Set<WorkspacePermission> permissions;

    WorkspaceRole(Set<WorkspacePermission> permissions) {
        this.permissions = permissions;
    }

    public boolean hasPermission(WorkspacePermission permission) {
        return permissions.contains(permission);
    }
}
