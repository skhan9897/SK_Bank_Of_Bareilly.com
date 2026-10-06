package com.skbank.dao.impl;

import com.skbank.dao.CustomerDAO;
import com.skbank.model.Customer;
import com.skbank.model.KycStatus;
import com.skbank.model.UserStatus;
import com.skbank.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAOImpl implements CustomerDAO {

    @Override
    public Customer findById(Long customerId) throws SQLException {
        String sql = "SELECT * FROM customers WHERE customer_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapCustomer(rs);
            }
        }
        return null;
    }

    @Override
    public Customer findByUserId(Long userId) throws SQLException {
        String sql = "SELECT * FROM customers WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapCustomer(rs);
            }
        }
        return null;
    }

    @Override
    public Customer findByCustomerNumber(String customerNumber) throws SQLException {
        String sql = "SELECT * FROM customers WHERE customer_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customerNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapCustomer(rs);
            }
        }
        return null;
    }

    @Override
    public Customer findByMobile(String mobile) throws SQLException {
        String sql = "SELECT * FROM customers WHERE mobile = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, mobile);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapCustomer(rs);
            }
        }
        return null;
    }

    @Override
    public Customer findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM customers WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapCustomer(rs);
            }
        }
        return null;
    }

    @Override
    public Long create(Customer customer) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return create(conn, customer);
        }
    }

    @Override
    public Long create(Connection conn, Customer customer) throws SQLException {
        // DO NOT INCLUDE customer_id IN INSERT STATEMENT (MySQL AUTO_INCREMENT customer_id)
        String sql = "INSERT INTO customers (user_id, customer_number, full_name, date_of_birth, gender, mobile, email, address, city, state, pincode, aadhaar_number, pan_number, profile_image, kyc_status, status, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, customer.getUserId());
            ps.setString(2, customer.getCustomerNumber());
            ps.setString(3, customer.getFullName());
            ps.setDate(4, customer.getDateOfBirth());
            ps.setString(5, customer.getGender());
            ps.setString(6, customer.getMobile());
            ps.setString(7, customer.getEmail());
            ps.setString(8, customer.getAddress());
            ps.setString(9, customer.getCity());
            ps.setString(10, customer.getState());
            ps.setString(11, customer.getPincode());
            ps.setString(12, customer.getAadhaarNumber());
            ps.setString(13, customer.getPanNumber());
            ps.setString(14, customer.getProfileImage());
            ps.setString(15, customer.getKycStatus() != null ? customer.getKycStatus().name() : KycStatus.VERIFIED.name());
            ps.setString(16, customer.getStatus() != null ? customer.getStatus().name() : UserStatus.ACTIVE.name());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    Long generatedId = rs.getLong(1);
                    customer.setCustomerId(generatedId);
                    return generatedId;
                }
            }
        }
        throw new SQLException("Failed to retrieve generated AUTO_INCREMENT customer_id for new customer.");
    }

    @Override
    public boolean updateProfile(Customer c) throws SQLException {
        String sql = "UPDATE customers SET full_name = ?, mobile = ?, email = ?, address = ?, city = ?, state = ?, pincode = ?, updated_at = NOW() WHERE customer_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getFullName());
            ps.setString(2, c.getMobile());
            ps.setString(3, c.getEmail());
            ps.setString(4, c.getAddress());
            ps.setString(5, c.getCity());
            ps.setString(6, c.getState());
            ps.setString(7, c.getPincode());
            ps.setLong(8, c.getCustomerId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateProfileImage(Long customerId, String imagePath) throws SQLException {
        String sql = "UPDATE customers SET profile_image = ?, updated_at = NOW() WHERE customer_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, imagePath);
            ps.setLong(2, customerId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateKycStatus(Long customerId, String kycStatus) throws SQLException {
        String sql = "UPDATE customers SET kyc_status = ?, updated_at = NOW() WHERE customer_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, kycStatus);
            ps.setLong(2, customerId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateStatus(Long customerId, String status) throws SQLException {
        String sql = "UPDATE customers SET status = ?, updated_at = NOW() WHERE customer_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setLong(2, customerId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public List<Customer> findAll(int offset, int limit, String searchQuery) throws SQLException {
        List<Customer> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM customers ");
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            sql.append("WHERE full_name LIKE ? OR mobile LIKE ? OR email LIKE ? OR customer_number LIKE ? ");
        }
        sql.append("ORDER BY customer_id DESC LIMIT ? OFFSET ?");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                String q = "%" + searchQuery.trim() + "%";
                ps.setString(idx++, q);
                ps.setString(idx++, q);
                ps.setString(idx++, q);
                ps.setString(idx++, q);
            }
            ps.setInt(idx++, limit);
            ps.setInt(idx, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapCustomer(rs));
            }
        }
        return list;
    }

    @Override
    public long countAll(String searchQuery) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM customers ");
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            sql.append("WHERE full_name LIKE ? OR mobile LIKE ? OR email LIKE ? OR customer_number LIKE ? ");
        }
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                String q = "%" + searchQuery.trim() + "%";
                ps.setString(1, q);
                ps.setString(2, q);
                ps.setString(3, q);
                ps.setString(4, q);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
        }
        return 0;
    }

    private Customer mapCustomer(ResultSet rs) throws SQLException {
        Customer c = new Customer();
        c.setCustomerId(rs.getLong("customer_id"));
        c.setUserId(rs.getLong("user_id"));
        c.setCustomerNumber(rs.getString("customer_number"));
        c.setFullName(rs.getString("full_name"));
        c.setDateOfBirth(rs.getDate("date_of_birth"));
        c.setGender(rs.getString("gender"));
        c.setMobile(rs.getString("mobile"));
        c.setEmail(rs.getString("email"));
        c.setAddress(rs.getString("address"));
        c.setCity(rs.getString("city"));
        c.setState(rs.getString("state"));
        c.setPincode(rs.getString("pincode"));
        c.setAadhaarNumber(rs.getString("aadhaar_number"));
        c.setPanNumber(rs.getString("pan_number"));
        c.setProfileImage(rs.getString("profile_image"));
        try { c.setKycStatus(KycStatus.valueOf(rs.getString("kyc_status"))); } catch (Exception ignored) {}
        try { c.setStatus(UserStatus.valueOf(rs.getString("status"))); } catch (Exception ignored) {}
        c.setCreatedAt(rs.getTimestamp("created_at"));
        c.setUpdatedAt(rs.getTimestamp("updated_at"));
        return c;
    }
}
