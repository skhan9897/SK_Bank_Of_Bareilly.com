package com.skbank.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseInitializer {

    private static final Logger LOGGER = Logger.getLogger(DatabaseInitializer.class.getName());

    private static final List<String> REQUIRED_TABLES = Arrays.asList(
            "users", "customers", "accounts", "branches", "account_types", "kyc", "notifications"
    );

    public static void initializeDatabaseIfMissing() {
        boolean missingTableFound = false;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            for (String table : REQUIRED_TABLES) {
                try (ResultSet rs = stmt.executeQuery("SHOW TABLES LIKE '" + table + "'")) {
                    if (!rs.next()) {
                        LOGGER.info("Table '" + table + "' missing in database.");
                        missingTableFound = true;
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Table existence check notice: " + e.getMessage());
            missingTableFound = true;
        }

        if (missingTableFound) {
            LOGGER.info("Executing core DDL setup to ensure users, customers, accounts, branches and account_types exist...");
            executeCoreDdlSetup();
        } else {
            LOGGER.info("Database health check passed: Core banking tables exist.");
        }

        // Always run full schema import for auxiliary/payment bank tables
        executeFullSchemaImport();
    }

    public static void executeCoreDdlSetup() {
        String[] coreDdl = new String[] {
            "SET FOREIGN_KEY_CHECKS = 0;",
            "CREATE TABLE IF NOT EXISTS users (id BIGINT AUTO_INCREMENT PRIMARY KEY, username VARCHAR(50) NOT NULL UNIQUE, password_hash VARCHAR(255) NOT NULL, role VARCHAR(20) NOT NULL DEFAULT 'CUSTOMER', status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', failed_login_attempts INT NOT NULL DEFAULT 0, account_locked_until DATETIME NULL, auth_token VARCHAR(255) NULL, token_expiry DATETIME NULL, last_login DATETIME NULL, created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;",
            "CREATE TABLE IF NOT EXISTS employees (employee_id BIGINT AUTO_INCREMENT PRIMARY KEY, employee_code VARCHAR(20) NOT NULL UNIQUE, full_name VARCHAR(100) NOT NULL, email VARCHAR(100) NOT NULL UNIQUE, mobile VARCHAR(15) NOT NULL UNIQUE, department VARCHAR(50) NOT NULL, designation VARCHAR(50) NOT NULL, status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;",
            "CREATE TABLE IF NOT EXISTS admins (admin_id BIGINT AUTO_INCREMENT PRIMARY KEY, user_id BIGINT NOT NULL UNIQUE, employee_id BIGINT NULL, admin_role VARCHAR(30) NOT NULL DEFAULT 'SUPER_ADMIN', status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;",
            "CREATE TABLE IF NOT EXISTS customers (customer_id BIGINT AUTO_INCREMENT PRIMARY KEY, user_id BIGINT NOT NULL UNIQUE, customer_number VARCHAR(20) NOT NULL UNIQUE, full_name VARCHAR(100) NOT NULL, date_of_birth DATE NOT NULL, gender VARCHAR(10) NOT NULL, mobile VARCHAR(15) NOT NULL UNIQUE, email VARCHAR(100) NOT NULL UNIQUE, address VARCHAR(255) NOT NULL, city VARCHAR(50) NOT NULL, state VARCHAR(50) NOT NULL, pincode VARCHAR(10) NOT NULL, aadhaar_number VARCHAR(20) NOT NULL UNIQUE, pan_number VARCHAR(20) NOT NULL UNIQUE, profile_image VARCHAR(255) NULL, kyc_status VARCHAR(20) NOT NULL DEFAULT 'VERIFIED', status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;",
            "CREATE TABLE IF NOT EXISTS branches (branch_id BIGINT AUTO_INCREMENT PRIMARY KEY, branch_code VARCHAR(20) NOT NULL UNIQUE, branch_name VARCHAR(100) NOT NULL, address VARCHAR(255) NOT NULL, city VARCHAR(50) NOT NULL, state VARCHAR(50) NOT NULL, pincode VARCHAR(10) NOT NULL, ifsc_code VARCHAR(20) NOT NULL UNIQUE, phone VARCHAR(15) NOT NULL, status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;",
            "CREATE TABLE IF NOT EXISTS account_types (account_type_id BIGINT AUTO_INCREMENT PRIMARY KEY, type_code VARCHAR(30) NOT NULL UNIQUE, type_name VARCHAR(50) NOT NULL, description VARCHAR(255) NULL, minimum_balance DECIMAL(18,2) NOT NULL DEFAULT 0.00, interest_rate DECIMAL(5,2) NOT NULL DEFAULT 0.00, status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;",
            "CREATE TABLE IF NOT EXISTS accounts (account_id BIGINT AUTO_INCREMENT PRIMARY KEY, customer_id BIGINT NOT NULL, account_type_id BIGINT NOT NULL, branch_id BIGINT NOT NULL, account_number VARCHAR(20) NOT NULL UNIQUE, balance DECIMAL(18,2) NOT NULL DEFAULT 0.00, available_balance DECIMAL(18,2) NOT NULL DEFAULT 0.00, status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', opened_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, closed_at DATETIME NULL, FOREIGN KEY (customer_id) REFERENCES customers(customer_id), FOREIGN KEY (account_type_id) REFERENCES account_types(account_type_id), FOREIGN KEY (branch_id) REFERENCES branches(branch_id)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;",
            "CREATE TABLE IF NOT EXISTS kyc (kyc_id BIGINT AUTO_INCREMENT PRIMARY KEY, customer_id BIGINT NOT NULL UNIQUE, aadhaar_number VARCHAR(20) NOT NULL, pan_number VARCHAR(20) NOT NULL, verification_status VARCHAR(20) NOT NULL DEFAULT 'VERIFIED', verified_at DATETIME NULL, created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;",
            "CREATE TABLE IF NOT EXISTS notifications (notification_id BIGINT AUTO_INCREMENT PRIMARY KEY, user_id BIGINT NOT NULL, title VARCHAR(100) NOT NULL, message TEXT NOT NULL, notification_type VARCHAR(30) NOT NULL DEFAULT 'GENERAL', is_read BOOLEAN NOT NULL DEFAULT FALSE, created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;",
            "INSERT IGNORE INTO branches (branch_id, branch_code, branch_name, address, city, state, pincode, ifsc_code, phone) VALUES (1, 'SKB001', 'Main Branch Bareilly', 'Civil Lines, Near Cantonment', 'Bareilly', 'Uttar Pradesh', '243001', 'SKBK0000001', '0581-2550001'), (2, 'SKB002', 'Izzatnagar Branch', 'Near Railway Station, Izzatnagar', 'Bareilly', 'Uttar Pradesh', '243122', 'SKBK0000002', '0581-2550002'), (3, 'SKB003', 'Rajendra Nagar Branch', 'Block B, Rajendra Nagar', 'Bareilly', 'Uttar Pradesh', '243122', 'SKBK0000003', '0581-2550003'), (4, 'SKB004', 'Noida Cyber Branch', 'Sector 62, Electronic City', 'Noida', 'Uttar Pradesh', '201309', 'SKBK0000004', '0120-2550004');",
            "INSERT IGNORE INTO account_types (account_type_id, type_code, type_name, description, minimum_balance, interest_rate) VALUES (1, 'SAVINGS', 'Savings Account', 'Standard personal savings account with interest', 1000.00, 4.00), (2, 'CURRENT', 'Current Account', 'Business account for high volume transactions', 5000.00, 0.00), (3, 'SALARY', 'Corporate Salary Account', 'Zero-balance salary account with premium benefits', 0.00, 4.50), (4, 'BASIC_SAVINGS', 'Basic Savings Account (BSBD)', 'Zero-balance basic savings bank deposit account', 0.00, 3.50), (5, 'SENIOR_CITIZEN', 'Senior Citizen Savings Account', 'Special savings account for citizens aged 60+', 1000.00, 5.00);",
            "SET FOREIGN_KEY_CHECKS = 1;"
        };

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            for (String sql : coreDdl) {
                try {
                    stmt.execute(sql);
                } catch (Exception e) {
                    LOGGER.log(Level.FINE, "Core DDL statement notice: " + e.getMessage());
                }
            }
            LOGGER.info("Core database DDL setup completed.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Core DDL setup failed", e);
        }
    }

    private static void executeFullSchemaImport() {
        InputStream is = DatabaseInitializer.class.getClassLoader().getResourceAsStream("database/sk_bank_of_bareilly.sql");
        if (is == null) {
            LOGGER.log(Level.WARNING, "database/sk_bank_of_bareilly.sql resource not found on classpath");
            return;
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
             Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("SET FOREIGN_KEY_CHECKS = 0;");

            StringBuilder sqlBuilder = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.startsWith("--") || trimmed.startsWith("//") || trimmed.isEmpty()) {
                    continue;
                }
                sqlBuilder.append(line).append("\n");
                if (trimmed.endsWith(";")) {
                    String query = sqlBuilder.toString().trim();
                    sqlBuilder.setLength(0);

                    if (query.toUpperCase().startsWith("CREATE DATABASE") || query.toUpperCase().startsWith("USE ")) {
                        continue;
                    }

                    try {
                        stmt.execute(query);
                    } catch (Exception e) {
                        LOGGER.log(Level.FINE, "Schema statement notice: " + e.getMessage());
                    }
                }
            }

            stmt.execute("SET FOREIGN_KEY_CHECKS = 1;");
            LOGGER.info("Full database schema import completed successfully via JDBC.");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Full schema import notice: " + e.getMessage());
        }
    }
}
