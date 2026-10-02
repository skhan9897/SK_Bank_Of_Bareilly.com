package com.skbank.listener;

import com.skbank.dao.SystemSettingsDAO;
import com.skbank.dao.impl.SystemSettingsDAOImpl;
import com.skbank.util.DatabaseConnection;
import com.skbank.util.SystemSettingsUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class AppContextListener implements ServletContextListener {

    private static final Logger LOGGER = Logger.getLogger(AppContextListener.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        LOGGER.info("SK BANK OF BAREILLY Application Starting Up...");
        autoSeedDatabaseIfEmpty();
        try {
            SystemSettingsDAO dao = new SystemSettingsDAOImpl();
            Map<String, String> dbSettings = dao.loadAllAsMap();
            if (!dbSettings.isEmpty()) {
                SystemSettingsUtil.updateSettings(dbSettings);
                LOGGER.info("Loaded " + dbSettings.size() + " system settings from database.");
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Could not load system settings on startup, using defaults", e);
        }
    }

    private void autoSeedDatabaseIfEmpty() {
        String checkSql = "SELECT COUNT(*) FROM branches";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(checkSql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next() && rs.getInt(1) > 0) {
                return; // Already seeded
            }
        } catch (Exception ignored) {
            // Table might not exist or be empty, proceed to seed
        }

        LOGGER.info("Seeding initial database tables and default records...");
        String[] seedSqls = new String[] {
            "CREATE TABLE IF NOT EXISTS branches (branch_id BIGINT AUTO_INCREMENT PRIMARY KEY, branch_code VARCHAR(20) NOT NULL UNIQUE, branch_name VARCHAR(100) NOT NULL, address VARCHAR(255) NOT NULL, city VARCHAR(50) NOT NULL, state VARCHAR(50) NOT NULL, pincode VARCHAR(10) NOT NULL, ifsc_code VARCHAR(20) NOT NULL UNIQUE, phone VARCHAR(15) NOT NULL, status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;",
            "CREATE TABLE IF NOT EXISTS account_types (account_type_id BIGINT AUTO_INCREMENT PRIMARY KEY, type_code VARCHAR(30) NOT NULL UNIQUE, type_name VARCHAR(50) NOT NULL, description VARCHAR(255) NULL, minimum_balance DECIMAL(18,2) NOT NULL DEFAULT 0.00, interest_rate DECIMAL(5,2) NOT NULL DEFAULT 0.00, status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;",
            "INSERT IGNORE INTO branches (branch_id, branch_code, branch_name, address, city, state, pincode, ifsc_code, phone) VALUES (1, 'SKB001', 'Main Branch Bareilly', 'Civil Lines, Near Cantonment', 'Bareilly', 'Uttar Pradesh', '243001', 'SKBK0000001', '0581-2550001'), (2, 'SKB002', 'Izzatnagar Branch', 'Near Railway Station, Izzatnagar', 'Bareilly', 'Uttar Pradesh', '243122', 'SKBK0000002', '0581-2550002'), (3, 'SKB003', 'Rajendra Nagar Branch', 'Block B, Rajendra Nagar', 'Bareilly', 'Uttar Pradesh', '243122', 'SKBK0000003', '0581-2550003'), (4, 'SKB004', 'Noida Cyber Branch', 'Sector 62, Electronic City', 'Noida', 'Uttar Pradesh', '201309', 'SKBK0000004', '0120-2550004');",
            "INSERT IGNORE INTO account_types (account_type_id, type_code, type_name, description, minimum_balance, interest_rate) VALUES (1, 'SAVINGS', 'Savings Account', 'Standard personal savings account with interest', 1000.00, 4.00), (2, 'CURRENT', 'Current Account', 'Business account for high volume transactions', 5000.00, 0.00), (3, 'SALARY', 'Corporate Salary Account', 'Zero-balance salary account with premium benefits', 0.00, 4.50), (4, 'BASIC_SAVINGS', 'Basic Savings Account (BSBD)', 'Zero-balance basic savings bank deposit account', 0.00, 3.50), (5, 'SENIOR_CITIZEN', 'Senior Citizen Savings Account', 'Special savings account for citizens aged 60+', 1000.00, 5.00);"
        };

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            for (String sql : seedSqls) {
                try {
                    stmt.execute(sql);
                } catch (Exception e) {
                    LOGGER.log(Level.FINE, "Seed statement skipped/failed: " + e.getMessage());
                }
            }
            LOGGER.info("Default branches and account types seeded successfully.");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Auto-seeding encountered issue: " + e.getMessage());
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        LOGGER.info("SK BANK OF BAREILLY Application Shutting Down.");
    }
}
