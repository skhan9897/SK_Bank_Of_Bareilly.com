package com.skbank.util;

import com.skbank.config.DatabaseConfig;
import com.skbank.model.User;
import com.skbank.service.AuthenticationService;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class TestCustomerLogin {

    public static void main(String[] args) {
        System.out.println("Testing Customer Login...");
        AuthenticationService authService = new AuthenticationService();

        // 1. Ensure 'rajesh123' password is set to 'Customer@123'
        String custHash = PasswordUtil.hashPassword("Customer@123");
        try (Connection conn = DatabaseConfig.getConnection()) {
            String sql = "UPDATE users SET password_hash = ?, status = 'ACTIVE', failed_attempts = 0 WHERE username = 'rajesh123'";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, custHash);
                ps.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            // Test 1: Username 'rajesh123' + Password 'Customer@123'
            User u1 = authService.authenticate("rajesh123", "Customer@123");
            if (u1 != null) {
                System.out.println("PASSED: Login by Username 'rajesh123' SUCCESSFUL!");
            } else {
                System.err.println("FAILED: Login by Username 'rajesh123' failed!");
            }

            // Test 2: Customer ID 'SKC10001' + Password 'Customer@123'
            User u2 = authService.authenticate("SKC10001", "Customer@123");
            if (u2 != null) {
                System.out.println("PASSED: Login by Customer ID 'SKC10001' SUCCESSFUL!");
            } else {
                System.err.println("FAILED: Login by Customer ID 'SKC10001' failed!");
            }

            System.exit(0);

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
