package com.skbank.service.impl;

import com.skbank.dao.*;
import com.skbank.dao.impl.*;
import com.skbank.dto.RecipientLookupDTO;
import com.skbank.dto.TransferDTO;
import com.skbank.exception.BankException;
import com.skbank.exception.InsufficientBalanceException;
import com.skbank.model.*;
import com.skbank.service.TransferService;
import com.skbank.util.DatabaseConnection;
import com.skbank.util.ValidationUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class TransferServiceImpl implements TransferService {

    private final AccountDAO accountDAO = new AccountDAOImpl();
    private final CustomerDAO customerDAO = new CustomerDAOImpl();
    private final BranchDAO branchDAO = new BranchDAOImpl();
    private final TransactionDAO transactionDAO = new TransactionDAOImpl();
    private final TransferDAO transferDAO = new TransferDAOImpl();
    private final NotificationDAO notificationDAO = new NotificationDAOImpl();
    private final UpiDAO upiDAO = new UpiDAOImpl();

    @Override
    public RecipientLookupDTO lookupByMobile(String mobile, Long senderCustomerId) throws BankException {
        if (!ValidationUtil.isValidMobile(mobile)) {
            throw new BankException("Invalid 10-digit mobile number");
        }
        try {
            Customer cust = customerDAO.findByMobile(mobile.trim());
            if (cust == null) {
                RecipientLookupDTO dto = new RecipientLookupDTO();
                dto.setSuccess(false);
                dto.setMessage("No registered SK Bank customer found with this mobile number");
                return dto;
            }

            List<Account> accounts = accountDAO.findByCustomerId(cust.getCustomerId());
            if (accounts.isEmpty()) {
                RecipientLookupDTO dto = new RecipientLookupDTO();
                dto.setSuccess(false);
                dto.setMessage("No active account found for this customer");
                return dto;
            }

            Account primaryAcc = accounts.get(0);
            Branch branch = branchDAO.findById(primaryAcc.getBranchId());

            RecipientLookupDTO dto = new RecipientLookupDTO();
            dto.setSuccess(true);
            dto.setRecipientName(cust.getFullName());
            dto.setMaskedMobile(cust.getMaskedAadhaar());
            dto.setMaskedAccount(primaryAcc.getMaskedAccountNumber());
            dto.setBankName("SK BANK OF BAREILLY");
            dto.setBranchName(branch != null ? branch.getBranchName() : "Main Branch");
            dto.setIfscCode(branch != null ? branch.getIfscCode() : "SKBK0000001");
            dto.setStatus(primaryAcc.getStatus().name());
            dto.setAccountId(primaryAcc.getAccountId());
            dto.setCustomerId(cust.getCustomerId());
            dto.setOwnAccount(cust.getCustomerId().equals(senderCustomerId));
            if (dto.isOwnAccount()) {
                dto.setMessage("Your own account detected");
            }
            return dto;
        } catch (Exception e) {
            throw new BankException("Lookup error: " + e.getMessage(), e);
        }
    }

    @Override
    public RecipientLookupDTO lookupByAccount(String accountNumber, Long senderCustomerId) throws BankException {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            throw new BankException("Account number cannot be empty");
        }
        try {
            Account acc = accountDAO.findByAccountNumber(accountNumber.trim());
            if (acc == null) {
                RecipientLookupDTO dto = new RecipientLookupDTO();
                dto.setSuccess(false);
                dto.setMessage("Account number not found");
                return dto;
            }

            Customer cust = customerDAO.findById(acc.getCustomerId());
            Branch branch = branchDAO.findById(acc.getBranchId());

            RecipientLookupDTO dto = new RecipientLookupDTO();
            dto.setSuccess(true);
            dto.setRecipientName(cust != null ? cust.getFullName() : "Account Holder");
            dto.setMaskedAccount(acc.getMaskedAccountNumber());
            dto.setBankName("SK BANK OF BAREILLY");
            dto.setBranchName(branch != null ? branch.getBranchName() : "Main Branch");
            dto.setIfscCode(branch != null ? branch.getIfscCode() : "SKBK0000001");
            dto.setStatus(acc.getStatus().name());
            dto.setAccountId(acc.getAccountId());
            dto.setCustomerId(acc.getCustomerId());
            dto.setOwnAccount(acc.getCustomerId().equals(senderCustomerId));
            if (dto.isOwnAccount()) {
                dto.setMessage("Your own account detected");
            }
            return dto;
        } catch (Exception e) {
            throw new BankException("Lookup error: " + e.getMessage(), e);
        }
    }

    @Override
    public RecipientLookupDTO lookupByUpi(String upiAddress, Long senderCustomerId) throws BankException {
        if (upiAddress == null || upiAddress.trim().isEmpty()) {
            throw new BankException("UPI ID cannot be empty");
        }
        try {
            UpiAccount upiAcc = upiDAO.findByUpiAddress(upiAddress.trim());
            if (upiAcc == null) {
                RecipientLookupDTO dto = new RecipientLookupDTO();
                dto.setSuccess(false);
                dto.setMessage("UPI ID not registered");
                return dto;
            }

            Account acc = accountDAO.findById(upiAcc.getAccountId());
            Customer cust = customerDAO.findById(upiAcc.getCustomerId());

            RecipientLookupDTO dto = new RecipientLookupDTO();
            dto.setSuccess(true);
            dto.setRecipientName(cust != null ? cust.getFullName() : "UPI Holder");
            dto.setUpiAddress(upiAcc.getUpiAddress());
            dto.setMaskedAccount(acc != null ? acc.getMaskedAccountNumber() : "XXXX");
            dto.setBankName("SK BANK OF BAREILLY");
            dto.setStatus(upiAcc.getStatus());
            dto.setAccountId(upiAcc.getAccountId());
            dto.setCustomerId(upiAcc.getCustomerId());
            dto.setOwnAccount(upiAcc.getCustomerId().equals(senderCustomerId));
            if (dto.isOwnAccount()) {
                dto.setMessage("Your own account detected");
            }
            return dto;
        } catch (Exception e) {
            throw new BankException("Lookup error: " + e.getMessage(), e);
        }
    }

    @Override
    public TransferRequest processTransfer(TransferDTO transferDTO, Long senderCustomerId) throws BankException {
        BigDecimal amount = transferDTO.getAmount();
        if (!ValidationUtil.isValidAmount(amount)) {
            throw new BankException("Transfer amount must be greater than zero");
        }

        Long senderAccId = transferDTO.getSenderAccountId();
        Long receiverAccId = transferDTO.getReceiverAccountId();

        if (senderAccId.equals(receiverAccId)) {
            throw new BankException("Sender and receiver account cannot be the same");
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Lock accounts in deterministic order to prevent deadlocks
            Long firstId = Math.min(senderAccId, receiverAccId);
            Long secondId = Math.max(senderAccId, receiverAccId);

            Account firstLock = accountDAO.findForUpdate(conn, firstId);
            Account secondLock = accountDAO.findForUpdate(conn, secondId);

            Account senderAcc = (firstId.equals(senderAccId)) ? firstLock : secondLock;
            Account receiverAcc = (firstId.equals(receiverAccId)) ? firstLock : secondLock;

            if (senderAcc == null || !senderAcc.getCustomerId().equals(senderCustomerId)) {
                throw new BankException("Invalid sender account");
            }
            if (receiverAcc == null) {
                throw new BankException("Receiver account not found");
            }

            if (senderAcc.getStatus() != AccountStatus.ACTIVE) {
                throw new BankException("Sender account is not active");
            }
            if (receiverAcc.getStatus() != AccountStatus.ACTIVE) {
                throw new BankException("Receiver account is not active");
            }

            if (senderAcc.getAvailableBalance().compareTo(amount) < 0) {
                throw new InsufficientBalanceException("Insufficient balance. Available: ₹" + senderAcc.getAvailableBalance());
            }

            // 1. Debit Sender
            BigDecimal senderBefore = senderAcc.getBalance();
            BigDecimal senderAfter = senderBefore.subtract(amount);
            accountDAO.updateBalance(conn, senderAccId, senderAfter, senderAfter);

            // 2. Credit Receiver
            BigDecimal receiverBefore = receiverAcc.getBalance();
            BigDecimal receiverAfter = receiverBefore.add(amount);
            accountDAO.updateBalance(conn, receiverAccId, receiverAfter, receiverAfter);

            // Generate Ref
            String ref = "SKTR" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 3).toUpperCase();
            String tTypeStr = transferDTO.getTransferType() != null ? transferDTO.getTransferType() : "ACCOUNT_TRANSFER";

            // Record Debit Transaction
            Transaction debitTxn = new Transaction();
            debitTxn.setTransactionReference(ref + "-D");
            debitTxn.setAccountId(senderAccId);
            debitTxn.setRelatedAccountId(receiverAccId);
            debitTxn.setTransactionType(TransactionType.TRANSFER);
            debitTxn.setAmount(amount);
            debitTxn.setBalanceBefore(senderBefore);
            debitTxn.setBalanceAfter(senderAfter);
            debitTxn.setDescription("Transfer to " + receiverAcc.getMaskedAccountNumber() + " (" + tTypeStr + ")");
            debitTxn.setStatus(TransactionStatus.SUCCESS);
            transactionDAO.create(conn, debitTxn);

            // Record Credit Transaction
            Transaction creditTxn = new Transaction();
            creditTxn.setTransactionReference(ref + "-C");
            creditTxn.setAccountId(receiverAccId);
            creditTxn.setRelatedAccountId(senderAccId);
            creditTxn.setTransactionType(TransactionType.TRANSFER);
            creditTxn.setAmount(amount);
            creditTxn.setBalanceBefore(receiverBefore);
            creditTxn.setBalanceAfter(receiverAfter);
            creditTxn.setDescription("Transfer from " + senderAcc.getMaskedAccountNumber() + " (" + tTypeStr + ")");
            creditTxn.setStatus(TransactionStatus.SUCCESS);
            transactionDAO.create(conn, creditTxn);

            // 3. Create TransferRequest entry
            TransferRequest tr = new TransferRequest();
            tr.setReferenceNumber(ref);
            tr.setSenderAccountId(senderAccId);
            tr.setReceiverAccountId(receiverAccId);
            tr.setAmount(amount);
            tr.setTransferType(tTypeStr);
            tr.setRemarks(transferDTO.getRemarks());
            tr.setStatus("COMPLETED");
            Long trId = transferDAO.create(conn, tr);
            tr.setTransferId(trId);

            // 4. Send Notifications
            Customer senderCust = customerDAO.findById(senderAcc.getCustomerId());
            if (senderCust != null) {
                Notification notifSender = new Notification();
                notifSender.setUserId(senderCust.getUserId());
                notifSender.setTitle("Money Transferred");
                notifSender.setMessage("₹" + amount + " transferred to " + receiverAcc.getMaskedAccountNumber() + ". Ref: " + ref);
                notifSender.setNotificationType("TRANSFER");
                notificationDAO.create(conn, notifSender);
            }

            if (!senderAcc.getCustomerId().equals(receiverAcc.getCustomerId())) {
                Customer receiverCust = customerDAO.findById(receiverAcc.getCustomerId());
                if (receiverCust != null) {
                    Notification notifReceiver = new Notification();
                    notifReceiver.setUserId(receiverCust.getUserId());
                    notifReceiver.setTitle("Money Received");
                    notifReceiver.setMessage("₹" + amount + " credited to your account " + receiverAcc.getMaskedAccountNumber() + ". Ref: " + ref);
                    notifReceiver.setNotificationType("TRANSFER");
                    notificationDAO.create(conn, notifReceiver);
                }
            }

            conn.commit();
            return tr;
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            throw new BankException("Transfer failed: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {}
            }
        }
    }
}
