package com.skbank.util;

import com.skbank.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AddSecondaryAdmin {

    public static void main(String[] args) {
        System.out.println("Adding secondary admin credentials SKBOB9897...");

        String hash1 = PasswordUtil.hashPassword("Admin@123");
        String hash2 = PasswordUtil.hashPassword("Admin9897");

        try (Connection conn = DatabaseConfig.getConnection()) {
            // Update admin / Admin@123
            String sql1 = "UPDATE users SET password_hash = ?, status = 'ACTIVE' WHERE username = 'admin'";
            try (PreparedStatement ps = conn.prepareStatement(sql1)) {
                ps.setString(1, hash1);
                ps.executeUpdate();
            }

            // Check if SKBOB9897 exists
            String checkSql = "SELECT user_id FROM users WHERE username = 'SKBOB9897'";
            long userId = -1;
            try (PreparedStatement ps = conn.prepareStatement(checkSql);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    userId = rs.getLong(1);
                }
            }

            if (userId <= 0) {
                // Insert SKBOB9897
                String insertUser = "INSERT INTO users (username, password_hash, role, status) VALUES ('SKBOB9897', ?, 'ADMIN', 'ACTIVE')";
                try (PreparedStatement ps = conn.prepareStatement(insertUser, PreparedStatement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, hash2);
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) userId = rs.getLong(1);
                    }
                }

                if (userId > 0) {
                    String insertAdmin = "INSERT INTO admins (user_id, full_name, email, phone, department) VALUES (?, 'Secondary Admin', 'skbob9897@skbankofbareilly.example', '9876598970', 'Administration')";
                    try (PreparedStatement ps = conn.prepareStatement(insertAdmin)) {
                        ps.setLong(1, userId);
                        ps.executeUpdate();
                    }
                }
                System.out.println("Inserted new Admin SKBOB9897!");
            } else {
                // Update password for SKBOB9897
                String updateSql = "UPDATE users SET password_hash = ?, status = 'ACTIVE' WHERE username = 'SKBOB9897'";
                try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                    ps.setString(1, hash2);
                    ps.executeUpdate();
                }
                System.out.println("Updated password for Admin SKBOB9897!");
            }

            System.out.println("SUCCESSFULLY CONFIGURED ADMIN USERS!");
            System.exit(0);

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
