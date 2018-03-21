CREATE TABLE clients (
    client_id BIGSERIAL PRIMARY KEY,
    username VARCHAR(128) UNIQUE NOT NULL,
    password VARCHAR(128) NOT NULL
);