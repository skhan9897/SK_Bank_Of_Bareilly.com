package com.skbank.dao;

import com.skbank.config.DatabaseConfig;
import com.skbank.model.Customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    public Customer findByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM customers WHERE user_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapCustomer(rs);
                }
            }
        }
        return null;
    }

    public Customer findByCustomerId(String customerId) throws SQLException {
        String sql = "SELECT * FROM customers WHERE customer_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapCustomer(rs);
                }
            }
        }
        return null;
    }

    public boolean existsByEmail(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM customers WHERE email = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public boolean existsByMobile(String mobile) throws SQLException {
        String sql = "SELECT COUNT(*) FROM customers WHERE mobile = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, mobile);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public boolean createCustomer(Customer customer) throws SQLException {
        String sql = "INSERT INTO customers (customer_id, user_id, first_name, last_name, dob, gender, mobile, email, aadhaar, pan, address, city, state, pincode, occupation, kyc_status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customer.getCustomerId());
            ps.setInt(2, customer.getUserId());
            ps.setString(3, customer.getFirstName());
            ps.setString(4, customer.getLastName());
            ps.setDate(5, customer.getDob());
            ps.setString(6, customer.getGender());
            ps.setString(7, customer.getMobile());
            ps.setString(8, customer.getEmail());
            ps.setString(9, customer.getAadhaar());
            ps.setString(10, customer.getPan());
            ps.setString(11, customer.getAddress());
            ps.setString(12, customer.getCity());
            ps.setString(13, customer.getState());
            ps.setString(14, customer.getPincode());
            ps.setString(15, customer.getOccupation());
            ps.setString(16, customer.getKycStatus() != null ? customer.getKycStatus() : "PENDING");
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateProfile(Customer customer) throws SQLException {
        String sql = "UPDATE customers SET first_name = ?, last_name = ?, mobile = ?, email = ?, address = ?, city = ?, state = ?, pincode = ?, occupation = ? WHERE customer_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customer.getFirstName());
            ps.setString(2, customer.getLastName());
            ps.setString(3, customer.getMobile());
            ps.setString(4, customer.getEmail());
            ps.setString(5, customer.getAddress());
            ps.setString(6, customer.getCity());
            ps.setString(7, customer.getState());
            ps.setString(8, customer.getPincode());
            ps.setString(9, customer.getOccupation());
            ps.setString(10, customer.getCustomerId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateKycStatus(String customerId, String status) throws SQLException {
        String sql = "UPDATE customers SET kyc_status = ? WHERE customer_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, customerId);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Customer> findAll() throws SQLException {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM customers ORDER BY created_at DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapCustomer(rs));
            }
        }
        return list;
    }

    public List<Customer> searchCustomers(String query) throws SQLException {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM customers WHERE customer_id LIKE ? OR first_name LIKE ? OR last_name LIKE ? OR mobile LIKE ? OR email LIKE ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String q = "%" + query + "%";
            ps.setString(1, q);
            ps.setString(2, q);
            ps.setString(3, q);
            ps.setString(4, q);
            ps.setString(5, q);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapCustomer(rs));
                }
            }
        }
        return list;
    }

    public int countTotalCustomers() throws SQLException {
        String sql = "SELECT COUNT(*) FROM customers";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    private Customer mapCustomer(ResultSet rs) throws SQLException {
        Customer c = new Customer();
        c.setCustomerId(rs.getString("customer_id"));
        c.setUserId(rs.getInt("user_id"));
        c.setFirstName(rs.getString("first_name"));
        c.setLastName(rs.getString("last_name"));
        c.setDob(rs.getDate("dob"));
        c.setGender(rs.getString("gender"));
        c.setMobile(rs.getString("mobile"));
        c.setEmail(rs.getString("email"));
        c.setAadhaar(rs.getString("aadhaar"));
        c.setPan(rs.getString("pan"));
        c.setAddress(rs.getString("address"));
        c.setCity(rs.getString("city"));
        c.setState(rs.getString("state"));
        c.setPincode(rs.getString("pincode"));
        c.setOccupation(rs.getString("occupation"));
        c.setKycStatus(rs.getString("kyc_status"));
        c.setProfilePhoto(rs.getString("profile_photo"));
        c.setCreatedAt(rs.getTimestamp("created_at"));
        return c;
    }
}
