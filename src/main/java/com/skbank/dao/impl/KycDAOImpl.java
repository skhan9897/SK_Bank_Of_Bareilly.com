package com.skbank.dao.impl;

import com.skbank.dao.KycDAO;
import com.skbank.model.Kyc;
import com.skbank.model.KycStatus;
import com.skbank.util.DatabaseConnection;

import java.sql.*;

public class KycDAOImpl implements KycDAO {

    @Override
    public Kyc findByCustomerId(String customerId) throws SQLException {
        String[] tables = new String[]{"customer_kyc", "kyc_documents", "kyc"};
        for (String table : tables) {
            String sql = "SELECT * FROM " + table + " WHERE customer_id = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, customerId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return mapKyc(rs);
                }
            } catch (SQLException ignored) {}
        }
        return null;
    }

    @Override
    public Long create(Kyc kyc) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return create(conn, kyc);
        }
    }

    @Override
    public Long create(Connection conn, Kyc kyc) throws SQLException {
        String[] tables = new String[]{"customer_kyc", "kyc_documents", "kyc"};
        SQLException lastEx = null;

        for (String table : tables) {
            String sql = "INSERT INTO " + table + " (customer_id, aadhaar_number, pan_number, verification_status, verified_at, created_at) " +
                         "VALUES (?, ?, ?, ?, NOW(), NOW())";
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, kyc.getCustomerId());
                ps.setString(2, kyc.getAadhaarNumber());
                ps.setString(3, kyc.getPanNumber());
                ps.setString(4, kyc.getVerificationStatus() != null ? kyc.getVerificationStatus().name() : KycStatus.VERIFIED.name());
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) return rs.getLong(1);
                }
                return 1L;
            } catch (SQLException e) {
                lastEx = e;
                if (e.getMessage() != null && e.getMessage().toLowerCase().contains("doesn't exist")) {
                    continue; // Try next table name
                }
                throw e; // Standard SQL error (e.g. duplicate key or constraint)
            }
        }

        if (lastEx != null) throw lastEx;
        throw new SQLException("Failed to insert KYC record into customer_kyc table.");
    }

    @Override
    public boolean updateVerificationStatus(String customerId, String status) throws SQLException {
        String[] tables = new String[]{"customer_kyc", "kyc_documents", "kyc"};
        for (String table : tables) {
            String sql = "UPDATE " + table + " SET verification_status = ?, verified_at = NOW() WHERE customer_id = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, status);
                ps.setString(2, customerId);
                if (ps.executeUpdate() > 0) return true;
            } catch (SQLException ignored) {}
        }
        return false;
    }

    private Kyc mapKyc(ResultSet rs) throws SQLException {
        Kyc k = new Kyc();
        try {
            k.setKycId(rs.getLong("customer_kyc_id"));
        } catch (SQLException e) {
            try { k.setKycId(rs.getLong("kyc_id")); } catch (SQLException ignored) {}
        }
        k.setCustomerId(rs.getString("customer_id"));
        k.setAadhaarNumber(rs.getString("aadhaar_number"));
        k.setPanNumber(rs.getString("pan_number"));
        k.setVerificationStatus(KycStatus.valueOf(rs.getString("verification_status")));
        try { k.setVerifiedAt(rs.getTimestamp("verified_at")); } catch (SQLException ignored) {}
        try { k.setCreatedAt(rs.getTimestamp("created_at")); } catch (SQLException ignored) {}
        return k;
    }
}
