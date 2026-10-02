package com.skbank.dao.impl;

import com.skbank.dao.KycDAO;
import com.skbank.model.Kyc;
import com.skbank.model.KycStatus;
import com.skbank.util.DatabaseConnection;

import java.sql.*;

public class KycDAOImpl implements KycDAO {

    @Override
    public Kyc findByCustomerId(Long customerId) throws SQLException {
        String sql = "SELECT * FROM kyc WHERE customer_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Kyc k = new Kyc();
                    k.setKycId(rs.getLong("kyc_id"));
                    k.setCustomerId(rs.getLong("customer_id"));
                    k.setAadhaarNumber(rs.getString("aadhaar_number"));
                    k.setPanNumber(rs.getString("pan_number"));
                    k.setVerificationStatus(KycStatus.valueOf(rs.getString("verification_status")));
                    k.setVerifiedAt(rs.getTimestamp("verified_at"));
                    k.setCreatedAt(rs.getTimestamp("created_at"));
                    return k;
                }
            }
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
        String sql = "INSERT INTO kyc (customer_id, aadhaar_number, pan_number, verification_status, verified_at, created_at) " +
                     "VALUES (?, ?, ?, ?, NOW(), NOW())";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, kyc.getCustomerId());
            ps.setString(2, kyc.getAadhaarNumber());
            ps.setString(3, kyc.getPanNumber());
            ps.setString(4, kyc.getVerificationStatus() != null ? kyc.getVerificationStatus().name() : KycStatus.VERIFIED.name());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        }
        return null;
    }

    @Override
    public boolean updateVerificationStatus(Long customerId, String status) throws SQLException {
        String sql = "UPDATE kyc SET verification_status = ?, verified_at = NOW() WHERE customer_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setLong(2, customerId);
            return ps.executeUpdate() > 0;
        }
    }
}
