package com.skbank.dao.impl;

import com.skbank.dao.BillPaymentDAO;
import com.skbank.model.BillPayment;
import com.skbank.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BillPaymentDAOImpl implements BillPaymentDAO {

    @Override
    public Long create(Connection conn, BillPayment bp) throws SQLException {
        String sql = "INSERT INTO bill_payments (customer_id, account_id, biller_type, biller_name, consumer_number, amount, payment_reference, status, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, 'SUCCESS', NOW())";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, bp.getCustomerId());
            ps.setLong(2, bp.getAccountId());
            ps.setString(3, bp.getBillerType());
            ps.setString(4, bp.getBillerName());
            ps.setString(5, bp.getConsumerNumber());
            ps.setBigDecimal(6, bp.getAmount());
            ps.setString(7, bp.getPaymentReference());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        }
        return null;
    }

    @Override
    public List<BillPayment> findByCustomerId(String customerId) throws SQLException {
        List<BillPayment> list = new ArrayList<>();
        String sql = "SELECT bp.*, a.account_number FROM bill_payments bp JOIN accounts a ON bp.account_id = a.account_id WHERE bp.customer_id = ? ORDER BY bp.created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapBill(rs));
                }
            }
        }
        return list;
    }

    @Override
    public List<BillPayment> findAllAdmin(int offset, int limit) throws SQLException {
        List<BillPayment> list = new ArrayList<>();
        String sql = "SELECT bp.*, a.account_number FROM bill_payments bp JOIN accounts a ON bp.account_id = a.account_id ORDER BY bp.created_at DESC LIMIT ? OFFSET ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapBill(rs));
                }
            }
        }
        return list;
    }

    @Override
    public long countAllAdmin() throws SQLException {
        String sql = "SELECT COUNT(*) FROM bill_payments";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getLong(1);
        }
        return 0;
    }

    private BillPayment mapBill(ResultSet rs) throws SQLException {
        BillPayment bp = new BillPayment();
        bp.setBillPaymentId(rs.getLong("bill_payment_id"));
        bp.setCustomerId(rs.getString("customer_id"));
        bp.setAccountId(rs.getLong("account_id"));
        bp.setBillerType(rs.getString("biller_type"));
        bp.setBillerName(rs.getString("biller_name"));
        bp.setConsumerNumber(rs.getString("consumer_number"));
        bp.setAmount(rs.getBigDecimal("amount"));
        bp.setPaymentReference(rs.getString("payment_reference"));
        bp.setStatus(rs.getString("status"));
        bp.setCreatedAt(rs.getTimestamp("created_at"));
        try { bp.setAccountNumber(rs.getString("account_number")); } catch (SQLException ignored) {}
        return bp;
    }
}
