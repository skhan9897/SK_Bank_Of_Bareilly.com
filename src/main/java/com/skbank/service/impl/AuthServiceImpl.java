package com.skbank.service.impl;

import com.skbank.dao.*;
import com.skbank.dao.impl.*;
import com.skbank.exception.BankException;
import com.skbank.model.*;
import com.skbank.service.AuthService;
import com.skbank.util.DatabaseConnection;
import com.skbank.util.PasswordUtil;
import com.skbank.util.ValidationUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Random;

public class AuthServiceImpl implements AuthService {

    private final UserDAO userDAO = new UserDAOImpl();
    private final CustomerDAO customerDAO = new CustomerDAOImpl();
    private final AdminDAO adminDAO = new AdminDAOImpl();
    private final AccountDAO accountDAO = new AccountDAOImpl();
    private final KycDAO kycDAO = new KycDAOImpl();
    private final NotificationDAO notificationDAO = new NotificationDAOImpl();

    @Override
    public User authenticateCustomer(String username, String password) throws BankException {
        try {
            User user = userDAO.findByUsername(username);
            if (user == null || user.getRole() != UserRole.CUSTOMER) {
                throw new BankException("Invalid username or password");
            }
            if (user.getStatus() == UserStatus.BLOCKED || user.getStatus() == UserStatus.DISABLED) {
                throw new BankException("Your account has been " + user.getStatus().name().toLowerCase() + ". Please contact support.");
            }

            if (!PasswordUtil.checkPassword(password, user.getPasswordHash())) {
                userDAO.updateFailedLoginAttempts(user.getId(), user.getFailedLoginAttempts() + 1);
                if (user.getFailedLoginAttempts() + 1 >= 5) {
                    userDAO.updateStatus(user.getId(), UserStatus.BLOCKED.name());
                    throw new BankException("Account blocked due to 5 consecutive failed login attempts.");
                }
                throw new BankException("Invalid username or password");
            }

            userDAO.updateLastLogin(user.getId());
            return user;
        } catch (SQLException e) {
            throw new BankException("Database error during customer authentication: " + e.getMessage(), e);
        }
    }

    @Override
    public User authenticateAdmin(String username, String password) throws BankException {
        try {
            User user = userDAO.findByUsername(username);
            if (user == null || user.getRole() != UserRole.ADMIN) {
                throw new BankException("Invalid admin credentials");
            }
            if (user.getStatus() != UserStatus.ACTIVE) {
                throw new BankException("Admin account is not active");
            }

            if (!PasswordUtil.checkPassword(password, user.getPasswordHash())) {
                throw new BankException("Invalid admin credentials");
            }

            userDAO.updateLastLogin(user.getId());
            return user;
        } catch (SQLException e) {
            throw new BankException("Database error during admin authentication: " + e.getMessage(), e);
        }
    }

    @Override
    public Customer registerCustomer(Customer customer, String username, String plainPassword, Long branchId, Long accountTypeId) throws BankException {
        // Validation
        if (!ValidationUtil.isValidMobile(customer.getMobile())) {
            throw new BankException("Invalid 10-digit mobile number");
        }
        if (!ValidationUtil.isValidEmail(customer.getEmail())) {
            throw new BankException("Invalid email address");
        }
        if (!ValidationUtil.isValidAadhaar(customer.getAadhaarNumber())) {
            throw new BankException("Invalid 12-digit Aadhaar number");
        }
        if (!ValidationUtil.isValidPan(customer.getPanNumber())) {
            throw new BankException("Invalid PAN number format (e.g., ABCDE1234F)");
        }
        if (plainPassword == null || plainPassword.length() < 6) {
            throw new BankException("Password must be at least 6 characters long");
        }

        Connection conn = null;
        try {
            // Check duplicates
            if (userDAO.findByUsername(username) != null) {
                throw new BankException("Username '" + username + "' is already taken");
            }
            if (customerDAO.findByMobile(customer.getMobile()) != null) {
                throw new BankException("Mobile number is already registered");
            }
            if (customerDAO.findByEmail(customer.getEmail()) != null) {
                throw new BankException("Email address is already registered");
            }

            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Create User
            User user = new User();
            user.setUsername(username);
            user.setPasswordHash(PasswordUtil.hashPassword(plainPassword));
            user.setRole(UserRole.CUSTOMER);
            user.setStatus(UserStatus.ACTIVE);
            Long userId = userDAO.create(conn, user);
            user.setId(userId);

            // 2. Create Customer
            Random rand = new Random();
            String custNum = "SKC" + (10000000 + rand.nextInt(90000000));
            customer.setUserId(userId);
            customer.setCustomerNumber(custNum);
            customer.setKycStatus(KycStatus.VERIFIED);
            customer.setStatus(UserStatus.ACTIVE);
            Long customerId = customerDAO.create(conn, customer);
            customer.setCustomerId(customerId);

            // 3. Create Account - MANDATORY RULE: balance = 0.00, available_balance = 0.00
            Account account = new Account();
            account.setCustomerId(customerId);
            account.setAccountTypeId(accountTypeId != null ? accountTypeId : 1L); // Default Savings
            account.setBranchId(branchId != null ? branchId : 1L); // Default Main Branch
            String accNum = "SK" + (1000000000L + (long)(rand.nextDouble() * 9000000000L));
            account.setAccountNumber(accNum);
            account.setBalance(BigDecimal.ZERO);
            account.setAvailableBalance(BigDecimal.ZERO);
            account.setStatus(AccountStatus.ACTIVE);
            accountDAO.create(conn, account);

            // 4. Create KYC
            Kyc kyc = new Kyc();
            kyc.setCustomerId(customerId);
            kyc.setAadhaarNumber(customer.getAadhaarNumber());
            kyc.setPanNumber(customer.getPanNumber());
            kyc.setVerificationStatus(KycStatus.VERIFIED);
            kycDAO.create(conn, kyc);

            // 5. Send Welcome Notification
            Notification notification = new Notification();
            notification.setUserId(userId);
            notification.setTitle("Welcome to SK BANK OF BAREILLY!");
            notification.setMessage("Dear " + customer.getFullName() + ", your account " + accNum + " has been successfully opened. Welcome aboard!");
            notification.setNotificationType("LOGIN");
            notificationDAO.create(conn, notification);

            conn.commit();
            return customer;
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            throw new BankException("Registration failed: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {}
            }
        }
    }

    @Override
    public Admin getAdminByUserId(Long userId) throws BankException {
        try {
            return adminDAO.findByUserId(userId);
        } catch (SQLException e) {
            throw new BankException("Error fetching admin details", e);
        }
    }

    @Override
    public Customer getCustomerByUserId(Long userId) throws BankException {
        try {
            return customerDAO.findByUserId(userId);
        } catch (SQLException e) {
            throw new BankException("Error fetching customer details", e);
        }
    }

    @Override
    public boolean changePassword(Long userId, String oldPassword, String newPassword) throws BankException {
        try {
            User user = userDAO.findById(userId);
            if (user == null) {
                throw new BankException("User not found");
            }
            if (!PasswordUtil.checkPassword(oldPassword, user.getPasswordHash())) {
                throw new BankException("Current password is incorrect");
            }
            if (newPassword == null || newPassword.length() < 6) {
                throw new BankException("New password must be at least 6 characters long");
            }
            String newHash = PasswordUtil.hashPassword(newPassword);
            return userDAO.updatePassword(userId, newHash);
        } catch (SQLException e) {
            throw new BankException("Error updating password", e);
        }
    }
}
