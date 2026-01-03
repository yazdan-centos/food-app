-- =====================================================
-- data.sql - Database Initialization Script
-- Rahkaran Util Application
-- =====================================================
-- This script initializes the database with default data
-- including privileges, roles, users, dishes, personnel,
-- daily meals, and reservations.
-- =====================================================

-- =====================================================
-- 1. CREATE PRIVILEGES
-- =====================================================
INSERT INTO privilege (id, name) VALUES (1, 'CREATE_USER');
INSERT INTO privilege (id, name) VALUES (2, 'EDIT_USER');
INSERT INTO privilege (id, name) VALUES (3, 'DELETE_USER');
INSERT INTO privilege (id, name) VALUES (4, 'VIEW_USER');
INSERT INTO privilege (id, name) VALUES (5, 'CREATE_DAILY_MEAL');
INSERT INTO privilege (id, name) VALUES (6, 'EDIT_DAILY_MEAL');
INSERT INTO privilege (id, name) VALUES (7, 'DELETE_DAILY_MEAL');
INSERT INTO privilege (id, name) VALUES (8, 'VIEW_DAILY_MEAL');
INSERT INTO privilege (id, name) VALUES (9, 'CREATE_DISH');
INSERT INTO privilege (id, name) VALUES (10, 'EDIT_DISH');
INSERT INTO privilege (id, name) VALUES (11, 'DELETE_DISH');
INSERT INTO privilege (id, name) VALUES (12, 'VIEW_DISH');
INSERT INTO privilege (id, name) VALUES (13, 'VIEW_REPORTS');
INSERT INTO privilege (id, name) VALUES (14, 'MANAGE_SETTINGS');
INSERT INTO privilege (id, name) VALUES (15, 'MANAGE_CONTRACTORS');
INSERT INTO privilege (id, name) VALUES (16, 'CREATE_RESERVATION');
INSERT INTO privilege (id, name) VALUES (17, 'EDIT_OWN_RESERVATION');
INSERT INTO privilege (id, name) VALUES (18, 'CANCEL_OWN_RESERVATION');
INSERT INTO privilege (id, name) VALUES (19, 'VIEW_OWN_PROFILE');
INSERT INTO privilege (id, name) VALUES (20, 'EDIT_OWN_PROFILE');
INSERT INTO privilege (id, name) VALUES (21, 'UPDATE_DISH_AVAILABILITY');
INSERT INTO privilege (id, name) VALUES (22, 'VIEW_ORDERS_TO_PREPARE');
INSERT INTO privilege (id, name) VALUES (23, 'UPDATE_ORDER_STATUS');

-- =====================================================
-- 2. CREATE ROLES
-- =====================================================
INSERT INTO role (id, name) VALUES (1, 'ROLE_ADMIN');
INSERT INTO role (id, name) VALUES (2, 'ROLE_USER');
INSERT INTO role (id, name) VALUES (3, 'ROLE_CONTRACTOR');

-- =====================================================
-- 3. ASSIGN PRIVILEGES TO ROLES
-- =====================================================

-- ROLE_ADMIN gets all privileges (1-23)
INSERT INTO roles_privileges (role_id, privilege_id) VALUES (1, 1);
INSERT INTO roles_privileges (role_id, privilege_id) VALUES (1, 2);
INSERT INTO roles_privileges (role_id, privilege_id) VALUES (1, 3);
INSERT INTO roles_privileges (role_id, privilege_id) VALUES (1, 4);
INSERT INTO roles_privileges (role_id, privilege_id) VALUES (1, 5);
INSERT INTO roles_privileges (role_id, privilege_id) VALUES (1, 6);
INSERT INTO roles_privileges (role_id, privilege_id) VALUES (1, 7);
INSERT INTO roles_privileges (role_id, privilege_id) VALUES (1, 8);
INSERT INTO roles_privileges (role_id, privilege_id) VALUES (1, 9);
INSERT INTO roles_privileges (role_id, privilege_id) VALUES (1, 10);
INSERT INTO roles_privileges (role_id, privilege_id) VALUES (1, 11);
INSERT INTO roles_privileges (role_id, privilege_id) VALUES (1, 12);
INSERT INTO roles_privileges (role_id, privilege_id) VALUES (1, 13);
INSERT INTO roles_privileges (role_id, privilege_id) VALUES (1, 14);
INSERT INTO roles_privileges (role_id, privilege_id) VALUES (1, 15);
INSERT INTO roles_privileges (role_id, privilege_id) VALUES (1, 16);
INSERT INTO roles_privileges (role_id, privilege_id) VALUES (1, 17);
INSERT INTO roles_privileges (role_id, privilege_id) VALUES (1, 18);
INSERT INTO roles_privileges (role_id, privilege_id) VALUES (1, 19);
INSERT INTO roles_privileges (role_id, privilege_id) VALUES (1, 20);
INSERT INTO roles_privileges (role_id, privilege_id) VALUES (1, 21);
INSERT INTO roles_privileges (role_id, privilege_id) VALUES (1, 22);
INSERT INTO roles_privileges (role_id, privilege_id) VALUES (1, 23);

-- ROLE_USER privileges
INSERT INTO roles_privileges (role_id, privilege_id) VALUES (2, 16); -- CREATE_RESERVATION
INSERT INTO roles_privileges (role_id, privilege_id) VALUES (2, 17); -- EDIT_OWN_RESERVATION
INSERT INTO roles_privileges (role_id, privilege_id) VALUES (2, 18); -- CANCEL_OWN_RESERVATION
INSERT INTO roles_privileges (role_id, privilege_id) VALUES (2, 19); -- VIEW_OWN_PROFILE
INSERT INTO roles_privileges (role_id, privilege_id) VALUES (2, 20); -- EDIT_OWN_PROFILE
INSERT INTO roles_privileges (role_id, privilege_id) VALUES (2, 8);  -- VIEW_DAILY_MEAL

-- ROLE_CONTRACTOR privileges
INSERT INTO roles_privileges (role_id, privilege_id) VALUES (3, 8);  -- VIEW_DAILY_MEAL
INSERT INTO roles_privileges (role_id, privilege_id) VALUES (3, 12); -- VIEW_DISH
INSERT INTO roles_privileges (role_id, privilege_id) VALUES (3, 21); -- UPDATE_DISH_AVAILABILITY
INSERT INTO roles_privileges (role_id, privilege_id) VALUES (3, 22); -- VIEW_ORDERS_TO_PREPARE
INSERT INTO roles_privileges (role_id, privilege_id) VALUES (3, 23); -- UPDATE_ORDER_STATUS

-- =====================================================
-- 4. CREATE USERS
-- =====================================================
-- Password for all users is BCrypt encoded 'password': $2a$10$N9qo8uLOickgx2ZMRZoMy.MqRqTyD/MwKNqW3S1H4qzJvZB3g3Y/K
-- Note: This is a sample BCrypt hash. In production, generate a proper hash.

-- Admin user
INSERT INTO users (id, username, password, first_name, last_name, enabled, token_expired, account_locked, account_expired, credentials_expired, employee_code)
VALUES (1, 'admin', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqRqTyD/MwKNqW3S1H4qzJvZB3g3Y/K', 'admin', 'user', true, false, false, false, false, NULL);

-- Sample users
INSERT INTO users (id, username, password, first_name, last_name, enabled, token_expired, account_locked, account_expired, credentials_expired, employee_code)
VALUES (2, 'adibi_m', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqRqTyD/MwKNqW3S1H4qzJvZB3g3Y/K', 'Seyed Mohammad', 'Adibi Vala', true, false, false, false, false, NULL);

INSERT INTO users (id, username, password, first_name, last_name, enabled, token_expired, account_locked, account_expired, credentials_expired, employee_code)
VALUES (3, 'dalaei_m', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqRqTyD/MwKNqW3S1H4qzJvZB3g3Y/K', 'Masoud', 'Dalaei', true, false, false, false, false, NULL);

INSERT INTO users (id, username, password, first_name, last_name, enabled, token_expired, account_locked, account_expired, credentials_expired, employee_code)
VALUES (4, 'mazloumi_m', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqRqTyD/MwKNqW3S1H4qzJvZB3g3Y/K', 'Mahdi', 'Mazloumi Gol', true, false, false, false, false, NULL);

INSERT INTO users (id, username, password, first_name, last_name, enabled, token_expired, account_locked, account_expired, credentials_expired, employee_code)
VALUES (5, 'esmaeili_m', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqRqTyD/MwKNqW3S1H4qzJvZB3g3Y/K', 'Mohammad', 'Esmaeil Pourganji', true, false, false, false, false, NULL);

INSERT INTO users (id, username, password, first_name, last_name, enabled, token_expired, account_locked, account_expired, credentials_expired, employee_code)
VALUES (6, 'ghanbari_m', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqRqTyD/MwKNqW3S1H4qzJvZB3g3Y/K', 'Rasoul', 'Ghanbari', true, false, false, false, false, NULL);

INSERT INTO users (id, username, password, first_name, last_name, enabled, token_expired, account_locked, account_expired, credentials_expired, employee_code)
VALUES (7, 'kholusi_m', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqRqTyD/MwKNqW3S1H4qzJvZB3g3Y/K', 'Ali', 'Kholousi', true, false, false, false, false, NULL);

INSERT INTO users (id, username, password, first_name, last_name, enabled, token_expired, account_locked, account_expired, credentials_expired, employee_code)
VALUES (8, 'bakhshi_j', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqRqTyD/MwKNqW3S1H4qzJvZB3g3Y/K', 'Javad', 'Bakhshi', true, false, false, false, false, NULL);

INSERT INTO users (id, username, password, first_name, last_name, enabled, token_expired, account_locked, account_expired, credentials_expired, employee_code)
VALUES (9, 'moshiri_m', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqRqTyD/MwKNqW3S1H4qzJvZB3g3Y/K', 'Mahdi', 'Moshiri', true, false, false, false, false, NULL);

INSERT INTO users (id, username, password, first_name, last_name, enabled, token_expired, account_locked, account_expired, credentials_expired, employee_code)
VALUES (10, 'hoseinabadi_m', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqRqTyD/MwKNqW3S1H4qzJvZB3g3Y/K', 'Asghar', 'Hossein Abadi', true, false, false, false, false, NULL);

INSERT INTO users (id, username, password, first_name, last_name, enabled, token_expired, account_locked, account_expired, credentials_expired, employee_code)
VALUES (11, 'habibi_a', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqRqTyD/MwKNqW3S1H4qzJvZB3g3Y/K', 'Amir Saleh', 'Habibi Khorasani', true, false, false, false, false, NULL);

-- =====================================================
-- 5. ASSIGN ROLES TO USERS
-- =====================================================
INSERT INTO users_roles (user_id, role_id) VALUES (1, 1);  -- admin has ROLE_ADMIN
INSERT INTO users_roles (user_id, role_id) VALUES (2, 2);  -- adibi_m has ROLE_USER
INSERT INTO users_roles (user_id, role_id) VALUES (3, 2);  -- dalaei_m has ROLE_USER
INSERT INTO users_roles (user_id, role_id) VALUES (4, 2);  -- mazloumi_m has ROLE_USER
INSERT INTO users_roles (user_id, role_id) VALUES (5, 2);  -- esmaeili_m has ROLE_USER
INSERT INTO users_roles (user_id, role_id) VALUES (6, 2);  -- ghanbari_m has ROLE_USER
INSERT INTO users_roles (user_id, role_id) VALUES (7, 2);  -- kholusi_m has ROLE_USER
INSERT INTO users_roles (user_id, role_id) VALUES (8, 2);  -- bakhshi_j has ROLE_USER
INSERT INTO users_roles (user_id, role_id) VALUES (9, 2);  -- moshiri_m has ROLE_USER
INSERT INTO users_roles (user_id, role_id) VALUES (10, 2); -- hoseinabadi_m has ROLE_USER
INSERT INTO users_roles (user_id, role_id) VALUES (11, 2); -- habibi_a has ROLE_USER

-- =====================================================
-- 6. CREATE DISHES
-- =====================================================
INSERT INTO dish (id, name, price, created_at, last_modified_at) VALUES (1, 'یتیمچه', 22500, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO dish (id, name, price, created_at, last_modified_at) VALUES (2, 'کشک بادمجان', 18000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO dish (id, name, price, created_at, last_modified_at) VALUES (3, 'کله جوش', 25000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO dish (id, name, price, created_at, last_modified_at) VALUES (4, 'تاس کباب یزدی', 28000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO dish (id, name, price, created_at, last_modified_at) VALUES (5, 'اشکنه', 17500, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO dish (id, name, price, created_at, last_modified_at) VALUES (6, 'خوراک کنگر', 20000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO dish (id, name, price, created_at, last_modified_at) VALUES (7, 'خوراک کدو', 16000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO dish (id, name, price, created_at, last_modified_at) VALUES (8, 'خوراک فصل', 19500, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO dish (id, name, price, created_at, last_modified_at) VALUES (9, 'خوراک قیمه', 24000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO dish (id, name, price, created_at, last_modified_at) VALUES (10, 'چی کوفته', 26000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO dish (id, name, price, created_at, last_modified_at) VALUES (11, 'کباب قارچ', 21000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO dish (id, name, price, created_at, last_modified_at) VALUES (12, 'خوراک جگر گوسفندی', 27000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO dish (id, name, price, created_at, last_modified_at) VALUES (13, 'خوراک میگو', 30000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO dish (id, name, price, created_at, last_modified_at) VALUES (14, 'خوراک بادمجان', 18500, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO dish (id, name, price, created_at, last_modified_at) VALUES (15, 'نیخود آب', 15000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO dish (id, name, price, created_at, last_modified_at) VALUES (16, 'خوراک لوبیا', 17000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO dish (id, name, price, created_at, last_modified_at) VALUES (17, 'بریانی', 29000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO dish (id, name, price, created_at, last_modified_at) VALUES (18, 'میرزا قاسمی', 19000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO dish (id, name, price, created_at, last_modified_at) VALUES (19, 'واویشکا', 23000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO dish (id, name, price, created_at, last_modified_at) VALUES (20, 'نون و پنیر و سبزیجات', 15000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO dish (id, name, price, created_at, last_modified_at) VALUES (21, 'آش کارده', 16500, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO dish (id, name, price, created_at, last_modified_at) VALUES (22, 'حلیم', 20000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO dish (id, name, price, created_at, last_modified_at) VALUES (23, 'کره و مربا', 15000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- =====================================================
-- 7. CREATE PERSONNEL
-- =====================================================
INSERT INTO personnel (id, pers_code, username, password, first_name, last_name, created_at, last_modified_at)
VALUES (1, '80262', 'habibi_a', '123', 'امیر صالح', 'حبیبی خراسانی', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO personnel (id, pers_code, username, password, first_name, last_name, created_at, last_modified_at)
VALUES (2, '80252', 'adibi_m', '123', 'سید محمد', 'ادیبی والا', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO personnel (id, pers_code, username, password, first_name, last_name, created_at, last_modified_at)
VALUES (3, '80259', 'dalaei_m', '123', 'مسعود', 'دالائی', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO personnel (id, pers_code, username, password, first_name, last_name, created_at, last_modified_at)
VALUES (4, '80306', 'mazloumi_m', '123', 'مهدی', 'مظلومی گل', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO personnel (id, pers_code, username, password, first_name, last_name, created_at, last_modified_at)
VALUES (5, '80248', 'esmaeili_m', '123', 'محمد', 'اسماعیل پورگنجی', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO personnel (id, pers_code, username, password, first_name, last_name, created_at, last_modified_at)
VALUES (6, '80891', 'ghanbari_m', '123', 'رسول', 'قنبری', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO personnel (id, pers_code, username, password, first_name, last_name, created_at, last_modified_at)
VALUES (7, '42583', 'kholusi_m', '123', 'علی', 'خلوصی', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO personnel (id, pers_code, username, password, first_name, last_name, created_at, last_modified_at)
VALUES (8, '80179', 'bakhshi_j', '123', 'جواد', 'بخشی', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO personnel (id, pers_code, username, password, first_name, last_name, created_at, last_modified_at)
VALUES (9, '80236', 'moshiri_m', '123', 'مهدی', 'مشیری', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO personnel (id, pers_code, username, password, first_name, last_name, created_at, last_modified_at)
VALUES (10, '80254', 'hoseinabadi_m', '123', 'اصغر', 'حسین آبادی', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- =====================================================
-- 8. CREATE APP SETTINGS (Singleton)
-- =====================================================
INSERT INTO app_settings (id, food_prices_active, employee_price_share, employer_price_share, reservation_hour, reservation_minute, company_name, address, phone, version, created_at, last_modified_at)
VALUES (1, true, 30, 70, 10, 0, 'شرکت مپنا', 'تهران، ایران', '021-12345678', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- =====================================================
-- 9. CREATE SAMPLE DAILY MEALS (for the current week)
-- =====================================================
-- Note: Jalali dates are calculated based on the Gregorian date 2025-04-03 to 2025-04-10
-- Using sample dates: 2025-04-05 (Saturday), 2025-04-06 (Sunday), 2025-04-07 (Monday), 2025-04-08 (Tuesday), 2025-04-09 (Wednesday)

-- Sample daily meals for a week (skipping Thursday and Friday which are weekends in Iran)
INSERT INTO daily_meal (id, meal_date, jalali_year, jalali_month, jalali_day, created_at, last_modified_at)
VALUES (1, '2025-04-05', 1404, 1, 16, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO daily_meal (id, meal_date, jalali_year, jalali_month, jalali_day, created_at, last_modified_at)
VALUES (2, '2025-04-06', 1404, 1, 17, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO daily_meal (id, meal_date, jalali_year, jalali_month, jalali_day, created_at, last_modified_at)
VALUES (3, '2025-04-07', 1404, 1, 18, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO daily_meal (id, meal_date, jalali_year, jalali_month, jalali_day, created_at, last_modified_at)
VALUES (4, '2025-04-08', 1404, 1, 19, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO daily_meal (id, meal_date, jalali_year, jalali_month, jalali_day, created_at, last_modified_at)
VALUES (5, '2025-04-09', 1404, 1, 20, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO daily_meal (id, meal_date, jalali_year, jalali_month, jalali_day, created_at, last_modified_at)
VALUES (6, '2025-04-12', 1404, 1, 23, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO daily_meal (id, meal_date, jalali_year, jalali_month, jalali_day, created_at, last_modified_at)
VALUES (7, '2025-04-13', 1404, 1, 24, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO daily_meal (id, meal_date, jalali_year, jalali_month, jalali_day, created_at, last_modified_at)
VALUES (8, '2025-04-14', 1404, 1, 25, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO daily_meal (id, meal_date, jalali_year, jalali_month, jalali_day, created_at, last_modified_at)
VALUES (9, '2025-04-15', 1404, 1, 26, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO daily_meal (id, meal_date, jalali_year, jalali_month, jalali_day, created_at, last_modified_at)
VALUES (10, '2025-04-16', 1404, 1, 27, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- =====================================================
-- 10. CREATE DAILY MEAL DISHES (3 dishes per daily meal)
-- =====================================================
-- Daily Meal 1 (2025-04-05)
INSERT INTO daily_meal_dish (id, daily_meal_id, dish_id) VALUES (1, 1, 1);
INSERT INTO daily_meal_dish (id, daily_meal_id, dish_id) VALUES (2, 1, 5);
INSERT INTO daily_meal_dish (id, daily_meal_id, dish_id) VALUES (3, 1, 9);

-- Daily Meal 2 (2025-04-06)
INSERT INTO daily_meal_dish (id, daily_meal_id, dish_id) VALUES (4, 2, 2);
INSERT INTO daily_meal_dish (id, daily_meal_id, dish_id) VALUES (5, 2, 6);
INSERT INTO daily_meal_dish (id, daily_meal_id, dish_id) VALUES (6, 2, 10);

-- Daily Meal 3 (2025-04-07)
INSERT INTO daily_meal_dish (id, daily_meal_id, dish_id) VALUES (7, 3, 3);
INSERT INTO daily_meal_dish (id, daily_meal_id, dish_id) VALUES (8, 3, 7);
INSERT INTO daily_meal_dish (id, daily_meal_id, dish_id) VALUES (9, 3, 11);

-- Daily Meal 4 (2025-04-08)
INSERT INTO daily_meal_dish (id, daily_meal_id, dish_id) VALUES (10, 4, 4);
INSERT INTO daily_meal_dish (id, daily_meal_id, dish_id) VALUES (11, 4, 8);
INSERT INTO daily_meal_dish (id, daily_meal_id, dish_id) VALUES (12, 4, 12);

-- Daily Meal 5 (2025-04-09)
INSERT INTO daily_meal_dish (id, daily_meal_id, dish_id) VALUES (13, 5, 13);
INSERT INTO daily_meal_dish (id, daily_meal_id, dish_id) VALUES (14, 5, 14);
INSERT INTO daily_meal_dish (id, daily_meal_id, dish_id) VALUES (15, 5, 15);

-- Daily Meal 6 (2025-04-12)
INSERT INTO daily_meal_dish (id, daily_meal_id, dish_id) VALUES (16, 6, 16);
INSERT INTO daily_meal_dish (id, daily_meal_id, dish_id) VALUES (17, 6, 17);
INSERT INTO daily_meal_dish (id, daily_meal_id, dish_id) VALUES (18, 6, 18);

-- Daily Meal 7 (2025-04-13)
INSERT INTO daily_meal_dish (id, daily_meal_id, dish_id) VALUES (19, 7, 19);
INSERT INTO daily_meal_dish (id, daily_meal_id, dish_id) VALUES (20, 7, 20);
INSERT INTO daily_meal_dish (id, daily_meal_id, dish_id) VALUES (21, 7, 21);

-- Daily Meal 8 (2025-04-14)
INSERT INTO daily_meal_dish (id, daily_meal_id, dish_id) VALUES (22, 8, 22);
INSERT INTO daily_meal_dish (id, daily_meal_id, dish_id) VALUES (23, 8, 23);
INSERT INTO daily_meal_dish (id, daily_meal_id, dish_id) VALUES (24, 8, 1);

-- Daily Meal 9 (2025-04-15)
INSERT INTO daily_meal_dish (id, daily_meal_id, dish_id) VALUES (25, 9, 2);
INSERT INTO daily_meal_dish (id, daily_meal_id, dish_id) VALUES (26, 9, 3);
INSERT INTO daily_meal_dish (id, daily_meal_id, dish_id) VALUES (27, 9, 4);

-- Daily Meal 10 (2025-04-16)
INSERT INTO daily_meal_dish (id, daily_meal_id, dish_id) VALUES (28, 10, 5);
INSERT INTO daily_meal_dish (id, daily_meal_id, dish_id) VALUES (29, 10, 6);
INSERT INTO daily_meal_dish (id, daily_meal_id, dish_id) VALUES (30, 10, 7);

-- =====================================================
-- 11. CREATE SAMPLE RESERVATIONS (for first 5 personnel for first 5 days)
-- =====================================================
-- Reservation for Personnel 1 (habibi_a) - Day 1
INSERT INTO reservation (id, personnel_id, daily_meal_id, daily_meal_dish_id, reservation_time, reservation_status, created_at, last_modified_at)
VALUES (1, 1, 1, 1, CURRENT_TIMESTAMP, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO cost_shares (id, reservation_id, quantity, total_cost, employee_portion, employer_portion, employee_share_percentage)
VALUES (1, 1, 1, 22500.00, 6750.00, 15750.00, 30.00);

-- Reservation for Personnel 2 (adibi_m) - Day 1
INSERT INTO reservation (id, personnel_id, daily_meal_id, daily_meal_dish_id, reservation_time, reservation_status, created_at, last_modified_at)
VALUES (2, 2, 1, 2, CURRENT_TIMESTAMP, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO cost_shares (id, reservation_id, quantity, total_cost, employee_portion, employer_portion, employee_share_percentage)
VALUES (2, 2, 1, 17500.00, 5250.00, 12250.00, 30.00);

-- Reservation for Personnel 3 (dalaei_m) - Day 1
INSERT INTO reservation (id, personnel_id, daily_meal_id, daily_meal_dish_id, reservation_time, reservation_status, created_at, last_modified_at)
VALUES (3, 3, 1, 3, CURRENT_TIMESTAMP, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO cost_shares (id, reservation_id, quantity, total_cost, employee_portion, employer_portion, employee_share_percentage)
VALUES (3, 3, 1, 24000.00, 7200.00, 16800.00, 30.00);

-- Reservation for Personnel 4 (mazloumi_m) - Day 2
INSERT INTO reservation (id, personnel_id, daily_meal_id, daily_meal_dish_id, reservation_time, reservation_status, created_at, last_modified_at)
VALUES (4, 4, 2, 4, CURRENT_TIMESTAMP, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO cost_shares (id, reservation_id, quantity, total_cost, employee_portion, employer_portion, employee_share_percentage)
VALUES (4, 4, 1, 18000.00, 5400.00, 12600.00, 30.00);

-- Reservation for Personnel 5 (esmaeili_m) - Day 2
INSERT INTO reservation (id, personnel_id, daily_meal_id, daily_meal_dish_id, reservation_time, reservation_status, created_at, last_modified_at)
VALUES (5, 5, 2, 5, CURRENT_TIMESTAMP, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO cost_shares (id, reservation_id, quantity, total_cost, employee_portion, employer_portion, employee_share_percentage)
VALUES (5, 5, 1, 20000.00, 6000.00, 14000.00, 30.00);

-- Reservation for Personnel 6 (ghanbari_m) - Day 3
INSERT INTO reservation (id, personnel_id, daily_meal_id, daily_meal_dish_id, reservation_time, reservation_status, created_at, last_modified_at)
VALUES (6, 6, 3, 7, CURRENT_TIMESTAMP, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO cost_shares (id, reservation_id, quantity, total_cost, employee_portion, employer_portion, employee_share_percentage)
VALUES (6, 6, 1, 25000.00, 7500.00, 17500.00, 30.00);

-- Reservation for Personnel 7 (kholusi_m) - Day 3
INSERT INTO reservation (id, personnel_id, daily_meal_id, daily_meal_dish_id, reservation_time, reservation_status, created_at, last_modified_at)
VALUES (7, 7, 3, 8, CURRENT_TIMESTAMP, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO cost_shares (id, reservation_id, quantity, total_cost, employee_portion, employer_portion, employee_share_percentage)
VALUES (7, 7, 1, 16000.00, 4800.00, 11200.00, 30.00);

-- Reservation for Personnel 8 (bakhshi_j) - Day 4
INSERT INTO reservation (id, personnel_id, daily_meal_id, daily_meal_dish_id, reservation_time, reservation_status, created_at, last_modified_at)
VALUES (8, 8, 4, 10, CURRENT_TIMESTAMP, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO cost_shares (id, reservation_id, quantity, total_cost, employee_portion, employer_portion, employee_share_percentage)
VALUES (8, 8, 1, 28000.00, 8400.00, 19600.00, 30.00);

-- Reservation for Personnel 9 (moshiri_m) - Day 4
INSERT INTO reservation (id, personnel_id, daily_meal_id, daily_meal_dish_id, reservation_time, reservation_status, created_at, last_modified_at)
VALUES (9, 9, 4, 11, CURRENT_TIMESTAMP, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO cost_shares (id, reservation_id, quantity, total_cost, employee_portion, employer_portion, employee_share_percentage)
VALUES (9, 9, 1, 19500.00, 5850.00, 13650.00, 30.00);

-- Reservation for Personnel 10 (hoseinabadi_m) - Day 5
INSERT INTO reservation (id, personnel_id, daily_meal_id, daily_meal_dish_id, reservation_time, reservation_status, created_at, last_modified_at)
VALUES (10, 10, 5, 13, CURRENT_TIMESTAMP, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO cost_shares (id, reservation_id, quantity, total_cost, employee_portion, employer_portion, employee_share_percentage)
VALUES (10, 10, 1, 30000.00, 9000.00, 21000.00, 30.00);

-- =====================================================
-- 12. UPDATE SEQUENCES (for PostgreSQL)
-- =====================================================
-- Uncomment these lines if using PostgreSQL to update sequences after manual inserts
SELECT setval('privilege_id_seq', (SELECT MAX(id) FROM privilege));
SELECT setval('role_id_seq', (SELECT MAX(id) FROM role));
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));
SELECT setval('dish_id_seq', (SELECT MAX(id) FROM dish));
SELECT setval('personnel_id_seq', (SELECT MAX(id) FROM personnel));
SELECT setval('daily_meal_id_seq', (SELECT MAX(id) FROM daily_meal));
SELECT setval('daily_meal_dish_id_seq', (SELECT MAX(id) FROM daily_meal_dish));
SELECT setval('reservation_id_seq', (SELECT MAX(id) FROM reservation));
SELECT setval('cost_shares_id_seq', (SELECT MAX(id) FROM cost_shares));

-- =====================================================
-- END OF INITIALIZATION SCRIPT
-- =====================================================

