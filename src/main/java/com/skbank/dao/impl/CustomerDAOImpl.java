package com.skbank.dao.impl;

import com.skbank.dao.CustomerDAO;
import com.skbank.model.Customer;
import com.skbank.model.KycStatus;
import com.skbank.model.UserStatus;
import com.skbank.util.DatabaseConnection;
import com.skbank.util.DatabaseInitializer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAOImpl implements CustomerDAO {

    private void ensureTableExists() {
        try {
            DatabaseInitializer.initializeDatabaseIfMissing();
        } catch (Exception ignored) {}
    }

    @Override
    public Customer findById(Long customerId) throws SQLException {
        ensureTableExists();
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
        ensureTableExists();
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
        ensureTableExists();
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
        ensureTableExists();
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
        ensureTableExists();
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
    public Customer findByAadhaar(String aadhaarNumber) throws SQLException {
        ensureTableExists();
        String sql = "SELECT * FROM customers WHERE aadhaar_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, aadhaarNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapCustomer(rs);
            }
        }
        return null;
    }

    @Override
    public Customer findByPan(String panNumber) throws SQLException {
        ensureTableExists();
        String sql = "SELECT * FROM customers WHERE pan_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, panNumber);
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
        ensureTableExists();

        // 1. Try Standard Auto-Increment Insert
        String sqlAuto = "INSERT INTO customers (user_id, customer_number, full_name, date_of_birth, gender, mobile, email, address, city, state, pincode, aadhaar_number, pan_number, profile_image, kyc_status, status, created_at, updated_at) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
        try (PreparedStatement ps = conn.prepareStatement(sqlAuto, Statement.RETURN_GENERATED_KEYS)) {
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
            ps.setString(15, customer.getKycStatus() != null ? customer.getKycStatus().name() : "VERIFIED");
            ps.setString(16, customer.getStatus() != null ? customer.getStatus().name() : UserStatus.ACTIVE.name());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next() && rs.getLong(1) > 0) return rs.getLong(1);
            }
        } catch (SQLException e) {
            // If AUTO_INCREMENT is missing on database column, fallback to explicit customer_id generation
            if (e.getMessage() != null && e.getMessage().contains("customer_id")) {
                return createWithExplicitId(conn, customer);
            }
            throw e;
        }

        // Fallback retrieval by user_id
        String queryKeySql = "SELECT customer_id FROM customers WHERE user_id = ?";
        try (PreparedStatement ps2 = conn.prepareStatement(queryKeySql)) {
            ps2.setLong(1, customer.getUserId());
            try (ResultSet rs2 = ps2.executeQuery()) {
                if (rs2.next()) return rs2.getLong(1);
            }
        }

        return null;
    }

    private Long createWithExplicitId(Connection conn, Customer customer) throws SQLException {
        long nextId = 1;
        String maxSql = "SELECT COALESCE(MAX(customer_id), 0) + 1 FROM customers";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(maxSql)) {
            if (rs.next()) {
                nextId = rs.getLong(1);
            }
        }

        String sqlExplicit = "INSERT INTO customers (customer_id, user_id, customer_number, full_name, date_of_birth, gender, mobile, email, address, city, state, pincode, aadhaar_number, pan_number, profile_image, kyc_status, status, created_at, updated_at) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
        try (PreparedStatement ps = conn.prepareStatement(sqlExplicit)) {
            ps.setLong(1, nextId);
            ps.setLong(2, customer.getUserId());
            ps.setString(3, customer.getCustomerNumber());
            ps.setString(4, customer.getFullName());
            ps.setDate(5, customer.getDateOfBirth());
            ps.setString(6, customer.getGender());
            ps.setString(7, customer.getMobile());
            ps.setString(8, customer.getEmail());
            ps.setString(9, customer.getAddress());
            ps.setString(10, customer.getCity());
            ps.setString(11, customer.getState());
            ps.setString(12, customer.getPincode());
            ps.setString(13, customer.getAadhaarNumber());
            ps.setString(14, customer.getPanNumber());
            ps.setString(15, customer.getProfileImage());
            ps.setString(16, customer.getKycStatus() != null ? customer.getKycStatus().name() : "VERIFIED");
            ps.setString(17, customer.getStatus() != null ? customer.getStatus().name() : UserStatus.ACTIVE.name());
            ps.executeUpdate();
            return nextId;
        }
    }

    @Override
    public boolean update(Customer customer) throws SQLException {
        ensureTableExists();
        String sql = "UPDATE customers SET full_name = ?, mobile = ?, email = ?, address = ?, city = ?, state = ?, pincode = ?, updated_at = NOW() WHERE customer_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customer.getFullName());
            ps.setString(2, customer.getMobile());
            ps.setString(3, customer.getEmail());
            ps.setString(4, customer.getAddress());
            ps.setString(5, customer.getCity());
            ps.setString(6, customer.getState());
            ps.setString(7, customer.getPincode());
            ps.setLong(8, customer.getCustomerId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateProfileImage(Long customerId, String imagePath) throws SQLException {
        ensureTableExists();
        String sql = "UPDATE customers SET profile_image = ?, updated_at = NOW() WHERE customer_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, imagePath);
            ps.setLong(2, customerId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateKycStatus(Long customerId, String status) throws SQLException {
        ensureTableExists();
        String sql = "UPDATE customers SET kyc_status = ?, updated_at = NOW() WHERE customer_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setLong(2, customerId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateStatus(Long customerId, String status) throws SQLException {
        ensureTableExists();
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
        ensureTableExists();
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM customers WHERE (full_name LIKE ? OR mobile LIKE ? OR email LIKE ? OR customer_number LIKE ?) ORDER BY created_at DESC LIMIT ? OFFSET ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String q = searchQuery != null ? "%" + searchQuery.trim() + "%" : "%%";
            ps.setString(1, q);
            ps.setString(2, q);
            ps.setString(3, q);
            ps.setString(4, q);
            ps.setInt(5, limit);
            ps.setInt(6, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapCustomer(rs));
            }
        }
        return list;
    }

    @Override
    public long countAll(String searchQuery) throws SQLException {
        ensureTableExists();
        String sql = "SELECT COUNT(*) FROM customers WHERE (full_name LIKE ? OR mobile LIKE ? OR email LIKE ? OR customer_number LIKE ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String q = searchQuery != null ? "%" + searchQuery.trim() + "%" : "%%";
            ps.setString(1, q);
            ps.setString(2, q);
            ps.setString(3, q);
            ps.setString(4, q);
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
        String kyc = rs.getString("kyc_status");
        if (kyc != null) c.setKycStatus(KycStatus.valueOf(kyc));
        String status = rs.getString("status");
        if (status != null) c.setStatus(UserStatus.valueOf(status));
        c.setCreatedAt(rs.getTimestamp("created_at"));
        c.setUpdatedAt(rs.getTimestamp("updated_at"));
        return c;
    }
}
