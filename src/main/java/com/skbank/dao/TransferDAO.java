package com.skbank.dao;

import com.skbank.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TransferDAO {

    public boolean createTransferRequest(String customerId, long senderAccountId, String receiverAccNo, String ifsc, String receiverName, String type, double amount, String remarks) throws SQLException {
        String sql = "INSERT INTO transfer_requests (customer_id, sender_account_id, receiver_account_number, receiver_ifsc, receiver_name, transfer_type, amount, remarks) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customerId);
            ps.setLong(2, senderAccountId);
            ps.setString(3, receiverAccNo);
            ps.setString(4, ifsc);
            ps.setString(5, receiverName);
            ps.setString(6, type);
            ps.setDouble(7, amount);
            ps.setString(8, remarks);
            return ps.executeUpdate() > 0;
        }
    }
}
