ALTER TABLE accounts
    RENAME TO workspaces;

ALTER TABLE account_members
    RENAME TO workspace_members;

ALTER TABLE workspace_members
    RENAME COLUMN account_id TO workspace_id;