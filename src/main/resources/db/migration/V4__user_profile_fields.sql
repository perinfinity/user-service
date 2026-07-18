-- V4 — Add profile fields to users: country, city, profile_image
--       Add user_preferred_categories join table (max 5 per user)

ALTER TABLE users
    ADD COLUMN country       VARCHAR(2)   NULL,
    ADD COLUMN city          VARCHAR(100) NULL,
    ADD COLUMN profile_image VARCHAR(255) NULL;

CREATE TABLE IF NOT EXISTS user_preferred_categories (
    user_id  BIGINT       NOT NULL,
    category VARCHAR(100) NOT NULL,
    CONSTRAINT fk_upc_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);
