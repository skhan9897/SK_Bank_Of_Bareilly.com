package com.skbank.dao;

import com.skbank.config.DatabaseConfig;
import com.skbank.model.Beneficiary;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BeneficiaryDAO {

    public List<Beneficiary> findByCustomerId(String customerId) throws SQLException {
        List<Beneficiary> list = new ArrayList<>();
        String sql = "SELECT * FROM beneficiaries WHERE customer_id = ? ORDER BY created_at DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapBeneficiary(rs));
                }
            }
        }
        return list;
    }

    public Beneficiary findById(int beneficiaryId) throws SQLException {
        String sql = "SELECT * FROM beneficiaries WHERE beneficiary_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, beneficiaryId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapBeneficiary(rs);
                }
            }
        }
        return null;
    }

    public boolean addBeneficiary(Beneficiary b) throws SQLException {
        String sql = "INSERT INTO beneficiaries (customer_id, name, account_number, ifsc, bank_name, nickname, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, b.getCustomerId());
            ps.setString(2, b.getName());
            ps.setString(3, b.getAccountNumber());
            ps.setString(4, b.getIfsc());
            ps.setString(5, b.getBankName());
            ps.setString(6, b.getNickname());
            ps.setString(7, b.getStatus() != null ? b.getStatus() : "ACTIVE");
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateBeneficiary(Beneficiary b) throws SQLException {
        String sql = "UPDATE beneficiaries SET name = ?, account_number = ?, ifsc = ?, bank_name = ?, nickname = ? WHERE beneficiary_id = ? AND customer_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, b.getName());
            ps.setString(2, b.getAccountNumber());
            ps.setString(3, b.getIfsc());
            ps.setString(4, b.getBankName());
            ps.setString(5, b.getNickname());
            ps.setInt(6, b.getBeneficiaryId());
            ps.setString(7, b.getCustomerId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteBeneficiary(int beneficiaryId, String customerId) throws SQLException {
        String sql = "DELETE FROM beneficiaries WHERE beneficiary_id = ? AND customer_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, beneficiaryId);
            ps.setString(2, customerId);
            return ps.executeUpdate() > 0;
        }
    }

    private Beneficiary mapBeneficiary(ResultSet rs) throws SQLException {
        Beneficiary b = new Beneficiary();
        b.setBeneficiaryId(rs.getInt("beneficiary_id"));
        b.setCustomerId(rs.getString("customer_id"));
        b.setName(rs.getString("name"));
        b.setAccountNumber(rs.getString("account_number"));
        b.setIfsc(rs.getString("ifsc"));
        b.setBankName(rs.getString("bank_name"));
        b.setNickname(rs.getString("nickname"));
        b.setStatus(rs.getString("status"));
        b.setCreatedAt(rs.getTimestamp("created_at"));
        return b;
    }
}
