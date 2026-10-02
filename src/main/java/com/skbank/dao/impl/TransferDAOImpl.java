package com.skbank.dao.impl;

import com.skbank.dao.TransferDAO;
import com.skbank.model.TransferRequest;
import com.skbank.model.TransferType;
import com.skbank.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransferDAOImpl implements TransferDAO {

    @Override
    public TransferRequest findById(Long transferId) throws SQLException {
        String sql = "SELECT * FROM transfer_requests WHERE transfer_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, transferId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapTransfer(rs);
            }
        }
        return null;
    }

    @Override
    public TransferRequest findByReference(String referenceNumber) throws SQLException {
        String sql = "SELECT * FROM transfer_requests WHERE reference_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, referenceNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapTransfer(rs);
            }
        }
        return null;
    }

    @Override
    public Long create(Connection conn, TransferRequest tr) throws SQLException {
        String sql = "INSERT INTO transfer_requests (reference_number, sender_account_id, receiver_account_id, amount, transfer_type, remarks, status, created_at, completed_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, tr.getReferenceNumber());
            ps.setLong(2, tr.getSenderAccountId());
            ps.setLong(3, tr.getReceiverAccountId());
            ps.setBigDecimal(4, tr.getAmount());
            ps.setString(5, tr.getTransferType().name());
            ps.setString(6, tr.getRemarks());
            ps.setString(7, tr.getStatus() != null ? tr.getStatus() : "COMPLETED");
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        }
        return null;
    }

    @Override
    public List<TransferRequest> findByAccountId(Long accountId, int offset, int limit) throws SQLException {
        List<TransferRequest> list = new ArrayList<>();
        String sql = "SELECT * FROM transfer_requests WHERE sender_account_id = ? OR receiver_account_id = ? ORDER BY created_at DESC LIMIT ? OFFSET ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, accountId);
            ps.setLong(2, accountId);
            ps.setInt(3, limit);
            ps.setInt(4, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapTransfer(rs));
                }
            }
        }
        return list;
    }

    private TransferRequest mapTransfer(ResultSet rs) throws SQLException {
        TransferRequest tr = new TransferRequest();
        tr.setTransferId(rs.getLong("transfer_id"));
        tr.setReferenceNumber(rs.getString("reference_number"));
        tr.setSenderAccountId(rs.getLong("sender_account_id"));
        tr.setReceiverAccountId(rs.getLong("receiver_account_id"));
        tr.setAmount(rs.getBigDecimal("amount"));
        tr.setTransferType(TransferType.valueOf(rs.getString("transfer_type")));
        tr.setRemarks(rs.getString("remarks"));
        tr.setStatus(rs.getString("status"));
        tr.setCreatedAt(rs.getTimestamp("created_at"));
        tr.setCompletedAt(rs.getTimestamp("completed_at"));
        return tr;
    }
}
