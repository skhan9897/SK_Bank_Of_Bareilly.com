package com.skbank.dao;

import com.skbank.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EmployeeDAO {

    public boolean createEmployee(long userId, long branchId, String employeeCode, String fullName, String designation, String email, String phone) throws SQLException {
        String sql = "INSERT INTO employees (user_id, branch_id, employee_code, full_name, designation, email, phone, hire_date) VALUES (?, ?, ?, ?, ?, ?, ?, CURDATE())";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, branchId);
            ps.setString(3, employeeCode);
            ps.setString(4, fullName);
            ps.setString(5, designation);
            ps.setString(6, email);
            ps.setString(7, phone);
            return ps.executeUpdate() > 0;
        }
    }
}
