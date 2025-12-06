#!/bin/bash

# =====================================================================
# PostgreSQL Status Script for AlmaLinux 9
# =====================================================================

set -e

COLOR_CYAN="\033[0;36m"
COLOR_YELLOW="\033[1;33m"
COLOR_GREEN="\033[0;32m"
COLOR_RED="\033[0;31m"
COLOR_RESET="\033[0m"

header() {
    echo -e "\n${COLOR_CYAN}==============================================================${COLOR_RESET}"
    echo -e "${COLOR_YELLOW}$1${COLOR_RESET}"
    echo -e "${COLOR_CYAN}==============================================================${COLOR_RESET}"
}

# ----------------------------
# 1. Check psql availability
# ----------------------------
header "CHECKING POSTGRESQL INSTALLATION"

if ! command -v psql >/dev/null 2>&1; then
    echo -e "${COLOR_RED}PostgreSQL client not installed.${COLOR_RESET}"
    echo "Install with:"
    echo "  sudo dnf install postgresql postgresql-server"
    exit 1
fi

echo -e "${COLOR_GREEN}PostgreSQL client detected.${COLOR_RESET}"

# ----------------------------
# 2. Service status
# ----------------------------
header "POSTGRESQL SERVICE STATUS"

systemctl status postgresql --no-pager -l | head -20 || true

if systemctl is-active --quiet postgresql; then
    echo -e "${COLOR_GREEN}Service is running.${COLOR_RESET}"
else
    echo -e "${COLOR_RED}PostgreSQL service is NOT running. Some queries will fail.${COLOR_RESET}"
fi

# ----------------------------
# 3. Version info
# ----------------------------
header "POSTGRESQL VERSION"

echo "Client version:"
psql --version

echo
echo "Server version:"
sudo -u postgres psql -t -c "SELECT version();" 2>/dev/null || echo "Server not reachable."

# ----------------------------
# 4. Database information
# ----------------------------
header "DATABASE LIST"

sudo -u postgres psql -c "
SELECT
    datname AS database,
    pg_get_userbyid(datdba) AS owner,
    pg_size_pretty(pg_database_size(datname)) AS size,
    CASE WHEN datistemplate THEN 'yes' ELSE 'no' END AS template
FROM pg_database
ORDER BY datname;
" 2>/dev/null || echo "Unable to fetch database list."

# ----------------------------
# 5. Roles and attributes
# ----------------------------
header "ROLES (USERS)"

sudo -u postgres psql -c "
SELECT
    rolname AS role,
    CASE WHEN rolsuper THEN 'yes' ELSE 'no' END AS superuser,
    CASE WHEN rolcreaterole THEN 'yes' ELSE 'no' END AS create_role,
    CASE WHEN rolcreatedb THEN 'yes' ELSE 'no' END AS create_db,
    CASE WHEN rolcanlogin THEN 'yes' ELSE 'no' END AS login,
    rolconnlimit AS conn_limit
FROM pg_roles
ORDER BY rolname;
" 2>/dev/null || echo "Unable to fetch roles."

# ----------------------------
# 6. Role memberships
# ----------------------------
header "ROLE MEMBERSHIPS"

sudo -u postgres psql -c "
SELECT
    r.rolname AS role,
    STRING_AGG(m.rolname, ', ') AS member_of
FROM pg_roles r
LEFT JOIN pg_auth_members am ON am.member = r.oid
LEFT JOIN pg_roles m ON m.oid = am.roleid
GROUP BY r.rolname
HAVING COUNT(m.rolname) > 0
ORDER BY r.rolname;
" 2>/dev/null || echo "Unable to fetch role memberships."

# ----------------------------
# 7. Database privileges
# ----------------------------
header "DATABASE PRIVILEGES"

sudo -u postgres psql -c "
SELECT
    datname AS database,
    datacl AS privileges
FROM pg_database
WHERE datacl IS NOT NULL
ORDER BY datname;
" 2>/dev/null || echo "No database privileges found."

# ----------------------------
# 8. Schema privileges per database
# ----------------------------
header "SCHEMA PRIVILEGES (PER DATABASE)"

DB_LIST=$(sudo -u postgres psql -t -c "SELECT datname FROM pg_database WHERE datistemplate = false;")

for DB in $DB_LIST; do
    DB=$(echo "$DB" | xargs)
    echo -e "\n--- Database: $DB ---"

    sudo -u postgres psql -d "$DB" -c "
        SELECT
            nspname AS schema,
            pg_get_userbyid(nspowner) AS owner,
            nspacl AS privileges
        FROM pg_namespace
        WHERE nspname NOT LIKE 'pg_%' AND nspname != 'information_schema'
        ORDER BY nspname;
    " 2>/dev/null || echo "No access to $DB"
done

# ----------------------------
# 9. Table privileges per database
# ----------------------------
header "TABLE PRIVILEGES (PER DATABASE)"

for DB in $DB_LIST; do
    DB=$(echo "$DB" | xargs)
    echo -e "\n--- Database: $DB ---"

    sudo -u postgres psql -d "$DB" -c "
        SELECT
            table_schema AS schema,
            table_name AS table,
            grantee,
            privilege_type
        FROM information_schema.table_privileges
        WHERE table_schema NOT IN ('pg_catalog', 'information_schema')
        ORDER BY table_schema, table_name, grantee;
    " 2>/dev/null || echo "No access to $DB"
done

# ----------------------------
# 10. Active connections
# ----------------------------
header "ACTIVE CONNECTIONS"

sudo -u postgres psql -c "
SELECT
    pid,
    usename AS user,
    datname AS database,
    client_addr AS client_ip,
    state,
    LEFT(query, 50) AS query
FROM pg_stat_activity
WHERE pid <> pg_backend_pid()
ORDER BY backend_start;
" 2>/dev/null || echo "Unable to fetch active connection list."

# ----------------------------
# 11. pg_hba.conf summary
# ----------------------------
header "pg_hba.conf RULES"

sudo -u postgres psql -c "
SELECT
    line_number,
    type,
    database,
    user_name,
    address,
    auth_method
FROM pg_hba_file_rules
WHERE error IS NULL
ORDER BY line_number;
" 2>/dev/null || {
    echo "pg_hba_file_rules not supported. Dumping raw file:"
    FILE=$(sudo -u postgres psql -t -c "SHOW hba_file;" | xargs)
    sudo grep -v '^#' "$FILE" | grep -v '^$'
}

echo -e "\n${COLOR_GREEN}Completed.${COLOR_RESET}"
