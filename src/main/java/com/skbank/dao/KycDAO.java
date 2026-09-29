package com.skbank.dao;

import com.skbank.config.DatabaseConfig;
import com.skbank.model.KycDocument;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KycDAO {

    public List<KycDocument> findByCustomerId(String customerId) throws SQLException {
        List<KycDocument> list = new ArrayList<>();
        String sql = "SELECT * FROM kyc_documents WHERE customer_id = ? ORDER BY uploaded_at DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapKyc(rs));
                }
            }
        }
        return list;
    }

    public boolean uploadDocument(KycDocument doc) throws SQLException {
        String sql = "INSERT INTO kyc_documents (customer_id, document_type, file_path, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, doc.getCustomerId());
            ps.setString(2, doc.getDocumentType());
            ps.setString(3, doc.getFilePath());
            ps.setString(4, doc.getStatus() != null ? doc.getStatus() : "PENDING");
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateKycStatus(int kycId, String status, String reason) throws SQLException {
        String sql = "UPDATE kyc_documents SET status = ?, rejection_reason = ? WHERE kyc_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, reason);
            ps.setInt(3, kycId);
            return ps.executeUpdate() > 0;
        }
    }

    public List<KycDocument> findAllPending() throws SQLException {
        List<KycDocument> list = new ArrayList<>();
        String sql = "SELECT k.*, CONCAT(c.first_name, ' ', c.last_name) AS customer_name " +
                     "FROM kyc_documents k JOIN customers c ON k.customer_id = c.customer_id " +
                     "WHERE k.status = 'PENDING' ORDER BY k.uploaded_at ASC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapKyc(rs));
            }
        }
        return list;
    }

    public List<KycDocument> findAll() throws SQLException {
        List<KycDocument> list = new ArrayList<>();
        String sql = "SELECT k.*, CONCAT(c.first_name, ' ', c.last_name) AS customer_name " +
                     "FROM kyc_documents k JOIN customers c ON k.customer_id = c.customer_id ORDER BY k.uploaded_at DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapKyc(rs));
            }
        }
        return list;
    }

    private KycDocument mapKyc(ResultSet rs) throws SQLException {
        KycDocument doc = new KycDocument();
        doc.setKycId(rs.getInt("kyc_id"));
        doc.setCustomerId(rs.getString("customer_id"));
        doc.setDocumentType(rs.getString("document_type"));
        doc.setFilePath(rs.getString("file_path"));
        doc.setStatus(rs.getString("status"));
        doc.setRejectionReason(rs.getString("rejection_reason"));
        doc.setUploadedAt(rs.getTimestamp("uploaded_at"));

        try { doc.setCustomerName(rs.getString("customer_name")); } catch (Exception ignored) {}

        return doc;
    }
}
