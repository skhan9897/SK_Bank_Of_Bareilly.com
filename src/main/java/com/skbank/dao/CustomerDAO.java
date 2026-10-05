package com.skbank.dao;

import com.skbank.model.Customer;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface CustomerDAO {
    Customer findById(String customerId) throws SQLException;
    Customer findByUserId(Long userId) throws SQLException;
    Customer findByCustomerNumber(String customerNumber) throws SQLException;
    Customer findByMobile(String mobile) throws SQLException;
    Customer findByEmail(String email) throws SQLException;
    String create(Customer customer) throws SQLException;
    String create(Connection conn, Customer customer) throws SQLException;
    boolean updateProfile(Customer customer) throws SQLException;
    boolean updateProfileImage(String customerId, String imagePath) throws SQLException;
    boolean updateKycStatus(String customerId, String kycStatus) throws SQLException;
    boolean updateStatus(String customerId, String status) throws SQLException;
    List<Customer> findAll(int offset, int limit, String searchQuery) throws SQLException;
    long countAll(String searchQuery) throws SQLException;
}
