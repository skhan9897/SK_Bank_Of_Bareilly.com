package com.skbank.service.impl;

import com.skbank.dao.*;
import com.skbank.dao.impl.*;
import com.skbank.exception.BankException;
import com.skbank.exception.InsufficientBalanceException;
import com.skbank.model.*;
import com.skbank.service.BillPaymentService;
import com.skbank.util.DatabaseConnection;
import com.skbank.util.ValidationUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class BillPaymentServiceImpl implements BillPaymentService {

    private final BillPaymentDAO billPaymentDAO = new BillPaymentDAOImpl();
    private final AccountDAO accountDAO = new AccountDAOImpl();
    private final CustomerDAO customerDAO = new CustomerDAOImpl();
    private final TransactionDAO transactionDAO = new TransactionDAOImpl();
    private final NotificationDAO notificationDAO = new NotificationDAOImpl();

    @Override
    public BillPayment processBillPayment(String customerId, Long accountId, String billerType, String billerName, String consumerNumber, BigDecimal amount) throws BankException {
        if (!ValidationUtil.isValidAmount(amount)) {
            throw new BankException("Payment amount must be greater than zero");
        }
        if (billerName == null || billerName.trim().isEmpty() || consumerNumber == null || consumerNumber.trim().isEmpty()) {
            throw new BankException("Biller name and Consumer number are required");
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            Account account = accountDAO.findForUpdate(conn, accountId);
            if (account == null || !account.getCustomerId().equals(customerId)) {
                throw new BankException("Invalid account selected");
            }
            if (account.getStatus() != AccountStatus.ACTIVE) {
                throw new BankException("Account is not active");
            }
            if (account.getAvailableBalance().compareTo(amount) < 0) {
                throw new InsufficientBalanceException("Insufficient balance for bill payment. Available: ₹" + account.getAvailableBalance());
            }

            // Debit balance
            BigDecimal before = account.getBalance();
            BigDecimal after = before.subtract(amount);
            accountDAO.updateBalance(conn, accountId, after, after);

            // Record Bill Payment
            String ref = "SKBILL" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 3).toUpperCase();
            BillPayment bp = new BillPayment();
            bp.setCustomerId(customerId);
            bp.setAccountId(accountId);
            bp.setBillerType(billerType);
            bp.setBillerName(billerName);
            bp.setConsumerNumber(consumerNumber);
            bp.setAmount(amount);
            bp.setPaymentReference(ref);
            bp.setStatus("SUCCESS");
            Long bpId = billPaymentDAO.create(conn, bp);
            bp.setBillPaymentId(bpId);

            // Record Transaction
            Transaction txn = new Transaction();
            txn.setTransactionReference(ref);
            txn.setAccountId(accountId);
            txn.setTransactionType(TransactionType.BILL_PAYMENT);
            txn.setAmount(amount);
            txn.setBalanceBefore(before);
            txn.setBalanceAfter(after);
            txn.setDescription("Bill Payment: " + billerName + " (" + billerType + " - " + consumerNumber + ")");
            txn.setStatus(TransactionStatus.SUCCESS);
            transactionDAO.create(conn, txn);

            // Send notification
            Customer cust = customerDAO.findById(customerId);
            if (cust != null) {
                Notification n = new Notification();
                n.setUserId(cust.getUserId());
                n.setTitle("Bill Payment Successful");
                n.setMessage("₹" + amount + " paid to " + billerName + " (" + consumerNumber + "). Ref: " + ref);
                n.setNotificationType("BILL");
                notificationDAO.create(conn, n);
            }

            conn.commit();
            return bp;
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            throw new BankException("Bill payment failed: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {}
            }
        }
    }

    @Override
    public List<BillPayment> getCustomerBillPayments(String customerId) throws BankException {
        try {
            return billPaymentDAO.findByCustomerId(customerId);
        } catch (Exception e) {
            throw new BankException("Error fetching bill payments", e);
        }
    }
}
