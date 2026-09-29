package com.skbank.service;

import com.skbank.dao.CustomerDAO;
import com.skbank.dao.UserDAO;
import com.skbank.model.Customer;

import com.skbank.model.User;
import com.skbank.util.AccountNumberGenerator;
import com.skbank.util.PasswordUtil;

import java.sql.SQLException;

public class AuthenticationService {

    private final UserDAO userDAO = new UserDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final AccountService accountService = new AccountService();

    public User authenticate(String usernameOrCustomerId, String plainPassword) throws SQLException {
        User user = userDAO.findByUsername(usernameOrCustomerId);
        if (user == null) {
            // Check if input was a Customer ID
            Customer customer = customerDAO.findByCustomerId(usernameOrCustomerId);
            if (customer != null) {
                user = userDAO.findById(customer.getUserId());
            }
        }

        if (user == null) {
            return null; // Invalid credentials
        }

        if ("BLOCKED".equalsIgnoreCase(user.getStatus())) {
            throw new IllegalStateException("Account is blocked due to security reasons. Please contact bank administrator.");
        }

        if (PasswordUtil.checkPassword(plainPassword, user.getPasswordHash())) {
            userDAO.updateLastLogin(user.getUserId());
            return user;
        } else {
            userDAO.incrementFailedAttempts(user.getUserId());
            if (user.getFailedAttempts() + 1 >= 5) {
                userDAO.lockAccount(user.getUserId());
                throw new IllegalStateException("Account locked after 5 failed login attempts. Contact bank support.");
            }
            return null;
        }
    }

    public Customer registerCustomer(User user, Customer customer) throws SQLException {
        if (userDAO.findByUsername(user.getUsername()) != null) {
            throw new IllegalArgumentException("Username already taken");
        }
        if (customerDAO.existsByEmail(customer.getEmail())) {
            throw new IllegalArgumentException("Email address already registered");
        }
        if (customerDAO.existsByMobile(customer.getMobile())) {
            throw new IllegalArgumentException("Mobile number already registered");
        }

        // Hash password
        user.setPasswordHash(PasswordUtil.hashPassword(user.getPasswordHash()));
        user.setRole("CUSTOMER");
        user.setStatus("ACTIVE");

        int userId = userDAO.createUser(user);
        if (userId <= 0) {
            throw new SQLException("Failed to create user record");
        }

        customer.setUserId(userId);
        customer.setCustomerId(AccountNumberGenerator.generateCustomerId());
        customer.setKycStatus("PENDING");

        boolean created = customerDAO.createCustomer(customer);
        if (!created) {
            throw new SQLException("Failed to create customer profile");
        }

        // Automatically create default Savings Account with 10,000 INR initial balance
        accountService.createDefaultSavingsAccount(customer.getCustomerId());

        return customer;
    }

    public boolean changePassword(int userId, String currentPassword, String newPassword) throws SQLException {
        User user = userDAO.findById(userId);
        if (user == null) return false;

        if (!PasswordUtil.checkPassword(currentPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Current password does not match");
        }

        String newHash = PasswordUtil.hashPassword(newPassword);
        return userDAO.updatePassword(userId, newHash);
    }
}
