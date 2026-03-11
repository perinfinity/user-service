-- Script SQL pour insérer des utilisateurs de test
-- 5 utilisateurs avec le rôle VOLUNTEER
-- 5 utilisateurs avec le rôle ORGANIZATION

-- Suppression des données existantes (optionnel)
-- DELETE FROM users;

-- Insertion des utilisateurs VOLUNTEER
INSERT INTO users (username, email, password, role, first_name, last_name, created_at, updated_at) VALUES
('sarah_volunteer', 'sarah.volunteer@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'VOLUNTEER', 'Sarah', 'Johnson', NOW(), NOW()),
('alex_volunteer', 'alex.volunteer@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'VOLUNTEER', 'Alex', 'Smith', NOW(), NOW()),
('emma_volunteer', 'emma.volunteer@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'VOLUNTEER', 'Emma', 'Brown', NOW(), NOW()),
('david_volunteer', 'david.volunteer@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'VOLUNTEER', 'David', 'Wilson', NOW(), NOW());

-- Insertion des utilisateurs ORGANIZATION
INSERT INTO users (username, email, password, role, first_name, last_name, org_name, created_at, updated_at) VALUES
('redcross_org', 'redcross@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ORGANIZATION', 'Jean', 'Dupont', 'Croix Rouge Française', NOW(), NOW()),
('unicef_org', 'unicef@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ORGANIZATION', 'Marie', 'Martin', 'UNICEF France', NOW(), NOW()),
('wwf_org', 'wwf@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ORGANIZATION', 'Pierre', 'Bernard', 'WWF France', NOW(), NOW()),
('msf_org', 'msf@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ORGANIZATION', 'Sophie', 'Leroy', 'Médecins Sans Frontières', NOW(), NOW());

-- Vérification des insertions
SELECT 'VOLUNTEER' as role, COUNT(*) as count FROM users WHERE role = 'VOLUNTEER'
UNION ALL
SELECT 'ORGANIZATION' as role, COUNT(*) as count FROM users WHERE role = 'ORGANIZATION';

-- Affichage de tous les utilisateurs
SELECT id, username, email, role, first_name, last_name, org_name, created_at 
FROM users 
ORDER BY role, created_at;


