package com.skbank.dao.impl;

import com.skbank.dao.BranchDAO;
import com.skbank.model.Branch;
import com.skbank.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BranchDAOImpl implements BranchDAO {

    @Override
    public Branch findById(Long branchId) throws SQLException {
        String sql = "SELECT * FROM branches WHERE branch_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, branchId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapBranch(rs);
            }
        }
        return null;
    }

    @Override
    public Branch findByBranchCode(String branchCode) throws SQLException {
        String sql = "SELECT * FROM branches WHERE branch_code = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, branchCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapBranch(rs);
            }
        }
        return null;
    }

    @Override
    public List<Branch> findAllActive() throws SQLException {
        List<Branch> list = new ArrayList<>();
        String sql = "SELECT * FROM branches WHERE status = 'ACTIVE' ORDER BY branch_name ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapBranch(rs));
            }
        }
        return list;
    }

    private Branch mapBranch(ResultSet rs) throws SQLException {
        Branch b = new Branch();
        b.setBranchId(rs.getLong("branch_id"));
        b.setBranchCode(rs.getString("branch_code"));
        b.setBranchName(rs.getString("branch_name"));
        b.setAddress(rs.getString("address"));
        b.setCity(rs.getString("city"));
        b.setState(rs.getString("state"));
        b.setPincode(rs.getString("pincode"));
        b.setIfscCode(rs.getString("ifsc_code"));
        b.setPhone(rs.getString("phone"));
        b.setStatus(rs.getString("status"));
        b.setCreatedAt(rs.getTimestamp("created_at"));
        return b;
    }
}
