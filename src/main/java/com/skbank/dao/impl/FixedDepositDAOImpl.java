package com.skbank.dao.impl;

import com.skbank.dao.FixedDepositDAO;
import com.skbank.model.FixedDeposit;
import com.skbank.util.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FixedDepositDAOImpl implements FixedDepositDAO {

    @Override
    public List<FixedDeposit> findByCustomerId(Long customerId) throws SQLException {
        List<FixedDeposit> list = new ArrayList<>();
        String sql = "SELECT * FROM fixed_deposits WHERE customer_id = ? ORDER BY fd_id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapFd(rs));
            }
        }
        return list;
    }

    @Override
    public FixedDeposit findById(Long fdId) throws SQLException {
        String sql = "SELECT * FROM fixed_deposits WHERE fd_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, fdId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapFd(rs);
            }
        }
        return null;
    }

    @Override
    public Long create(Connection conn, FixedDeposit fd) throws SQLException {
        String sql = "INSERT INTO fixed_deposits (customer_id, account_id, fd_number, principal_amount, interest_rate, tenure_months, maturity_amount, start_date, maturity_date, status, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, fd.getCustomerId());
            ps.setLong(2, fd.getAccountId());
            ps.setString(3, fd.getFdNumber());
            ps.setBigDecimal(4, fd.getPrincipalAmount());
            ps.setBigDecimal(5, fd.getInterestRate());
            ps.setInt(6, fd.getTenureMonths());
            ps.setBigDecimal(7, fd.getMaturityAmount());
            ps.setDate(8, fd.getStartDate());
            ps.setDate(9, fd.getMaturityDate());
            ps.setString(10, fd.getStatus() != null ? fd.getStatus() : "ACTIVE");

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        }
        return null;
    }

    @Override
    public long countActiveFds() throws SQLException {
        String sql = "SELECT COUNT(*) FROM fixed_deposits WHERE status = 'ACTIVE'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getLong(1);
        }
        return 0;
    }

    @Override
    public BigDecimal getTotalFdInvestmentByCustomerId(Long customerId) throws SQLException {
        String sql = "SELECT SUM(principal_amount) FROM fixed_deposits WHERE customer_id = ? AND status = 'ACTIVE'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal sum = rs.getBigDecimal(1);
                    return sum != null ? sum : BigDecimal.ZERO;
                }
            }
        }
        return BigDecimal.ZERO;
    }

    @Override
    public List<FixedDeposit> findAllAdmin(int offset, int limit) throws SQLException {
        List<FixedDeposit> list = new ArrayList<>();
        String sql = "SELECT * FROM fixed_deposits ORDER BY fd_id DESC LIMIT ? OFFSET ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapFd(rs));
            }
        }
        return list;
    }

    @Override
    public long countAllAdmin() throws SQLException {
        String sql = "SELECT COUNT(*) FROM fixed_deposits";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getLong(1);
        }
        return 0;
    }

    private FixedDeposit mapFd(ResultSet rs) throws SQLException {
        FixedDeposit fd = new FixedDeposit();
        fd.setFdId(rs.getLong("fd_id"));
        fd.setCustomerId(rs.getLong("customer_id"));
        fd.setAccountId(rs.getLong("account_id"));
        fd.setFdNumber(rs.getString("fd_number"));
        fd.setPrincipalAmount(rs.getBigDecimal("principal_amount"));
        fd.setInterestRate(rs.getBigDecimal("interest_rate"));
        fd.setTenureMonths(rs.getInt("tenure_months"));
        fd.setMaturityAmount(rs.getBigDecimal("maturity_amount"));
        fd.setStartDate(rs.getDate("start_date"));
        fd.setMaturityDate(rs.getDate("maturity_date"));
        try { fd.setStatus(rs.getString("status")); } catch (Exception ignored) {}
        try { fd.setCreatedAt(rs.getTimestamp("created_at")); } catch (Exception ignored) {}
        return fd;
    }
}
