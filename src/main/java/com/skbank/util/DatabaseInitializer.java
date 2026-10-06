package com.skbank.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseInitializer {

    private static final Logger LOGGER = Logger.getLogger(DatabaseInitializer.class.getName());

    public static void initializeDatabaseIfMissing() {
        LOGGER.info("Verifying and auto-migrating database schema for SK Bank of Bareilly...");
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn != null && !conn.isClosed()) {
                ensureColumnsExist(conn);
                ensureSeedAdminsExist(conn);
                LOGGER.info("Database schema verification and admin seeding completed successfully.");
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Database schema initialization notice: " + e.getMessage());
        }
    }

    private static void ensureColumnsExist(Connection conn) {
        String[] alterStatements = new String[] {
            "ALTER TABLE users ADD COLUMN failed_login_attempts INT NOT NULL DEFAULT 0",
            "ALTER TABLE users ADD COLUMN account_locked_until DATETIME NULL",
            "ALTER TABLE users ADD COLUMN last_login_at DATETIME NULL",
            "ALTER TABLE users ADD COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP",
            "ALTER TABLE users ADD COLUMN updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP",
            "ALTER TABLE customers ADD COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP",
            "ALTER TABLE customers ADD COLUMN updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP",
            "ALTER TABLE accounts ADD COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP",
            "ALTER TABLE accounts ADD COLUMN updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP",
            "ALTER TABLE accounts ADD COLUMN opened_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP",
            "ALTER TABLE notifications ADD COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP",
            "ALTER TABLE kyc_documents ADD COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP",
            "ALTER TABLE kyc_documents ADD COLUMN verified_at DATETIME NULL",
            "ALTER TABLE transfer_requests ADD COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP",
            "ALTER TABLE transfer_requests ADD COLUMN completed_at DATETIME NULL",
            "ALTER TABLE fixed_deposits ADD COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP",
            "ALTER TABLE loans ADD COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP",
            "ALTER TABLE loans ADD COLUMN updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP",
            "ALTER TABLE bill_payments ADD COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP",
            "ALTER TABLE payment_wallets ADD COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP",
            "ALTER TABLE payment_transactions ADD COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP"
        };

        for (String sql : alterStatements) {
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(sql);
            } catch (Exception ignored) {
                // Ignore if column already exists
            }
        }
    }

    private static void ensureSeedAdminsExist(Connection conn) {
        String[][] seedAdmins = new String[][] {
            {"admin", "admin123"},
            {"superadmin", "admin123"},
            {"SKBOB9897", "BOB9897"}
        };

        for (String[] adminData : seedAdmins) {
            String username = adminData[0];
            String plainPassword = adminData[1];

            try {
                String checkSql = "SELECT user_id FROM users WHERE username = ?";
                try (PreparedStatement psCheck = conn.prepareStatement(checkSql)) {
                    psCheck.setString(1, username);
                    try (ResultSet rs = psCheck.executeQuery()) {
                        if (!rs.next()) {
                            // Seed User
                            String hash = PasswordUtil.hashPassword(plainPassword);
                            String insertUserSql = "INSERT INTO users (username, password_hash, role, status) VALUES (?, ?, 'ADMIN', 'ACTIVE')";
                            try (PreparedStatement psUser = conn.prepareStatement(insertUserSql, Statement.RETURN_GENERATED_KEYS)) {
                                psUser.setString(1, username);
                                psUser.setString(2, hash);
                                psUser.executeUpdate();
                                try (ResultSet rsKeys = psUser.getGeneratedKeys()) {
                                    if (rsKeys.next()) {
                                        Long userId = rsKeys.getLong(1);
                                        // Seed Admin
                                        String insertAdminSql = "INSERT INTO admins (user_id, full_name, status) VALUES (?, ?, 'ACTIVE')";
                                        try (PreparedStatement psAdmin = conn.prepareStatement(insertAdminSql)) {
                                            psAdmin.setLong(1, userId);
                                            psAdmin.setString(2, "Administrator (" + username + ")");
                                            psAdmin.executeUpdate();
                                        } catch (Exception ignored) {}
                                    }
                                }
                            }
                            LOGGER.info("Seeded default admin user: " + username);
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.log(Level.FINE, "Admin seed notice for " + username + ": " + e.getMessage());
            }
        }
    }
}
