CREATE TABLE user_roles
(
    user_id UUID        NOT NULL REFERENCES users (id),
    role    VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, role)
);

INSERT INTO user_roles (user_id, role)
SELECT id, role
FROM users;