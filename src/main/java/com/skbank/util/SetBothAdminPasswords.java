package com.skbank.util;

import com.skbank.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class SetBothAdminPasswords {

    public static void main(String[] args) {
        System.out.println("Updating admin passwords in live DB...");

        String hashAdmin123 = PasswordUtil.hashPassword("Admin@123");
        String hashAdmin9897 = PasswordUtil.hashPassword("Admin9897");

        try (Connection conn = DatabaseConfig.getConnection()) {
            // Set 'admin' password to 'Admin@123'
            String sql1 = "UPDATE users SET password_hash = ?, status = 'ACTIVE' WHERE username = 'admin'";
            try (PreparedStatement ps = conn.prepareStatement(sql1)) {
                ps.setString(1, hashAdmin123);
                ps.executeUpdate();
            }

            // Set 'SKBOB9897' password to 'Admin9897'
            String sql2 = "UPDATE users SET password_hash = ?, status = 'ACTIVE' WHERE username = 'SKBOB9897'";
            try (PreparedStatement ps = conn.prepareStatement(sql2)) {
                ps.setString(1, hashAdmin9897);
                ps.executeUpdate();
            }

            System.out.println("Admin Passwords Successfully Set!");
            System.out.println("- Username: admin | Password: Admin@123");
            System.out.println("- Username: SKBOB9897 | Password: Admin9897");

            System.exit(0);

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
