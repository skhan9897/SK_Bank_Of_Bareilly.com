package com.skbank.dao.impl;

import com.skbank.dao.ComplaintDAO;
import com.skbank.model.Complaint;
import com.skbank.model.ComplaintMessage;
import com.skbank.model.ComplaintPriority;
import com.skbank.model.ComplaintStatus;
import com.skbank.model.UserRole;
import com.skbank.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ComplaintDAOImpl implements ComplaintDAO {

    private static final String SELECT_JOIN_SQL = 
        "SELECT comp.*, c.full_name AS customer_name " +
        "FROM complaints comp " +
        "JOIN customers c ON comp.customer_id = c.customer_id ";

    @Override
    public Complaint findById(Long complaintId) throws SQLException {
        String sql = SELECT_JOIN_SQL + "WHERE comp.complaint_id = ?";
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
    public List<Complaint> findByCustomerId(String customerId) throws SQLException {
        List<Complaint> list = new ArrayList<>();
        String sql = SELECT_JOIN_SQL + "WHERE comp.customer_id = ? ORDER BY comp.updated_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapComplaint(rs));
            }
        }
        return list;
    }

    @Override
    public Long create(Complaint comp) throws SQLException {
        String sql = "INSERT INTO complaints (customer_id, subject, description, priority, status, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, NOW(), NOW())";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, comp.getCustomerId());
            ps.setString(2, comp.getSubject());
            ps.setString(3, comp.getDescription());
            ps.setString(4, comp.getPriority() != null ? comp.getPriority().name() : ComplaintPriority.MEDIUM.name());
            ps.setString(5, comp.getStatus() != null ? comp.getStatus().name() : ComplaintStatus.OPEN.name());
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
    public Long addMessage(ComplaintMessage msg) throws SQLException {
        String sql = "INSERT INTO complaint_messages (complaint_id, sender_user_id, message, created_at) VALUES (?, ?, ?, NOW())";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, msg.getComplaintId());
            ps.setLong(2, msg.getSenderUserId());
            ps.setString(3, msg.getMessage());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    Long msgId = rs.getLong(1);
                    updateStatus(msg.getComplaintId(), "IN_PROGRESS");
                    return msgId;
                }
            }
        }
        return null;
    }

    @Override
    public List<ComplaintMessage> findMessagesByComplaintId(Long complaintId) throws SQLException {
        List<ComplaintMessage> list = new ArrayList<>();
        String sql = "SELECT cm.*, u.username, u.role FROM complaint_messages cm JOIN users u ON cm.sender_user_id = u.user_id WHERE cm.complaint_id = ? ORDER BY cm.created_at ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, complaintId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ComplaintMessage cm = new ComplaintMessage();
                    cm.setMessageId(rs.getLong("message_id"));
                    cm.setComplaintId(rs.getLong("complaint_id"));
                    cm.setSenderUserId(rs.getLong("sender_user_id"));
                    cm.setMessage(rs.getString("message"));
                    cm.setCreatedAt(rs.getTimestamp("created_at"));
                    cm.setSenderName(rs.getString("username"));
                    cm.setSenderRole(UserRole.valueOf(rs.getString("role")));
                    list.add(cm);
                }
            }
        }
        return list;
    }

    @Override
    public long countPendingComplaints() throws SQLException {
        String sql = "SELECT COUNT(*) FROM complaints WHERE status IN ('OPEN', 'IN_PROGRESS')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getLong(1);
        }
        return 0;
    }

    @Override
    public List<Complaint> findAllAdmin(int offset, int limit, String statusFilter) throws SQLException {
        List<Complaint> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(SELECT_JOIN_SQL);
        if (statusFilter != null && !statusFilter.trim().isEmpty()) {
            sql.append("WHERE comp.status = ? ");
        }
        sql.append("ORDER BY comp.updated_at DESC LIMIT ? OFFSET ?");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int p = 1;
            if (statusFilter != null && !statusFilter.trim().isEmpty()) {
                ps.setString(p++, statusFilter.trim());
            }
            ps.setInt(p++, limit);
            ps.setInt(p, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapComplaint(rs));
            }
        }
        return list;
    }

    @Override
    public long countAllAdmin(String statusFilter) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM complaints comp ");
        if (statusFilter != null && !statusFilter.trim().isEmpty()) {
            sql.append("WHERE comp.status = ? ");
        }
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            if (statusFilter != null && !statusFilter.trim().isEmpty()) {
                ps.setString(1, statusFilter.trim());
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
        }
        return 0;
    }

    private Complaint mapComplaint(ResultSet rs) throws SQLException {
        Complaint c = new Complaint();
        c.setComplaintId(rs.getLong("complaint_id"));
        c.setCustomerId(rs.getString("customer_id"));
        c.setSubject(rs.getString("subject"));
        c.setDescription(rs.getString("description"));
        c.setPriority(ComplaintPriority.valueOf(rs.getString("priority")));
        c.setStatus(ComplaintStatus.valueOf(rs.getString("status")));
        c.setCreatedAt(rs.getTimestamp("created_at"));
        c.setUpdatedAt(rs.getTimestamp("updated_at"));
        try { c.setCustomerName(rs.getString("customer_name")); } catch (SQLException ignored) {}
        return c;
    }
}
