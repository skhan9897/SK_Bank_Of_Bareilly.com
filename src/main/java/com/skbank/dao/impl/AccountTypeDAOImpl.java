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
        String sql = "SELECT * FROM account_types WHERE account_type_id = ?";
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
        }
        return list;
    }

    private AccountType mapAccountType(ResultSet rs) throws SQLException {
        AccountType at = new AccountType();
        at.setAccountTypeId(rs.getLong("account_type_id"));
        at.setTypeCode(rs.getString("type_code"));
        at.setTypeName(rs.getString("type_name"));
        at.setDescription(rs.getString("description"));
        at.setMinimumBalance(rs.getBigDecimal("minimum_balance"));
        at.setInterestRate(rs.getBigDecimal("interest_rate"));
        at.setStatus(rs.getString("status"));
        at.setCreatedAt(rs.getTimestamp("created_at"));
        return at;
    }
}
