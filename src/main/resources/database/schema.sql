-- =========================================================
-- SK BANK OF BAREILLY - COMPLETE PRODUCTION DATABASE SCHEMA
-- Target Engine: MySQL 8.0 / Clever Cloud MySQL
-- =========================================================

SET FOREIGN_KEY_CHECKS = 0;

DROP VIEW IF EXISTS fd_summary;
DROP VIEW IF EXISTS loan_summary;
DROP VIEW IF EXISTS transaction_summary;
DROP VIEW IF EXISTS customer_account_summary;

DROP TABLE IF EXISTS system_settings;
DROP TABLE IF EXISTS otp_verifications;
DROP TABLE IF EXISTS password_reset_tokens;
DROP TABLE IF EXISTS audit_logs;
DROP TABLE IF EXISTS complaint_messages;
DROP TABLE IF EXISTS complaints;
DROP TABLE IF EXISTS notifications;
DROP TABLE IF EXISTS kyc_documents;
DROP TABLE IF EXISTS bill_payments;
DROP TABLE IF EXISTS card_transactions;
DROP TABLE IF EXISTS cards;
DROP TABLE IF EXISTS loan_payments;
DROP TABLE IF EXISTS loans;
DROP TABLE IF EXISTS loan_types;
DROP TABLE IF EXISTS fixed_deposits;
DROP TABLE IF EXISTS transfer_requests;
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS beneficiaries;
DROP TABLE IF EXISTS nominees;
DROP TABLE IF EXISTS accounts;
DROP TABLE IF EXISTS account_types;
DROP TABLE IF EXISTS branches;
DROP TABLE IF EXISTS customers;
DROP TABLE IF EXISTS employees;
DROP TABLE IF EXISTS admins;
DROP TABLE IF EXISTS users;

SET FOREIGN_KEY_CHECKS = 1;

-- 1. USERS
CREATE TABLE users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('CUSTOMER', 'EMPLOYEE', 'ADMIN') NOT NULL DEFAULT 'CUSTOMER',
    status ENUM('ACTIVE', 'BLOCKED', 'PENDING', 'DEACTIVATED') NOT NULL DEFAULT 'ACTIVE',
    failed_attempts INT NOT NULL DEFAULT 0,
    last_login DATETIME NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 2. ADMINS
CREATE TABLE admins (
    admin_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL,
    department VARCHAR(50) DEFAULT 'GENERAL',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 3. BRANCHES
CREATE TABLE branches (
    branch_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    branch_code VARCHAR(20) NOT NULL UNIQUE,
    branch_name VARCHAR(100) NOT NULL,
    address TEXT NOT NULL,
    city VARCHAR(50) NOT NULL,
    state VARCHAR(50) NOT NULL,
    pincode VARCHAR(10) NOT NULL,
    ifsc_code VARCHAR(11) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 4. EMPLOYEES
CREATE TABLE employees (
    employee_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    branch_id BIGINT NOT NULL,
    employee_code VARCHAR(30) NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    designation VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL,
    hire_date DATE NOT NULL,
    salary DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (branch_id) REFERENCES branches(branch_id)
) ENGINE=InnoDB;

-- 5. CUSTOMERS
CREATE TABLE customers (
    customer_id VARCHAR(20) PRIMARY KEY, -- e.g. SKC10001
    user_id BIGINT NOT NULL UNIQUE,
    branch_id BIGINT NOT NULL DEFAULT 1,
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
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (branch_id) REFERENCES branches(branch_id)
) ENGINE=InnoDB;

-- 6. ACCOUNT TYPES
CREATE TABLE account_types (
    type_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    type_name VARCHAR(50) NOT NULL UNIQUE, -- SAVINGS, CURRENT, SALARY, SENIOR, BUSINESS
    minimum_balance DECIMAL(18,2) NOT NULL DEFAULT 1000.00,
    interest_rate DECIMAL(5,2) NOT NULL DEFAULT 3.50,
    description TEXT,
    status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 7. ACCOUNTS
CREATE TABLE accounts (
    account_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_number VARCHAR(30) NOT NULL UNIQUE, -- e.g. SKB24010000001
    customer_id VARCHAR(20) NOT NULL,
    type_id BIGINT NOT NULL,
    branch_id BIGINT NOT NULL,
    balance DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    status ENUM('ACTIVE', 'BLOCKED', 'CLOSED') NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE,
    FOREIGN KEY (type_id) REFERENCES account_types(type_id),
    FOREIGN KEY (branch_id) REFERENCES branches(branch_id),
    CONSTRAINT chk_account_balance CHECK (balance >= 0.00)
) ENGINE=InnoDB;

-- 8. NOMINEES
CREATE TABLE nominees (
    nominee_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id VARCHAR(20) NOT NULL,
    account_id BIGINT NOT NULL,
    nominee_name VARCHAR(100) NOT NULL,
    relationship VARCHAR(50) NOT NULL,
    dob DATE NOT NULL,
    mobile VARCHAR(15),
    address TEXT,
    share_percentage DECIMAL(5,2) DEFAULT 100.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE,
    FOREIGN KEY (account_id) REFERENCES accounts(account_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 9. BENEFICIARIES
CREATE TABLE beneficiaries (
    beneficiary_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id VARCHAR(20) NOT NULL,
    name VARCHAR(100) NOT NULL,
    account_number VARCHAR(30) NOT NULL,
    ifsc VARCHAR(11) NOT NULL,
    bank_name VARCHAR(100) NOT NULL,
    nickname VARCHAR(50),
    status ENUM('PENDING', 'ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 10. TRANSACTIONS
CREATE TABLE transactions (
    transaction_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    transaction_reference VARCHAR(50) NOT NULL UNIQUE, -- e.g. SKTXN202609291234
    account_id BIGINT NOT NULL,
    type ENUM('DEPOSIT', 'WITHDRAWAL', 'TRANSFER', 'NEFT', 'RTGS', 'IMPS', 'UPI', 'BILL_PAYMENT', 'CARD_PAYMENT', 'LOAN_EMI', 'FD_INVESTMENT', 'INTEREST_CREDIT', 'REFUND') NOT NULL,
    direction ENUM('CREDIT', 'DEBIT') NOT NULL,
    amount DECIMAL(18,2) NOT NULL,
    balance_before DECIMAL(18,2) NOT NULL,
    balance_after DECIMAL(18,2) NOT NULL,
    sender_account VARCHAR(30),
    receiver_account VARCHAR(30),
    description TEXT,
    status ENUM('SUCCESS', 'PENDING', 'FAILED', 'REVERSED') NOT NULL DEFAULT 'SUCCESS',
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES accounts(account_id),
    CONSTRAINT chk_txn_amount CHECK (amount > 0.00)
) ENGINE=InnoDB;

-- 11. TRANSFER REQUESTS
CREATE TABLE transfer_requests (
    transfer_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id VARCHAR(20) NOT NULL,
    sender_account_id BIGINT NOT NULL,
    receiver_account_number VARCHAR(30) NOT NULL,
    receiver_ifsc VARCHAR(11) NOT NULL,
    receiver_name VARCHAR(100) NOT NULL,
    transfer_type ENUM('INTERNAL', 'NEFT', 'RTGS', 'IMPS', 'UPI') NOT NULL,
    amount DECIMAL(18,2) NOT NULL,
    remarks VARCHAR(200),
    status ENUM('PENDING', 'APPROVED', 'REJECTED', 'PROCESSED') NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE,
    FOREIGN KEY (sender_account_id) REFERENCES accounts(account_id)
) ENGINE=InnoDB;

-- 12. FIXED DEPOSITS
CREATE TABLE fixed_deposits (
    fd_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    fd_number VARCHAR(30) NOT NULL UNIQUE, -- e.g. SKFD2026001
    customer_id VARCHAR(20) NOT NULL,
    account_id BIGINT NOT NULL,
    principal_amount DECIMAL(18,2) NOT NULL,
    interest_rate DECIMAL(5,2) NOT NULL,
    tenure_months INT NOT NULL,
    start_date DATE NOT NULL,
    maturity_date DATE NOT NULL,
    interest_amount DECIMAL(18,2) NOT NULL,
    maturity_amount DECIMAL(18,2) NOT NULL,
    status ENUM('ACTIVE', 'MATURED', 'CLOSED', 'PREMATURE_CLOSED') NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE,
    FOREIGN KEY (account_id) REFERENCES accounts(account_id)
) ENGINE=InnoDB;

-- 13. LOAN TYPES
CREATE TABLE loan_types (
    loan_type_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    type_name VARCHAR(50) NOT NULL UNIQUE, -- PERSONAL, HOME, CAR, EDUCATION, BUSINESS
    minimum_amount DECIMAL(18,2) NOT NULL DEFAULT 10000.00,
    maximum_amount DECIMAL(18,2) NOT NULL DEFAULT 50000000.00,
    interest_rate DECIMAL(5,2) NOT NULL DEFAULT 10.50,
    description TEXT,
    status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 14. LOANS
CREATE TABLE loans (
    loan_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    loan_number VARCHAR(30) NOT NULL UNIQUE, -- e.g. SKL2026001
    customer_id VARCHAR(20) NOT NULL,
    account_id BIGINT NOT NULL,
    loan_type_id BIGINT NOT NULL,
    requested_amount DECIMAL(18,2) NOT NULL,
    approved_amount DECIMAL(18,2) NOT NULL,
    interest_rate DECIMAL(5,2) NOT NULL,
    tenure_months INT NOT NULL,
    emi_amount DECIMAL(18,2) NOT NULL,
    total_interest DECIMAL(18,2) NOT NULL,
    total_payable DECIMAL(18,2) NOT NULL,
    outstanding_amount DECIMAL(18,2) NOT NULL,
    purpose TEXT NOT NULL,
    status ENUM('APPLIED', 'UNDER_REVIEW', 'APPROVED', 'REJECTED', 'ACTIVE', 'CLOSED') NOT NULL DEFAULT 'APPLIED',
    application_date DATE NOT NULL,
    approval_date DATE NULL,
    rejection_reason TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE,
    FOREIGN KEY (account_id) REFERENCES accounts(account_id),
    FOREIGN KEY (loan_type_id) REFERENCES loan_types(loan_type_id)
) ENGINE=InnoDB;

-- 15. LOAN PAYMENTS (EMI)
CREATE TABLE loan_payments (
    payment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    loan_id BIGINT NOT NULL,
    installment_number INT NOT NULL,
    due_date DATE NOT NULL,
    payment_date DATETIME NULL,
    principal_amount DECIMAL(18,2) NOT NULL,
    interest_amount DECIMAL(18,2) NOT NULL,
    emi_amount DECIMAL(18,2) NOT NULL,
    outstanding_after_payment DECIMAL(18,2) NOT NULL,
    status ENUM('PENDING', 'PAID', 'OVERDUE', 'FAILED') NOT NULL DEFAULT 'PENDING',
    transaction_id BIGINT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (loan_id) REFERENCES loans(loan_id) ON DELETE CASCADE,
    FOREIGN KEY (transaction_id) REFERENCES transactions(transaction_id)
) ENGINE=InnoDB;

-- 16. CARDS
CREATE TABLE cards (
    card_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    card_number VARCHAR(16) NOT NULL UNIQUE,
    customer_id VARCHAR(20) NOT NULL,
    account_id BIGINT NOT NULL,
    card_holder_name VARCHAR(100) NOT NULL,
    card_type ENUM('DEBIT', 'CREDIT') NOT NULL,
    expiry_date VARCHAR(7) NOT NULL, -- MM/YYYY
    cvv_hash VARCHAR(255) NOT NULL,
    pin_hash VARCHAR(255) NOT NULL,
    daily_limit DECIMAL(18,2) NOT NULL DEFAULT 50000.00,
    credit_limit DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    available_limit DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    outstanding_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    status ENUM('ACTIVE', 'BLOCKED', 'EXPIRED', 'CANCELLED') NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE,
    FOREIGN KEY (account_id) REFERENCES accounts(account_id)
) ENGINE=InnoDB;

-- 17. CARD TRANSACTIONS
CREATE TABLE card_transactions (
    card_txn_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    card_id BIGINT NOT NULL,
    merchant_name VARCHAR(100) NOT NULL,
    amount DECIMAL(18,2) NOT NULL,
    transaction_type ENUM('POS', 'ONLINE', 'ATM_WITHDRAWAL') NOT NULL,
    status ENUM('SUCCESS', 'FAILED', 'DECLINED') NOT NULL DEFAULT 'SUCCESS',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (card_id) REFERENCES cards(card_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 18. BILL PAYMENTS
CREATE TABLE bill_payments (
    bill_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id VARCHAR(20) NOT NULL,
    account_id BIGINT NOT NULL,
    bill_category ENUM('Electricity', 'Water', 'Gas', 'Mobile Recharge', 'DTH', 'Internet', 'Insurance', 'Credit Card Bill') NOT NULL,
    provider VARCHAR(100) NOT NULL,
    consumer_number VARCHAR(50) NOT NULL,
    amount DECIMAL(18,2) NOT NULL,
    reference_number VARCHAR(50) NOT NULL UNIQUE,
    status ENUM('SUCCESS', 'FAILED') NOT NULL DEFAULT 'SUCCESS',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE,
    FOREIGN KEY (account_id) REFERENCES accounts(account_id)
) ENGINE=InnoDB;

-- 19. KYC DOCUMENTS
CREATE TABLE kyc_documents (
    kyc_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id VARCHAR(20) NOT NULL,
    document_type ENUM('AADHAAR', 'PAN', 'ADDRESS_PROOF', 'PHOTO', 'OTHER') NOT NULL,
    file_path VARCHAR(255) NOT NULL,
    status ENUM('PENDING', 'VERIFIED', 'REJECTED') NOT NULL DEFAULT 'PENDING',
    rejection_reason TEXT,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 20. NOTIFICATIONS
CREATE TABLE notifications (
    notification_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(150) NOT NULL,
    message TEXT NOT NULL,
    notification_type ENUM('TRANSACTION', 'SECURITY', 'LOAN', 'FD', 'KYC', 'GENERAL') NOT NULL DEFAULT 'GENERAL',
    is_read TINYINT(1) NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 21. COMPLAINTS
CREATE TABLE complaints (
    complaint_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    complaint_number VARCHAR(30) NOT NULL UNIQUE, -- e.g. SKCMP202600001
    customer_id VARCHAR(20) NOT NULL,
    subject VARCHAR(200) NOT NULL,
    category VARCHAR(50) NOT NULL,
    description TEXT NOT NULL,
    priority ENUM('LOW', 'MEDIUM', 'HIGH', 'URGENT') NOT NULL DEFAULT 'MEDIUM',
    status ENUM('OPEN', 'IN_PROGRESS', 'RESOLVED', 'CLOSED') NOT NULL DEFAULT 'OPEN',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 22. COMPLAINT MESSAGES
CREATE TABLE complaint_messages (
    message_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    complaint_id BIGINT NOT NULL,
    sender_id BIGINT NOT NULL,
    sender_role ENUM('CUSTOMER', 'EMPLOYEE', 'ADMIN') NOT NULL,
    message TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (complaint_id) REFERENCES complaints(complaint_id) ON DELETE CASCADE,
    FOREIGN KEY (sender_id) REFERENCES users(user_id)
) ENGINE=InnoDB;

-- 23. AUDIT LOGS
CREATE TABLE audit_logs (
    log_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NULL,
    action ENUM('LOGIN', 'LOGOUT', 'ACCOUNT_CREATED', 'ACCOUNT_BLOCKED', 'TRANSFER', 'DEPOSIT', 'WITHDRAWAL', 'LOAN_APPROVED', 'LOAN_REJECTED', 'FD_CREATED', 'KYC_APPROVED', 'KYC_REJECTED', 'PROFILE_UPDATED') NOT NULL,
    module VARCHAR(50) NOT NULL,
    description TEXT NOT NULL,
    ip_address VARCHAR(50),
    user_agent VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- 24. PASSWORD RESET TOKENS
CREATE TABLE password_reset_tokens (
    token_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token_hash VARCHAR(255) NOT NULL UNIQUE,
    expires_at DATETIME NOT NULL,
    used TINYINT(1) NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 25. OTP VERIFICATIONS
CREATE TABLE otp_verifications (
    otp_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NULL,
    mobile VARCHAR(15) NULL,
    email VARCHAR(100) NULL,
    otp_hash VARCHAR(255) NOT NULL,
    purpose ENUM('LOGIN', 'REGISTRATION', 'TRANSFER', 'BENEFICIARY', 'PASSWORD_RESET', 'PROFILE_UPDATE') NOT NULL,
    expires_at DATETIME NOT NULL,
    verified TINYINT(1) NOT NULL DEFAULT 0,
    attempts INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 26. SYSTEM SETTINGS
CREATE TABLE system_settings (
    setting_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    setting_key VARCHAR(100) NOT NULL UNIQUE,
    setting_value TEXT NOT NULL,
    description VARCHAR(255),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- =========================================================
-- INDEXES
-- =========================================================

CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_customers_user_id ON customers(user_id);
CREATE INDEX idx_accounts_customer_id ON accounts(customer_id);
CREATE INDEX idx_accounts_account_number ON accounts(account_number);
CREATE INDEX idx_transactions_account_id ON transactions(account_id);
CREATE INDEX idx_transactions_reference ON transactions(transaction_reference);
CREATE INDEX idx_transactions_date ON transactions(transaction_date);
CREATE INDEX idx_loans_customer_id ON loans(customer_id);
CREATE INDEX idx_loans_number ON loans(loan_number);
CREATE INDEX idx_fds_number ON fixed_deposits(fd_number);
CREATE INDEX idx_cards_number ON cards(card_number);
CREATE INDEX idx_complaints_number ON complaints(complaint_number);
CREATE INDEX idx_audit_user_id ON audit_logs(user_id);
CREATE INDEX idx_audit_created_at ON audit_logs(created_at);

-- =========================================================
-- VIEWS
-- =========================================================

CREATE VIEW customer_account_summary AS
SELECT
    c.customer_id,
    CONCAT(c.first_name, ' ', c.last_name) AS customer_name,
    c.mobile,
    c.email,
    c.kyc_status,
    COUNT(a.account_id) AS total_accounts,
    COALESCE(SUM(a.balance), 0.00) AS total_balance
FROM customers c
LEFT JOIN accounts a ON c.customer_id = a.customer_id AND a.status = 'ACTIVE'
GROUP BY c.customer_id, c.first_name, c.last_name, c.mobile, c.email, c.kyc_status;

CREATE VIEW transaction_summary AS
SELECT
    t.transaction_id,
    t.transaction_reference,
    a.account_number,
    c.customer_id,
    CONCAT(c.first_name, ' ', c.last_name) AS customer_name,
    t.type,
    t.direction,
    t.amount,
    t.balance_after,
    t.status,
    t.transaction_date
FROM transactions t
JOIN accounts a ON t.account_id = a.account_id
JOIN customers c ON a.customer_id = c.customer_id;

CREATE VIEW loan_summary AS
SELECT
    l.loan_id,
    l.loan_number,
    c.customer_id,
    CONCAT(c.first_name, ' ', c.last_name) AS customer_name,
    lt.type_name AS loan_type,
    l.approved_amount,
    l.outstanding_amount,
    l.emi_amount,
    l.status
FROM loans l
JOIN customers c ON l.customer_id = c.customer_id
JOIN loan_types lt ON l.loan_type_id = lt.loan_type_id;

CREATE VIEW fd_summary AS
SELECT
    fd.fd_id,
    fd.fd_number,
    c.customer_id,
    CONCAT(c.first_name, ' ', c.last_name) AS customer_name,
    fd.principal_amount,
    fd.interest_rate,
    fd.tenure_months,
    fd.maturity_amount,
    fd.maturity_date,
    fd.status
FROM fixed_deposits fd
JOIN customers c ON fd.customer_id = c.customer_id;
