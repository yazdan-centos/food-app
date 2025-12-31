-- Enable clean reruns
TRUNCATE TABLE roles_privileges, users_roles, reservation, cost_shares, guest, daily_meal_dish, daily_meal, personnel, dish, users, role, privilege, cost_center, app_settings RESTART IDENTITY CASCADE;

-- === Privileges ===
INSERT INTO privilege (id, name) VALUES
  (nextval('privilege_seq'), 'CREATE_USER'),
  (nextval('privilege_seq'), 'EDIT_USER'),
  (nextval('privilege_seq'), 'DELETE_USER'),
  (nextval('privilege_seq'), 'VIEW_USER'),
  (nextval('privilege_seq'), 'CREATE_DAILY_MEAL'),
  (nextval('privilege_seq'), 'EDIT_DAILY_MEAL'),
  (nextval('privilege_seq'), 'DELETE_DAILY_MEAL'),
  (nextval('privilege_seq'), 'VIEW_DAILY_MEAL'),
  (nextval('privilege_seq'), 'CREATE_DISH'),
  (nextval('privilege_seq'), 'EDIT_DISH'),
  (nextval('privilege_seq'), 'DELETE_DISH'),
  (nextval('privilege_seq'), 'VIEW_DISH'),
  (nextval('privilege_seq'), 'VIEW_REPORTS'),
  (nextval('privilege_seq'), 'MANAGE_SETTINGS'),
  (nextval('privilege_seq'), 'MANAGE_CONTRACTORS'),
  (nextval('privilege_seq'), 'CREATE_RESERVATION'),
  (nextval('privilege_seq'), 'EDIT_OWN_RESERVATION'),
  (nextval('privilege_seq'), 'CANCEL_OWN_RESERVATION'),
  (nextval('privilege_seq'), 'VIEW_OWN_PROFILE'),
  (nextval('privilege_seq'), 'EDIT_OWN_PROFILE'),
  (nextval('privilege_seq'), 'UPDATE_DISH_AVAILABILITY'),
  (nextval('privilege_seq'), 'VIEW_ORDERS_TO_PREPARE'),
  (nextval('privilege_seq'), 'UPDATE_ORDER_STATUS');

-- === Roles ===
INSERT INTO role (id, name) VALUES
  (nextval('role_seq'), 'ROLE_ADMIN'),
  (nextval('role_seq'), 'ROLE_USER'),
  (nextval('role_seq'), 'ROLE_CONTRACTOR');

-- === Role ↔ Privilege mapping ===
-- Admin gets all privileges
INSERT INTO roles_privileges (role_id, privilege_id)
SELECT r.id, p.id FROM role r CROSS JOIN privilege p WHERE r.name = 'ROLE_ADMIN';

-- User subset
INSERT INTO roles_privileges (role_id, privilege_id)
SELECT r.id, p.id FROM role r JOIN privilege p ON p.name IN (
  'CREATE_RESERVATION','EDIT_OWN_RESERVATION','CANCEL_OWN_RESERVATION',
  'VIEW_OWN_PROFILE','EDIT_OWN_PROFILE','VIEW_DAILY_MEAL'
) WHERE r.name = 'ROLE_USER';

-- Contractor subset
INSERT INTO roles_privileges (role_id, privilege_id)
SELECT r.id, p.id FROM role r JOIN privilege p ON p.name IN (
  'VIEW_DAILY_MEAL','VIEW_DISH','UPDATE_DISH_AVAILABILITY',
  'VIEW_ORDERS_TO_PREPARE','UPDATE_ORDER_STATUS'
) WHERE r.name = 'ROLE_CONTRACTOR';

-- === Users ===
-- bcrypt hash for the literal password "password"
INSERT INTO users (id, first_name, last_name, username, employee_code, password, enabled, token_expired, account_locked, account_expired, credentials_expired) VALUES
  (nextval('users_seq'), 'admin', 'user', 'admin', NULL, '{bcrypt}$2a$10$7EqJtq98hPqEX7fNZaFWoOQ5zL.KhO6Uq1bR7lQ0WAW7v1/uuOeWy', true, false, false, false, false),
  (nextval('users_seq'), 'Seyed Mohammad', 'Adibi Vala', 'adibi_m', NULL, '{bcrypt}$2a$10$7EqJtq98hPqEX7fNZaFWoOQ5zL.KhO6Uq1bR7lQ0WAW7v1/uuOeWy', true, false, false, false, false),
  (nextval('users_seq'), 'Masoud', 'Dalaei', 'dalaei_m', NULL, '{bcrypt}$2a$10$7EqJtq98hPqEX7fNZaFWoOQ5zL.KhO6Uq1bR7lQ0WAW7v1/uuOeWy', true, false, false, false, false),
  (nextval('users_seq'), 'Mahdi', 'Mazloumi Gol', 'mazloumi_m', NULL, '{bcrypt}$2a$10$7EqJtq98hPqEX7fNZaFWoOQ5zL.KhO6Uq1bR7lQ0WAW7v1/uuOeWy', true, false, false, false, false),
  (nextval('users_seq'), 'Mohammad', 'Esmaeil Pourganji', 'esmaeili_m', NULL, '{bcrypt}$2a$10$7EqJtq98hPqEX7fNZaFWoOQ5zL.KhO6Uq1bR7lQ0WAW7v1/uuOeWy', true, false, false, false, false),
  (nextval('users_seq'), 'Rasoul', 'Ghanbari', 'ghanbari_m', NULL, '{bcrypt}$2a$10$7EqJtq98hPqEX7fNZaFWoOQ5zL.KhO6Uq1bR7lQ0WAW7v1/uuOeWy', true, false, false, false, false),
  (nextval('users_seq'), 'Ali', 'Kholousi', 'kholusi_m', NULL, '{bcrypt}$2a$10$7EqJtq98hPqEX7fNZaFWoOQ5zL.KhO6Uq1bR7lQ0WAW7v1/uuOeWy', true, false, false, false, false),
  (nextval('users_seq'), 'Javad', 'Bakhshi', 'bakhshi_j', NULL, '{bcrypt}$2a$10$7EqJtq98hPqEX7fNZaFWoOQ5zL.KhO6Uq1bR7lQ0WAW7v1/uuOeWy', true, false, false, false, false),
  (nextval('users_seq'), 'Mahdi', 'Moshiri', 'moshiri_m', NULL, '{bcrypt}$2a$10$7EqJtq98hPqEX7fNZaFWoOQ5zL.KhO6Uq1bR7lQ0WAW7v1/uuOeWy', true, false, false, false, false),
  (nextval('users_seq'), 'Asghar', 'Hossein Abadi', 'hoseinabadi_m', NULL, '{bcrypt}$2a$10$7EqJtq98hPqEX7fNZaFWoOQ5zL.KhO6Uq1bR7lQ0WAW7v1/uuOeWy', true, false, false, false, false),
  (nextval('users_seq'), 'Amir Saleh', 'Habibi Khorasani', 'habibi_a', NULL, '{bcrypt}$2a$10$7EqJtq98hPqEX7fNZaFWoOQ5zL.KhO6Uq1bR7lQ0WAW7v1/uuOeWy', true, false, false, false, false);

-- User ↔ Role mapping
INSERT INTO users_roles (role_id, user_id)
SELECT r.id, u.id FROM role r JOIN users u ON r.name = 'ROLE_ADMIN' AND u.username = 'admin';
INSERT INTO users_roles (role_id, user_id)
SELECT r.id, u.id FROM role r JOIN users u ON r.name = 'ROLE_USER' AND u.username <> 'admin';

-- === Personnel ===
INSERT INTO personnel (username, password, pers_code, first_name, last_name) VALUES
 ('habibi_a','123','80262','امیر صالح','حبیبی خراسانی'),
 ('adibi_m','123','80252','سید محمد','ادیبی والا'),
 ('dalaei_m','123','80259','مسعود','دالائی'),
 ('mazloumi_m','123','80306','مهدی','مظلومی گل'),
 ('esmaeili_m','123','80248','محمد','اسماعیل پورگنجی'),
 ('ghanbari_m','123','80891','رسول','قنبری'),
 ('kholusi_m','123','42583','علی','خلوصی'),
 ('bakhshi_j','123','80179','جواد','بخشی'),
 ('moshiri_m','123','80236','مهدی','مشیری'),
 ('hoseinabadi_m','123','80254','اصغر','حسین آبادی');

-- === Dishes ===
INSERT INTO dish (name, price) VALUES
 ('یتیمچه', 25000), ('کشک بادمجان', 24000), ('کله جوش', 23000), ('تاس کباب یزدی', 26000),
 ('اشکنه', 20000), ('خوراک کنگر', 27000), ('خوراک کدو', 22000), ('خوراک فصل', 23000),
 ('خوراک قیمه', 28000), ('چی کوفته', 24000), ('کباب قارچ', 26000),
 ('خوراک جگر گوسفندی', 32000), ('خوراک میگو', 35000), ('خوراک بادمجان', 23000),
 ('نیخود آب', 20000), ('خوراک لوبیا', 21000), ('بریانی', 33000), ('میرزا قاسمی', 26000),
 ('واویشکا', 25000), ('نون و پنیر و سبزیجات', 18000), ('آش کارده', 19000),
 ('حلیم', 22000), ('کره و مربا', 17000);

-- === App settings (singleton) ===
INSERT INTO app_settings (id, food_prices_active, company_name, address, phone, version, employee_price_share, employer_price_share, reservation_hour, reservation_minute)
VALUES (1, true, 'Demo Company', 'Demo Address', '000-0000', 1, 30, 70, 10, 0);

-- === Cost center (for potential guests) ===
INSERT INTO cost_center (name) VALUES ('Default Cost Center');

-- === Daily meals (3 working days) ===
-- Gregorian: 2025-04-07, 2025-04-08, 2025-04-09
-- Jalali:    1404-01-18, 1404-01-19, 1404-01-20
INSERT INTO daily_meal (meal_date, jalali_month, jalali_day, jalali_year) VALUES
 ('2025-04-07', 1, 18, 1404),
 ('2025-04-08', 1, 19, 1404),
 ('2025-04-09', 1, 20, 1404);

-- === Daily meal dishes (3 per day) ===
INSERT INTO daily_meal_dish (daily_meal_id, dish_id)
SELECT dm.id, d.id FROM daily_meal dm JOIN dish d ON dm.meal_date = '2025-04-07' AND d.name IN ('یتیمچه','کشک بادمجان','کله جوش');
INSERT INTO daily_meal_dish (daily_meal_id, dish_id)
SELECT dm.id, d.id FROM daily_meal dm JOIN dish d ON dm.meal_date = '2025-04-08' AND d.name IN ('خوراک قیمه','کباب قارچ','بریانی');
INSERT INTO daily_meal_dish (daily_meal_id, dish_id)
SELECT dm.id, d.id FROM daily_meal dm JOIN dish d ON dm.meal_date = '2025-04-09' AND d.name IN ('میرزا قاسمی','واویشکا','حلیم');

-- === Reservations (sample) ===
-- Reservation status set to 'CREATED'
INSERT INTO reservation (personnel_id, daily_meal_id, daily_meal_dish_id, reservation_time, reservation_status)
SELECT p.id, dm.id, dmd.id, NOW(), 'CREATED'
FROM personnel p
JOIN daily_meal dm ON dm.meal_date = '2025-04-07'
JOIN daily_meal_dish dmd ON dmd.daily_meal_id = dm.id
JOIN dish d ON d.id = dmd.dish_id AND d.name = 'یتیمچه'
WHERE p.username = 'habibi_a';

INSERT INTO reservation (personnel_id, daily_meal_id, daily_meal_dish_id, reservation_time, reservation_status)
SELECT p.id, dm.id, dmd.id, NOW(), 'CREATED'
FROM personnel p
JOIN daily_meal dm ON dm.meal_date = '2025-04-08'
JOIN daily_meal_dish dmd ON dmd.daily_meal_id = dm.id
JOIN dish d ON d.id = dmd.dish_id AND d.name = 'کباب قارچ'
WHERE p.username = 'adibi_m';

INSERT INTO reservation (personnel_id, daily_meal_id, daily_meal_dish_id, reservation_time, reservation_status)
SELECT p.id, dm.id, dmd.id, NOW(), 'CREATED'
FROM personnel p
JOIN daily_meal dm ON dm.meal_date = '2025-04-09'
JOIN daily_meal_dish dmd ON dmd.daily_meal_id = dm.id
JOIN dish d ON d.id = dmd.dish_id AND d.name = 'حلیم'
WHERE p.username = 'dalaei_m';

-- === Cost shares (30% employee / 70% employer from app_settings) ===
INSERT INTO cost_shares (employee_portion, employee_share_percentage, employer_portion, quantity, total_cost, reservation_id)
SELECT
  ROUND(d.price * 0.30, 2) AS employee_portion,
  30.00 AS employee_share_percentage,
  ROUND(d.price * 0.70, 2) AS employer_portion,
  1 AS quantity,
  d.price AS total_cost,
  r.id AS reservation_id
FROM reservation r
JOIN daily_meal_dish dmd ON r.daily_meal_dish_id = dmd.id
JOIN dish d ON dmd.dish_id = d.id;