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
import java.util.List;
import java.util.Random;

public class AuthServiceImpl implements AuthService {

    private final UserDAO userDAO = new UserDAOImpl();
    private final CustomerDAO customerDAO = new CustomerDAOImpl();
    private final AccountDAO accountDAO = new AccountDAOImpl();
    private final BranchDAO branchDAO = new BranchDAOImpl();
    private final AccountTypeDAO accountTypeDAO = new AccountTypeDAOImpl();
    private final KycDAO kycDAO = new KycDAOImpl();
    private final NotificationDAO notificationDAO = new NotificationDAOImpl();
    private final AdminDAO adminDAO = new AdminDAOImpl();

    @Override
    public User authenticateCustomer(String username, String password) throws BankException {
        try {
            User user = userDAO.findByUsername(username);
            if (user == null || user.getRole() != UserRole.CUSTOMER) {
                throw new BankException("Invalid username or password");
            }
            if (user.getStatus() != UserStatus.ACTIVE) {
                throw new BankException("Customer account is disabled or blocked. Please contact support.");
            }

            if (!PasswordUtil.checkPassword(password, user.getPasswordHash())) {
                int attempts = user.getFailedLoginAttempts() + 1;
                userDAO.updateFailedLoginAttempts(user.getId(), attempts);
                if (attempts >= 5) {
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
        if (customer.getFullName() == null || customer.getFullName().trim().isEmpty()) {
            throw new BankException("Full name is required");
        }
        if (customer.getDateOfBirth() == null) {
            throw new BankException("Date of birth is required");
        }
        if (!ValidationUtil.isValidMobile(customer.getMobile())) {
            throw new BankException("Invalid 10-digit mobile number");
        }
        if (customer.getEmail() != null && !customer.getEmail().trim().isEmpty() && !ValidationUtil.isValidEmail(customer.getEmail())) {
            throw new BankException("Invalid email address");
        }
        if (customer.getAadhaarNumber() != null && !customer.getAadhaarNumber().trim().isEmpty() && !ValidationUtil.isValidAadhaar(customer.getAadhaarNumber())) {
            throw new BankException("Invalid 12-digit Aadhaar number");
        }
        if (customer.getPanNumber() != null && !customer.getPanNumber().trim().isEmpty() && !ValidationUtil.isValidPan(customer.getPanNumber())) {
            throw new BankException("Invalid PAN number format (e.g., ABCDE1234F)");
        }
        if (plainPassword == null || plainPassword.length() < 6) {
            throw new BankException("Password must be at least 6 characters long");
        }

        Connection conn = null;
        try {
            // Auto-generate username if empty or "AUTO"
            if (username == null || username.trim().isEmpty() || "AUTO".equalsIgnoreCase(username.trim())) {
                username = generateUniqueUsername(customer.getFullName(), customer.getMobile());
            }

            // Check duplicates
            if (userDAO.findByUsername(username) != null) {
                username = generateUniqueUsername(customer.getFullName(), customer.getMobile());
            }
            if (customerDAO.findByMobile(customer.getMobile()) != null) {
                throw new BankException("Mobile number is already registered");
            }
            if (customer.getEmail() != null && !customer.getEmail().trim().isEmpty() && customerDAO.findByEmail(customer.getEmail()) != null) {
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

            // 2. Create Customer with VARCHAR(20) SKC... customer_id
            Random rand = new Random();
            String custNum = "SKC" + (10000000 + rand.nextInt(90000000));
            customer.setUserId(userId);
            customer.setCustomerId(custNum);
            customer.setCustomerNumber(custNum);
            customer.setKycStatus(KycStatus.VERIFIED);
            customer.setStatus(UserStatus.ACTIVE);
            String customerId = customerDAO.create(conn, customer);

            // Validate accountTypeId against account_types table
            AccountType selectedType = null;
            if (accountTypeId != null) {
                selectedType = accountTypeDAO.findById(accountTypeId);
            }
            if (selectedType == null) {
                List<AccountType> allTypes = accountTypeDAO.findAllActive();
                if (!allTypes.isEmpty()) {
                    selectedType = allTypes.get(0);
                }
            }
            if (selectedType == null) {
                throw new BankException("Selected account type is invalid.");
            }
            Long validAccountTypeId = selectedType.getAccountTypeId();

            // Validate branchId against branches table
            Branch selectedBranch = null;
            if (branchId != null) {
                selectedBranch = branchDAO.findById(branchId);
            }
            if (selectedBranch == null) {
                List<Branch> allBranches = branchDAO.findAllActive();
                if (!allBranches.isEmpty()) {
                    selectedBranch = allBranches.get(0);
                }
            }
            Long validBranchId = selectedBranch != null ? selectedBranch.getBranchId() : 1L;

            // 3. Create Account - MANDATORY RULE: balance = 0.00, available_balance = 0.00
            Account account = new Account();
            account.setCustomerId(customerId);
            account.setAccountTypeId(validAccountTypeId);
            account.setBranchId(validBranchId);
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
            notification.setMessage("Dear " + customer.getFullName() + ", your account " + accNum + " has been successfully opened. Your username is: " + username);
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

    private String generateUniqueUsername(String fullName, String mobile) throws SQLException {
        String baseName = "";
        if (fullName != null && !fullName.trim().isEmpty()) {
            baseName = fullName.trim().replaceAll("[^a-zA-Z]", "").toLowerCase();
            if (baseName.length() > 8) {
                baseName = baseName.substring(0, 8);
            }
        }
        if (baseName.isEmpty()) {
            baseName = "skb";
        }

        String mobileSuffix = (mobile != null && mobile.length() >= 4) ? mobile.substring(mobile.length() - 4) : String.valueOf(1000 + new Random().nextInt(9000));

        String candidate = baseName + mobileSuffix;
        int count = 1;

        while (userDAO.findByUsername(candidate) != null) {
            candidate = baseName + mobileSuffix + count;
            count++;
        }

        return candidate;
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
    public Customer getCustomerById(String customerId) throws BankException {
        try {
            return customerDAO.findById(customerId);
        } catch (SQLException e) {
            throw new BankException("Error fetching customer details", e);
        }
    }

    @Override
    public boolean changePassword(Long userId, String currentPassword, String newPassword) throws BankException {
        try {
            User user = userDAO.findById(userId);
            if (user == null) {
                throw new BankException("User not found");
            }
            if (!PasswordUtil.checkPassword(currentPassword, user.getPasswordHash())) {
                throw new BankException("Current password is incorrect");
            }
            if (newPassword == null || newPassword.length() < 6) {
                throw new BankException("New password must be at least 6 characters long");
            }
            return userDAO.updatePassword(userId, PasswordUtil.hashPassword(newPassword));
        } catch (SQLException e) {
            throw new BankException("Database error during password change: " + e.getMessage(), e);
        }
    }
}
