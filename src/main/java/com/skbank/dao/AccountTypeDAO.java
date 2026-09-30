package com.skbank.dao;

import com.skbank.config.DatabaseConfig;
import com.skbank.model.AccountType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountTypeDAO {

    public List<AccountType> findAllActive() throws SQLException {
        List<AccountType> list = new ArrayList<>();
        String sql = "SELECT * FROM account_types WHERE status = 'ACTIVE' ORDER BY type_id ASC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapAccountType(rs));
            }
        }
        return list;
    }

    public AccountType findById(int typeId) throws SQLException {
        String sql = "SELECT * FROM account_types WHERE type_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, typeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapAccountType(rs);
                }
            }
        }
        return null;
    }

    private AccountType mapAccountType(ResultSet rs) throws SQLException {
        AccountType at = new AccountType();
        at.setTypeId(rs.getInt("type_id"));
        try { at.setTypeCode(rs.getString("type_code")); } catch (Exception ignored) {}
        at.setTypeName(rs.getString("type_name"));
        at.setDescription(rs.getString("description"));
        at.setMinBalance(rs.getDouble("minimum_balance"));
        at.setInterestRate(rs.getDouble("interest_rate"));
        at.setStatus(rs.getString("status"));
        return at;
    }
}
