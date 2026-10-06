package com.skbank.dao.impl;

import com.skbank.dao.BeneficiaryDAO;
import com.skbank.model.Beneficiary;
import com.skbank.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BeneficiaryDAOImpl implements BeneficiaryDAO {

    @Override
    public List<Beneficiary> findByCustomerId(Long customerId) throws SQLException {
        List<Beneficiary> list = new ArrayList<>();
        String sql = "SELECT * FROM beneficiaries WHERE customer_id = ? ORDER BY beneficiary_id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapBeneficiary(rs));
            }
        }
        return list;
    }

    @Override
    public Beneficiary findById(Long beneficiaryId) throws SQLException {
        String sql = "SELECT * FROM beneficiaries WHERE beneficiary_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, beneficiaryId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapBeneficiary(rs);
            }
        }
        return null;
    }

    @Override
    public Long create(Beneficiary b) throws SQLException {
        String sql = "INSERT INTO beneficiaries (customer_id, beneficiary_name, account_number, ifsc_code, bank_name, nickname, status, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, 'ACTIVE', NOW())";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, b.getCustomerId());
            ps.setString(2, b.getBeneficiaryName());
            ps.setString(3, b.getAccountNumber());
            ps.setString(4, b.getIfscCode());
            ps.setString(5, b.getBankName());
            ps.setString(6, b.getNickname());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    Long id = rs.getLong(1);
                    b.setBeneficiaryId(id);
                    return id;
                }
            }
        }
        return null;
    }

    @Override
    public boolean delete(Long beneficiaryId, Long customerId) throws SQLException {
        String sql = "DELETE FROM beneficiaries WHERE beneficiary_id = ? AND customer_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, beneficiaryId);
            ps.setLong(2, customerId);
            return ps.executeUpdate() > 0;
        }
    }

    private Beneficiary mapBeneficiary(ResultSet rs) throws SQLException {
        Beneficiary b = new Beneficiary();
        b.setBeneficiaryId(rs.getLong("beneficiary_id"));
        b.setCustomerId(rs.getLong("customer_id"));
        b.setBeneficiaryName(rs.getString("beneficiary_name"));
        b.setAccountNumber(rs.getString("account_number"));
        b.setIfscCode(rs.getString("ifsc_code"));
        b.setBankName(rs.getString("bank_name"));
        b.setNickname(rs.getString("nickname"));
        try { b.setStatus(rs.getString("status")); } catch (Exception ignored) {}
        try { b.setCreatedAt(rs.getTimestamp("created_at")); } catch (Exception ignored) {}
        return b;
    }
}
