package com.brababiba.billing.exception;

public class WorkspaceNotFoundException extends RuntimeException {
    public WorkspaceNotFoundException(String id) {
        super("Workspace not found: " + id);
    }
}
