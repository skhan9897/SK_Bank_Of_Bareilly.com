package com.skbank.util;

import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseInitializer {

    private static final Logger LOGGER = Logger.getLogger(DatabaseInitializer.class.getName());

    public static void initializeDatabaseIfMissing() {
        LOGGER.info("Verifying database connection for SK Bank of Bareilly...");
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn != null && !conn.isClosed()) {
                LOGGER.info("Database connection verified successfully.");
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Database connection check notice: " + e.getMessage());
        }
    }
}
