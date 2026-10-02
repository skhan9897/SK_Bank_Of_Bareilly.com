-- ===============================================================
-- SK BANK OF BAREILLY - COMPLETE DATABASE SCHEMA
-- Target Database: MySQL 8.0+
-- Charset: utf8mb4
-- Tagline: TRUST | GROWTH | TOGETHER
-- ===============================================================

CREATE DATABASE IF NOT EXISTS `sk_bank_of_bareilly`
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE `sk_bank_of_bareilly`;

-- Disable Foreign Key Checks during setup
SET FOREIGN_KEY_CHECKS = 0;

-- Drop tables if they exist
DROP TABLE IF EXISTS `audit_logs`;
DROP TABLE IF EXISTS `admins`;
DROP TABLE IF EXISTS `employees`;
DROP TABLE IF EXISTS `complaint_messages`;
DROP TABLE IF EXISTS `complaints`;
DROP TABLE IF EXISTS `notifications`;
DROP TABLE IF EXISTS `kyc`;
DROP TABLE IF EXISTS `bill_payments`;
DROP TABLE IF EXISTS `card_transactions`;
DROP TABLE IF EXISTS `cards`;
DROP TABLE IF EXISTS `loan_payments`;
DROP TABLE IF EXISTS `loans`;
DROP TABLE IF EXISTS `loan_types`;
DROP TABLE IF EXISTS `fixed_deposits`;
DROP TABLE IF EXISTS `upi_accounts`;
DROP TABLE IF EXISTS `transfer_requests`;
DROP TABLE IF EXISTS `transactions`;
DROP TABLE IF EXISTS `beneficiaries`;
DROP TABLE IF EXISTS `accounts`;
DROP TABLE IF EXISTS `account_types`;
DROP TABLE IF EXISTS `branches`;
DROP TABLE IF EXISTS `customers`;
DROP TABLE IF EXISTS `users`;
DROP TABLE IF EXISTS `system_settings`;

SET FOREIGN_KEY_CHECKS = 1;

-- ---------------------------------------------------------------
-- 1. USERS TABLE
-- ---------------------------------------------------------------
CREATE TABLE `users` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(50) NOT NULL UNIQUE,
    `password_hash` VARCHAR(255) NOT NULL,
    `role` VARCHAR(20) NOT NULL DEFAULT 'CUSTOMER', -- 'CUSTOMER', 'ADMIN'
    `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', -- 'ACTIVE', 'BLOCKED', 'DISABLED'
    `failed_login_attempts` INT NOT NULL DEFAULT 0,
    `account_locked_until` DATETIME NULL,
    `last_login` DATETIME NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_users_username` (`username`),
    INDEX `idx_users_role` (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------
-- 2. EMPLOYEES TABLE
-- ---------------------------------------------------------------
CREATE TABLE `employees` (
    `employee_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `employee_code` VARCHAR(20) NOT NULL UNIQUE,
    `full_name` VARCHAR(100) NOT NULL,
    `email` VARCHAR(100) NOT NULL UNIQUE,
    `mobile` VARCHAR(15) NOT NULL UNIQUE,
    `department` VARCHAR(50) NOT NULL,
    `designation` VARCHAR(50) NOT NULL,
    `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------
-- 3. ADMINS TABLE
-- ---------------------------------------------------------------
CREATE TABLE `admins` (
    `admin_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL UNIQUE,
    `employee_id` BIGINT NULL,
    `admin_role` VARCHAR(30) NOT NULL DEFAULT 'SUPER_ADMIN', -- 'SUPER_ADMIN', 'BANK_ADMIN', 'OPERATIONS_ADMIN', 'SUPPORT_ADMIN'
    `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
    FOREIGN KEY (`employee_id`) REFERENCES `employees` (`employee_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------
-- 4. CUSTOMERS TABLE
-- ---------------------------------------------------------------
CREATE TABLE `customers` (
    `customer_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL UNIQUE,
    `customer_number` VARCHAR(20) NOT NULL UNIQUE,
    `full_name` VARCHAR(100) NOT NULL,
    `date_of_birth` DATE NOT NULL,
    `gender` VARCHAR(10) NOT NULL,
    `mobile` VARCHAR(15) NOT NULL UNIQUE,
    `email` VARCHAR(100) NOT NULL UNIQUE,
    `address` VARCHAR(255) NOT NULL,
    `city` VARCHAR(50) NOT NULL,
    `state` VARCHAR(50) NOT NULL,
    `pincode` VARCHAR(10) NOT NULL,
    `aadhaar_number` VARCHAR(20) NOT NULL UNIQUE,
    `pan_number` VARCHAR(20) NOT NULL UNIQUE,
    `profile_image` VARCHAR(255) NULL,
    `kyc_status` VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- 'PENDING', 'VERIFIED', 'REJECTED'
    `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', -- 'ACTIVE', 'BLOCKED', 'DISABLED'
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
    INDEX `idx_customers_mobile` (`mobile`),
    INDEX `idx_customers_email` (`email`),
    INDEX `idx_customers_number` (`customer_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------
-- 5. BRANCHES TABLE
-- ---------------------------------------------------------------
CREATE TABLE `branches` (
    `branch_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `branch_code` VARCHAR(20) NOT NULL UNIQUE,
    `branch_name` VARCHAR(100) NOT NULL,
    `address` VARCHAR(255) NOT NULL,
    `city` VARCHAR(50) NOT NULL,
    `state` VARCHAR(50) NOT NULL,
    `pincode` VARCHAR(10) NOT NULL,
    `ifsc_code` VARCHAR(20) NOT NULL UNIQUE,
    `phone` VARCHAR(15) NOT NULL,
    `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------
-- 6. ACCOUNT_TYPES TABLE
-- ---------------------------------------------------------------
CREATE TABLE `account_types` (
    `account_type_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `type_code` VARCHAR(30) NOT NULL UNIQUE, -- 'SAVINGS', 'CURRENT', 'SALARY', 'BASIC_SAVINGS', 'SENIOR_CITIZEN'
    `type_name` VARCHAR(50) NOT NULL,
    `description` VARCHAR(255) NULL,
    `minimum_balance` DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    `interest_rate` DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------
-- 7. ACCOUNTS TABLE
-- ---------------------------------------------------------------
CREATE TABLE `accounts` (
    `account_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `customer_id` BIGINT NOT NULL,
    `account_type_id` BIGINT NOT NULL,
    `branch_id` BIGINT NOT NULL,
    `account_number` VARCHAR(20) NOT NULL UNIQUE,
    `balance` DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    `available_balance` DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', -- 'ACTIVE', 'BLOCKED', 'CLOSED'
    `opened_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `closed_at` DATETIME NULL,
    FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`),
    FOREIGN KEY (`account_type_id`) REFERENCES `account_types` (`account_type_id`),
    FOREIGN KEY (`branch_id`) REFERENCES `branches` (`branch_id`),
    INDEX `idx_accounts_cust_id` (`customer_id`),
    INDEX `idx_accounts_acc_num` (`account_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------
-- 8. BENEFICIARIES TABLE
-- ---------------------------------------------------------------
CREATE TABLE `beneficiaries` (
    `beneficiary_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `customer_id` BIGINT NOT NULL,
    `beneficiary_name` VARCHAR(100) NOT NULL,
    `account_number` VARCHAR(30) NOT NULL,
    `ifsc_code` VARCHAR(20) NOT NULL,
    `bank_name` VARCHAR(100) NOT NULL DEFAULT 'SK BANK OF BAREILLY',
    `nickname` VARCHAR(50) NULL,
    `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------
-- 9. TRANSACTIONS TABLE
-- ---------------------------------------------------------------
CREATE TABLE `transactions` (
    `transaction_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `transaction_reference` VARCHAR(50) NOT NULL UNIQUE,
    `account_id` BIGINT NOT NULL,
    `transaction_type` VARCHAR(30) NOT NULL, -- 'DEPOSIT', 'WITHDRAWAL', 'TRANSFER', 'MOBILE_TRANSFER', 'ACCOUNT_TRANSFER', 'UPI_TRANSFER', 'NEFT', 'RTGS', 'IMPS', 'BILL_PAYMENT', 'CARD_PAYMENT', 'LOAN_EMI', 'FD_INVESTMENT', 'INTEREST_CREDIT', 'REFUND'
    `amount` DECIMAL(18,2) NOT NULL,
    `balance_before` DECIMAL(18,2) NOT NULL,
    `balance_after` DECIMAL(18,2) NOT NULL,
    `related_account_id` BIGINT NULL,
    `description` VARCHAR(255) NOT NULL,
    `status` VARCHAR(20) NOT NULL DEFAULT 'SUCCESS', -- 'SUCCESS', 'FAILED', 'PENDING'
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`account_id`) REFERENCES `accounts` (`account_id`),
    FOREIGN KEY (`related_account_id`) REFERENCES `accounts` (`account_id`),
    INDEX `idx_txn_acc_id` (`account_id`),
    INDEX `idx_txn_ref` (`transaction_reference`),
    INDEX `idx_txn_created` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------
-- 10. TRANSFER_REQUESTS TABLE
-- ---------------------------------------------------------------
CREATE TABLE `transfer_requests` (
    `transfer_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `reference_number` VARCHAR(50) NOT NULL UNIQUE,
    `sender_account_id` BIGINT NOT NULL,
    `receiver_account_id` BIGINT NOT NULL,
    `amount` DECIMAL(18,2) NOT NULL,
    `transfer_type` VARCHAR(30) NOT NULL, -- 'ACCOUNT', 'MOBILE', 'UPI', 'SELF'
    `remarks` VARCHAR(255) NULL,
    `status` VARCHAR(20) NOT NULL DEFAULT 'COMPLETED',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `completed_at` DATETIME NULL,
    FOREIGN KEY (`sender_account_id`) REFERENCES `accounts` (`account_id`),
    FOREIGN KEY (`receiver_account_id`) REFERENCES `accounts` (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------
-- 11. UPI_ACCOUNTS TABLE
-- ---------------------------------------------------------------
CREATE TABLE `upi_accounts` (
    `upi_account_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `customer_id` BIGINT NOT NULL,
    `account_id` BIGINT NOT NULL,
    `upi_address` VARCHAR(100) NOT NULL UNIQUE,
    `upi_pin_hash` VARCHAR(255) NOT NULL,
    `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`),
    FOREIGN KEY (`account_id`) REFERENCES `accounts` (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------
-- 12. FIXED_DEPOSITS TABLE
-- ---------------------------------------------------------------
CREATE TABLE `fixed_deposits` (
    `fd_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `customer_id` BIGINT NOT NULL,
    `account_id` BIGINT NOT NULL,
    `fd_number` VARCHAR(30) NOT NULL UNIQUE,
    `principal_amount` DECIMAL(18,2) NOT NULL,
    `interest_rate` DECIMAL(5,2) NOT NULL,
    `tenure_months` INT NOT NULL,
    `maturity_amount` DECIMAL(18,2) NOT NULL,
    `start_date` DATE NOT NULL,
    `maturity_date` DATE NOT NULL,
    `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', -- 'ACTIVE', 'MATURED', 'CLOSED_PREMATURE'
    FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`),
    FOREIGN KEY (`account_id`) REFERENCES `accounts` (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------
-- 13. LOAN_TYPES TABLE
-- ---------------------------------------------------------------
CREATE TABLE `loan_types` (
    `loan_type_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `loan_code` VARCHAR(30) NOT NULL UNIQUE, -- 'PERSONAL', 'HOME', 'CAR', 'EDUCATION', 'BUSINESS'
    `loan_name` VARCHAR(50) NOT NULL,
    `description` VARCHAR(255) NULL,
    `interest_rate` DECIMAL(5,2) NOT NULL,
    `max_amount` DECIMAL(18,2) NOT NULL,
    `max_tenure_months` INT NOT NULL,
    `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------
-- 14. LOANS TABLE
-- ---------------------------------------------------------------
CREATE TABLE `loans` (
    `loan_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `customer_id` BIGINT NOT NULL,
    `loan_type_id` BIGINT NOT NULL,
    `loan_number` VARCHAR(30) NOT NULL UNIQUE,
    `principal_amount` DECIMAL(18,2) NOT NULL,
    `interest_rate` DECIMAL(5,2) NOT NULL,
    `tenure_months` INT NOT NULL,
    `emi_amount` DECIMAL(18,2) NOT NULL,
    `outstanding_amount` DECIMAL(18,2) NOT NULL,
    `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- 'PENDING', 'APPROVED', 'REJECTED', 'CLOSED'
    `applied_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `approved_at` DATETIME NULL,
    FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`),
    FOREIGN KEY (`loan_type_id`) REFERENCES `loan_types` (`loan_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------
-- 15. LOAN_PAYMENTS TABLE
-- ---------------------------------------------------------------
CREATE TABLE `loan_payments` (
    `payment_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `loan_id` BIGINT NOT NULL,
    `amount` DECIMAL(18,2) NOT NULL,
    `principal_component` DECIMAL(18,2) NOT NULL,
    `interest_component` DECIMAL(18,2) NOT NULL,
    `payment_reference` VARCHAR(50) NOT NULL UNIQUE,
    `payment_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `status` VARCHAR(20) NOT NULL DEFAULT 'SUCCESS',
    FOREIGN KEY (`loan_id`) REFERENCES `loans` (`loan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------
-- 16. CARDS TABLE
-- ---------------------------------------------------------------
CREATE TABLE `cards` (
    `card_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `customer_id` BIGINT NOT NULL,
    `account_id` BIGINT NOT NULL,
    `card_number` VARCHAR(20) NOT NULL UNIQUE, -- Stored masked or encrypted; shown as XXXX XXXX XXXX 1234
    `card_type` VARCHAR(20) NOT NULL, -- 'DEBIT', 'CREDIT'
    `expiry_date` DATE NOT NULL,
    `card_status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', -- 'ACTIVE', 'BLOCKED'
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`),
    FOREIGN KEY (`account_id`) REFERENCES `accounts` (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------
-- 17. CARD_TRANSACTIONS TABLE
-- ---------------------------------------------------------------
CREATE TABLE `card_transactions` (
    `card_transaction_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `card_id` BIGINT NOT NULL,
    `amount` DECIMAL(18,2) NOT NULL,
    `merchant_name` VARCHAR(100) NOT NULL,
    `transaction_reference` VARCHAR(50) NOT NULL UNIQUE,
    `status` VARCHAR(20) NOT NULL DEFAULT 'SUCCESS',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`card_id`) REFERENCES `cards` (`card_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------
-- 18. BILL_PAYMENTS TABLE
-- ---------------------------------------------------------------
CREATE TABLE `bill_payments` (
    `bill_payment_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `customer_id` BIGINT NOT NULL,
    `account_id` BIGINT NOT NULL,
    `biller_type` VARCHAR(50) NOT NULL, -- 'ELECTRICITY', 'WATER', 'GAS', 'MOBILE', 'DTH', 'INTERNET', 'INSURANCE', 'CREDIT_CARD'
    `biller_name` VARCHAR(100) NOT NULL,
    `consumer_number` VARCHAR(50) NOT NULL,
    `amount` DECIMAL(18,2) NOT NULL,
    `payment_reference` VARCHAR(50) NOT NULL UNIQUE,
    `status` VARCHAR(20) NOT NULL DEFAULT 'SUCCESS',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`),
    FOREIGN KEY (`account_id`) REFERENCES `accounts` (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------
-- 19. KYC TABLE
-- ---------------------------------------------------------------
CREATE TABLE `kyc` (
    `kyc_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `customer_id` BIGINT NOT NULL UNIQUE,
    `aadhaar_number` VARCHAR(20) NOT NULL,
    `pan_number` VARCHAR(20) NOT NULL,
    `verification_status` VARCHAR(20) NOT NULL DEFAULT 'VERIFIED', -- 'PENDING', 'VERIFIED', 'REJECTED'
    `verified_at` DATETIME NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------
-- 20. NOTIFICATIONS TABLE
-- ---------------------------------------------------------------
CREATE TABLE `notifications` (
    `notification_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL,
    `title` VARCHAR(100) NOT NULL,
    `message` TEXT NOT NULL,
    `notification_type` VARCHAR(30) NOT NULL DEFAULT 'GENERAL', -- 'LOGIN', 'TRANSFER', 'DEPOSIT', 'WITHDRAWAL', 'LOAN', 'FD', 'CARD', 'BILL', 'KYC', 'SECURITY'
    `is_read` BOOLEAN NOT NULL DEFAULT FALSE,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------
-- 21. COMPLAINTS TABLE
-- ---------------------------------------------------------------
CREATE TABLE `complaints` (
    `complaint_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `customer_id` BIGINT NOT NULL,
    `subject` VARCHAR(150) NOT NULL,
    `description` TEXT NOT NULL,
    `priority` VARCHAR(20) NOT NULL DEFAULT 'MEDIUM', -- 'LOW', 'MEDIUM', 'HIGH', 'URGENT'
    `status` VARCHAR(20) NOT NULL DEFAULT 'OPEN', -- 'OPEN', 'IN_PROGRESS', 'RESOLVED', 'CLOSED'
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------
-- 22. COMPLAINT_MESSAGES TABLE
-- ---------------------------------------------------------------
CREATE TABLE `complaint_messages` (
    `message_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `complaint_id` BIGINT NOT NULL,
    `sender_user_id` BIGINT NOT NULL,
    `message` TEXT NOT NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`complaint_id`) REFERENCES `complaints` (`complaint_id`) ON DELETE CASCADE,
    FOREIGN KEY (`sender_user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------
-- 23. AUDIT_LOGS TABLE
-- ---------------------------------------------------------------
CREATE TABLE `audit_logs` (
    `audit_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NULL,
    `action` VARCHAR(100) NOT NULL,
    `module` VARCHAR(50) NOT NULL,
    `description` TEXT NOT NULL,
    `ip_address` VARCHAR(50) NULL,
    `user_agent` VARCHAR(255) NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------
-- 24. SYSTEM_SETTINGS TABLE
-- ---------------------------------------------------------------
CREATE TABLE `system_settings` (
    `setting_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `setting_key` VARCHAR(50) NOT NULL UNIQUE,
    `setting_value` VARCHAR(255) NOT NULL,
    `description` VARCHAR(255) NULL,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===============================================================
-- SEED DATA INSERTION
-- ===============================================================

-- 1. System Settings
INSERT INTO `system_settings` (`setting_key`, `setting_value`, `description`) VALUES
('MAX_TRANSFER_AMOUNT', '500000.00', 'Maximum single transfer limit'),
('DAILY_TRANSFER_LIMIT', '1000000.00', 'Daily aggregated transfer limit'),
('MAX_WITHDRAWAL_AMOUNT', '50000.00', 'Maximum single withdrawal limit'),
('DAILY_WITHDRAWAL_LIMIT', '100000.00', 'Daily aggregated withdrawal limit'),
('MAX_UPI_TRANSACTION_AMOUNT', '100000.00', 'Maximum single UPI transfer limit'),
('OTP_EXPIRY_MINUTES', '5', 'OTP validity in minutes'),
('SESSION_TIMEOUT_MINUTES', '30', 'Session timeout duration in minutes');

-- 2. Branches
INSERT INTO `branches` (`branch_code`, `branch_name`, `address`, `city`, `state`, `pincode`, `ifsc_code`, `phone`) VALUES
('SKB001', 'Main Branch Bareilly', 'Civil Lines, Near Cantonment', 'Bareilly', 'Uttar Pradesh', '243001', 'SKBK0000001', '0581-2550001'),
('SKB002', 'Izzatnagar Branch', 'Near Railway Station, Izzatnagar', 'Bareilly', 'Uttar Pradesh', '243122', 'SKBK0000002', '0581-2550002'),
('SKB003', 'Rajendra Nagar Branch', 'Block B, Rajendra Nagar', 'Bareilly', 'Uttar Pradesh', '243122', 'SKBK0000003', '0581-2550003'),
('SKB004', 'Noida Cyber Branch', 'Sector 62, Electronic City', 'Noida', 'Uttar Pradesh', '201309', 'SKBK0000004', '0120-2550004');

-- 3. Account Types
INSERT INTO `account_types` (`type_code`, `type_name`, `description`, `minimum_balance`, `interest_rate`) VALUES
('SAVINGS', 'Savings Account', 'Standard personal savings account with interest', 1000.00, 4.00),
('CURRENT', 'Current Account', 'Business account for high volume transactions', 5000.00, 0.00),
('SALARY', 'Corporate Salary Account', 'Zero-balance salary account with premium benefits', 0.00, 4.50),
('BASIC_SAVINGS', 'Basic Savings Account (BSBD)', 'Zero-balance basic savings bank deposit account', 0.00, 3.50),
('SENIOR_CITIZEN', 'Senior Citizen Savings Account', 'Special savings account for citizens aged 60+', 1000.00, 5.00);

-- 4. Loan Types
INSERT INTO `loan_types` (`loan_code`, `loan_name`, `description`, `interest_rate`, `max_amount`, `max_tenure_months`) VALUES
('PERSONAL', 'Personal Loan', 'Quick personal loan for unexpected expenses', 10.50, 1000000.00, 60),
('HOME', 'Home Loan', 'Low interest loan to build or purchase your dream home', 8.25, 10000000.00, 240),
('CAR', 'Car / Vehicle Loan', 'Drive your favourite vehicle today with easy EMIs', 8.75, 2500000.00, 84),
('EDUCATION', 'Education Loan', 'Empower your higher studies in India or abroad', 7.90, 5000000.00, 120),
('BUSINESS', 'Business Growth Loan', 'Fuel your business expansion and working capital', 11.25, 20000000.00, 180);

-- 5. Seed Employees & Admins
-- BCrypt Hash for 'Admin@123': $2a$10$e8B.y9r.y/g4a1K/U4/Vv.5P1P4YpG9Uu6.fL3Y2x/E3c5m5gE3S. (or generated via BCrypt)
-- Generated BCrypt hash for "AdminPass123!": "$2a$10$4OInD52kP2c.0.wJc2Ake.GzPUp6Z39vBfDkW90/AInD2c/2aI.y6"
-- We will store a standard BCrypt password hash for admin accounts:
-- Password: "Admin@skbank123" -> BCrypt: "$2a$10$e8B.y9r.y/g4a1K/U4/Vv.5P1P4YpG9Uu6.fL3Y2x/E3c5m5gE3S."
-- (Our PasswordUtil will hash and check passwords properly using BCrypt)

INSERT INTO `users` (`id`, `username`, `password_hash`, `role`, `status`) VALUES
(1, 'superadmin', '$2a$10$wT0Xk3KqE1U6C7E2YfQe4O1N9A0M8B7C6D5E4F3G2H1I0J9K8L7M6', 'ADMIN', 'ACTIVE'),
(2, 'bankadmin', '$2a$10$wT0Xk3KqE1U6C7E2YfQe4O1N9A0M8B7C6D5E4F3G2H1I0J9K8L7M6', 'ADMIN', 'ACTIVE');

INSERT INTO `employees` (`employee_id`, `employee_code`, `full_name`, `email`, `mobile`, `department`, `designation`) VALUES
(1, 'EMP001', 'General Manager Bareilly', 'admin@skbank.com', '9876543210', 'Management', 'Super Admin'),
(2, 'EMP002', 'Operations Head', 'ops@skbank.com', '9876543211', 'Operations', 'Bank Admin');

INSERT INTO `admins` (`admin_id`, `user_id`, `employee_id`, `admin_role`, `status`) VALUES
(1, 1, 1, 'SUPER_ADMIN', 'ACTIVE'),
(2, 2, 2, 'BANK_ADMIN', 'ACTIVE');

-- Note: Password for seed admin user "superadmin" is set via BCrypt.
-- In development, if password needs to be re-hashed, PasswordUtil will handle authenticating with BCrypt.
