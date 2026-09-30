package com.skbank.dao;

import com.skbank.config.DatabaseConfig;
import com.skbank.model.CustomerKyc;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KycDAO {

    public CustomerKyc findByCustomerId(String customerId) throws SQLException {
        String sql = "SELECT * FROM customer_kyc WHERE customer_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapKyc(rs);
                }
            }
        }
        return null;
    }

    public CustomerKyc findByAadhaar(String aadhaarHash) throws SQLException {
        String sql = "SELECT * FROM customer_kyc WHERE aadhaar_number = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, aadhaarHash);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapKyc(rs);
                }
            }
        }
        return null;
    }

    public CustomerKyc findByPan(String panHash) throws SQLException {
        String sql = "SELECT * FROM customer_kyc WHERE pan_number = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, panHash);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapKyc(rs);
                }
            }
        }
        return null;
    }

    public boolean saveKyc(CustomerKyc kyc) throws SQLException {
        CustomerKyc existing = findByCustomerId(kyc.getCustomerId());
        if (existing == null) {
            String sql = "INSERT INTO customer_kyc (customer_id, aadhaar_number, aadhaar_masked, pan_number, pan_masked, kyc_status, verification_reference, rejection_reason) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (Connection conn = DatabaseConfig.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, kyc.getCustomerId());
                ps.setString(2, kyc.getAadhaarNumber());
                ps.setString(3, kyc.getAadhaarMasked());
                ps.setString(4, kyc.getPanNumber());
                ps.setString(5, kyc.getPanMasked());
                ps.setString(6, kyc.getKycStatus() != null ? kyc.getKycStatus() : "PENDING");
                ps.setString(7, kyc.getVerificationReference());
                ps.setString(8, kyc.getRejectionReason());
                return ps.executeUpdate() > 0;
            }
        } else {
            String sql = "UPDATE customer_kyc SET aadhaar_number = ?, aadhaar_masked = ?, pan_number = ?, pan_masked = ?, kyc_status = ?, verification_reference = ?, rejection_reason = ?, verified_at = ? WHERE customer_id = ?";
            try (Connection conn = DatabaseConfig.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, kyc.getAadhaarNumber());
                ps.setString(2, kyc.getAadhaarMasked());
                ps.setString(3, kyc.getPanNumber());
                ps.setString(4, kyc.getPanMasked());
                ps.setString(5, kyc.getKycStatus());
                ps.setString(6, kyc.getVerificationReference());
                ps.setString(7, kyc.getRejectionReason());
                if ("VERIFIED".equalsIgnoreCase(kyc.getKycStatus())) {
                    ps.setTimestamp(8, new Timestamp(System.currentTimeMillis()));
                } else {
                    ps.setNull(8, Types.TIMESTAMP);
                }
                ps.setString(9, kyc.getCustomerId());
                return ps.executeUpdate() > 0;
            }
        }
    }

    public boolean updateKycStatus(String customerId, String status, String refNo, String rejectionReason) throws SQLException {
        String sql = "UPDATE customer_kyc SET kyc_status = ?, verification_reference = ?, rejection_reason = ?, verified_at = ? WHERE customer_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, refNo);
            ps.setString(3, rejectionReason);
            if ("VERIFIED".equalsIgnoreCase(status)) {
                ps.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            } else {
                ps.setNull(4, Types.TIMESTAMP);
            }
            ps.setString(5, customerId);
            return ps.executeUpdate() > 0;
        }
    }

    public List<CustomerKyc> findAllPendingKyc() throws SQLException {
        List<CustomerKyc> list = new ArrayList<>();
        String sql = "SELECT * FROM customer_kyc WHERE kyc_status = 'PENDING' ORDER BY created_at DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapKyc(rs));
            }
        }
        return list;
    }

    public List<CustomerKyc> findAllKyc() throws SQLException {
        List<CustomerKyc> list = new ArrayList<>();
        String sql = "SELECT * FROM customer_kyc ORDER BY created_at DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapKyc(rs));
            }
        }
        return list;
    }

    private CustomerKyc mapKyc(ResultSet rs) throws SQLException {
        CustomerKyc k = new CustomerKyc();
        k.setKycId(rs.getLong("kyc_id"));
        k.setCustomerId(rs.getString("customer_id"));
        k.setAadhaarNumber(rs.getString("aadhaar_number"));
        k.setAadhaarMasked(rs.getString("aadhaar_masked"));
        k.setPanNumber(rs.getString("pan_number"));
        k.setPanMasked(rs.getString("pan_masked"));
        k.setKycStatus(rs.getString("kyc_status"));
        k.setVerificationReference(rs.getString("verification_reference"));
        k.setVerifiedAt(rs.getTimestamp("verified_at"));
        k.setRejectionReason(rs.getString("rejection_reason"));
        k.setCreatedAt(rs.getTimestamp("created_at"));
        k.setUpdatedAt(rs.getTimestamp("updated_at"));
        return k;
    }
}
