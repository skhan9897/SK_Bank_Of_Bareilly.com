package com.skbank.dao.impl;

import com.skbank.dao.TransferDAO;
import com.skbank.model.TransferRequest;
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
        String transferType = (tr.getTransferType() != null && !tr.getTransferType().trim().isEmpty()) 
                              ? tr.getTransferType() : "INTERNAL";
        String status = (tr.getStatus() != null && !tr.getStatus().trim().isEmpty()) 
                        ? tr.getStatus() : "COMPLETED";

        String fullSql = "INSERT INTO transfer_requests (reference_number, sender_account_id, receiver_account_id, amount, transfer_type, remarks, status) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try {
            return executeInsert(conn, fullSql, tr, transferType, status);
        } catch (SQLException e) {
            String msg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
            if (msg.contains("transfer_type") || msg.contains("unknown column") || msg.contains("field list")) {
                ensureColumns(conn);
                return executeInsert(conn, fullSql, tr, transferType, status);
            }
            throw e;
        }
    }

    private Long executeInsert(Connection conn, String sql, TransferRequest tr, String transferType, String status) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, tr.getReferenceNumber());
            ps.setLong(2, tr.getSenderAccountId());
            ps.setLong(3, tr.getReceiverAccountId());
            ps.setBigDecimal(4, tr.getAmount());
            ps.setString(5, transferType);
            ps.setString(6, tr.getRemarks());
            ps.setString(7, status);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        }
        return null;
    }

    private void ensureColumns(Connection conn) {
        String[] alterSqls = new String[] {
            "ALTER TABLE transfer_requests ADD COLUMN transfer_type VARCHAR(30) NOT NULL DEFAULT 'INTERNAL'",
            "ALTER TABLE transfer_requests ADD COLUMN remarks VARCHAR(255) NULL",
            "ALTER TABLE transfer_requests ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'COMPLETED'",
            "ALTER TABLE transfer_requests ADD COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP",
            "ALTER TABLE transfer_requests ADD COLUMN completed_at DATETIME NULL"
        };
        for (String sql : alterSqls) {
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(sql);
            } catch (Exception ignored) {}
        }
    }

    @Override
    public List<TransferRequest> findByAccountId(Long accountId, int offset, int limit) throws SQLException {
        List<TransferRequest> list = new ArrayList<>();
        String sql = "SELECT * FROM transfer_requests WHERE sender_account_id = ? OR receiver_account_id = ? ORDER BY transfer_id DESC LIMIT ? OFFSET ?";
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
        try { tr.setTransferType(rs.getString("transfer_type")); } catch (Exception ignored) { tr.setTransferType("INTERNAL"); }
        try { tr.setRemarks(rs.getString("remarks")); } catch (Exception ignored) {}
        try { tr.setStatus(rs.getString("status")); } catch (Exception ignored) { tr.setStatus("COMPLETED"); }
        try { tr.setCreatedAt(rs.getTimestamp("created_at")); } catch (Exception ignored) {}
        try { tr.setCompletedAt(rs.getTimestamp("completed_at")); } catch (Exception ignored) {}
        return tr;
    }
}
