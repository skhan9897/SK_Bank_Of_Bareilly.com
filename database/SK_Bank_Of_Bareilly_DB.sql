-- ===============================================================
-- SK BANK OF BAREILLY - MASTER CANONICAL DATABASE SCHEMA
-- Target Database: MySQL 8.0+ / MariaDB 10+
-- Database Name: SK_Bank_Of_Bareilly_DB
-- Charset: utf8mb4
-- Tagline: TRUST | GROWTH | TOGETHER
-- ===============================================================

CREATE DATABASE IF NOT EXISTS `SK_Bank_Of_Bareilly_DB`
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE `SK_Bank_Of_Bareilly_DB`;

SET FOREIGN_KEY_CHECKS = 0;

-- 1. USERS TABLE
CREATE TABLE IF NOT EXISTS `users` (
    `user_id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(100) NOT NULL UNIQUE,
    `password_hash` VARCHAR(255) NOT NULL,
    `role` VARCHAR(30) NOT NULL DEFAULT 'CUSTOMER',
    `status` VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    `failed_login_attempts` INT NOT NULL DEFAULT 0,
    `account_locked_until` DATETIME NULL,
    `auth_token` VARCHAR(255) NULL,
    `token_expiry` DATETIME NULL,
    `last_login_at` DATETIME NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_users_username` (`username`),
    INDEX `idx_users_role` (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. BRANCHES TABLE
CREATE TABLE IF NOT EXISTS `branches` (
    `branch_id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `branch_code` VARCHAR(20) NOT NULL UNIQUE,
    `branch_name` VARCHAR(100) NOT NULL,
    `address` VARCHAR(255) NOT NULL,
    `city` VARCHAR(50) NOT NULL,
    `state` VARCHAR(50) NOT NULL,
    `pincode` VARCHAR(10) NOT NULL,
    `ifsc_code` VARCHAR(20) NOT NULL UNIQUE,
    `phone` VARCHAR(15) NOT NULL,
    `email` VARCHAR(100) NULL,
    `status` VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. CUSTOMERS TABLE
CREATE TABLE IF NOT EXISTS `customers` (
    `customer_id` VARCHAR(20) NOT NULL PRIMARY KEY,
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
    `profile_image` VARCHAR(500) NULL,
    `kyc_status` VARCHAR(20) NOT NULL DEFAULT 'VERIFIED',
    `status` VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
    INDEX `idx_cust_user_id` (`user_id`),
    INDEX `idx_cust_number` (`customer_number`),
    INDEX `idx_cust_mobile` (`mobile`),
    INDEX `idx_cust_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. ACCOUNT_TYPES TABLE
CREATE TABLE IF NOT EXISTS `account_types` (
    `account_type_id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `type_code` VARCHAR(30) NOT NULL UNIQUE,
    `type_name` VARCHAR(50) NOT NULL,
    `description` VARCHAR(255) NULL,
    `minimum_balance` DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    `interest_rate` DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    `status` VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. ACCOUNTS TABLE
CREATE TABLE IF NOT EXISTS `accounts` (
    `account_id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `customer_id` VARCHAR(20) NOT NULL,
    `account_type_id` BIGINT NOT NULL,
    `branch_id` BIGINT NOT NULL,
    `account_number` VARCHAR(20) NOT NULL UNIQUE,
    `balance` DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    `available_balance` DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    `status` VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    `opened_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `closed_at` DATETIME NULL,
    FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`),
    FOREIGN KEY (`account_type_id`) REFERENCES `account_types` (`account_type_id`),
    FOREIGN KEY (`branch_id`) REFERENCES `branches` (`branch_id`),
    INDEX `idx_acc_cust_id` (`customer_id`),
    INDEX `idx_acc_number` (`account_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. BENEFICIARIES TABLE
CREATE TABLE IF NOT EXISTS `beneficiaries` (
    `beneficiary_id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `customer_id` VARCHAR(20) NOT NULL,
    `beneficiary_name` VARCHAR(100) NOT NULL,
    `account_number` VARCHAR(30) NOT NULL,
    `bank_name` VARCHAR(100) NOT NULL DEFAULT 'SK BANK OF BAREILLY',
    `ifsc_code` VARCHAR(20) NOT NULL,
    `mobile` VARCHAR(15) NULL,
    `upi_id` VARCHAR(100) NULL,
    `status` VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`) ON DELETE CASCADE,
    INDEX `idx_ben_cust_id` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. TRANSACTIONS TABLE
CREATE TABLE IF NOT EXISTS `transactions` (
    `transaction_id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `transaction_reference` VARCHAR(50) NOT NULL UNIQUE,
    `account_id` BIGINT NOT NULL,
    `related_account_id` BIGINT NULL,
    `transaction_type` VARCHAR(30) NOT NULL,
    `channel` VARCHAR(30) NOT NULL DEFAULT 'DIGITAL',
    `amount` DECIMAL(18,2) NOT NULL,
    `balance_after` DECIMAL(18,2) NOT NULL,
    `description` VARCHAR(255) NOT NULL,
    `status` VARCHAR(30) NOT NULL DEFAULT 'SUCCESS',
    `transaction_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`account_id`) REFERENCES `accounts` (`account_id`),
    INDEX `idx_txn_acc_id` (`account_id`),
    INDEX `idx_txn_date` (`transaction_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 8. TRANSFER_REQUESTS TABLE
CREATE TABLE IF NOT EXISTS `transfer_requests` (
    `transfer_id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `reference_number` VARCHAR(50) NOT NULL UNIQUE,
    `sender_account_id` BIGINT NOT NULL,
    `receiver_account_id` BIGINT NOT NULL,
    `amount` DECIMAL(18,2) NOT NULL,
    `transfer_type` VARCHAR(30) NOT NULL,
    `remarks` VARCHAR(255) NULL,
    `status` VARCHAR(30) NOT NULL DEFAULT 'COMPLETED',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `completed_at` DATETIME NULL,
    FOREIGN KEY (`sender_account_id`) REFERENCES `accounts` (`account_id`),
    FOREIGN KEY (`receiver_account_id`) REFERENCES `accounts` (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 9. UPI_ACCOUNTS TABLE
CREATE TABLE IF NOT EXISTS `upi_accounts` (
    `upi_account_id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `customer_id` VARCHAR(20) NOT NULL,
    `account_id` BIGINT NOT NULL,
    `upi_address` VARCHAR(100) NOT NULL UNIQUE,
    `upi_pin_hash` VARCHAR(255) NOT NULL,
    `status` VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`),
    FOREIGN KEY (`account_id`) REFERENCES `accounts` (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 10. FIXED_DEPOSITS TABLE
CREATE TABLE IF NOT EXISTS `fixed_deposits` (
    `fd_id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `customer_id` VARCHAR(20) NOT NULL,
    `account_id` BIGINT NOT NULL,
    `fd_number` VARCHAR(30) NOT NULL UNIQUE,
    `principal_amount` DECIMAL(18,2) NOT NULL,
    `interest_rate` DECIMAL(5,2) NOT NULL,
    `tenure_months` INT NOT NULL,
    `maturity_amount` DECIMAL(18,2) NOT NULL,
    `start_date` DATE NOT NULL,
    `maturity_date` DATE NOT NULL,
    `status` VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`),
    FOREIGN KEY (`account_id`) REFERENCES `accounts` (`account_id`),
    INDEX `idx_fd_cust_id` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 11. LOAN_TYPES TABLE
CREATE TABLE IF NOT EXISTS `loan_types` (
    `loan_type_id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `loan_code` VARCHAR(30) NOT NULL UNIQUE,
    `loan_name` VARCHAR(50) NOT NULL,
    `description` VARCHAR(255) NULL,
    `interest_rate` DECIMAL(5,2) NOT NULL,
    `min_amount` DECIMAL(18,2) NOT NULL DEFAULT 10000.00,
    `max_amount` DECIMAL(18,2) NOT NULL,
    `max_tenure_months` INT NOT NULL,
    `status` VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 12. LOANS TABLE
CREATE TABLE IF NOT EXISTS `loans` (
    `loan_id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `customer_id` VARCHAR(20) NOT NULL,
    `account_id` BIGINT NOT NULL,
    `loan_type_id` BIGINT NOT NULL,
    `loan_number` VARCHAR(30) NOT NULL UNIQUE,
    `principal_amount` DECIMAL(18,2) NOT NULL,
    `interest_rate` DECIMAL(5,2) NOT NULL,
    `tenure_months` INT NOT NULL,
    `emi_amount` DECIMAL(18,2) NOT NULL,
    `outstanding_amount` DECIMAL(18,2) NOT NULL,
    `application_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `approval_date` DATETIME NULL,
    `disbursement_date` DATETIME NULL,
    `status` VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    `remarks` VARCHAR(255) NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`),
    FOREIGN KEY (`loan_type_id`) REFERENCES `loan_types` (`loan_type_id`),
    INDEX `idx_loan_cust_id` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 13. LOAN_PAYMENTS TABLE
CREATE TABLE IF NOT EXISTS `loan_payments` (
    `payment_id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `loan_id` BIGINT NOT NULL,
    `payment_reference` VARCHAR(50) NOT NULL UNIQUE,
    `amount` DECIMAL(18,2) NOT NULL,
    `principal_component` DECIMAL(18,2) NOT NULL,
    `interest_component` DECIMAL(18,2) NOT NULL,
    `payment_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `status` VARCHAR(30) NOT NULL DEFAULT 'SUCCESS',
    FOREIGN KEY (`loan_id`) REFERENCES `loans` (`loan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 14. CARDS TABLE
CREATE TABLE IF NOT EXISTS `cards` (
    `card_id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `customer_id` VARCHAR(20) NOT NULL,
    `account_id` BIGINT NOT NULL,
    `card_number` VARCHAR(20) NOT NULL UNIQUE,
    `card_type` VARCHAR(20) NOT NULL,
    `card_holder_name` VARCHAR(100) NOT NULL,
    `expiry_month` INT NOT NULL DEFAULT 12,
    `expiry_year` INT NOT NULL DEFAULT 2030,
    `expiry_date` DATE NOT NULL,
    `card_status` VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    `daily_limit` DECIMAL(18,2) NOT NULL DEFAULT 100000.00,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`),
    FOREIGN KEY (`account_id`) REFERENCES `accounts` (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 15. CARD_TRANSACTIONS TABLE
CREATE TABLE IF NOT EXISTS `card_transactions` (
    `card_transaction_id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `card_id` BIGINT NOT NULL,
    `transaction_reference` VARCHAR(50) NOT NULL UNIQUE,
    `merchant_name` VARCHAR(100) NOT NULL,
    `amount` DECIMAL(18,2) NOT NULL,
    `transaction_type` VARCHAR(30) NOT NULL DEFAULT 'PURCHASE',
    `status` VARCHAR(30) NOT NULL DEFAULT 'SUCCESS',
    `transaction_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`card_id`) REFERENCES `cards` (`card_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 16. BILL_PAYMENTS TABLE
CREATE TABLE IF NOT EXISTS `bill_payments` (
    `bill_payment_id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `customer_id` VARCHAR(20) NOT NULL,
    `account_id` BIGINT NOT NULL,
    `biller_category` VARCHAR(50) NOT NULL,
    `biller_name` VARCHAR(100) NOT NULL,
    `consumer_number` VARCHAR(50) NOT NULL,
    `amount` DECIMAL(18,2) NOT NULL,
    `transaction_reference` VARCHAR(50) NOT NULL UNIQUE,
    `status` VARCHAR(30) NOT NULL DEFAULT 'SUCCESS',
    `payment_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`),
    FOREIGN KEY (`account_id`) REFERENCES `accounts` (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 17. KYC_DOCUMENTS TABLE
CREATE TABLE IF NOT EXISTS `kyc_documents` (
    `kyc_id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `customer_id` VARCHAR(20) NOT NULL UNIQUE,
    `aadhaar_number` VARCHAR(20) NOT NULL,
    `pan_number` VARCHAR(20) NOT NULL,
    `verification_status` VARCHAR(30) NOT NULL DEFAULT 'VERIFIED',
    `verified_at` DATETIME NULL,
    `remarks` VARCHAR(255) NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 18. NOTIFICATIONS TABLE
CREATE TABLE IF NOT EXISTS `notifications` (
    `notification_id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL,
    `title` VARCHAR(100) NOT NULL,
    `message` TEXT NOT NULL,
    `notification_type` VARCHAR(50) NOT NULL DEFAULT 'GENERAL',
    `is_read` BOOLEAN NOT NULL DEFAULT FALSE,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
    INDEX `idx_notif_user_id` (`user_id`),
    INDEX `idx_notif_is_read` (`is_read`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 19. COMPLAINTS TABLE
CREATE TABLE IF NOT EXISTS `complaints` (
    `complaint_id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `customer_id` VARCHAR(20) NOT NULL,
    `complaint_number` VARCHAR(50) NOT NULL UNIQUE,
    `subject` VARCHAR(150) NOT NULL,
    `category` VARCHAR(50) NOT NULL DEFAULT 'GENERAL',
    `description` TEXT NOT NULL,
    `priority` VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    `status` VARCHAR(30) NOT NULL DEFAULT 'OPEN',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`),
    INDEX `idx_complaint_cust_id` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 20. COMPLAINT_MESSAGES TABLE
CREATE TABLE IF NOT EXISTS `complaint_messages` (
    `message_id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `complaint_id` BIGINT NOT NULL,
    `sender_user_id` BIGINT NOT NULL,
    `message` TEXT NOT NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`complaint_id`) REFERENCES `complaints` (`complaint_id`) ON DELETE CASCADE,
    FOREIGN KEY (`sender_user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 21. EMPLOYEES TABLE
CREATE TABLE IF NOT EXISTS `employees` (
    `employee_id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL UNIQUE,
    `branch_id` BIGINT NULL,
    `employee_number` VARCHAR(20) NOT NULL UNIQUE,
    `full_name` VARCHAR(100) NOT NULL,
    `designation` VARCHAR(50) NOT NULL,
    `mobile` VARCHAR(15) NOT NULL UNIQUE,
    `email` VARCHAR(100) NOT NULL UNIQUE,
    `status` VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 22. ADMINS TABLE
CREATE TABLE IF NOT EXISTS `admins` (
    `admin_id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL UNIQUE,
    `employee_id` BIGINT NULL,
    `full_name` VARCHAR(100) NOT NULL,
    `status` VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 23. AUDIT_LOGS TABLE
CREATE TABLE IF NOT EXISTS `audit_logs` (
    `audit_id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NULL,
    `action` VARCHAR(100) NOT NULL,
    `module` VARCHAR(50) NOT NULL,
    `description` TEXT NOT NULL,
    `ip_address` VARCHAR(50) NULL,
    `user_agent` VARCHAR(255) NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE SET NULL,
    INDEX `idx_audit_user_id` (`user_id`),
    INDEX `idx_audit_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 24. PASSWORD_RESET_TOKENS TABLE
CREATE TABLE IF NOT EXISTS `password_reset_tokens` (
    `token_id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL,
    `token_hash` VARCHAR(255) NOT NULL,
    `expires_at` DATETIME NOT NULL,
    `used_at` DATETIME NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 25. OTP_VERIFICATIONS TABLE
CREATE TABLE IF NOT EXISTS `otp_verifications` (
    `otp_id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NULL,
    `mobile` VARCHAR(15) NULL,
    `email` VARCHAR(100) NULL,
    `purpose` VARCHAR(50) NOT NULL,
    `otp_hash` VARCHAR(255) NOT NULL,
    `expires_at` DATETIME NOT NULL,
    `verified_at` DATETIME NULL,
    `attempts` INT NOT NULL DEFAULT 0,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 26. NOMINEES TABLE
CREATE TABLE IF NOT EXISTS `nominees` (
    `nominee_id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `customer_id` VARCHAR(20) NOT NULL,
    `account_id` BIGINT NOT NULL,
    `nominee_name` VARCHAR(100) NOT NULL,
    `relationship` VARCHAR(50) NOT NULL,
    `date_of_birth` DATE NOT NULL,
    `mobile` VARCHAR(15) NULL,
    `address` VARCHAR(255) NULL,
    `status` VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`),
    FOREIGN KEY (`account_id`) REFERENCES `accounts` (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 27. SYSTEM_SETTINGS TABLE
CREATE TABLE IF NOT EXISTS `system_settings` (
    `setting_id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `setting_key` VARCHAR(50) NOT NULL UNIQUE,
    `setting_value` VARCHAR(255) NOT NULL,
    `description` VARCHAR(255) NULL,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 28. PAYMENT BANK TABLES
CREATE TABLE IF NOT EXISTS `payment_wallets` (
    `wallet_id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `customer_id` VARCHAR(20) NOT NULL UNIQUE,
    `wallet_number` VARCHAR(30) NOT NULL UNIQUE,
    `balance` DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    `status` VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `payment_transactions` (
    `payment_transaction_id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `customer_id` VARCHAR(20) NOT NULL,
    `source_account_id` BIGINT NULL,
    `payment_type` VARCHAR(50) NOT NULL,
    `provider_code` VARCHAR(50) NULL,
    `recipient_identifier` VARCHAR(100) NOT NULL,
    `amount` DECIMAL(18,2) NOT NULL,
    `reference_number` VARCHAR(50) NOT NULL UNIQUE,
    `idempotency_key` VARCHAR(100) NULL UNIQUE,
    `status` VARCHAR(30) NOT NULL DEFAULT 'SUCCESS',
    `remarks` VARCHAR(255) NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `recharge_transactions` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `customer_id` VARCHAR(20) NOT NULL,
    `mobile_number` VARCHAR(15) NOT NULL,
    `operator` VARCHAR(50) NOT NULL,
    `circle` VARCHAR(50) NULL,
    `recharge_type` VARCHAR(20) NOT NULL DEFAULT 'PREPAID',
    `amount` DECIMAL(18,2) NOT NULL,
    `reference_number` VARCHAR(50) NOT NULL UNIQUE,
    `status` VARCHAR(30) NOT NULL DEFAULT 'SUCCESS',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `fastag_accounts` (
    `fastag_id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `customer_id` VARCHAR(20) NOT NULL,
    `vehicle_number` VARCHAR(20) NOT NULL UNIQUE,
    `tag_id` VARCHAR(50) NOT NULL UNIQUE,
    `issuer_bank` VARCHAR(100) NOT NULL DEFAULT 'SK BANK OF BAREILLY',
    `balance` DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    `status` VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `payment_providers` (
    `provider_id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `provider_type` VARCHAR(50) NOT NULL,
    `provider_name` VARCHAR(100) NOT NULL,
    `code` VARCHAR(50) NOT NULL UNIQUE,
    `status` VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `payment_idempotency` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `idempotency_key` VARCHAR(100) NOT NULL UNIQUE,
    `reference_number` VARCHAR(50) NOT NULL,
    `response_payload` TEXT NOT NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===============================================================
-- VIEWS FOR LEGACY / COMPATIBILITY
-- ===============================================================

CREATE OR REPLACE VIEW `kyc` AS
SELECT kyc_id, customer_id, aadhaar_number, pan_number, verification_status, verified_at, created_at, updated_at
FROM kyc_documents;

CREATE OR REPLACE VIEW `customer_kyc` AS
SELECT kyc_id AS customer_kyc_id, customer_id, aadhaar_number, pan_number, verification_status, verified_at, created_at, updated_at
FROM kyc_documents;

CREATE OR REPLACE VIEW `customer_account_summary` AS
SELECT
    c.customer_id,
    c.user_id,
    c.customer_number,
    c.full_name,
    c.mobile,
    c.email,
    COUNT(a.account_id) AS total_accounts,
    COALESCE(SUM(a.balance), 0.00) AS total_balance,
    COALESCE(SUM(a.available_balance), 0.00) AS total_available_balance
FROM customers c
LEFT JOIN accounts a ON c.customer_id = a.customer_id AND a.status = 'ACTIVE'
GROUP BY c.customer_id, c.user_id, c.customer_number, c.full_name, c.mobile, c.email;

CREATE OR REPLACE VIEW `transaction_summary` AS
SELECT
    t.transaction_id,
    t.transaction_reference,
    t.account_id,
    a.account_number,
    c.customer_id,
    c.full_name AS customer_name,
    t.transaction_type,
    t.channel,
    t.amount,
    t.balance_after,
    t.description,
    t.status,
    t.transaction_date
FROM transactions t
JOIN accounts a ON t.account_id = a.account_id
JOIN customers c ON a.customer_id = c.customer_id;

CREATE OR REPLACE VIEW `loan_summary` AS
SELECT
    l.loan_id,
    l.loan_number,
    l.customer_id,
    c.full_name AS customer_name,
    lt.loan_name,
    l.principal_amount,
    l.interest_rate,
    l.tenure_months,
    l.emi_amount,
    l.outstanding_amount,
    l.status,
    l.application_date
FROM loans l
JOIN customers c ON l.customer_id = c.customer_id
JOIN loan_types lt ON l.loan_type_id = lt.loan_type_id;

CREATE OR REPLACE VIEW `fd_summary` AS
SELECT
    f.fd_id,
    f.fd_number,
    f.customer_id,
    c.full_name AS customer_name,
    f.principal_amount,
    f.interest_rate,
    f.tenure_months,
    f.maturity_amount,
    f.start_date,
    f.maturity_date,
    f.status
FROM fixed_deposits f
JOIN customers c ON f.customer_id = c.customer_id;

SET FOREIGN_KEY_CHECKS = 1;

-- ===============================================================
-- SEED DATA INSERTION
-- ===============================================================

INSERT IGNORE INTO `system_settings` (`setting_id`, `setting_key`, `setting_value`, `description`) VALUES
(1, 'BANK_NAME', 'SK BANK OF BAREILLY', 'Official Bank Name'),
(2, 'BANK_SHORT_NAME', 'SKBANK', 'Short Brand Name'),
(3, 'SUPPORT_EMAIL', 'support@skbank.com', 'Customer Support Email'),
(4, 'SUPPORT_PHONE', '1800-123-SKBANK', 'Toll Free Support Line'),
(5, 'DEFAULT_BRANCH_ID', '1', 'Main Branch ID'),
(6, 'DEFAULT_ACCOUNT_TYPE_ID', '1', 'Default Savings Account Type'),
(7, 'CURRENCY', 'INR', 'Base Currency'),
(8, 'PROFILE_IMAGE_MAX_MB', '5', 'Max Profile Image Upload Size in MB'),
(9, 'MAX_TRANSFER_AMOUNT', '500000.00', 'Maximum single transfer limit'),
(10, 'DAILY_TRANSFER_LIMIT', '1000000.00', 'Daily aggregated transfer limit'),
(11, 'MAX_WITHDRAWAL_AMOUNT', '50000.00', 'Maximum single withdrawal limit'),
(12, 'DAILY_WITHDRAWAL_LIMIT', '100000.00', 'Daily aggregated withdrawal limit'),
(13, 'MAX_UPI_TRANSACTION_AMOUNT', '100000.00', 'Maximum single UPI transfer limit'),
(14, 'OTP_EXPIRY_MINUTES', '5', 'OTP validity in minutes'),
(15, 'SESSION_TIMEOUT_MINUTES', '30', 'Session timeout duration in minutes');

INSERT IGNORE INTO `branches` (`branch_id`, `branch_code`, `branch_name`, `address`, `city`, `state`, `pincode`, `ifsc_code`, `phone`, `email`) VALUES
(1, 'SKB001', 'Main Branch Bareilly', 'Civil Lines, Near Cantonment', 'Bareilly', 'Uttar Pradesh', '243001', 'SKBK0000001', '0581-2550001', 'main.bareilly@skbank.com'),
(2, 'SKB002', 'Izzatnagar Branch', 'Near Railway Station, Izzatnagar', 'Bareilly', 'Uttar Pradesh', '243122', 'SKBK0000002', '0581-2550002', 'izzatnagar@skbank.com'),
(3, 'SKB003', 'Rajendra Nagar Branch', 'Block B, Rajendra Nagar', 'Bareilly', 'Uttar Pradesh', '243122', 'SKBK0000003', '0581-2550003', 'rajendra.nagar@skbank.com'),
(4, 'SKB004', 'Noida Cyber Branch', 'Sector 62, Electronic City', 'Noida', 'Uttar Pradesh', '201309', 'SKBK0000004', '0120-2550004', 'noida@skbank.com');

INSERT IGNORE INTO `account_types` (`account_type_id`, `type_code`, `type_name`, `description`, `minimum_balance`, `interest_rate`) VALUES
(1, 'SAVINGS', 'Savings Account', 'Standard personal savings account with interest', 1000.00, 4.00),
(2, 'CURRENT', 'Current Account', 'Business account for high volume transactions', 5000.00, 0.00),
(3, 'SALARY', 'Corporate Salary Account', 'Zero-balance salary account with premium benefits', 0.00, 4.50),
(4, 'PREMIUM', 'Premium Savings Account', 'High interest rate account with dedicated relationship manager', 10000.00, 5.50),
(5, 'BASIC_SAVINGS', 'Basic Savings Account (BSBD)', 'Zero-balance basic savings bank deposit account', 0.00, 3.50);

INSERT IGNORE INTO `loan_types` (`loan_type_id`, `loan_code`, `loan_name`, `description`, `interest_rate`, `min_amount`, `max_amount`, `max_tenure_months`) VALUES
(1, 'PERSONAL', 'Personal Loan', 'Quick personal loan for unexpected expenses', 10.50, 10000.00, 1000000.00, 60),
(2, 'HOME', 'Home Loan', 'Low interest loan to build or purchase your dream home', 8.25, 100000.00, 10000000.00, 240),
(3, 'VEHICLE', 'Car / Vehicle Loan', 'Drive your favourite vehicle today with easy EMIs', 8.75, 50000.00, 2500000.00, 84),
(4, 'EDUCATION', 'Education Loan', 'Empower your higher studies in India or abroad', 7.90, 25000.00, 5000000.00, 120);

INSERT IGNORE INTO `payment_providers` (`provider_id`, `provider_type`, `provider_name`, `code`) VALUES
(1, 'MOBILE_OPERATOR', 'Jio Prepaid / Postpaid', 'JIO'),
(2, 'MOBILE_OPERATOR', 'Airtel India', 'AIRTEL'),
(3, 'MOBILE_OPERATOR', 'Vi (Vodafone Idea)', 'VI'),
(4, 'MOBILE_OPERATOR', 'BSNL Prepaid', 'BSNL'),
(5, 'DTH', 'Tata Play (Tata Sky)', 'TATAPLAY'),
(6, 'DTH', 'Airtel Digital TV', 'AIRTEL_DTH'),
(7, 'DTH', 'Dish TV', 'DISHTV'),
(8, 'DTH', 'Sun Direct', 'SUNDIRECT'),
(9, 'ELECTRICITY', 'UPPCL Urban (Uttar Pradesh)', 'UPPCL_URBAN'),
(10, 'ELECTRICITY', 'UPPCL Rural (Uttar Pradesh)', 'UPPCL_RURAL'),
(11, 'ELECTRICITY', 'BSES Rajdhani Delhi', 'BSES_DELHI'),
(12, 'WATER', 'Bareilly Nagar Nigam Water', 'BAREILLY_WATER'),
(13, 'WATER', 'Delhi Jal Board', 'DELHI_WATER'),
(14, 'GAS', 'Indane Gas (LPG)', 'INDANE_GAS'),
(15, 'GAS', 'Bharat Gas', 'BHARAT_GAS'),
(16, 'GAS', 'HP Gas', 'HP_GAS'),
(17, 'BROADBAND', 'Airtel Xstream Fiber', 'AIRTEL_FIBER'),
(18, 'BROADBAND', 'JioFiber', 'JIO_FIBER'),
(19, 'FASTAG', 'NHAI FASTag SK Bank', 'SK_FASTAG'),
(20, 'INSURANCE', 'LIC India Premium', 'LIC_INDIA'),
(21, 'CREDIT_CARD', 'SK Bank Credit Card Pay', 'SK_CREDIT_CARD');

INSERT IGNORE INTO `users` (`user_id`, `username`, `password_hash`, `role`, `status`) VALUES
(1, 'superadmin', '$2a$10$wT0Xk3KqE1U6C7E2YfQe4O1N9A0M8B7C6D5E4F3G2H1I0J9K8L7M6', 'ADMIN', 'ACTIVE'),
(2, 'bankadmin', '$2a$10$wT0Xk3KqE1U6C7E2YfQe4O1N9A0M8B7C6D5E4F3G2H1I0J9K8L7M6', 'ADMIN', 'ACTIVE');

INSERT IGNORE INTO `employees` (`employee_id`, `user_id`, `branch_id`, `employee_number`, `full_name`, `designation`, `mobile`, `email`, `status`) VALUES
(1, 1, 1, 'EMP001', 'General Manager Bareilly', 'Super Admin', '9876543210', 'admin@skbank.com', 'ACTIVE'),
(2, 2, 1, 'EMP002', 'Operations Head', 'Bank Admin', '9876543211', 'ops@skbank.com', 'ACTIVE');

INSERT IGNORE INTO `admins` (`admin_id`, `user_id`, `employee_id`, `full_name`, `status`) VALUES
(1, 1, 1, 'General Manager Bareilly', 'ACTIVE'),
(2, 2, 2, 'Operations Head', 'ACTIVE');
