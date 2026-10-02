# SK BANK OF BAREILLY - REST API SPECIFICATION

**Application Name:** SK Bank of Bareilly Mobile Customer Channel  
**Base URL:** `https://sk-bank-of-bareilly-com.onrender.com/` (or `http://<host>:8080/sk-bank-of-bareilly/`)  
**Authorization:** `Authorization: Bearer <TOKEN>`

---

## 1. Authentication APIs

### 1.1 Customer Login
* **URL:** `/api/customer/auth/login`
* **Method:** `POST`
* **Body:**
  ```json
  {
    "username": "customer123",
    "password": "Password123!"
  }
  ```
* **Response:**
  ```json
  {
    "success": true,
    "message": "Credentials verified. Enter OTP.",
    "data": {
      "requiresOtp": true,
      "userId": 10,
      "message": "OTP dispatched to registered mobile."
    }
  }
  ```

### 1.2 Verify OTP & Token Issue
* **URL:** `/api/customer/auth/verify-otp`
* **Method:** `POST`
* **Body:**
  ```json
  {
    "otp": "123456",
    "purpose": "API_CUSTOMER_LOGIN"
  }
  ```
* **Response:**
  ```json
  {
    "success": true,
    "message": "Login successful",
    "data": {
      "requiresOtp": false,
      "token": "SKM_9f8e7d6c5b4a3a1_1727891234",
      "userId": 10,
      "customerId": 5,
      "customerNumber": "SKC10023456",
      "customerName": "Sajid Khan",
      "role": "CUSTOMER"
    }
  }
  ```

---

## 2. Customer Banking APIs

### 2.1 Dashboard Summary
* **URL:** `/api/customer/dashboard`
* **Method:** `GET`
* **Headers:** `Authorization: Bearer <TOKEN>`
* **Response:** Returns aggregated balances, active loans, FD investments, linked accounts, and 5 recent transactions.

### 2.2 List Accounts
* **URL:** `/api/customer/accounts`
* **Method:** `GET`
* **Headers:** `Authorization: Bearer <TOKEN>`

### 2.3 Send Money / Fund Transfer
* **URL:** `/api/customer/transfer`
* **Method:** `POST`
* **Body:**
  ```json
  {
    "senderAccountId": 1,
    "receiverAccountId": 2,
    "amount": 2500.00,
    "transferType": "ACCOUNT",
    "remarks": "Rent Payment"
  }
  ```

### 2.4 Internal Cash Withdrawal
* **URL:** `/api/customer/withdraw`
* **Method:** `POST`
* **Body:**
  ```json
  {
    "accountId": 1,
    "amount": 1000.00,
    "description": "Mobile Withdrawal"
  }
  ```

---

## 3. Payment Bank APIs

### 3.1 Payment Wallet Balance
* **URL:** `/api/payment-bank/balance`
* **Method:** `GET`

### 3.2 Process Payment (QR, Recharges, Utility Bills, FASTag)
* **URL:** `/api/payment-bank/pay`
* **Method:** `POST`
* **Headers:**
  * `Authorization: Bearer <TOKEN>`
  * `X-Idempotency-Key: IDEM_123456789_UUID`
* **Body:**
  ```json
  {
    "sourceAccountId": 1,
    "paymentType": "RECHARGE",
    "providerCode": "JIO",
    "mobileNumber": "9876543210",
    "amount": 299.00,
    "remarks": "Monthly Recharge"
  }
  ```

---

## 4. Error Code Standard

| Error Code | HTTP Status | Description |
| :--- | :--- | :--- |
| `AUTH_REQUIRED` | 401 | Missing or malformed Authorization header |
| `SESSION_EXPIRED` | 401 | Invalid or expired bearer token |
| `INSUFFICIENT_BALANCE` | 400 | Account or wallet balance insufficient |
| `LIMIT_EXCEEDED` | 400 | Transaction amount exceeds system limits |
| `DUPLICATE_REQUEST` | 400 | Idempotency key already processed |
