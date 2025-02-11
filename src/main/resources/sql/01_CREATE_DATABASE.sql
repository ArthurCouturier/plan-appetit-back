CREATE DATABASE planappetit_test;

CREATE TABLE users
(
    uid           VARCHAR(255) PRIMARY KEY, -- Identifiant Firebase
    email         VARCHAR(255) NOT NULL,
    display_name  VARCHAR(255) NOT NULL,
    token         VARCHAR(1024),
    provider      VARCHAR(50)  NOT NULL,
    role          VARCHAR(50)  NOT NULL,
    created_at    TIMESTAMP    NOT NULL,
    last_login    TIMESTAMP,
    profile_photo VARCHAR(255)
);

CREATE TABLE