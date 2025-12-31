-- =====================================================
-- Roles, Privileges, and Users Initialization Script
-- =====================================================
-- This script initializes the database with roles, privileges,
-- and their mappings with corrected role IDs.
--
-- Corrected Role IDs:
--   1 = ROLE_ADMIN
--   2 = ROLE_USER  
--   3 = ROLE_CONTRACTOR
-- =====================================================

-- Insert Privileges
INSERT INTO privilege (id, name) VALUES
(1, 'READ_PRIVILEGE'),
(2, 'WRITE_PRIVILEGE'),
(3, 'DELETE_PRIVILEGE'),
(4, 'UPDATE_PRIVILEGE'),
(5, 'CREATE_USER_PRIVILEGE'),
(6, 'UPDATE_USER_PRIVILEGE'),
(7, 'DELETE_USER_PRIVILEGE'),
(8, 'READ_USER_PRIVILEGE'),
(9, 'CREATE_DISH_PRIVILEGE'),
(10, 'UPDATE_DISH_PRIVILEGE'),
(11, 'DELETE_DISH_PRIVILEGE'),
(12, 'READ_DISH_PRIVILEGE'),
(13, 'CREATE_DAILY_MEAL_PRIVILEGE'),
(14, 'UPDATE_DAILY_MEAL_PRIVILEGE'),
(15, 'DELETE_DAILY_MEAL_PRIVILEGE'),
(16, 'READ_DAILY_MEAL_PRIVILEGE'),
(17, 'CREATE_RESERVATION_PRIVILEGE'),
(18, 'UPDATE_RESERVATION_PRIVILEGE'),
(19, 'READ_RESERVATION_PRIVILEGE'),
(20, 'DELETE_RESERVATION_PRIVILEGE'),
(21, 'CREATE_PERSONNEL_PRIVILEGE'),
(22, 'UPDATE_PERSONNEL_PRIVILEGE'),
(23, 'READ_PERSONNEL_PRIVILEGE'),
(24, 'DELETE_PERSONNEL_PRIVILEGE');

-- Insert Roles with Corrected IDs
INSERT INTO role (id, name) VALUES
(1, 'ROLE_ADMIN'),
(2, 'ROLE_USER'),
(3, 'ROLE_CONTRACTOR');

-- Map Roles to Privileges
-- ROLE_ADMIN (id=1) gets all privileges
INSERT INTO roles_privileges (role_id, privilege_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8),
(1, 9), (1, 10), (1, 11), (1, 12), (1, 13), (1, 14), (1, 15), (1, 16),
(1, 17), (1, 18), (1, 19), (1, 20), (1, 21), (1, 22), (1, 23), (1, 24);

-- ROLE_USER (id=2) gets limited privileges: 8, 15, 16, 17, 18, 19, 23
INSERT INTO roles_privileges (role_id, privilege_id) VALUES
(2, 8),  -- READ_USER_PRIVILEGE
(2, 15), -- DELETE_DAILY_MEAL_PRIVILEGE
(2, 16), -- READ_DAILY_MEAL_PRIVILEGE
(2, 17), -- CREATE_RESERVATION_PRIVILEGE
(2, 18), -- UPDATE_RESERVATION_PRIVILEGE
(2, 19), -- READ_RESERVATION_PRIVILEGE
(2, 23); -- READ_PERSONNEL_PRIVILEGE

-- ROLE_CONTRACTOR (id=3) gets privileges: 8, 12, 21, 22, 23
INSERT INTO roles_privileges (role_id, privilege_id) VALUES
(3, 8),  -- READ_USER_PRIVILEGE
(3, 12), -- READ_DISH_PRIVILEGE
(3, 21), -- CREATE_PERSONNEL_PRIVILEGE
(3, 22), -- UPDATE_PERSONNEL_PRIVILEGE
(3, 23); -- READ_PERSONNEL_PRIVILEGE

-- Insert Users
INSERT INTO users (id, username, password, first_name, last_name, enabled, token_expired, account_locked, account_expired, credentials_expired) VALUES
(1, 'admin', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'Admin', 'User', true, false, false, false, false),
(2, 'user', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'Regular', 'User', true, false, false, false, false),
(3, 'contractor', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'Contractor', 'User', true, false, false, false, false);

-- Map Users to Roles with Corrected role_id values
INSERT INTO users_roles (user_id, role_id) VALUES
(1, 1), -- admin user gets ROLE_ADMIN (id=1)
(2, 2), -- regular user gets ROLE_USER (id=2)
(3, 3); -- contractor user gets ROLE_CONTRACTOR (id=3)
