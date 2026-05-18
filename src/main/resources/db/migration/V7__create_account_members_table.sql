CREATE TABLE account_members
(
    account_id UUID        NOT NULL REFERENCES accounts (id),

    user_id    UUID        NOT NULL REFERENCES users (id),

    role       VARCHAR(50) NOT NULL,

    created_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    PRIMARY KEY (account_id, user_id)
);
