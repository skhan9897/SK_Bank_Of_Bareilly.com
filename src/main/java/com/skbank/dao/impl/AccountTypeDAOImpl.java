package com.skbank.dao.impl;

import com.skbank.dao.AccountTypeDAO;
import com.skbank.model.AccountType;
import com.skbank.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountTypeDAOImpl implements AccountTypeDAO {

    @Override
    public AccountType findById(Long accountTypeId) throws SQLException {
        String sql = "SELECT * FROM account_types WHERE type_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, accountTypeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapAccountType(rs);
            }
        }
        return null;
    }

    @Override
    public AccountType findByCode(String typeCode) throws SQLException {
        String sql = "SELECT * FROM account_types WHERE type_code = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, typeCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapAccountType(rs);
            }
        }
        return null;
    }

    @Override
    public List<AccountType> findAllActive() throws SQLException {
        List<AccountType> list = new ArrayList<>();
        String sql = "SELECT * FROM account_types WHERE status = 'ACTIVE' ORDER BY type_name ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapAccountType(rs));
            }
        } catch (Exception e) {
            String fallbackSql = "SELECT * FROM account_types ORDER BY type_id ASC";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(fallbackSql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapAccountType(rs));
                }
            }
        }
        return list;
    }

    private AccountType mapAccountType(ResultSet rs) throws SQLException {
        AccountType at = new AccountType();
        at.setAccountTypeId(rs.getLong("type_id"));
        try { at.setTypeCode(rs.getString("type_code")); } catch (SQLException ignored) {}
        try { at.setTypeName(rs.getString("type_name")); } catch (SQLException ignored) {}
        try { at.setDescription(rs.getString("description")); } catch (SQLException ignored) {}
        try { at.setMinimumBalance(rs.getBigDecimal("minimum_balance")); } catch (SQLException ignored) {}
        try { at.setInterestRate(rs.getBigDecimal("interest_rate")); } catch (SQLException ignored) {}
        try { at.setStatus(rs.getString("status")); } catch (SQLException ignored) {}
        try { at.setCreatedAt(rs.getTimestamp("created_at")); } catch (SQLException ignored) {}
        return at;
    }
}
