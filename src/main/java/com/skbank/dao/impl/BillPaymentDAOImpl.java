package com.skbank.dao.impl;

import com.skbank.dao.BillPaymentDAO;
import com.skbank.model.BillPayment;
import com.skbank.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BillPaymentDAOImpl implements BillPaymentDAO {

    @Override
    public List<BillPayment> findByCustomerId(Long customerId) throws SQLException {
        List<BillPayment> list = new ArrayList<>();
        String sql = "SELECT * FROM bill_payments WHERE customer_id = ? ORDER BY bill_payment_id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapBillPayment(rs));
            }
        }
        return list;
    }

    @Override
    public Long create(BillPayment bp) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return create(conn, bp);
        }
    }

    @Override
    public Long create(Connection conn, BillPayment bp) throws SQLException {
        String sql = "INSERT INTO bill_payments (customer_id, account_id, biller_type, biller_name, consumer_number, amount, payment_reference, status, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW())";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, bp.getCustomerId());
            ps.setLong(2, bp.getAccountId());
            ps.setString(3, bp.getBillerType());
            ps.setString(4, bp.getBillerName());
            ps.setString(5, bp.getConsumerNumber());
            ps.setBigDecimal(6, bp.getAmount());
            ps.setString(7, bp.getPaymentReference());
            ps.setString(8, bp.getStatus() != null ? bp.getStatus() : "SUCCESS");

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    Long id = rs.getLong(1);
                    bp.setBillPaymentId(id);
                    return id;
                }
            }
        }
        return null;
    }

    @Override
    public List<BillPayment> findAllAdmin(int offset, int limit) throws SQLException {
        List<BillPayment> list = new ArrayList<>();
        String sql = "SELECT * FROM bill_payments ORDER BY bill_payment_id DESC LIMIT ? OFFSET ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapBillPayment(rs));
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

    private BillPayment mapBillPayment(ResultSet rs) throws SQLException {
        BillPayment bp = new BillPayment();
        bp.setBillPaymentId(rs.getLong("bill_payment_id"));
        bp.setCustomerId(rs.getLong("customer_id"));
        bp.setAccountId(rs.getLong("account_id"));
        bp.setBillerType(rs.getString("biller_type"));
        bp.setBillerName(rs.getString("biller_name"));
        bp.setConsumerNumber(rs.getString("consumer_number"));
        bp.setAmount(rs.getBigDecimal("amount"));
        bp.setPaymentReference(rs.getString("payment_reference"));
        try { bp.setStatus(rs.getString("status")); } catch (Exception ignored) {}
        try { bp.setCreatedAt(rs.getTimestamp("created_at")); } catch (Exception ignored) {}
        return bp;
    }
}
