package com.skbank.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseInitializer {

    private static final Logger LOGGER = Logger.getLogger(DatabaseInitializer.class.getName());

    public static void initializeDatabaseIfMissing() {
        LOGGER.info("Sanitizing and ensuring full database schema & AUTO_INCREMENT compatibility across all tables...");
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.createStatement().execute("SET FOREIGN_KEY_CHECKS = 0;");

            ensureUsersTable(conn);
            ensureEmployeesTable(conn);
            ensureAdminsTable(conn);
            ensureCustomersTable(conn);
            ensureBranchesTable(conn);
            ensureAccountTypesTable(conn);
            ensureAccountsTable(conn);
            ensureKycTable(conn);
            ensureNotificationsTable(conn);

            ensureAutoIncrementPrimaryKeys(conn);
            ensureLegacyColumnsHaveDefaults(conn);

            conn.createStatement().execute("SET FOREIGN_KEY_CHECKS = 1;");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during database schema initialization", e);
        }

        // Always run full schema import for auxiliary/payment bank tables
        executeFullSchemaImport();
    }

    private static void ensureLegacyColumnsHaveDefaults(Connection conn) {
        String[] legacyFixes = new String[] {
            "ALTER TABLE customers MODIFY COLUMN first_name VARCHAR(100) NULL DEFAULT '';",
            "ALTER TABLE customers MODIFY COLUMN last_name VARCHAR(100) NULL DEFAULT '';",
            "ALTER TABLE customers MODIFY COLUMN dob VARCHAR(50) NULL DEFAULT '';",
            "ALTER TABLE customers MODIFY COLUMN aadhaar VARCHAR(50) NULL DEFAULT '';",
            "ALTER TABLE customers MODIFY COLUMN pan VARCHAR(50) NULL DEFAULT '';"
        };
        for (String sql : legacyFixes) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(sql);
            } catch (Exception ignored) {
                // Ignore if legacy column does not exist on table
            }
        }
    }

    private static void ensureAutoIncrementPrimaryKeys(Connection conn) {
        String[] autoIncQueries = new String[] {
            "ALTER TABLE users MODIFY COLUMN id BIGINT NOT NULL AUTO_INCREMENT;",
            "ALTER TABLE customers MODIFY COLUMN customer_id BIGINT NOT NULL AUTO_INCREMENT;",
            "ALTER TABLE accounts MODIFY COLUMN account_id BIGINT NOT NULL AUTO_INCREMENT;",
            "ALTER TABLE branches MODIFY COLUMN branch_id BIGINT NOT NULL AUTO_INCREMENT;",
            "ALTER TABLE account_types MODIFY COLUMN account_type_id BIGINT NOT NULL AUTO_INCREMENT;",
            "ALTER TABLE kyc MODIFY COLUMN kyc_id BIGINT NOT NULL AUTO_INCREMENT;",
            "ALTER TABLE notifications MODIFY COLUMN notification_id BIGINT NOT NULL AUTO_INCREMENT;"
        };
        for (String q : autoIncQueries) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(q);
            } catch (Exception e1) {
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(q.replace(";", " PRIMARY KEY;"));
                } catch (Exception ignored) {}
            }
        }
    }

    private static void ensureUsersTable(Connection conn) {
        String createDdl = "CREATE TABLE IF NOT EXISTS users (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "username VARCHAR(50) NOT NULL UNIQUE, " +
                "password_hash VARCHAR(255) NOT NULL, " +
                "role VARCHAR(20) NOT NULL DEFAULT 'CUSTOMER', " +
                "status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', " +
                "failed_login_attempts INT NOT NULL DEFAULT 0, " +
                "account_locked_until DATETIME NULL, " +
                "auth_token VARCHAR(255) NULL, " +
                "token_expiry DATETIME NULL, " +
                "last_login DATETIME NULL, " +
                "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";

        String[] cols = new String[]{"username", "password_hash", "role", "status", "failed_login_attempts", "auth_token", "token_expiry"};
        String[] alterDdls = new String[]{
                "VARCHAR(50) NOT NULL UNIQUE",
                "VARCHAR(255) NOT NULL",
                "VARCHAR(20) NOT NULL DEFAULT 'CUSTOMER'",
                "VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'",
                "INT NOT NULL DEFAULT 0",
                "VARCHAR(255) NULL",
                "DATETIME NULL"
        };
        sanitizeAndEnsureTableSchema(conn, "users", createDdl, cols, alterDdls);
    }

    private static void ensureEmployeesTable(Connection conn) {
        String createDdl = "CREATE TABLE IF NOT EXISTS employees (" +
                "employee_id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "employee_code VARCHAR(20) NOT NULL UNIQUE, " +
                "full_name VARCHAR(100) NOT NULL, " +
                "email VARCHAR(100) NOT NULL UNIQUE, " +
                "mobile VARCHAR(15) NOT NULL UNIQUE, " +
                "department VARCHAR(50) NOT NULL, " +
                "designation VARCHAR(50) NOT NULL, " +
                "status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', " +
                "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";

        String[] cols = new String[]{"employee_code", "full_name", "email", "mobile", "department", "designation", "status"};
        String[] alterDdls = new String[]{"VARCHAR(20) NOT NULL UNIQUE", "VARCHAR(100) NOT NULL", "VARCHAR(100) NOT NULL UNIQUE", "VARCHAR(15) NOT NULL UNIQUE", "VARCHAR(50) NOT NULL", "VARCHAR(50) NOT NULL", "VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'"};
        sanitizeAndEnsureTableSchema(conn, "employees", createDdl, cols, alterDdls);
    }

    private static void ensureAdminsTable(Connection conn) {
        String createDdl = "CREATE TABLE IF NOT EXISTS admins (" +
                "admin_id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "user_id BIGINT NOT NULL UNIQUE, " +
                "employee_id BIGINT NULL, " +
                "admin_role VARCHAR(30) NOT NULL DEFAULT 'SUPER_ADMIN', " +
                "status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', " +
                "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";

        String[] cols = new String[]{"user_id", "admin_role", "status"};
        String[] alterDdls = new String[]{"BIGINT NOT NULL UNIQUE", "VARCHAR(30) NOT NULL DEFAULT 'SUPER_ADMIN'", "VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'"};
        sanitizeAndEnsureTableSchema(conn, "admins", createDdl, cols, alterDdls);
    }

    private static void ensureCustomersTable(Connection conn) {
        String createDdl = "CREATE TABLE IF NOT EXISTS customers (" +
                "customer_id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "user_id BIGINT NOT NULL UNIQUE, " +
                "customer_number VARCHAR(20) NOT NULL UNIQUE, " +
                "full_name VARCHAR(100) NOT NULL, " +
                "date_of_birth DATE NOT NULL, " +
                "gender VARCHAR(10) NOT NULL, " +
                "mobile VARCHAR(15) NOT NULL UNIQUE, " +
                "email VARCHAR(100) NOT NULL UNIQUE, " +
                "address VARCHAR(255) NOT NULL, " +
                "city VARCHAR(50) NOT NULL, " +
                "state VARCHAR(50) NOT NULL, " +
                "pincode VARCHAR(10) NOT NULL, " +
                "aadhaar_number VARCHAR(20) NOT NULL UNIQUE, " +
                "pan_number VARCHAR(20) NOT NULL UNIQUE, " +
                "profile_image VARCHAR(255) NULL, " +
                "kyc_status VARCHAR(20) NOT NULL DEFAULT 'VERIFIED', " +
                "status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', " +
                "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";

        String[] cols = new String[]{
                "user_id", "customer_number", "full_name", "date_of_birth", "gender",
                "mobile", "email", "address", "city", "state", "pincode",
                "aadhaar_number", "pan_number", "profile_image", "kyc_status", "status"
        };
        String[] alterDdls = new String[]{
                "BIGINT NOT NULL UNIQUE",
                "VARCHAR(20) NULL",
                "VARCHAR(100) NOT NULL DEFAULT ''",
                "DATE NOT NULL DEFAULT '1995-01-01'",
                "VARCHAR(10) NOT NULL DEFAULT 'MALE'",
                "VARCHAR(15) NOT NULL DEFAULT ''",
                "VARCHAR(100) NOT NULL DEFAULT ''",
                "VARCHAR(255) NOT NULL DEFAULT ''",
                "VARCHAR(50) NOT NULL DEFAULT 'Bareilly'",
                "VARCHAR(50) NOT NULL DEFAULT 'Uttar Pradesh'",
                "VARCHAR(10) NOT NULL DEFAULT '243001'",
                "VARCHAR(20) NOT NULL DEFAULT ''",
                "VARCHAR(20) NOT NULL DEFAULT ''",
                "VARCHAR(255) NULL",
                "VARCHAR(20) NOT NULL DEFAULT 'VERIFIED'",
                "VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'"
        };
        sanitizeAndEnsureTableSchema(conn, "customers", createDdl, cols, alterDdls);
    }

    private static void ensureBranchesTable(Connection conn) {
        String createDdl = "CREATE TABLE IF NOT EXISTS branches (" +
                "branch_id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "branch_code VARCHAR(20) NOT NULL UNIQUE, " +
                "branch_name VARCHAR(100) NOT NULL, " +
                "address VARCHAR(255) NOT NULL, " +
                "city VARCHAR(50) NOT NULL, " +
                "state VARCHAR(50) NOT NULL, " +
                "pincode VARCHAR(10) NOT NULL, " +
                "ifsc_code VARCHAR(20) NOT NULL UNIQUE, " +
                "phone VARCHAR(15) NOT NULL, " +
                "status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', " +
                "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";

        String[] cols = new String[]{"branch_code", "branch_name", "ifsc_code", "status"};
        String[] alterDdls = new String[]{"VARCHAR(20) NOT NULL UNIQUE", "VARCHAR(100) NOT NULL", "VARCHAR(20) NOT NULL UNIQUE", "VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'"};
        sanitizeAndEnsureTableSchema(conn, "branches", createDdl, cols, alterDdls);

        // Seed default branches
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("INSERT IGNORE INTO branches (branch_id, branch_code, branch_name, address, city, state, pincode, ifsc_code, phone) VALUES " +
                    "(1, 'SKB001', 'Main Branch Bareilly', 'Civil Lines, Near Cantonment', 'Bareilly', 'Uttar Pradesh', '243001', 'SKBK0000001', '0581-2550001'), " +
                    "(2, 'SKB002', 'Izzatnagar Branch', 'Near Railway Station, Izzatnagar', 'Bareilly', 'Uttar Pradesh', '243122', 'SKBK0000002', '0581-2550002'), " +
                    "(3, 'SKB003', 'Rajendra Nagar Branch', 'Block B, Rajendra Nagar', 'Bareilly', 'Uttar Pradesh', '243122', 'SKBK0000003', '0581-2550003'), " +
                    "(4, 'SKB004', 'Noida Cyber Branch', 'Sector 62, Electronic City', 'Noida', 'Uttar Pradesh', '201309', 'SKBK0000004', '0120-2550004');");
        } catch (Exception ignored) {}
    }

    private static void ensureAccountTypesTable(Connection conn) {
        String createDdl = "CREATE TABLE IF NOT EXISTS account_types (" +
                "account_type_id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "type_code VARCHAR(30) NOT NULL UNIQUE, " +
                "type_name VARCHAR(50) NOT NULL, " +
                "description VARCHAR(255) NULL, " +
                "minimum_balance DECIMAL(18,2) NOT NULL DEFAULT 0.00, " +
                "interest_rate DECIMAL(5,2) NOT NULL DEFAULT 0.00, " +
                "status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', " +
                "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";

        String[] cols = new String[]{"type_code", "type_name", "status"};
        String[] alterDdls = new String[]{"VARCHAR(30) NOT NULL UNIQUE", "VARCHAR(50) NOT NULL", "VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'"};
        sanitizeAndEnsureTableSchema(conn, "account_types", createDdl, cols, alterDdls);

        // Seed default account types
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("INSERT IGNORE INTO account_types (account_type_id, type_code, type_name, description, minimum_balance, interest_rate) VALUES " +
                    "(1, 'SAVINGS', 'Savings Account', 'Standard personal savings account with interest', 1000.00, 4.00), " +
                    "(2, 'CURRENT', 'Current Account', 'Business account for high volume transactions', 5000.00, 0.00), " +
                    "(3, 'SALARY', 'Corporate Salary Account', 'Zero-balance salary account with premium benefits', 0.00, 4.50), " +
                    "(4, 'BASIC_SAVINGS', 'Basic Savings Account (BSBD)', 'Zero-balance basic savings bank deposit account', 0.00, 3.50), " +
                    "(5, 'SENIOR_CITIZEN', 'Senior Citizen Savings Account', 'Special savings account for citizens aged 60+', 1000.00, 5.00);");
        } catch (Exception ignored) {}
    }

    private static void ensureAccountsTable(Connection conn) {
        String createDdl = "CREATE TABLE IF NOT EXISTS accounts (" +
                "account_id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "customer_id BIGINT NOT NULL, " +
                "account_type_id BIGINT NOT NULL, " +
                "branch_id BIGINT NOT NULL, " +
                "account_number VARCHAR(20) NOT NULL UNIQUE, " +
                "balance DECIMAL(18,2) NOT NULL DEFAULT 0.00, " +
                "available_balance DECIMAL(18,2) NOT NULL DEFAULT 0.00, " +
                "status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', " +
                "opened_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                "closed_at DATETIME NULL, " +
                "FOREIGN KEY (customer_id) REFERENCES customers(customer_id), " +
                "FOREIGN KEY (account_type_id) REFERENCES account_types(account_type_id), " +
                "FOREIGN KEY (branch_id) REFERENCES branches(branch_id)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";

        String[] cols = new String[]{"customer_id", "account_type_id", "branch_id", "account_number", "balance", "available_balance", "status"};
        String[] alterDdls = new String[]{"BIGINT NOT NULL", "BIGINT NOT NULL", "BIGINT NOT NULL", "VARCHAR(20) NOT NULL UNIQUE", "DECIMAL(18,2) NOT NULL DEFAULT 0.00", "DECIMAL(18,2) NOT NULL DEFAULT 0.00", "VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'"};
        sanitizeAndEnsureTableSchema(conn, "accounts", createDdl, cols, alterDdls);
    }

    private static void ensureKycTable(Connection conn) {
        String createDdl = "CREATE TABLE IF NOT EXISTS kyc (" +
                "kyc_id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "customer_id BIGINT NOT NULL UNIQUE, " +
                "aadhaar_number VARCHAR(20) NOT NULL, " +
                "pan_number VARCHAR(20) NOT NULL, " +
                "verification_status VARCHAR(20) NOT NULL DEFAULT 'VERIFIED', " +
                "verified_at DATETIME NULL, " +
                "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";

        String[] cols = new String[]{"customer_id", "aadhaar_number", "pan_number", "verification_status"};
        String[] alterDdls = new String[]{"BIGINT NOT NULL UNIQUE", "VARCHAR(20) NOT NULL", "VARCHAR(20) NOT NULL", "VARCHAR(20) NOT NULL DEFAULT 'VERIFIED'"};
        sanitizeAndEnsureTableSchema(conn, "kyc", createDdl, cols, alterDdls);
    }

    private static void ensureNotificationsTable(Connection conn) {
        String createDdl = "CREATE TABLE IF NOT EXISTS notifications (" +
                "notification_id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "user_id BIGINT NOT NULL, " +
                "title VARCHAR(100) NOT NULL, " +
                "message TEXT NOT NULL, " +
                "notification_type VARCHAR(30) NOT NULL DEFAULT 'GENERAL', " +
                "is_read BOOLEAN NOT NULL DEFAULT FALSE, " +
                "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";

        String[] cols = new String[]{"user_id", "title", "message", "notification_type", "is_read"};
        String[] alterDdls = new String[]{"BIGINT NOT NULL", "VARCHAR(100) NOT NULL", "TEXT NOT NULL", "VARCHAR(30) NOT NULL DEFAULT 'GENERAL'", "BOOLEAN NOT NULL DEFAULT FALSE"};
        sanitizeAndEnsureTableSchema(conn, "notifications", createDdl, cols, alterDdls);
    }

    private static void sanitizeAndEnsureTableSchema(Connection conn, String tableName, String createDdl, String[] requiredColumns, String[] alterDdls) {
        boolean tableExists = false;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SHOW TABLES LIKE '" + tableName + "'")) {
            if (rs.next()) {
                tableExists = true;
            }
        } catch (Exception ignored) {}

        if (!tableExists) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(createDdl);
                LOGGER.info("Table '" + tableName + "' created successfully.");
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Error creating table " + tableName + ": " + e.getMessage());
            }
            return;
        }

        // Table exists - check if it is missing any required columns
        boolean missingColumnFound = false;
        for (String col : requiredColumns) {
            String checkColSql = "SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = ? AND column_name = ?";
            try (PreparedStatement ps = conn.prepareStatement(checkColSql)) {
                ps.setString(1, tableName);
                ps.setString(2, col);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getInt(1) == 0) {
                        missingColumnFound = true;
                        LOGGER.info("Table '" + tableName + "' is missing column '" + col + "'.");
                        break;
                    }
                }
            } catch (Exception ignored) {}
        }

        if (missingColumnFound) {
            // Check if table contains rows
            long rowCount = -1;
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM `" + tableName + "`")) {
                if (rs.next()) {
                    rowCount = rs.getLong(1);
                }
            } catch (Exception ignored) {}

            if (rowCount == 0) {
                LOGGER.info("Table '" + tableName + "' has 0 rows and missing columns. Re-creating clean table...");
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute("SET FOREIGN_KEY_CHECKS = 0;");
                    stmt.execute("DROP TABLE IF EXISTS `" + tableName + "`;");
                    stmt.execute(createDdl);
                    stmt.execute("SET FOREIGN_KEY_CHECKS = 1;");
                    LOGGER.info("Table '" + tableName + "' re-created with complete 100% schema.");
                    return;
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Error re-creating table " + tableName + ": " + e.getMessage());
                }
            }

            // If table has existing rows, alter missing columns individually
            for (int i = 0; i < requiredColumns.length; i++) {
                String col = requiredColumns[i];
                String ddl = alterDdls[i];
                ensureColumnExists(conn, tableName, col, ddl);
            }
        }
    }

    private static void ensureColumnExists(Connection conn, String table, String column, String ddl) {
        String checkSql = "SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = ? AND column_name = ?";
        try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
            ps.setString(1, table);
            ps.setString(2, column);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                    LOGGER.info("Adding missing column '" + column + "' to table '" + table + "'...");
                    try (Statement stmt = conn.createStatement()) {
                        stmt.execute("ALTER TABLE `" + table + "` ADD COLUMN `" + column + "` " + ddl);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.FINE, "Column check notice for " + table + "." + column + ": " + e.getMessage());
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
