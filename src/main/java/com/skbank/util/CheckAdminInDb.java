package com.skbank.util;

import com.skbank.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CheckAdminInDb {

    public static void main(String[] args) {
        System.out.println("Checking users table in Clever Cloud DB...");
        try (Connection conn = DatabaseConfig.getConnection()) {
            String sql = "SELECT user_id, username, password_hash, role, status FROM users";
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    System.out.println("ID: " + rs.getInt("user_id") +
                                       " | Username: '" + rs.getString("username") + "'" +
                                       " | Role: " + rs.getString("role") +
                                       " | Status: " + rs.getString("status") +
                                       " | Hash: " + rs.getString("password_hash"));
                }
            }
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
