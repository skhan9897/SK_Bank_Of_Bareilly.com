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
    public PaymentTransaction findByIdempotencyKey(String key) throws SQLException {
        if (key == null || key.trim().isEmpty()) return null;
        String sql = "SELECT * FROM payment_transactions WHERE idempotency_key = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, key.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapPaymentTransaction(rs);
            }
        }
        return null;
    }

    @Override
    public PaymentTransaction findByReference(String referenceNumber) throws SQLException {
        String sql = "SELECT * FROM payment_transactions WHERE reference_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, referenceNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapPaymentTransaction(rs);
            }
        }
        return null;
    }

    @Override
    public Long create(Connection conn, PaymentTransaction pt) throws SQLException {
        String sql = "INSERT INTO payment_transactions (customer_id, source_account_id, payment_type, provider_code, recipient_identifier, amount, reference_number, idempotency_key, status, remarks, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, pt.getCustomerId());
            if (pt.getSourceAccountId() != null) {
                ps.setLong(2, pt.getSourceAccountId());
            } else {
                ps.setNull(2, Types.BIGINT);
            }
            ps.setString(3, pt.getPaymentType().name());
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

    @Override
    public List<PaymentTransaction> findByCustomerId(String customerId, int offset, int limit) throws SQLException {
        List<PaymentTransaction> list = new ArrayList<>();
        String sql = "SELECT * FROM payment_transactions WHERE customer_id = ? ORDER BY created_at DESC LIMIT ? OFFSET ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customerId);
            ps.setInt(2, limit);
            ps.setInt(3, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapPaymentTransaction(rs));
            }
        }
        return list;
    }

    private PaymentTransaction mapPaymentTransaction(ResultSet rs) throws SQLException {
        PaymentTransaction pt = new PaymentTransaction();
        pt.setPaymentTransactionId(rs.getLong("payment_transaction_id"));
        pt.setCustomerId(rs.getString("customer_id"));
        long srcAcc = rs.getLong("source_account_id");
        if (!rs.wasNull()) pt.setSourceAccountId(srcAcc);
        pt.setPaymentType(PaymentType.valueOf(rs.getString("payment_type")));
        pt.setProviderCode(rs.getString("provider_code"));
        pt.setRecipientIdentifier(rs.getString("recipient_identifier"));
        pt.setAmount(rs.getBigDecimal("amount"));
        pt.setReferenceNumber(rs.getString("reference_number"));
        pt.setIdempotencyKey(rs.getString("idempotency_key"));
        pt.setStatus(PaymentStatus.valueOf(rs.getString("status")));
        pt.setRemarks(rs.getString("remarks"));
        pt.setCreatedAt(rs.getTimestamp("created_at"));
        pt.setUpdatedAt(rs.getTimestamp("updated_at"));
        return pt;
    }
}
