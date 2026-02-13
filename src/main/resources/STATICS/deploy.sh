#!/usr/bin/env bash
# =============================================================================
# deploy.sh — Food App Full-Stack Deployment (Revised)
# =============================================================================
# Repositories:
#   Backend:  github.com/yazdan-centos/food-app         (Spring Boot 3.4 / Java 17)
#   Frontend: github.com/yazdan-centos/daily-meal-web-app (React CRA / react-router-dom v7)
#
# Stack: Java 17 + Spring Boot + React + PostgreSQL 15 + Nginx
# Target: RHEL 8 / CentOS Stream 8 / AlmaLinux 8 / Rocky Linux 8
#
# Usage:
#   1. Place deploy.env alongside this script
#   2. chmod +x deploy.sh
#   3. sudo ./deploy.sh
# =============================================================================

set -Eeuo pipefail
trap 'echo -e "\n\033[1;31m✖ FAILED at line $LINENO. Check output above.\033[0m" >&2' ERR

# =============================================================================
# 0. LOAD & VALIDATE CONFIGURATION
# =============================================================================

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ENV_FILE="${SCRIPT_DIR}/deploy.env"

if [[ ! -f "$ENV_FILE" ]]; then
    cat <<'EOF'

  ✖ Missing deploy.env file!

  Create deploy.env alongside this script. See deploy.env.example.

EOF
    exit 1
fi

# shellcheck source=/dev/null
source "$ENV_FILE"

# --- Validate all required variables ---
REQUIRED_VARS=(
    IP_ADDRESS GITHUB_USER GITHUB_PAT
    DB_NAME DB_USER DB_PASS DB_PORT
    BACKEND_REPO_URL BACKEND_PORT BACKEND_DIR
    FRONTEND_REPO_URL FRONTEND_PORT FRONTEND_DIR
)

MISSING=()
for var in "${REQUIRED_VARS[@]}"; do
    [[ -z "${!var:-}" ]] && MISSING+=("$var")
done

if [[ ${#MISSING[@]} -gt 0 ]]; then
    echo "✖ Missing required variables in deploy.env:" >&2
    printf '    - %s\n' "${MISSING[@]}" >&2
    exit 1
fi

# --- Derived constants ---
REPO_DIR="/srv/repos"
BACKEND_BUILD_DIR="${REPO_DIR}/backend"
FRONTEND_BUILD_DIR="${REPO_DIR}/frontend"
DB_LISTEN_ADDRESS="${DB_LISTEN_ADDRESS:-0.0.0.0}"
DB_ALLOWED_CIDR="${DB_ALLOWED_CIDR:-${IP_ADDRESS}/32}"

# =============================================================================
# HELPER FUNCTIONS
# =============================================================================

log()  { echo -e "\n\033[1;32m>>>\033[0m \033[1m$*\033[0m"; }
warn() { echo -e "    \033[1;33m⚠\033[0m  $*"; }
ok()   { echo -e "    \033[1;32m✔\033[0m  $*"; }
err()  { echo -e "    \033[1;31m✖\033[0m  $*" >&2; }
cmd_exists() { command -v "$1" &>/dev/null; }

# =============================================================================
# 1. INSTALL SYSTEM PACKAGES
# =============================================================================

log "Step 1/8 — Installing system packages"

# ─── Java 17 ────────────────────────────────────────────────────────────
# The pom.xml requires Java 17. Lombok 1.18.28 is INCOMPATIBLE with Java 21+.
# If Java 21 is the active JDK, the build fails with:
#   NoSuchFieldError: JCTree$JCImport does not have member field 'qualid'
# We must install Java 17 AND ensure it is the ACTIVE default.
# ────────────────────────────────────────────────────────────────────────

echo "    Installing/verifying Java 17..."
dnf install -y java-17-openjdk java-17-openjdk-devel

# Locate the Java 17 installation directory
JAVA17_PATH=$(find /usr/lib/jvm -maxdepth 1 -name "java-17-openjdk-*" -type d | sort | head -1)

if [[ -z "$JAVA17_PATH" ]]; then
    err "Java 17 installation directory not found in /usr/lib/jvm!"
    echo "      Contents of /usr/lib/jvm/:"
    ls -d /usr/lib/jvm/java-* 2>/dev/null || echo "        (empty)"
    exit 1
fi

# Force Java 17 as the system default
alternatives --set java  "${JAVA17_PATH}/bin/java"   2>/dev/null || \
    alternatives --install /usr/bin/java java "${JAVA17_PATH}/bin/java" 1700

alternatives --set javac "${JAVA17_PATH}/bin/javac"  2>/dev/null || \
    alternatives --install /usr/bin/javac javac "${JAVA17_PATH}/bin/javac" 1700

# Export JAVA_HOME for this script session + Maven
export JAVA_HOME="$JAVA17_PATH"
export PATH="${JAVA_HOME}/bin:${PATH}"

# Verify the active version is actually 17
ACTIVE_VERSION=$(java -version 2>&1 | head -1)
if echo "$ACTIVE_VERSION" | grep -q '"17\.'; then
    ok "Java 17 is active: ${ACTIVE_VERSION}"
    ok "JAVA_HOME=${JAVA_HOME}"
else
    err "Expected Java 17 but got: ${ACTIVE_VERSION}"
    echo ""
    echo "      All installed JVMs:"
    ls -d /usr/lib/jvm/java-* 2>/dev/null
    echo ""
    echo "      To fix manually:  alternatives --config java"
    echo "      Or remove Java 21: dnf remove java-21-openjdk*"
    exit 1
fi

# ─── Node.js 20 ──────────────────────────────────────────────────────────
if ! cmd_exists node; then
    echo "    Installing Node.js 20..."
    dnf module reset -y nodejs 2>/dev/null || true
    dnf module enable -y nodejs:20
    dnf module install -y nodejs:20
    ok "Node.js installed: $(node --version)"
else
    ok "Node.js already present: $(node --version)"
fi

# ─── Nginx ────────────────────────────────────────────────────────────────
if ! cmd_exists nginx; then
    echo "    Installing Nginx..."
    dnf install -y nginx
    ok "Nginx installed"
else
    ok "Nginx already present"
fi

# ─── Git ──────────────────────────────────────────────────────────────────
cmd_exists git || dnf install -y git

# =============================================================================
# 2. INSTALL & CONFIGURE POSTGRESQL 15
# =============================================================================

log "Step 2/8 — Setting up PostgreSQL 15"

if ! rpm -q postgresql15-server &>/dev/null; then
    echo "    Installing PostgreSQL 15..."
    dnf install -y \
        "https://download.postgresql.org/pub/repos/yum/reporpms/EL-8-x86_64/pgdg-redhat-repo-latest.noarch.rpm" \
        2>/dev/null || true
    dnf -qy module disable postgresql
    dnf install -y postgresql15-server postgresql15
    ok "PostgreSQL 15 installed"
else
    ok "PostgreSQL 15 already installed"
fi

PG_DATA="/var/lib/pgsql/15/data"
PG_CONF="${PG_DATA}/postgresql.conf"
PG_HBA="${PG_DATA}/pg_hba.conf"

if [[ ! -f "${PG_DATA}/PG_VERSION" ]]; then
    echo "    Initializing database cluster..."
    /usr/pgsql-15/bin/postgresql-15-setup initdb
    ok "Database cluster initialized"
else
    ok "Database cluster already exists"
fi

# Configure network listening and port
sed -i "s/^#\?listen_addresses\s*=.*/listen_addresses = '${DB_LISTEN_ADDRESS}'/" "$PG_CONF"
sed -i "s/^#\?port\s*=.*/port = ${DB_PORT}/" "$PG_CONF"
echo "    PostgreSQL listen_addresses=${DB_LISTEN_ADDRESS} port=${DB_PORT}"

# Switch local TCP auth from ident → md5 (required for Spring Boot JDBC)
sed -i 's|^\(host\s\+all\s\+all\s\+127\.0\.0\.1/32\s\+\)ident|\1md5|'   "$PG_HBA"
sed -i 's|^\(host\s\+all\s\+all\s\+::1/128\s\+\)ident|\1md5|'            "$PG_HBA"

# Allow remote clients from the configured CIDR with password auth
if ! grep -qE "host\s+all\s+all\s+${DB_ALLOWED_CIDR//\//\\/}\s+md5" "$PG_HBA"; then
    echo "host    all             all             ${DB_ALLOWED_CIDR}            md5" >> "$PG_HBA"
fi
echo "    PostgreSQL pg_hba allows ${DB_ALLOWED_CIDR} with md5"

systemctl enable --now postgresql-15
systemctl restart postgresql-15
ok "PostgreSQL running on port ${DB_PORT}"

# Create user & database
echo "    Configuring database..."
cd /tmp

sudo -u postgres psql -p "$DB_PORT" -v ON_ERROR_STOP=1 <<EOSQL
    DO
    $$
    BEGIN
        IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = '${DB_USER}') THEN
            EXECUTE format('CREATE ROLE %I LOGIN PASSWORD %L', '${DB_USER}', '${DB_PASS}');
        ELSE
            EXECUTE format('ALTER ROLE %I WITH LOGIN PASSWORD %L', '${DB_USER}', '${DB_PASS}');
        END IF;
    END
    $$;
    SELECT 'CREATE DATABASE ${DB_NAME} OWNER ${DB_USER}'
    WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = '${DB_NAME}')
    \gexec
EOSQL

ok "Database '${DB_NAME}' ready (user: ${DB_USER})"

# =============================================================================
# 3. CREATE DEDICATED SERVICE USER
# =============================================================================

log "Step 3/8 — Creating application service user"

if ! id "appuser" &>/dev/null; then
    useradd --system --shell /usr/sbin/nologin --home-dir /opt/app --no-create-home appuser
    ok "User 'appuser' created"
else
    ok "User 'appuser' already exists"
fi

# =============================================================================
# 4. CLONE REPOSITORIES
# =============================================================================

log "Step 4/8 — Cloning repositories from GitHub"

mkdir -p "$REPO_DIR" "$BACKEND_DIR" "$FRONTEND_DIR"

BACKEND_AUTH_URL="${BACKEND_REPO_URL/https:\/\//https://${GITHUB_PAT}@}"
FRONTEND_AUTH_URL="${FRONTEND_REPO_URL/https:\/\//https://${GITHUB_PAT}@}"

if [[ -d "$BACKEND_BUILD_DIR/.git" ]]; then
    echo "    Pulling latest backend..."
    cd "$BACKEND_BUILD_DIR" && git pull --ff-only
else
    rm -rf "$BACKEND_BUILD_DIR"
    git clone "$BACKEND_AUTH_URL" "$BACKEND_BUILD_DIR"
fi
ok "Backend repo ready at ${BACKEND_BUILD_DIR}"

if [[ -d "$FRONTEND_BUILD_DIR/.git" ]]; then
    echo "    Pulling latest frontend..."
    cd "$FRONTEND_BUILD_DIR" && git pull --ff-only
else
    rm -rf "$FRONTEND_BUILD_DIR"
    git clone "$FRONTEND_AUTH_URL" "$FRONTEND_BUILD_DIR"
fi
ok "Frontend repo ready at ${FRONTEND_BUILD_DIR}"

# =============================================================================
# 5. BUILD BACKEND (Spring Boot)
# =============================================================================

log "Step 5/8 — Building Spring Boot backend"

cd "$BACKEND_BUILD_DIR"
chmod +x mvnw

# Verify JAVA_HOME is Java 17 before building
if [[ -z "${JAVA_HOME:-}" ]]; then
    JAVA17_PATH=$(find /usr/lib/jvm -maxdepth 1 -name "java-17-openjdk-*" -type d | sort | head -1)
    export JAVA_HOME="$JAVA17_PATH"
    export PATH="${JAVA_HOME}/bin:${PATH}"
fi

echo "    JAVA_HOME = ${JAVA_HOME}"
echo "    javac:      $(javac -version 2>&1)"
echo "    java:       $(java -version 2>&1 | head -1)"

# Build the JAR
./mvnw clean package -DskipTests -B

# Deploy
cp target/*.jar "${BACKEND_DIR}/app.jar"
chown appuser:appuser "${BACKEND_DIR}/app.jar"

ok "Backend JAR deployed → ${BACKEND_DIR}/app.jar"

# =============================================================================
# 6. BUILD FRONTEND (React CRA)
# =============================================================================

log "Step 6/8 — Building React frontend"

cd "$FRONTEND_BUILD_DIR"

# Override .env for frontend to point at backend on the same host
cat > .env <<REACTENV
PORT=${FRONTEND_PORT}
REACT_APP_API_URL=http://${IP_ADDRESS}:${BACKEND_PORT}/api
REACT_APP_IPADDRESS=${IP_ADDRESS}
REACT_APP_PORT=${BACKEND_PORT}
GENERATE_SOURCEMAP=false
REACTENV

ok "React .env configured (REACT_APP_API_URL=http://${IP_ADDRESS}:${BACKEND_PORT}/api)"

npm ci --loglevel=warn
npm run build

rm -rf "${FRONTEND_DIR:?}"/*
cp -r build/* "${FRONTEND_DIR}/"
chown -R appuser:nginx "${FRONTEND_DIR}"
chmod -R 755 "${FRONTEND_DIR}"

ok "Frontend built and deployed → ${FRONTEND_DIR}/"

# =============================================================================
# 7. CREATE SYSTEMD SERVICE (Backend only)
# =============================================================================

log "Step 7/8 — Configuring systemd services"

# Resolve the absolute path to the Java 17 binary
JAVA17_BIN=$(readlink -f "${JAVA_HOME}/bin/java")

cat > /etc/systemd/system/food-app-backend.service <<EOF
[Unit]
Description=Food App — Spring Boot Backend (port ${BACKEND_PORT})
Documentation=https://github.com/${GITHUB_USER}/food-app
After=network.target postgresql-15.service
Requires=postgresql-15.service

[Service]
Type=simple
User=appuser
Group=appuser
WorkingDirectory=${BACKEND_DIR}

# Force Java 17 — prevents using wrong JDK if multiple are installed
Environment="JAVA_HOME=${JAVA_HOME}"
Environment="PATH=${JAVA_HOME}/bin:/usr/local/bin:/usr/bin:/bin"
Environment="APP_CORS_ALLOWED_ORIGINS=http://${IP_ADDRESS},http://localhost"
Environment="SPRING_PROFILES_ACTIVE=product"
Environment="SPRING_JPA_HIBERNATE_DDL_AUTO=update"

ExecStart=${JAVA17_BIN} \\
    -Xms256m -Xmx512m \\
    -Djava.security.egd=file:/dev/./urandom \\
    -jar ${BACKEND_DIR}/app.jar \\
    --server.address=127.0.0.1 \\
    --server.port=${BACKEND_PORT} \\
    --spring.datasource.url=jdbc:postgresql://localhost:${DB_PORT}/${DB_NAME} \\
    --spring.datasource.username=${DB_USER} \\
    --spring.datasource.password=${DB_PASS}

Restart=on-failure
RestartSec=10
StartLimitIntervalSec=60
StartLimitBurst=3

NoNewPrivileges=true
ProtectSystem=full
ProtectHome=true
PrivateTmp=true

StandardOutput=journal
StandardError=journal
SyslogIdentifier=food-app-backend

[Install]
WantedBy=multi-user.target
EOF

systemctl daemon-reload
systemctl enable food-app-backend.service
systemctl restart food-app-backend.service

ok "Backend service started → 127.0.0.1:${BACKEND_PORT} (${JAVA17_BIN})"

# =============================================================================
# 8. CONFIGURE NGINX
# =============================================================================

log "Step 8/8 — Configuring Nginx reverse proxy"

cat > /etc/nginx/conf.d/food-app.conf <<'NGINXEOF'
upstream food_app_backend {
    server 127.0.0.1:__BACKEND_PORT__;
    keepalive 32;
}

server {
    listen 80;
    listen [::]:80;
    server_name __IP_ADDRESS__;

    client_max_body_size 20M;

    access_log /var/log/nginx/food-app.access.log;
    error_log  /var/log/nginx/food-app.error.log warn;

    add_header X-Frame-Options        "SAMEORIGIN"                       always;
    add_header X-Content-Type-Options "nosniff"                          always;
    add_header X-XSS-Protection       "1; mode=block"                    always;
    add_header Referrer-Policy        "strict-origin-when-cross-origin"  always;

    gzip on;
    gzip_vary on;
    gzip_proxied any;
    gzip_min_length 256;
    gzip_comp_level 5;
    gzip_types
        text/plain text/css text/javascript
        application/json application/javascript
        application/xml application/xml+rss
        image/svg+xml;

    # ── React Frontend (static) ──
    root __FRONTEND_DIR__;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    # ── API → Spring Boot ──
    location /api/ {
        proxy_pass http://food_app_backend/api/;

        proxy_set_header Host              $host;
        proxy_set_header X-Real-IP         $remote_addr;
        proxy_set_header X-Forwarded-For   $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_set_header X-Forwarded-Port  $server_port;

        proxy_http_version 1.1;
        proxy_set_header   Connection "";

        proxy_connect_timeout 30s;
        proxy_send_timeout    120s;
        proxy_read_timeout    120s;

        proxy_buffering on;
        proxy_buffer_size 16k;
        proxy_buffers 4 32k;
    }

    # ── Actuator health check ──
    location /actuator/health {
        proxy_pass http://food_app_backend/actuator/health;
        proxy_set_header Host $host;
    }

    # ── Cache CRA hashed assets ──
    location /static/ {
        expires 1y;
        add_header Cache-Control "public, immutable";
        access_log off;
    }

    location ~* \.(?:ico|gif|jpe?g|png|svg|webp|woff2?|ttf|eot|otf)$ {
        expires 30d;
        add_header Cache-Control "public";
        access_log off;
    }

    # ── Block hidden files ──
    location ~ /\. {
        deny all;
        access_log off;
        log_not_found off;
    }
}
NGINXEOF

sed -i "s|__BACKEND_PORT__|${BACKEND_PORT}|g"   /etc/nginx/conf.d/food-app.conf
sed -i "s|__IP_ADDRESS__|${IP_ADDRESS}|g"        /etc/nginx/conf.d/food-app.conf
sed -i "s|__FRONTEND_DIR__|${FRONTEND_DIR}|g"    /etc/nginx/conf.d/food-app.conf

[[ -f /etc/nginx/conf.d/default.conf ]] && \
    mv /etc/nginx/conf.d/default.conf /etc/nginx/conf.d/default.conf.disabled 2>/dev/null || true

# SELinux
if cmd_exists setsebool; then
    echo "    Configuring SELinux..."
    setsebool -P httpd_can_network_connect 1  2>/dev/null || true
    setsebool -P httpd_read_user_content 1    2>/dev/null || true
    chcon -Rt httpd_sys_content_t "${FRONTEND_DIR}" 2>/dev/null || true
    ok "SELinux configured"
fi

nginx -t
systemctl enable --now nginx
systemctl reload nginx

ok "Nginx running → http://${IP_ADDRESS}/"

# =============================================================================
# 9. FIREWALL
# =============================================================================

log "Configuring firewall"

if cmd_exists firewall-cmd; then
    firewall-cmd --permanent --zone=public --add-service=http    2>/dev/null || true
    firewall-cmd --permanent --zone=public --add-service=https   2>/dev/null || true
    firewall-cmd --permanent --zone=public --remove-port=8080/tcp  2>/dev/null || true
    firewall-cmd --permanent --zone=public --remove-port=9091/tcp  2>/dev/null || true
    firewall-cmd --permanent --zone=public --remove-port=9011/tcp  2>/dev/null || true
    firewall-cmd --permanent --zone=public --remove-port=3000/tcp  2>/dev/null || true
    firewall-cmd --reload
    ok "Firewall: HTTP/HTTPS open — backend ports blocked"
else
    warn "firewall-cmd not found."
fi

# =============================================================================
# 10. HEALTH CHECKS
# =============================================================================

log "Running post-deployment health checks"

echo "    Waiting 15s for Spring Boot to start..."
sleep 15

declare -A SERVICES=(
    ["postgresql-15"]="PostgreSQL 15"
    ["food-app-backend"]="Spring Boot Backend"
    ["nginx"]="Nginx"
)

ALL_OK=true
for svc in "${!SERVICES[@]}"; do
    if systemctl is-active --quiet "$svc"; then
        ok "${SERVICES[$svc]} — running"
    else
        err "${SERVICES[$svc]} — NOT RUNNING"
        echo "      → journalctl -u $svc --no-pager -n 30"
        ALL_OK=false
    fi
done

echo ""
BACKEND_HTTP=$(curl -sf -o /dev/null -w "%{http_code}" "http://127.0.0.1:${BACKEND_PORT}/actuator/health" 2>/dev/null || echo "000")
if [[ "$BACKEND_HTTP" == "200" ]]; then
    ok "Backend actuator/health → HTTP ${BACKEND_HTTP}"
else
    warn "Backend returned HTTP ${BACKEND_HTTP} (may still be starting)"
fi

NGINX_HTTP=$(curl -sf -o /dev/null -w "%{http_code}" "http://127.0.0.1/" 2>/dev/null || echo "000")
if [[ "$NGINX_HTTP" == "200" ]]; then
    ok "Nginx frontend → HTTP ${NGINX_HTTP}"
else
    warn "Nginx returned HTTP ${NGINX_HTTP}"
fi

PROXY_HTTP=$(curl -sf -o /dev/null -w "%{http_code}" "http://127.0.0.1/api/actuator/health" 2>/dev/null || echo "000")
if [[ "$PROXY_HTTP" == "200" ]]; then
    ok "Nginx → API proxy → HTTP ${PROXY_HTTP}"
else
    warn "API proxy returned HTTP ${PROXY_HTTP}"
fi

# =============================================================================
# DONE
# =============================================================================

echo ""
if $ALL_OK; then
    log "✅ Deployment complete!"
else
    warn "Deployment finished with warnings. Review output above."
fi

cat <<SUMMARY

╔══════════════════════════════════════════════════════════════════════╗
║                      DEPLOYMENT SUMMARY                            ║
╠══════════════════════════════════════════════════════════════════════╣
║                                                                    ║
║  ������ Frontend:  http://${IP_ADDRESS}/                               
║  ������ API:       http://${IP_ADDRESS}/api/                           
║  ������ Health:    http://${IP_ADDRESS}/actuator/health                 
║  ☕ Java:      ${JAVA_HOME}                                        
║                                                                    ║
║  ������ Commands:                                                      ║
║     journalctl -u food-app-backend -f      # backend logs          ║
║     tail -f /var/log/nginx/food-app.*.log  # nginx logs            ║
║     systemctl restart food-app-backend     # restart backend       ║
║     systemctl restart nginx                # restart nginx         ║
║                                                                    ║
╚══════════════════════════════════════════════════════════════════════╝

SUMMARY

