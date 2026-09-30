package com.skbank.dao;

import com.skbank.config.DatabaseConfig;
import com.skbank.model.Branch;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BranchDAO {

    public List<Branch> findAllActive() throws SQLException {
        List<Branch> list = new ArrayList<>();
        String sql = "SELECT * FROM branches WHERE status = 'ACTIVE' ORDER BY branch_id ASC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapBranch(rs));
            }
        }
        return list;
    }

    public Branch findById(int branchId) throws SQLException {
        String sql = "SELECT * FROM branches WHERE branch_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapBranch(rs);
                }
            }
        }
        return null;
    }

    private Branch mapBranch(ResultSet rs) throws SQLException {
        Branch b = new Branch();
        b.setBranchId(rs.getInt("branch_id"));
        b.setBranchCode(rs.getString("branch_code"));
        b.setBranchName(rs.getString("branch_name"));
        b.setAddress(rs.getString("address"));
        b.setCity(rs.getString("city"));
        b.setState(rs.getString("state"));
        b.setPincode(rs.getString("pincode"));
        b.setIfscCode(rs.getString("ifsc_code"));
        b.setPhone(rs.getString("phone"));
        b.setEmail(rs.getString("email"));
        b.setStatus(rs.getString("status"));
        return b;
    }
}
