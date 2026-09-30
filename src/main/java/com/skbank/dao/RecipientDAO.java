package com.skbank.dao;

import com.skbank.config.DatabaseConfig;
import com.skbank.model.RecipientDTO;

import java.sql.*;

public class RecipientDAO {

    public RecipientDTO findByMobile(String mobile) throws SQLException {
        String sql = "SELECT c.customer_id, CONCAT(c.first_name, ' ', c.last_name) AS customer_name, c.mobile, " +
                     "a.account_number, b.branch_name, b.ifsc_code, u.upi_address " +
                     "FROM customers c " +
                     "JOIN accounts a ON c.customer_id = a.customer_id " +
                     "JOIN branches b ON a.branch_id = b.branch_id " +
                     "LEFT JOIN upi_accounts u ON c.customer_id = u.customer_id " +
                     "WHERE c.mobile = ? AND a.status = 'ACTIVE' LIMIT 1";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, mobile.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRecipient(rs);
                }
            }
        }
        return null;
    }

    public RecipientDTO findByAccountNumber(String accountNumber) throws SQLException {
        String sql = "SELECT c.customer_id, CONCAT(c.first_name, ' ', c.last_name) AS customer_name, c.mobile, " +
                     "a.account_number, b.branch_name, b.ifsc_code, u.upi_address " +
                     "FROM accounts a " +
                     "JOIN customers c ON a.customer_id = c.customer_id " +
                     "JOIN branches b ON a.branch_id = b.branch_id " +
                     "LEFT JOIN upi_accounts u ON c.customer_id = u.customer_id " +
                     "WHERE a.account_number = ? AND a.status = 'ACTIVE' LIMIT 1";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountNumber.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRecipient(rs);
                }
            }
        }
        return null;
    }

    public RecipientDTO findByUpiId(String upiId) throws SQLException {
        String sql = "SELECT c.customer_id, CONCAT(c.first_name, ' ', c.last_name) AS customer_name, c.mobile, " +
                     "a.account_number, b.branch_name, b.ifsc_code, u.upi_address " +
                     "FROM upi_accounts u " +
                     "JOIN customers c ON u.customer_id = c.customer_id " +
                     "JOIN accounts a ON u.account_id = a.account_id " +
                     "JOIN branches b ON a.branch_id = b.branch_id " +
                     "WHERE u.upi_address = ? AND u.status = 'ACTIVE' AND a.status = 'ACTIVE' LIMIT 1";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, upiId.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRecipient(rs);
                }
            }
        }
        return null;
    }

    private RecipientDTO mapRecipient(ResultSet rs) throws SQLException {
        RecipientDTO dto = new RecipientDTO();
        dto.setCustomerId(rs.getString("customer_id"));
        dto.setCustomerName(rs.getString("customer_name"));

        String rawMob = rs.getString("mobile");
        if (rawMob != null && rawMob.length() == 10) {
            dto.setMaskedMobile(rawMob.substring(0, 2) + "******" + rawMob.substring(8));
        } else {
            dto.setMaskedMobile("98******10");
        }

        String rawAcc = rs.getString("account_number");
        dto.setAccountNumber(rawAcc);
        if (rawAcc != null && rawAcc.length() >= 4) {
            dto.setMaskedAccountNumber("XXXXXX" + rawAcc.substring(rawAcc.length() - 4));
        } else {
            dto.setMaskedAccountNumber("XXXXXX1234");
        }

        String upi = rs.getString("upi_address");
        dto.setUpiId(upi != null ? upi : dto.getCustomerName().toLowerCase().replaceAll("\\s+", "") + "@skbank");

        dto.setBranchName(rs.getString("branch_name"));
        dto.setIfsc(rs.getString("ifsc_code"));
        dto.setBankName("SK Bank of Bareilly");
        dto.setStatus("ACTIVE");

        return dto;
    }
}
