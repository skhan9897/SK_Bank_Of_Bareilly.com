package com.skbank.dao;

import com.skbank.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountTypeDAO {

    public double getMinBalance(long typeId) throws SQLException {
        String sql = "SELECT minimum_balance FROM account_types WHERE type_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, typeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        }
        return 1000.00;
    }
}
