-- =========================================================
-- SK BANK OF BAREILLY - PRODUCTION SEED DATA
-- =========================================================

-- 1. BRANCHES
INSERT INTO branches (branch_code, branch_name, address, city, state, pincode, ifsc_code, phone, email) VALUES
('SKB001', 'SK Bank Bareilly Main Branch', '124 Civil Lines, Near Railway Station', 'Bareilly', 'Uttar Pradesh', '243001', 'SKB0002401', '0581-2401001', 'mainbranch@skbankofbareilly.example'),
('SKB002', 'SK Bank Bareilly City Branch', '45 Model Town Market, Station Road', 'Bareilly', 'Uttar Pradesh', '243005', 'SKB0002402', '0581-2401002', 'citybranch@skbankofbareilly.example');

-- 2. ACCOUNT TYPES
INSERT INTO account_types (type_name, minimum_balance, interest_rate, description, status) VALUES
('SAVINGS', 1000.00, 3.50, 'Standard savings account for individuals with attractive interest rate and zero maintenance charges.', 'ACTIVE'),
('CURRENT', 5000.00, 0.00, 'Flexible account for commercial transactions with unlimited daily deposits and withdrawals.', 'ACTIVE'),
('SALARY', 0.00, 4.00, 'Zero-balance salary account for corporate employees with complimentary insurance.', 'ACTIVE'),
('SENIOR', 500.00, 4.50, 'High-yield savings account specially designed for senior citizens aged 60+.', 'ACTIVE'),
('BUSINESS', 10000.00, 2.00, 'Comprehensive business account with multi-user authorization and POS support.', 'ACTIVE');

-- 3. LOAN TYPES
INSERT INTO loan_types (type_name, minimum_amount, maximum_amount, interest_rate, description, status) VALUES
('PERSONAL', 10000.00, 1500000.00, 10.50, 'Unsecured personal loan for personal, medical, or urgent expenses with flexible tenure.', 'ACTIVE'),
('HOME', 100000.00, 50000000.00, 8.50, 'Low-interest home loan for buying, building, or renovating property with tenure up to 30 years.', 'ACTIVE'),
('CAR', 50000.00, 5000000.00, 9.25, 'Attractive auto loan covering up to 90% road price for new and pre-owned vehicles.', 'ACTIVE'),
('EDUCATION', 25000.00, 2500000.00, 7.50, 'Education loan supporting higher studies in India and abroad with moratorium period.', 'ACTIVE'),
('BUSINESS', 100000.00, 10000000.00, 11.00, 'Commercial business expansion loan for working capital and equipment acquisition.', 'ACTIVE');

-- 4. SYSTEM SETTINGS
INSERT INTO system_settings (setting_key, setting_value, description) VALUES
('bank_name', 'SK Bank of Bareilly', 'Official Bank Name'),
('bank_ifsc_prefix', 'SKB0', 'IFSC Prefix'),
('max_daily_transfer_limit', '500000.00', 'Maximum Daily Internet Banking Transfer Limit in INR'),
('session_timeout_minutes', '30', 'Customer Internet Banking Session Timeout in Minutes'),
('failed_login_lock_attempts', '5', 'Account Lockout Threshold on Repeated Invalid Passwords');

-- 5. DEMO USERS (Passwords hashed using BCrypt)
-- Admin User (Username: admin, Password: Admin@123)
INSERT INTO users (username, password_hash, role, status) VALUES
('admin', '$2a$10$282JYUVCbBPv28NiHy7RKOzL16828tRAt0az7f37QzXjAi7ExZumi', 'ADMIN', 'ACTIVE');

INSERT INTO admins (user_id, full_name, email, phone, department) VALUES
(1, 'System Administrator', 'admin@skbankofbareilly.example', '9876500000', 'IT & Administration');

-- Employee User (Username: emp_bareilly, Password: Admin@123)
INSERT INTO users (username, password_hash, role, status) VALUES
('emp_bareilly', '$2a$10$282JYUVCbBPv28NiHy7RKOzL16828tRAt0az7f37QzXjAi7ExZumi', 'EMPLOYEE', 'ACTIVE');

INSERT INTO employees (user_id, branch_id, employee_code, full_name, designation, email, phone, hire_date, salary, status) VALUES
(2, 1, 'SKEMP1001', 'Amit Sharma', 'Branch Operations Manager', 'amit.sharma@skbankofbareilly.example', '9876511111', '2020-01-15', 65000.00, 'ACTIVE');

-- Customer User (Username: rajesh123, Password: Customer@123)
INSERT INTO users (username, password_hash, role, status) VALUES
('rajesh123', '$2a$10$DNMCSEv0EqnnCptsfwhekOH544rmT24vERWFBgD5aRmSEo.Y5tZMG', 'CUSTOMER', 'ACTIVE');

INSERT INTO customers (customer_id, user_id, branch_id, first_name, last_name, dob, gender, mobile, email, aadhaar, pan, address, city, state, pincode, occupation, kyc_status) VALUES
('SKC10001', 3, 1, 'Rajesh', 'Kumar', '1990-05-15', 'Male', '9876543210', 'rajesh.kumar@example.com', '123456789012', 'ABCDE1234F', '78 Civil Lines, Station Road', 'Bareilly', 'Uttar Pradesh', '243001', 'Business', 'VERIFIED');

-- Customer Account
INSERT INTO accounts (account_number, customer_id, type_id, branch_id, balance, status) VALUES
('SKB24010000001', 'SKC10001', 1, 1, 125000.50, 'ACTIVE');

-- Nominee
INSERT INTO nominees (customer_id, account_id, nominee_name, relationship, dob, mobile, address, share_percentage) VALUES
('SKC10001', 1, 'Sunita Kumar', 'Spouse', '1992-08-20', '9876543211', '78 Civil Lines, Bareilly', 100.00);

-- Transactions
INSERT INTO transactions (transaction_reference, account_id, type, direction, amount, balance_before, balance_after, sender_account, receiver_account, description, status, transaction_date) VALUES
('SKTXN202609291001', 1, 'DEPOSIT', 'CREDIT', 150000.00, 0.00, 150000.00, 'CASH_COUNTER', 'SKB24010000001', 'Initial Account Opening Deposit', 'SUCCESS', NOW() - INTERVAL 10 DAY),
('SKTXN202609291002', 1, 'WITHDRAWAL', 'DEBIT', 5000.00, 150000.00, 145000.00, 'SKB24010000001', 'ATM_COUNTER', 'ATM Cash Withdrawal', 'SUCCESS', NOW() - INTERVAL 5 DAY),
('SKTXN202609291003', 1, 'TRANSFER', 'DEBIT', 19999.50, 145000.00, 125000.50, 'SKB24010000001', 'SKB24010000002', 'Fund Transfer via IMPS to Beneficiary', 'SUCCESS', NOW() - INTERVAL 2 DAY);

-- Cards
INSERT INTO cards (card_number, customer_id, account_id, card_holder_name, card_type, expiry_date, cvv_hash, pin_hash, daily_limit, status) VALUES
('4532890123456789', 'SKC10001', 1, 'RAJESH KUMAR', 'DEBIT', '12/28', '$2a$10$DNMCSEv0EqnnCptsfwhekOH544rmT24vERWFBgD5aRmSEo.Y5tZMG', '$2a$10$DNMCSEv0EqnnCptsfwhekOH544rmT24vERWFBgD5aRmSEo.Y5tZMG', 50000.00, 'ACTIVE');

-- Fixed Deposit
INSERT INTO fixed_deposits (fd_number, customer_id, account_id, principal_amount, interest_rate, tenure_months, start_date, maturity_date, interest_amount, maturity_amount, status) VALUES
('SKFD2026001', 'SKC10001', 1, 50000.00, 6.75, 12, CURDATE(), CURDATE() + INTERVAL 1 YEAR, 3375.00, 53375.00, 'ACTIVE');

-- Loan
INSERT INTO loans (loan_number, customer_id, account_id, loan_type_id, requested_amount, approved_amount, interest_rate, tenure_months, emi_amount, total_interest, total_payable, outstanding_amount, purpose, status, application_date, approval_date) VALUES
('SKL2026001', 'SKC10001', 1, 1, 200000.00, 200000.00, 10.50, 24, 9276.00, 22624.00, 222624.00, 200000.00, 'Home Renovation & Furnishing', 'APPROVED', CURDATE() - INTERVAL 1 MONTH, CURDATE() - INTERVAL 25 DAY);

-- Notifications
INSERT INTO notifications (user_id, title, message, notification_type) VALUES
(3, 'Welcome to SK Bank of Bareilly', 'Your Savings Account SKB24010000001 is active and ready for digital transactions.', 'GENERAL'),
(3, 'KYC Status: Verified', 'Your identity documents have been verified by the bank administration.', 'KYC');

