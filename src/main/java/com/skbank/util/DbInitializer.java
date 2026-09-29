package com.skbank.util;

import com.skbank.config.DatabaseConfig;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;

public class DbInitializer {

    public static void main(String[] args) {
        System.out.println("Starting Full Production Database Schema & Seed Data Initialization on Clever Cloud...");
        
        executeSqlResource("database/schema.sql");
        executeSqlResource("database/seed-data.sql");

        System.out.println("Database Initialization Finished Successfully!");
        System.exit(0);
    }

    private static void executeSqlResource(String resourcePath) {
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             InputStream is = DbInitializer.class.getClassLoader().getResourceAsStream(resourcePath)) {

            if (is == null) {
                System.err.println("Could not find " + resourcePath + " in classpath!");
                return;
            }

            String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            String[] statements = content.split(";");

            int successCount = 0;
            for (String sql : statements) {
                String trimmed = sql.trim();
                if (trimmed.isEmpty()) continue;

                String query = cleanSqlComments(trimmed);
                if (query.isEmpty()) continue;

                try {
                    stmt.execute(query);
                    successCount++;
                } catch (Exception e) {
                    System.err.println("SQL Warning [" + resourcePath + "]: " + e.getMessage());
                }
            }

            System.out.println("Successfully executed " + successCount + " SQL statements from " + resourcePath);

        } catch (Exception e) {
            System.err.println("Error reading/executing " + resourcePath + ": " + e.getMessage());
        }
    }

    private static String cleanSqlComments(String sql) {
        StringBuilder sb = new StringBuilder();
        for (String line : sql.split("\n")) {
            String lineTrim = line.trim();
            if (!lineTrim.startsWith("--") && !lineTrim.startsWith("/*") && !lineTrim.startsWith("#")) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString().trim();
    }
}
