package com.skbank.dao;

import com.skbank.config.DatabaseConfig;
import com.skbank.model.FixedDeposit;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class  FDDAO {

    public List<FixedDeposit> findByCustomerId(String customerId) throws SQLException {
        List<FixedDeposit> list = new ArrayList<>();
        String sql = "SELECT fd.*, a.account_number FROM fixed_deposits fd " +
                     "JOIN accounts a ON fd.account_id = a.account_id WHERE fd.customer_id = ? ORDER BY fd.created_at DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapFD(rs));
                }
            }
        }
        return list;
    }

    public FixedDeposit findById(int fdId) throws SQLException {
        String sql = "SELECT fd.*, a.account_number, CONCAT(c.first_name, ' ', c.last_name) AS customer_name " +
                     "FROM fixed_deposits fd " +
                     "JOIN accounts a ON fd.account_id = a.account_id " +
                     "JOIN customers c ON fd.customer_id = c.customer_id WHERE fd.fd_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, fdId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapFD(rs);
                }
            }
        }
        return null;
    }

    public boolean createFD(FixedDeposit fd) throws SQLException {
        String sql = "INSERT INTO fixed_deposits (customer_id, account_id, receipt_number, deposit_amount, interest_rate, tenure_months, maturity_amount, maturity_date, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fd.getCustomerId());
            ps.setInt(2, fd.getAccountId());
            ps.setString(3, fd.getReceiptNumber());
            ps.setDouble(4, fd.getDepositAmount());
            ps.setDouble(5, fd.getInterestRate());
            ps.setInt(6, fd.getTenureMonths());
            ps.setDouble(7, fd.getMaturityAmount());
            ps.setDate(8, fd.getMaturityDate());
            ps.setString(9, fd.getStatus() != null ? fd.getStatus() : "ACTIVE");
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateStatus(int fdId, String status) throws SQLException {
        String sql = "UPDATE fixed_deposits SET status = ? WHERE fd_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, fdId);
            return ps.executeUpdate() > 0;
        }
    }

    public List<FixedDeposit> findAll() throws SQLException {
        List<FixedDeposit> list = new ArrayList<>();
        String sql = "SELECT fd.*, a.account_number, CONCAT(c.first_name, ' ', c.last_name) AS customer_name " +
                     "FROM fixed_deposits fd " +
                     "JOIN accounts a ON fd.account_id = a.account_id " +
                     "JOIN customers c ON fd.customer_id = c.customer_id ORDER BY fd.created_at DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapFD(rs));
            }
        }
        return list;
    }

    public double getTotalActiveFDAmount() throws SQLException {
        String sql = "SELECT SUM(deposit_amount) FROM fixed_deposits WHERE status = 'ACTIVE'";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        }
        return 0.0;
    }

    private FixedDeposit mapFD(ResultSet rs) throws SQLException {
        FixedDeposit fd = new FixedDeposit();
        fd.setFdId(rs.getInt("fd_id"));
        fd.setCustomerId(rs.getString("customer_id"));
        fd.setAccountId(rs.getInt("account_id"));
        fd.setReceiptNumber(rs.getString("receipt_number"));
        fd.setDepositAmount(rs.getDouble("deposit_amount"));
        fd.setInterestRate(rs.getDouble("interest_rate"));
        fd.setTenureMonths(rs.getInt("tenure_months"));
        fd.setMaturityAmount(rs.getDouble("maturity_amount"));
        fd.setMaturityDate(rs.getDate("maturity_date"));
        fd.setStatus(rs.getString("status"));
        fd.setCreatedAt(rs.getTimestamp("created_at"));

        try { fd.setAccountNumber(rs.getString("account_number")); } catch (Exception ignored) {}
        try { fd.setCustomerName(rs.getString("customer_name")); } catch (Exception ignored) {}

        return fd;
    }
}
