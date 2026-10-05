package com.skbank.service.impl;

import com.google.gson.Gson;
import com.skbank.dao.*;
import com.skbank.dao.impl.*;
import com.skbank.dto.PaymentRequestDTO;
import com.skbank.exception.BankException;
import com.skbank.exception.InsufficientBalanceException;
import com.skbank.model.*;
import com.skbank.service.PaymentBankService;
import com.skbank.util.DatabaseConnection;
import com.skbank.util.SystemSettingsUtil;
import com.skbank.util.ValidationUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class PaymentBankServiceImpl implements PaymentBankService {

    private final PaymentWalletDAO walletDAO = new PaymentWalletDAOImpl();
    private final PaymentTransactionDAO paymentTxnDAO = new PaymentTransactionDAOImpl();
    private final PaymentProviderDAO providerDAO = new PaymentProviderDAOImpl();
    private final IdempotencyDAO idempotencyDAO = new IdempotencyDAOImpl();
    private final AccountDAO accountDAO = new AccountDAOImpl();
    private final TransactionDAO transactionDAO = new TransactionDAOImpl();
    private final NotificationDAO notificationDAO = new NotificationDAOImpl();
    private final CustomerDAO customerDAO = new CustomerDAOImpl();
    private final Gson gson = new Gson();

    @Override
    public PaymentWallet getWallet(String customerId) throws BankException {
        try {
            PaymentWallet w = walletDAO.findByCustomerId(customerId);
            if (w == null) {
                // Auto create wallet with 0.00
                w = new PaymentWallet();
                w.setCustomerId(customerId);
                w.setWalletNumber("SKW" + (1000000000L + (long)(Math.random() * 9000000000L)));
                w.setBalance(BigDecimal.ZERO);
                w.setStatus("ACTIVE");
                try (Connection conn = DatabaseConnection.getConnection()) {
                    Long id = walletDAO.create(conn, w);
                    w.setWalletId(id);
                }
            }
            return w;
        } catch (Exception e) {
            throw new BankException("Error loading wallet", e);
        }
    }

    @Override
    public PaymentTransaction processPayment(String customerId, PaymentRequestDTO request) throws BankException {
        // 1. Check Idempotency (Double Payment Protection!)
        if (request.getIdempotencyKey() != null && !request.getIdempotencyKey().trim().isEmpty()) {
            try {
                String cachedResponse = idempotencyDAO.getExistingResponse(request.getIdempotencyKey().trim());
                if (cachedResponse != null) {
                    return gson.fromJson(cachedResponse, PaymentTransaction.class);
                }
            } catch (Exception ignored) {}
        }

        BigDecimal amount = request.getAmount();
        if (!ValidationUtil.isValidAmount(amount)) {
            throw new BankException("Payment amount must be greater than zero");
        }

        BigDecimal maxLimit = SystemSettingsUtil.getSettingAsBigDecimal("MAX_PAYMENT_AMOUNT", new BigDecimal("100000.00"));
        if (amount.compareTo(maxLimit) > 0) {
            throw new BankException("Payment amount exceeds maximum limit of ₹" + maxLimit);
        }

        PaymentType type = PaymentType.valueOf(request.getPaymentType() != null ? request.getPaymentType() : "QR_PAY");

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            Account sourceAcc = accountDAO.findForUpdate(conn, request.getSourceAccountId());
            if (sourceAcc == null || !sourceAcc.getCustomerId().equals(customerId)) {
                throw new BankException("Invalid source account");
            }
            if (sourceAcc.getStatus() != AccountStatus.ACTIVE) {
                throw new BankException("Source account is not active");
            }
            if (sourceAcc.getAvailableBalance().compareTo(amount) < 0) {
                throw new InsufficientBalanceException("Insufficient balance for payment. Available: ₹" + sourceAcc.getAvailableBalance());
            }

            // Debit source account
            BigDecimal before = sourceAcc.getBalance();
            BigDecimal after = before.subtract(amount);
            accountDAO.updateBalance(conn, request.getSourceAccountId(), after, after);

            // Generate Ref
            String ref = "SKPAY" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 3).toUpperCase();

            // Create PaymentTransaction
            PaymentTransaction pt = new PaymentTransaction();
            pt.setCustomerId(customerId);
            pt.setSourceAccountId(request.getSourceAccountId());
            pt.setPaymentType(type);
            pt.setProviderCode(request.getProviderCode());
            pt.setRecipientIdentifier(request.getRecipientIdentifier() != null ? request.getRecipientIdentifier() : request.getConsumerNumber());
            pt.setAmount(amount);
            pt.setReferenceNumber(ref);
            pt.setIdempotencyKey(request.getIdempotencyKey());
            pt.setStatus(PaymentStatus.SUCCESS);
            pt.setRemarks(request.getRemarks() != null ? request.getRemarks() : type.name() + " Payment");

            Long ptId = paymentTxnDAO.create(conn, pt);
            pt.setPaymentTransactionId(ptId);

            // Create Transaction entry in core ledger
            Transaction txn = new Transaction();
            txn.setTransactionReference(ref);
            txn.setAccountId(request.getSourceAccountId());
            txn.setTransactionType(TransactionType.BILL_PAYMENT);
            txn.setAmount(amount);
            txn.setBalanceBefore(before);
            txn.setBalanceAfter(after);
            txn.setDescription("Payment Bank: " + type.name() + " - " + pt.getRecipientIdentifier());
            txn.setStatus(TransactionStatus.SUCCESS);
            transactionDAO.create(conn, txn);

            // Save Idempotency
            if (request.getIdempotencyKey() != null && !request.getIdempotencyKey().trim().isEmpty()) {
                idempotencyDAO.saveIdempotency(conn, request.getIdempotencyKey().trim(), ref, gson.toJson(pt));
            }

            // Send notification
            Customer cust = customerDAO.findById(customerId);
            if (cust != null) {
                Notification n = new Notification();
                n.setUserId(cust.getUserId());
                n.setTitle("Payment Bank Transaction Successful");
                n.setMessage("₹" + amount + " paid for " + type.name() + " (" + pt.getRecipientIdentifier() + "). Ref: " + ref);
                n.setNotificationType("BILL");
                notificationDAO.create(conn, n);
            }

            conn.commit();
            return pt;
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            throw new BankException("Payment failed: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {}
            }
        }
    }

    @Override
    public List<PaymentTransaction> getPaymentHistory(String customerId, int page, int pageSize) throws BankException {
        try {
            int offset = (page - 1) * pageSize;
            return paymentTxnDAO.findByCustomerId(customerId, offset, pageSize);
        } catch (Exception e) {
            throw new BankException("Error fetching payment history", e);
        }
    }

    @Override
    public List<PaymentProvider> getProvidersByType(String providerType) throws BankException {
        try {
            return providerDAO.findByType(providerType);
        } catch (Exception e) {
            throw new BankException("Error fetching providers", e);
        }
    }
}
