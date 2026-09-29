package com.skbank.dao;

import com.skbank.config.DatabaseConfig;
import com.skbank.model.BillPayment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BillPaymentDAO {

    public List<BillPayment> findByCustomerId(String customerId) throws SQLException {
        List<BillPayment> list = new ArrayList<>();
        String sql = "SELECT bp.*, a.account_number FROM bill_payments bp " +
                     "JOIN accounts a ON bp.account_id = a.account_id WHERE bp.customer_id = ? ORDER BY bp.created_at DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapBillPayment(rs));
                }
            }
        }
        return list;
    }

    public boolean createBillPayment(BillPayment bp) throws SQLException {
        String sql = "INSERT INTO bill_payments (customer_id, account_id, bill_category, provider, consumer_number, amount, reference_number, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, bp.getCustomerId());
            ps.setInt(2, bp.getAccountId());
            ps.setString(3, bp.getBillCategory());
            ps.setString(4, bp.getProvider());
            ps.setString(5, bp.getConsumerNumber());
            ps.setDouble(6, bp.getAmount());
            ps.setString(7, bp.getReferenceNumber());
            ps.setString(8, bp.getStatus() != null ? bp.getStatus() : "SUCCESS");
            return ps.executeUpdate() > 0;
        }
    }

    private BillPayment mapBillPayment(ResultSet rs) throws SQLException {
        BillPayment bp = new BillPayment();
        bp.setBillId(rs.getInt("bill_id"));
        bp.setCustomerId(rs.getString("customer_id"));
        bp.setAccountId(rs.getInt("account_id"));
        bp.setBillCategory(rs.getString("bill_category"));
        bp.setProvider(rs.getString("provider"));
        bp.setConsumerNumber(rs.getString("consumer_number"));
        bp.setAmount(rs.getDouble("amount"));
        bp.setReferenceNumber(rs.getString("reference_number"));
        bp.setStatus(rs.getString("status"));
        bp.setCreatedAt(rs.getTimestamp("created_at"));

        try { bp.setAccountNumber(rs.getString("account_number")); } catch (Exception ignored) {}

        return bp;
    }
}
