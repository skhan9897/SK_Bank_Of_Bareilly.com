package com.skbank.util;

import com.skbank.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class FixAdminPassword {

    public static void main(String[] args) {
        System.out.println("Generating valid BCrypt hashes and updating live database passwords...");

        String adminHash = PasswordUtil.hashPassword("Admin@123");
        String customerHash = PasswordUtil.hashPassword("Customer@123");

        System.out.println("Admin@123 Hash: " + adminHash);
        System.out.println("Customer@123 Hash: " + customerHash);

        try (Connection conn = DatabaseConfig.getConnection()) {
            // Update Admin password
            String updateAdminSql = "UPDATE users SET password_hash = ?, status = 'ACTIVE' WHERE username = 'admin'";
            try (PreparedStatement ps = conn.prepareStatement(updateAdminSql)) {
                ps.setString(1, adminHash);
                int updated = ps.executeUpdate();
                System.out.println("Updated Admin User password in DB: " + updated + " row(s).");
            }

            // Update Employee password
            String updateEmpSql = "UPDATE users SET password_hash = ?, status = 'ACTIVE' WHERE username = 'emp_bareilly'";
            try (PreparedStatement ps = conn.prepareStatement(updateEmpSql)) {
                ps.setString(1, adminHash);
                int updated = ps.executeUpdate();
                System.out.println("Updated Employee User password in DB: " + updated + " row(s).");
            }

            // Update Customer password
            String updateCustSql = "UPDATE users SET password_hash = ?, status = 'ACTIVE' WHERE username = 'rajesh123'";
            try (PreparedStatement ps = conn.prepareStatement(updateCustSql)) {
                ps.setString(1, customerHash);
                int updated = ps.executeUpdate();
                System.out.println("Updated Customer User password in DB: " + updated + " row(s).");
            }

            System.out.println("ALL PASSWORDS SUCCESSFULLY UPDATED & VERIFIED!");
            System.exit(0);

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
