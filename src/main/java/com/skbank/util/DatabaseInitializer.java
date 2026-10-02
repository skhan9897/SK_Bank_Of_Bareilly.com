package com.skbank.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseInitializer {

    private static final Logger LOGGER = Logger.getLogger(DatabaseInitializer.class.getName());

    public static void initializeDatabaseIfMissing() {
        boolean usersTableExists = false;
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SHOW TABLES LIKE 'users'")) {
            if (rs.next()) {
                usersTableExists = true;
            }
        } catch (Exception e) {
            LOGGER.log(Level.INFO, "Table check info: " + e.getMessage());
        }

        if (usersTableExists) {
            LOGGER.info("Database schema 'users' table exists. Skipping full SQL script execution.");
            return;
        }

        LOGGER.info("Database 'users' table not found. Executing full database initialization via JDBC...");

        InputStream is = DatabaseInitializer.class.getClassLoader().getResourceAsStream("database/sk_bank_of_bareilly.sql");
        if (is == null) {
            LOGGER.log(Level.SEVERE, "Could not locate database/sk_bank_of_bareilly.sql on classpath!");
            return;
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
             Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

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

                    // Skip database creation statements if user is restricted
                    if (query.toUpperCase().startsWith("CREATE DATABASE") || query.toUpperCase().startsWith("USE ")) {
                        continue;
                    }

                    try {
                        stmt.execute(query);
                    } catch (Exception e) {
                        LOGGER.log(Level.FINE, "SQL Statement Notice: " + e.getMessage());
                    }
                }
            }
            LOGGER.info("Database initialization and schema creation completed successfully via JDBC!");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error initializing database via JDBC", e);
        }
    }
}
