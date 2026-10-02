# WEBSITE ↔ ANDROID FEATURE & ARCHITECTURE MAPPING

This document details the exact end-to-end mapping between the Website UI, Backend Services, REST APIs, Android Mobile UI, and Database Tables for **SK BANK OF BAREILLY**.

---

## 1. Feature Mapping Table

| Website Feature | Backend Service | REST API Endpoint | Android Screen / Component | Database Tables |
| :--- | :--- | :--- | :--- | :--- |
| **Customer Login** | `AuthService` | `POST /api/customer/auth/login` | `LoginActivity` | `users` |
| **OTP Verification** | `AuthService`, `OtpUtil` | `POST /api/customer/auth/verify-otp` | `OtpVerificationActivity` | `users` |
| **Open Account** | `AuthService`, `AccountService` | `POST /api/customer/register` | `OpenAccountActivity` | `users`, `customers`, `accounts`, `kyc` |
| **Dashboard** | `CustomerService` | `GET /api/customer/dashboard` | `DashboardFragment` | `accounts`, `transactions`, `loans`, `fixed_deposits` |
| **My Accounts** | `AccountService` | `GET /api/customer/accounts` | `AccountsFragment` | `accounts`, `account_types`, `branches` |
| **Account Details** | `AccountService` | `GET /api/customer/account-details` | `AccountDetailsActivity` | `accounts`, `transactions` |
| **Send Money** | `TransferService` | `POST /api/customer/transfer` | `SendMoneyFragment` | `accounts`, `transactions`, `transfer_requests` |
| **Recipient Lookup** | `TransferService` | `GET /api/customer/recipient/*` | `SendMoneyFragment` | `customers`, `accounts`, `upi_accounts` |
| **Withdraw** | `TransactionService` | `POST /api/customer/withdraw` | `WithdrawFragment` | `accounts`, `transactions` |
| **Beneficiaries** | `BeneficiaryService` | `GET/POST/DELETE /api/customer/beneficiaries` | `BeneficiariesFragment` | `beneficiaries` |
| **Transactions** | `TransactionService` | `GET /api/customer/transactions` | `TransactionsFragment` | `transactions` |
| **Statements** | `TransactionService` | `GET /api/customer/statements` | `StatementsFragment` | `accounts`, `transactions` |
| **My UPI** | `UpiService` | `GET/POST /api/customer/upi` | `UpiFragment` | `upi_accounts`, `accounts` |
| **Fixed Deposits** | `FdService` | `GET/POST /api/customer/fixed-deposits` | `FixedDepositFragment` | `fixed_deposits`, `accounts`, `transactions` |
| **Loans & EMI** | `LoanService` | `GET/POST /api/customer/loans` | `LoansFragment`, `LoanApplicationActivity` | `loans`, `loan_types`, `loan_payments` |
| **Cards** | `CardService` | `GET/POST /api/customer/cards` | `CardsFragment` | `cards`, `card_transactions` |
| **Bill Payments** | `BillPaymentService` | `GET/POST /api/customer/bill-payments` | `BillPaymentFragment` | `bill_payments`, `accounts`, `transactions` |
| **KYC Details** | `CustomerService` | `GET /api/customer/kyc` | `KycFragment` | `kyc`, `customers` |
| **Notifications** | `NotificationService` | `GET/POST /api/customer/notifications` | `NotificationsFragment` | `notifications` |
| **Complaints** | `ComplaintService` | `GET/POST /api/customer/complaints` | `ComplaintsFragment` | `complaints`, `complaint_messages` |
| **Profile** | `CustomerService` | `GET/POST /api/customer/profile` | `ProfileFragment` | `customers` |
| **Security** | `AuthService` | `POST /api/customer/security` | `SecurityFragment` | `users` |
| **Payment Bank Home** | `PaymentBankService` | `GET /api/payment-bank/balance` | `PaymentsBankFragment` | `payment_wallets` |
| **Scan & Pay QR** | `PaymentBankService` | `POST /api/payment-bank/pay` | `QRScannerActivity` | `payment_transactions`, `payment_idempotency` |
| **Mobile Recharge** | `PaymentBankService` | `POST /api/payment-bank/pay` | `MobileRechargeFragment` | `payment_transactions`, `recharge_transactions` |
| **FASTag Recharge** | `PaymentBankService` | `POST /api/payment-bank/pay` | `FastagFragment` | `payment_transactions`, `fastag_accounts` |
| **Payment History** | `PaymentBankService` | `GET /api/payment-bank/history` | `PaymentHistoryFragment` | `payment_transactions` |

---

## 2. Universal Data Consistency Assurance

1. **Shared Ledger:** Both the Web Application (Servlets/JSPs) and the Android Mobile Application consume the exact same Java Service Layer (`TransferService`, `TransactionService`, `AccountService`, `PaymentBankService`).
2. **Double Spending & Idempotency:** The `X-Idempotency-Key` header ensures that duplicate network requests or retries do not trigger duplicate debits on the ledger.
3. **Strict Account Ownership:** Every REST API verifies the authenticated token, ensuring that users can only transact on accounts owned by their `customer_id`.
