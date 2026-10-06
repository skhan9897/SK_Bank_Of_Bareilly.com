package com.skbank.dao.impl;

import com.skbank.dao.PaymentTransactionDAO;
import com.skbank.model.PaymentStatus;
import com.skbank.model.PaymentTransaction;
import com.skbank.model.PaymentType;
import com.skbank.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentTransactionDAOImpl implements PaymentTransactionDAO {

    @Override
    public List<PaymentTransaction> findByCustomerId(Long customerId, int offset, int limit) throws SQLException {
        List<PaymentTransaction> list = new ArrayList<>();
        String sql = "SELECT * FROM payment_transactions WHERE customer_id = ? ORDER BY payment_transaction_id DESC LIMIT ? OFFSET ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, customerId);
            ps.setInt(2, limit);
            ps.setInt(3, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapPaymentTxn(rs));
            }
        }
        return list;
    }

    @Override
    public Long create(Connection conn, PaymentTransaction pt) throws SQLException {
        String sql = "INSERT INTO payment_transactions (customer_id, source_account_id, payment_type, provider_code, recipient_identifier, amount, reference_number, idempotency_key, status, remarks, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, pt.getCustomerId());
            ps.setLong(2, pt.getSourceAccountId());
            ps.setString(3, pt.getPaymentType() != null ? pt.getPaymentType().name() : PaymentType.QR_PAY.name());
            ps.setString(4, pt.getProviderCode());
            ps.setString(5, pt.getRecipientIdentifier());
            ps.setBigDecimal(6, pt.getAmount());
            ps.setString(7, pt.getReferenceNumber());
            ps.setString(8, pt.getIdempotencyKey());
            ps.setString(9, pt.getStatus() != null ? pt.getStatus().name() : PaymentStatus.SUCCESS.name());
            ps.setString(10, pt.getRemarks());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        }
        return null;
    }

    private PaymentTransaction mapPaymentTxn(ResultSet rs) throws SQLException {
        PaymentTransaction pt = new PaymentTransaction();
        pt.setPaymentTransactionId(rs.getLong("payment_transaction_id"));
        pt.setCustomerId(rs.getLong("customer_id"));
        pt.setSourceAccountId(rs.getLong("source_account_id"));
        try { pt.setPaymentType(PaymentType.valueOf(rs.getString("payment_type"))); } catch (Exception ignored) {}
        pt.setProviderCode(rs.getString("provider_code"));
        pt.setRecipientIdentifier(rs.getString("recipient_identifier"));
        pt.setAmount(rs.getBigDecimal("amount"));
        pt.setReferenceNumber(rs.getString("reference_number"));
        pt.setIdempotencyKey(rs.getString("idempotency_key"));
        try { pt.setStatus(PaymentStatus.valueOf(rs.getString("status"))); } catch (Exception ignored) {}
        pt.setRemarks(rs.getString("remarks"));
        try { pt.setCreatedAt(rs.getTimestamp("created_at")); } catch (Exception ignored) {}
        return pt;
    }
}
