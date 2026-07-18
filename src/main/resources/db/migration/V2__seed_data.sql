-- V2 — Development seed data
-- All passwords are bcrypt of "password" — CHANGE IN PRODUCTION

INSERT INTO users (username, email, password, role, first_name, last_name, created_at, updated_at) VALUES
('superadmin', 'admin@perinfinity.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ADMIN', 'Super', 'Admin', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO users (username, email, password, role, first_name, last_name, created_at, updated_at) VALUES
('sarah_volunteer', 'sarah.volunteer@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'VOLUNTEER', 'Sarah', 'Johnson', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('alex_volunteer',  'alex.volunteer@example.com',  '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'VOLUNTEER', 'Alex',  'Smith',   CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('emma_volunteer',  'emma.volunteer@example.com',  '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'VOLUNTEER', 'Emma',  'Brown',   CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('david_volunteer', 'david.volunteer@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'VOLUNTEER', 'David', 'Wilson',  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO users (username, email, password, role, first_name, last_name, org_name, created_at, updated_at) VALUES
('redcross_org', 'redcross@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ORGANIZATION', 'Jean',   'Dupont',  'Croix Rouge Française',    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('unicef_org',   'unicef@example.com',   '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ORGANIZATION', 'Marie',  'Martin',  'UNICEF France',            CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('wwf_org',      'wwf@example.com',      '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ORGANIZATION', 'Pierre', 'Bernard', 'WWF France',               CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('msf_org',      'msf@example.com',      '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ORGANIZATION', 'Sophie', 'Leroy',   'Médecins Sans Frontières', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
