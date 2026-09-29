package com.skbank.util;

import com.skbank.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class DbVerifier {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SHOW TABLES")) {

            System.out.println("Tables present in Clever Cloud Database:");
            while (rs.next()) {
                System.out.println("- " + rs.getString(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
