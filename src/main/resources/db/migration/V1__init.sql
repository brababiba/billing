CREATE TABLE accounts
(
    id         UUID PRIMARY KEY,
    name       TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT now()
);