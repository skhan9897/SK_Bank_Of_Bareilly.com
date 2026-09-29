package com.skbank.service;

import com.skbank.config.DatabaseConfig;
import com.skbank.model.Account;
import com.skbank.model.Transaction;
import com.skbank.util.TransactionIdGenerator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TransferService {

    public Transaction processTransfer(String senderAccNo, String receiverAccNo, double amount, String transferType, String remarks) throws SQLException {
        if (amount <= 0) {
            throw new IllegalArgumentException("Transfer amount must be greater than zero.");
        }
        if (senderAccNo.equals(receiverAccNo)) {
            throw new IllegalArgumentException("Sender and receiver accounts cannot be the same.");
        }

        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false); // Begin Database Transaction

            // 1. Lock and validate Sender Account
            String senderSql = "SELECT * FROM accounts WHERE account_number = ? FOR UPDATE";
            PreparedStatement psSender = conn.prepareStatement(senderSql);
            psSender.setString(1, senderAccNo);
            ResultSet rsSender = psSender.executeQuery();

            if (!rsSender.next()) {
                throw new IllegalArgumentException("Sender account " + senderAccNo + " does not exist.");
            }
            int senderAccId = rsSender.getInt("account_id");
            double senderBalance = rsSender.getDouble("balance");
            String senderStatus = rsSender.getString("status");

            if (!"ACTIVE".equalsIgnoreCase(senderStatus)) {
                throw new IllegalStateException("Sender account is not active.");
            }
            if (senderBalance < amount) {
                throw new IllegalStateException("Insufficient balance in account. Available balance: ₹" + senderBalance);
            }

            // 2. Lock and validate Receiver Account
            String receiverSql = "SELECT * FROM accounts WHERE account_number = ? FOR UPDATE";
            PreparedStatement psReceiver = conn.prepareStatement(receiverSql);
            psReceiver.setString(1, receiverAccNo);
            ResultSet rsReceiver = psReceiver.executeQuery();

            if (!rsReceiver.next()) {
                throw new IllegalArgumentException("Receiver account " + receiverAccNo + " does not exist.");
            }
            int receiverAccId = rsReceiver.getInt("account_id");
            double receiverBalance = rsReceiver.getDouble("balance");
            String receiverStatus = rsReceiver.getString("status");

            if (!"ACTIVE".equalsIgnoreCase(receiverStatus)) {
                throw new IllegalStateException("Receiver account is currently blocked or inactive.");
            }

            // 3. Perform Debit and Credit
            double newSenderBalance = senderBalance - amount;
            double newReceiverBalance = receiverBalance + amount;

            String updateDebitSql = "UPDATE accounts SET balance = ? WHERE account_id = ?";
            PreparedStatement psDebit = conn.prepareStatement(updateDebitSql);
            psDebit.setDouble(1, newSenderBalance);
            psDebit.setInt(2, senderAccId);
            psDebit.executeUpdate();

            String updateCreditSql = "UPDATE accounts SET balance = ? WHERE account_id = ?";
            PreparedStatement psCredit = conn.prepareStatement(updateCreditSql);
            psCredit.setDouble(1, newReceiverBalance);
            psCredit.setInt(2, receiverAccId);
            psCredit.executeUpdate();

            // 4. Create Transaction Records
            String txnId = TransactionIdGenerator.generateTransactionId();
            String refNo = TransactionIdGenerator.generateReferenceNumber();

            // Sender Debit Record
            String insertTxnSql = "INSERT INTO transactions (transaction_id, account_id, type, amount, balance_after, reference_number, description, status) VALUES (?, ?, ?, ?, ?, ?, ?, 'SUCCESS')";
            PreparedStatement psTxnDebit = conn.prepareStatement(insertTxnSql);
            psTxnDebit.setString(1, txnId);
            psTxnDebit.setInt(2, senderAccId);
            psTxnDebit.setString(3, "TRANSFER");
            psTxnDebit.setDouble(4, amount);
            psTxnDebit.setDouble(5, newSenderBalance);
            psTxnDebit.setString(6, refNo);
            psTxnDebit.setString(7, "Transfer via " + transferType + " to " + receiverAccNo + ". " + (remarks != null ? remarks : ""));
            psTxnDebit.executeUpdate();

            // Receiver Credit Record
            String creditTxnId = TransactionIdGenerator.generateTransactionId();
            PreparedStatement psTxnCredit = conn.prepareStatement(insertTxnSql);
            psTxnCredit.setString(1, creditTxnId);
            psTxnCredit.setInt(2, receiverAccId);
            psTxnCredit.setString(3, "TRANSFER");
            psTxnCredit.setDouble(4, amount);
            psTxnCredit.setDouble(5, newReceiverBalance);
            psTxnCredit.setString(6, refNo);
            psTxnCredit.setString(7, "Received via " + transferType + " from " + senderAccNo + ". " + (remarks != null ? remarks : ""));
            psTxnCredit.executeUpdate();

            // Commit Transaction
            conn.commit();

            // Build result DTO
            Transaction resultTxn = new Transaction();
            resultTxn.setTransactionId(txnId);
            resultTxn.setAccountId(senderAccId);
            resultTxn.setType("TRANSFER");
            resultTxn.setAmount(amount);
            resultTxn.setBalanceAfter(newSenderBalance);
            resultTxn.setReferenceNumber(refNo);
            resultTxn.setDescription("Transfer via " + transferType + " to " + receiverAccNo);
            resultTxn.setStatus("SUCCESS");
            resultTxn.setAccountNumber(senderAccNo);

            return resultTxn;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
