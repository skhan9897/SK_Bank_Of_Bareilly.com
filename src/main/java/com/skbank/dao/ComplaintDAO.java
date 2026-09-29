package com.skbank.dao;

import com.skbank.config.DatabaseConfig;
import com.skbank.model.Complaint;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ComplaintDAO {

    public List<Complaint> findByCustomerId(String customerId) throws SQLException {
        List<Complaint> list = new ArrayList<>();
        String sql = "SELECT * FROM complaints WHERE customer_id = ? ORDER BY created_at DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapComplaint(rs));
                }
            }
        }
        return list;
    }

    public boolean createComplaint(Complaint c) throws SQLException {
        String sql = "INSERT INTO complaints (complaint_id, customer_id, subject, category, description, attachment_path, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getComplaintId());
            ps.setString(2, c.getCustomerId());
            ps.setString(3, c.getSubject());
            ps.setString(4, c.getCategory());
            ps.setString(5, c.getDescription());
            ps.setString(6, c.getAttachmentPath());
            ps.setString(7, c.getStatus() != null ? c.getStatus() : "OPEN");
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateComplaintStatus(String complaintId, String status) throws SQLException {
        String sql = "UPDATE complaints SET status = ? WHERE complaint_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, complaintId);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Complaint> findAll() throws SQLException {
        List<Complaint> list = new ArrayList<>();
        String sql = "SELECT c.*, CONCAT(cust.first_name, ' ', cust.last_name) AS customer_name " +
                     "FROM complaints c JOIN customers cust ON c.customer_id = cust.customer_id ORDER BY c.created_at DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapComplaint(rs));
            }
        }
        return list;
    }

    private Complaint mapComplaint(ResultSet rs) throws SQLException {
        Complaint c = new Complaint();
        c.setComplaintId(rs.getString("complaint_id"));
        c.setCustomerId(rs.getString("customer_id"));
        c.setSubject(rs.getString("subject"));
        c.setCategory(rs.getString("category"));
        c.setDescription(rs.getString("description"));
        c.setAttachmentPath(rs.getString("attachment_path"));
        c.setStatus(rs.getString("status"));
        c.setCreatedAt(rs.getTimestamp("created_at"));

        try { c.setCustomerName(rs.getString("customer_name")); } catch (Exception ignored) {}

        return c;
    }
}
