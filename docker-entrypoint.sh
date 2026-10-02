#!/bin/bash
set -e

# Substitute PORT in Tomcat server.xml for Render/Cloud environments (default 8080)
PORT="${PORT:-8080}"
sed -i "s/port=\"8080\"/port=\"${PORT}\"/g" /usr/local/tomcat/conf/server.xml

# Start local MariaDB server if DB_URL is default/not external
if [ -z "$DB_URL" ] || [[ "$DB_URL" == *"localhost"* ]] || [[ "$DB_URL" == *"127.0.0.1"* ]]; then
    echo "Initializing local MariaDB database server..."

    if [ ! -d "/var/lib/mysql/mysql" ]; then
        mysql_install_db --user=mysql --datadir=/var/lib/mysql
    fi

    echo "Starting MariaDB service..."
    mysqld_safe --user=mysql --datadir=/var/lib/mysql &

    # Wait for MariaDB to start
    echo "Waiting for MariaDB to become ready..."
    until mysqladmin ping --silent; do
        sleep 1
    done

    echo "Configuring MySQL root user and database..."
    mysql -u root <<EOF
CREATE DATABASE IF NOT EXISTS sk_bank_of_bareilly CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER USER 'root'@'localhost' IDENTIFIED BY 'root';
CREATE USER IF NOT EXISTS 'root'@'%' IDENTIFIED BY 'root';
GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' WITH GRANT OPTION;
FLUSH PRIVILEGES;
EOF

    if [ -f "/docker-entrypoint-initdb.d/schema.sql" ]; then
        echo "Importing initial database schema and seed data..."
        mysql -u root -proot sk_bank_of_bareilly < /docker-entrypoint-initdb.d/schema.sql || true
        echo "Database schema imported successfully."
    fi
fi

exec "$@"
