package com.skbank.dao.impl;

import com.skbank.dao.ComplaintDAO;
import com.skbank.model.*;
import com.skbank.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ComplaintDAOImpl implements ComplaintDAO {

    @Override
    public List<Complaint> findByCustomerId(Long customerId) throws SQLException {
        List<Complaint> list = new ArrayList<>();
        String sql = "SELECT * FROM complaints WHERE customer_id = ? ORDER BY complaint_id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapComplaint(rs));
            }
        }
        return list;
    }

    @Override
    public Complaint findById(Long complaintId) throws SQLException {
        String sql = "SELECT * FROM complaints WHERE complaint_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, complaintId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapComplaint(rs);
            }
        }
        return null;
    }

    @Override
    public Long create(Complaint c) throws SQLException {
        String sql = "INSERT INTO complaints (customer_id, subject, description, priority, status, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, NOW(), NOW())";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, c.getCustomerId());
            ps.setString(2, c.getSubject());
            ps.setString(3, c.getDescription());
            ps.setString(4, c.getPriority() != null ? c.getPriority().name() : ComplaintPriority.MEDIUM.name());
            ps.setString(5, c.getStatus() != null ? c.getStatus().name() : ComplaintStatus.OPEN.name());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        }
        return null;
    }

    @Override
    public List<ComplaintMessage> findMessagesByComplaintId(Long complaintId) throws SQLException {
        List<ComplaintMessage> list = new ArrayList<>();
        String sql = "SELECT cm.*, u.username FROM complaint_messages cm LEFT JOIN users u ON cm.sender_user_id = u.id WHERE cm.complaint_id = ? ORDER BY cm.message_id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, complaintId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ComplaintMessage msg = new ComplaintMessage();
                    msg.setMessageId(rs.getLong("message_id"));
                    msg.setComplaintId(rs.getLong("complaint_id"));
                    msg.setSenderUserId(rs.getLong("sender_user_id"));
                    msg.setMessage(rs.getString("message"));
                    msg.setSentAt(rs.getTimestamp("sent_at"));
                    try { msg.setSenderUsername(rs.getString("username")); } catch (Exception ignored) {}
                    list.add(msg);
                }
            }
        }
        return list;
    }

    @Override
    public Long addMessage(ComplaintMessage msg) throws SQLException {
        String sql = "INSERT INTO complaint_messages (complaint_id, sender_user_id, message, sent_at) VALUES (?, ?, ?, NOW())";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, msg.getComplaintId());
            ps.setLong(2, msg.getSenderUserId());
            ps.setString(3, msg.getMessage());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        }
        return null;
    }

    @Override
    public boolean updateStatus(Long complaintId, String status) throws SQLException {
        String sql = "UPDATE complaints SET status = ?, updated_at = NOW() WHERE complaint_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setLong(2, complaintId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public List<Complaint> findAllAdmin(int offset, int limit, String statusFilter) throws SQLException {
        List<Complaint> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM complaints ");
        if (statusFilter != null && !statusFilter.trim().isEmpty() && !"ALL".equalsIgnoreCase(statusFilter.trim())) {
            sql.append("WHERE status = ? ");
        }
        sql.append("ORDER BY complaint_id DESC LIMIT ? OFFSET ?");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            if (statusFilter != null && !statusFilter.trim().isEmpty() && !"ALL".equalsIgnoreCase(statusFilter.trim())) {
                ps.setString(idx++, statusFilter.trim());
            }
            ps.setInt(idx++, limit);
            ps.setInt(idx, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapComplaint(rs));
            }
        }
        return list;
    }

    @Override
    public long countAllAdmin(String statusFilter) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM complaints ");
        if (statusFilter != null && !statusFilter.trim().isEmpty() && !"ALL".equalsIgnoreCase(statusFilter.trim())) {
            sql.append("WHERE status = ? ");
        }
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            if (statusFilter != null && !statusFilter.trim().isEmpty() && !"ALL".equalsIgnoreCase(statusFilter.trim())) {
                ps.setString(1, statusFilter.trim());
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
        }
        return 0;
    }

    @Override
    public long countPendingComplaints() throws SQLException {
        String sql = "SELECT COUNT(*) FROM complaints WHERE status = 'OPEN' OR status = 'IN_PROGRESS'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getLong(1);
        }
        return 0;
    }

    private Complaint mapComplaint(ResultSet rs) throws SQLException {
        Complaint c = new Complaint();
        c.setComplaintId(rs.getLong("complaint_id"));
        c.setCustomerId(rs.getLong("customer_id"));
        c.setSubject(rs.getString("subject"));
        c.setDescription(rs.getString("description"));
        try { c.setPriority(ComplaintPriority.valueOf(rs.getString("priority"))); } catch (Exception ignored) {}
        try { c.setStatus(ComplaintStatus.valueOf(rs.getString("status"))); } catch (Exception ignored) {}
        c.setCreatedAt(rs.getTimestamp("created_at"));
        c.setUpdatedAt(rs.getTimestamp("updated_at"));
        return c;
    }
}
