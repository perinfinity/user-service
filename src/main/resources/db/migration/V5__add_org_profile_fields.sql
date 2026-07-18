-- V5 — Add organisation profile fields to users table

ALTER TABLE users ADD COLUMN IF NOT EXISTS mission_statement TEXT;
ALTER TABLE users ADD COLUMN IF NOT EXISTS about TEXT;
ALTER TABLE users ADD COLUMN IF NOT EXISTS verified_badge BOOLEAN DEFAULT FALSE;
ALTER TABLE users ADD COLUMN IF NOT EXISTS completeness_score INT DEFAULT 0;
