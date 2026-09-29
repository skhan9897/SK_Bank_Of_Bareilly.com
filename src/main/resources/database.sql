-- =========================================================
-- SK BANK OF BAREILLY - DATABASE SCHEMA & INITIAL DATA
-- Clever Cloud Database: bjkcueu7xmg0w4f52r7x
-- =========================================================

-- Disable Foreign Key Checks during setup
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS audit_logs;
DROP TABLE IF EXISTS complaint_messages;
DROP TABLE IF EXISTS complaints;
DROP TABLE IF EXISTS notifications;
DROP TABLE IF EXISTS kyc_documents;
DROP TABLE IF EXISTS bill_payments;
DROP TABLE IF EXISTS card_transactions;
DROP TABLE IF EXISTS cards;
DROP TABLE IF EXISTS fixed_deposits;
DROP TABLE IF EXISTS loan_payments;
DROP TABLE IF EXISTS loans;
DROP TABLE IF EXISTS beneficiaries;
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS accounts;
DROP TABLE IF EXISTS account_types;
DROP TABLE IF EXISTS customers;
DROP TABLE IF EXISTS admins;
DROP TABLE IF EXISTS employees;
DROP TABLE IF EXISTS branches;
DROP TABLE IF EXISTS users;

SET FOREIGN_KEY_CHECKS = 1;

-- 1. USERS TABLE
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('CUSTOMER', 'EMPLOYEE', 'ADMIN') NOT NULL DEFAULT 'CUSTOMER',
    status ENUM('ACTIVE', 'BLOCKED', 'PENDING') NOT NULL DEFAULT 'ACTIVE',
    failed_attempts INT DEFAULT 0,
    last_login TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 2. BRANCHES TABLE
CREATE TABLE branches (
    branch_id INT AUTO_INCREMENT PRIMARY KEY,
    branch_code VARCHAR(20) NOT NULL UNIQUE,
    branch_name VARCHAR(100) NOT NULL,
    ifsc VARCHAR(11) NOT NULL UNIQUE,
    address TEXT NOT NULL,
    city VARCHAR(50) NOT NULL,
    state VARCHAR(50) NOT NULL
) ENGINE=InnoDB;

-- 3. ADMINS TABLE
CREATE TABLE admins (
    admin_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 4. CUSTOMERS TABLE
CREATE TABLE customers (
    customer_id VARCHAR(20) PRIMARY KEY, -- e.g., SKC10001
    user_id INT NOT NULL UNIQUE,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    dob DATE NOT NULL,
    gender ENUM('Male', 'Female', 'Other') NOT NULL,
    mobile VARCHAR(15) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    aadhaar VARCHAR(12) NOT NULL UNIQUE,
    pan VARCHAR(10) NOT NULL UNIQUE,
    address TEXT NOT NULL,
    city VARCHAR(50) NOT NULL,
    state VARCHAR(50) NOT NULL,
    pincode VARCHAR(10) NOT NULL,
    occupation VARCHAR(50) NOT NULL,
    kyc_status ENUM('PENDING', 'VERIFIED', 'REJECTED') NOT NULL DEFAULT 'PENDING',
    profile_photo VARCHAR(255) DEFAULT 'default-avatar.png',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 5. ACCOUNT TYPES TABLE
CREATE TABLE account_types (
    type_id INT AUTO_INCREMENT PRIMARY KEY,
    type_name VARCHAR(50) NOT NULL UNIQUE,
    min_balance DECIMAL(12,2) NOT NULL DEFAULT 1000.00,
    interest_rate DECIMAL(5,2) NOT NULL DEFAULT 3.50,
    description TEXT
) ENGINE=InnoDB;

-- 6. ACCOUNTS TABLE
CREATE TABLE accounts (
    account_id INT AUTO_INCREMENT PRIMARY KEY,
    account_number VARCHAR(20) NOT NULL UNIQUE, -- e.g. SKB24010000001
    customer_id VARCHAR(20) NOT NULL,
    type_id INT NOT NULL,
    branch_id INT NOT NULL,
    balance DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    status ENUM('ACTIVE', 'BLOCKED', 'CLOSED') NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE,
    FOREIGN KEY (type_id) REFERENCES account_types(type_id),
    FOREIGN KEY (branch_id) REFERENCES branches(branch_id)
) ENGINE=InnoDB;

-- 7. TRANSACTIONS TABLE
CREATE TABLE transactions (
    transaction_id VARCHAR(30) PRIMARY KEY, -- e.g. SKTXN202609291234
    account_id INT NOT NULL,
    type ENUM('DEPOSIT', 'WITHDRAWAL', 'TRANSFER', 'NEFT', 'RTGS', 'IMPS', 'UPI', 'BILL_PAYMENT', 'CARD_PAYMENT', 'LOAN_EMI', 'FD_INVESTMENT', 'INTEREST_CREDIT') NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    balance_after DECIMAL(15,2) NOT NULL,
    reference_number VARCHAR(50),
    description TEXT,
    status ENUM('SUCCESS', 'PENDING', 'FAILED', 'REVERSED') NOT NULL DEFAULT 'SUCCESS',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES accounts(account_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 8. BENEFICIARIES TABLE
CREATE TABLE beneficiaries (
    beneficiary_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id VARCHAR(20) NOT NULL,
    name VARCHAR(100) NOT NULL,
    account_number VARCHAR(20) NOT NULL,
    ifsc VARCHAR(11) NOT NULL,
    bank_name VARCHAR(100) NOT NULL,
    nickname VARCHAR(50),
    status ENUM('PENDING', 'ACTIVE') NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 9. LOANS TABLE
CREATE TABLE loans (
    loan_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id VARCHAR(20) NOT NULL,
    loan_type ENUM('Personal Loan', 'Home Loan', 'Car Loan', 'Education Loan', 'Business Loan') NOT NULL,
    principal_amount DECIMAL(15,2) NOT NULL,
    interest_rate DECIMAL(5,2) NOT NULL,
    tenure_months INT NOT NULL,
    monthly_emi DECIMAL(12,2) NOT NULL,
    outstanding_amount DECIMAL(15,2) NOT NULL,
    purpose TEXT,
    monthly_income DECIMAL(12,2) NOT NULL,
    employment_type VARCHAR(50) NOT NULL,
    status ENUM('APPLIED', 'UNDER_REVIEW', 'APPROVED', 'REJECTED', 'ACTIVE', 'CLOSED') NOT NULL DEFAULT 'APPLIED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 10. FIXED DEPOSITS TABLE
CREATE TABLE fixed_deposits (
    fd_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id VARCHAR(20) NOT NULL,
    account_id INT NOT NULL,
    receipt_number VARCHAR(30) NOT NULL UNIQUE,
    deposit_amount DECIMAL(15,2) NOT NULL,
    interest_rate DECIMAL(5,2) NOT NULL,
    tenure_months INT NOT NULL,
    maturity_amount DECIMAL(15,2) NOT NULL,
    maturity_date DATE NOT NULL,
    status ENUM('ACTIVE', 'CLOSED', 'MATURED') NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE,
    FOREIGN KEY (account_id) REFERENCES accounts(account_id)
) ENGINE=InnoDB;

-- 11. CARDS TABLE
CREATE TABLE cards (
    card_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id VARCHAR(20) NOT NULL,
    account_id INT NOT NULL,
    card_number VARCHAR(16) NOT NULL UNIQUE,
    card_holder_name VARCHAR(100) NOT NULL,
    card_type ENUM('DEBIT', 'CREDIT') NOT NULL,
    expiry_date VARCHAR(7) NOT NULL, -- MM/YYYY
    cvv_hash VARCHAR(255) NOT NULL,
    pin_hash VARCHAR(255) NOT NULL,
    daily_limit DECIMAL(12,2) DEFAULT 50000.00,
    credit_limit DECIMAL(12,2) DEFAULT 0.00,
    available_limit DECIMAL(12,2) DEFAULT 0.00,
    outstanding DECIMAL(12,2) DEFAULT 0.00,
    status ENUM('ACTIVE', 'BLOCKED') NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE,
    FOREIGN KEY (account_id) REFERENCES accounts(account_id)
) ENGINE=InnoDB;

-- 12. BILL PAYMENTS TABLE
CREATE TABLE bill_payments (
    bill_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id VARCHAR(20) NOT NULL,
    account_id INT NOT NULL,
    bill_category ENUM('Electricity', 'Water', 'Gas', 'Mobile Recharge', 'DTH', 'Internet', 'Insurance', 'Credit Card Bill') NOT NULL,
    provider VARCHAR(100) NOT NULL,
    consumer_number VARCHAR(50) NOT NULL,
    amount DECIMAL(12,2) NOT NULL,
    reference_number VARCHAR(50) NOT NULL UNIQUE,
    status ENUM('SUCCESS', 'FAILED') NOT NULL DEFAULT 'SUCCESS',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE,
    FOREIGN KEY (account_id) REFERENCES accounts(account_id)
) ENGINE=InnoDB;

-- 13. KYC DOCUMENTS TABLE
CREATE TABLE kyc_documents (
    kyc_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id VARCHAR(20) NOT NULL,
    document_type ENUM('Aadhaar', 'PAN', 'Address Proof', 'Photo') NOT NULL,
    file_path VARCHAR(255) NOT NULL,
    status ENUM('PENDING', 'VERIFIED', 'REJECTED') NOT NULL DEFAULT 'PENDING',
    rejection_reason TEXT,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 14. NOTIFICATIONS TABLE
CREATE TABLE notifications (
    notification_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    title VARCHAR(150) NOT NULL,
    message TEXT NOT NULL,
    type VARCHAR(50) DEFAULT 'GENERAL',
    is_read TINYINT(1) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 15. COMPLAINTS TABLE
CREATE TABLE complaints (
    complaint_id VARCHAR(30) PRIMARY KEY, -- e.g. SKCMP20260929001
    customer_id VARCHAR(20) NOT NULL,
    subject VARCHAR(200) NOT NULL,
    category VARCHAR(50) NOT NULL,
    description TEXT NOT NULL,
    attachment_path VARCHAR(255),
    status ENUM('OPEN', 'IN_PROGRESS', 'RESOLVED', 'CLOSED') NOT NULL DEFAULT 'OPEN',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 16. AUDIT LOGS TABLE
CREATE TABLE audit_logs (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    action VARCHAR(100) NOT NULL,
    ip_address VARCHAR(50),
    details TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- =========================================================
-- SEED DATA
-- =========================================================

-- Branches
INSERT INTO branches (branch_code, branch_name, ifsc, address, city, state) VALUES
('BR001', 'Bareilly Civil Lines Branch', 'SKB0002401', '124 Civil Lines, Near Railway Station', 'Bareilly', 'Uttar Pradesh'),
('BR002', 'Bareilly Model Town Branch', 'SKB0002402', '45 Model Town Market', 'Bareilly', 'Uttar Pradesh');

-- Account Types
INSERT INTO account_types (type_name, min_balance, interest_rate, description) VALUES
('Savings Account', 1000.00, 3.50, 'Standard savings account for individuals with attractive interest rate.'),
('Current Account', 5000.00, 0.00, 'Flexible account for business transactions with zero maintenance charge.'),
('Salary Account', 0.00, 4.00, 'Zero balance salary account with complementary insurance coverage.'),
('Senior Citizen Account', 500.00, 4.50, 'Higher interest savings account tailored for senior citizens.'),
('Business Account', 10000.00, 2.00, 'Comprehensive business account with high daily transaction limit.');

-- Passwords hashed using BCrypt (Password for admin: Admin@123, customer: Customer@123)
-- Admin User (username: admin, password: Admin@123)
INSERT INTO users (username, password_hash, role, status) VALUES
('admin', '$2a$10$e8T7O2jXf2R9q8S.H1wZ..DqTq2wM0A5X2L5P6K7Q8R9S0T1U2V3W', 'ADMIN', 'ACTIVE');

INSERT INTO admins (user_id, full_name, email) VALUES
(1, 'System Administrator', 'admin@skbankofbareilly.example');

-- Sample Customer User (username: rajesh123, password: Customer@123)
INSERT INTO users (username, password_hash, role, status) VALUES
('rajesh123', '$2a$10$e8T7O2jXf2R9q8S.H1wZ..DqTq2wM0A5X2L5P6K7Q8R9S0T1U2V3W', 'CUSTOMER', 'ACTIVE');

INSERT INTO customers (customer_id, user_id, first_name, last_name, dob, gender, mobile, email, aadhaar, pan, address, city, state, pincode, occupation, kyc_status) VALUES
('SKC10001', 2, 'Rajesh', 'Kumar', '1990-05-15', 'Male', '9876543210', 'rajesh.kumar@example.com', '123456789012', 'ABCDE1234F', '78 Station Road', 'Bareilly', 'Uttar Pradesh', '243001', 'Business', 'VERIFIED');

-- Sample Customer Account
INSERT INTO accounts (account_number, customer_id, type_id, branch_id, balance, status) VALUES
('SKB24010000001', 'SKC10001', 1, 1, 125000.50, 'ACTIVE');

-- Sample Transactions
INSERT INTO transactions (transaction_id, account_id, type, amount, balance_after, reference_number, description, status, created_at) VALUES
('SKTXN202609291001', 1, 'DEPOSIT', 150000.00, 150000.00, 'REF982310', 'Initial Account Deposit', 'SUCCESS', NOW() - INTERVAL 10 DAY),
('SKTXN202609291002', 1, 'WITHDRAWAL', 5000.00, 145000.00, 'REF982311', 'ATM Cash Withdrawal', 'SUCCESS', NOW() - INTERVAL 5 DAY),
('SKTXN202609291003', 1, 'TRANSFER', 19999.50, 125000.50, 'REF982312', 'Fund Transfer to Beneficiary', 'SUCCESS', NOW() - INTERVAL 2 DAY);

-- Sample Card for Customer
INSERT INTO cards (customer_id, account_id, card_number, card_holder_name, card_type, expiry_date, cvv_hash, pin_hash, status, daily_limit) VALUES
('SKC10001', 1, '4532890123456789', 'RAJESH KUMAR', 'DEBIT', '12/28', '$2a$10$e8T7O2jXf2R9q8S.H1wZ..DqTq2wM0A5X2L5P6K7Q8R9S0T1U2V3W', '$2a$10$e8T7O2jXf2R9q8S.H1wZ..DqTq2wM0A5X2L5P6K7Q8R9S0T1U2V3W', 'ACTIVE', 50000.00);

-- Sample Fixed Deposit
INSERT INTO fixed_deposits (customer_id, account_id, receipt_number, deposit_amount, interest_rate, tenure_months, maturity_amount, maturity_date, status) VALUES
('SKC10001', 1, 'SKFD2026001', 50000.00, 6.75, 12, 53375.00, CURDATE() + INTERVAL 1 YEAR, 'ACTIVE');

-- Sample Loan
INSERT INTO loans (customer_id, loan_type, principal_amount, interest_rate, tenure_months, monthly_emi, outstanding_amount, purpose, monthly_income, employment_type, status) VALUES
('SKC10001', 'Personal Loan', 200000.00, 10.50, 24, 9276.00, 200000.00, 'Home Renovation', 75000.00, 'Salaried', 'APPROVED');

-- Sample Notification
INSERT INTO notifications (user_id, title, message, type) VALUES
(2, 'Welcome to SK Bank of Bareilly', 'Your account SKB24010000001 has been activated successfully.', 'SYSTEM'),
(2, 'KYC Verified', 'Your KYC documents have been verified by the bank admin.', 'KYC');

