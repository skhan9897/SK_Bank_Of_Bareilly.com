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
            "users", "customers", "accounts", "branches", "account_types",
            "beneficiaries", "transactions", "transfer_requests", "upi_accounts",
            "fixed_deposits", "loan_types", "loans", "loan_payments", "cards",
            "card_transactions", "bill_payments", "kyc", "notifications",
            "complaints", "complaint_messages", "employees", "admins",
            "audit_logs", "system_settings", "payment_wallets",
            "payment_transactions", "recharge_transactions", "fastag_accounts",
            "payment_providers", "payment_idempotency"
    );

    public static void initializeDatabaseIfMissing() {
        boolean allTablesExist = true;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            for (String tableName : REQUIRED_TABLES) {
                try (ResultSet rs = stmt.executeQuery("SHOW TABLES LIKE '" + tableName + "'")) {
                    if (!rs.next()) {
                        LOGGER.info("Table '" + tableName + "' is missing in database.");
                        allTablesExist = false;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Database health check notice: " + e.getMessage());
            allTablesExist = false;
        }

        if (allTablesExist) {
            LOGGER.info("Database health check passed: All required banking tables exist.");
            return;
        }

        LOGGER.info("Initializing database schema and missing tables via JDBC...");

        InputStream is = DatabaseInitializer.class.getClassLoader().getResourceAsStream("database/sk_bank_of_bareilly.sql");
        if (is == null) {
            LOGGER.log(Level.SEVERE, "Could not locate database/sk_bank_of_bareilly.sql on classpath resource stream!");
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

                    // Skip USE or CREATE DATABASE statements when operating on explicit connection URL
                    if (query.toUpperCase().startsWith("CREATE DATABASE") || query.toUpperCase().startsWith("USE ")) {
                        continue;
                    }

                    try {
                        stmt.execute(query);
                    } catch (Exception e) {
                        LOGGER.log(Level.FINE, "SQL Statement execution notice: " + e.getMessage());
                    }
                }
            }

            stmt.execute("SET FOREIGN_KEY_CHECKS = 1;");
            LOGGER.info("Database schema and missing tables created successfully via JDBC!");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error initializing database schema via JDBC", e);
        }
    }
}
