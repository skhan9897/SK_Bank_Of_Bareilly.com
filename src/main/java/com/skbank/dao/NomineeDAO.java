package com.skbank.dao;

import com.skbank.util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class NomineeDAO {

    public boolean addNominee(String customerId, long accountId, String name, String relationship, Date dob, String mobile) throws SQLException {
        String sql = "INSERT INTO nominees (customer_id, account_id, nominee_name, relationship, dob, mobile) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customerId);
            ps.setLong(2, accountId);
            ps.setString(3, name);
            ps.setString(4, relationship);
            ps.setDate(5, dob);
            ps.setString(6, mobile);
            return ps.executeUpdate() > 0;
        }
    }
}
