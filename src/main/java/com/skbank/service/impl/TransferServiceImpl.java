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
import com.skbank.util.SystemSettingsUtil;
import com.skbank.util.ValidationUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class TransferServiceImpl implements TransferService {

    private final AccountDAO accountDAO = new AccountDAOImpl();
    private final CustomerDAO customerDAO = new CustomerDAOImpl();
    private final UpiDAO upiDAO = new UpiDAOImpl();
    private final BranchDAO branchDAO = new BranchDAOImpl();
    private final TransactionDAO transactionDAO = new TransactionDAOImpl();
    private final TransferDAO transferDAO = new TransferDAOImpl();
    private final NotificationDAO notificationDAO = new NotificationDAOImpl();

    @Override
    public RecipientLookupDTO lookupByMobile(String mobile, Long senderCustomerId) throws BankException {
        if (!ValidationUtil.isValidMobile(mobile)) {
            throw new BankException("Invalid 10-digit mobile number");
        }
        try {
            Customer cust = customerDAO.findByMobile(mobile);
            if (cust == null) {
                RecipientLookupDTO dto = new RecipientLookupDTO();
                dto.setSuccess(false);
                dto.setMessage("Mobile number not registered with SK Bank");
                return dto;
            }

            List<Account> accounts = accountDAO.findByCustomerId(cust.getCustomerId());
            if (accounts.isEmpty()) {
                RecipientLookupDTO dto = new RecipientLookupDTO();
                dto.setSuccess(false);
                dto.setMessage("No active account found for this mobile number");
                return dto;
            }

            Account primaryAcc = accounts.get(0);
            Branch branch = branchDAO.findById(primaryAcc.getBranchId());

            RecipientLookupDTO dto = new RecipientLookupDTO();
            dto.setSuccess(true);
            dto.setRecipientName(cust.getFullName());
            dto.setMaskedMobile("XXXXXX" + mobile.substring(6));
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
                dto.setMessage("Your own UPI ID detected");
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

        BigDecimal maxLimit = SystemSettingsUtil.getSettingAsBigDecimal("MAX_TRANSFER_AMOUNT", new BigDecimal("500000.00"));
        if (amount.compareTo(maxLimit) > 0) {
            throw new BankException("Transfer amount exceeds maximum limit of ₹" + maxLimit);
        }

        Long senderAccId = transferDTO.getSenderAccountId();
        Long receiverAccId = transferDTO.getReceiverAccountId();

        if (senderAccId == null || receiverAccId == null) {
            throw new BankException("Invalid sender or receiver account");
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Lock accounts in consistent order to prevent deadlocks
            Account senderAcc;
            Account receiverAcc;

            if (senderAccId < receiverAccId) {
                senderAcc = accountDAO.findForUpdate(conn, senderAccId);
                receiverAcc = accountDAO.findForUpdate(conn, receiverAccId);
            } else if (senderAccId > receiverAccId) {
                receiverAcc = accountDAO.findForUpdate(conn, receiverAccId);
                senderAcc = accountDAO.findForUpdate(conn, senderAccId);
            } else {
                // Same account self transfer - just lock once
                senderAcc = accountDAO.findForUpdate(conn, senderAccId);
                receiverAcc = senderAcc;
            }

            if (senderAcc == null) throw new BankException("Sender account not found");
            if (receiverAcc == null) throw new BankException("Receiver account not found");

            // Verify sender ownership
            if (!senderAcc.getCustomerId().equals(senderCustomerId)) {
                throw new BankException("Unauthorized: You can only transfer money from your own account");
            }

            if (senderAcc.getStatus() != AccountStatus.ACTIVE) {
                throw new BankException("Sender account is not active");
            }
            if (receiverAcc.getStatus() != AccountStatus.ACTIVE) {
                throw new BankException("Receiver account is not active");
            }

            if (senderAcc.getAvailableBalance().compareTo(amount) < 0) {
                throw new InsufficientBalanceException("Insufficient available balance. Available: ₹" + senderAcc.getAvailableBalance());
            }

            boolean isSelf = senderAcc.getCustomerId().equals(receiverAcc.getCustomerId());
            TransferType tType = isSelf ? TransferType.SELF : TransferType.valueOf(transferDTO.getTransferType() != null ? transferDTO.getTransferType() : "ACCOUNT");

            // Perform debit & credit
            BigDecimal senderBefore = senderAcc.getBalance();
            BigDecimal senderAfter = senderBefore.subtract(amount);

            BigDecimal receiverBefore = receiverAcc.getBalance();
            BigDecimal receiverAfter = isSelf && senderAccId.equals(receiverAccId) ? senderAfter : receiverBefore.add(amount);

            accountDAO.updateBalance(conn, senderAccId, senderAfter, senderAfter);
            if (!senderAccId.equals(receiverAccId)) {
                accountDAO.updateBalance(conn, receiverAccId, receiverAfter, receiverAfter);
            }

            String ref = "SKTXN" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();

            // 1. Debit Transaction for Sender
            Transaction debitTxn = new Transaction();
            debitTxn.setTransactionReference(ref + "-D");
            debitTxn.setAccountId(senderAccId);
            debitTxn.setTransactionType(TransactionType.TRANSFER);
            debitTxn.setAmount(amount);
            debitTxn.setBalanceBefore(senderBefore);
            debitTxn.setBalanceAfter(senderAfter);
            debitTxn.setRelatedAccountId(receiverAccId);
            debitTxn.setDescription("Transfer to " + (transferDTO.getRecipientName() != null ? transferDTO.getRecipientName() : receiverAcc.getMaskedAccountNumber()) + " (" + tType.name() + ")");
            debitTxn.setStatus(TransactionStatus.SUCCESS);
            transactionDAO.create(conn, debitTxn);

            // 2. Credit Transaction for Receiver (if different account)
            if (!senderAccId.equals(receiverAccId)) {
                Transaction creditTxn = new Transaction();
                creditTxn.setTransactionReference(ref + "-C");
                creditTxn.setAccountId(receiverAccId);
                creditTxn.setTransactionType(TransactionType.TRANSFER);
                creditTxn.setAmount(amount);
                creditTxn.setBalanceBefore(receiverBefore);
                creditTxn.setBalanceAfter(receiverAfter);
                creditTxn.setRelatedAccountId(senderAccId);
                creditTxn.setDescription("Received from " + senderAcc.getMaskedAccountNumber() + " (" + tType.name() + ")");
                creditTxn.setStatus(TransactionStatus.SUCCESS);
                transactionDAO.create(conn, creditTxn);
            }

            // 3. Create TransferRequest entry
            TransferRequest tr = new TransferRequest();
            tr.setReferenceNumber(ref);
            tr.setSenderAccountId(senderAccId);
            tr.setReceiverAccountId(receiverAccId);
            tr.setAmount(amount);
            tr.setTransferType(tType);
            tr.setRemarks(transferDTO.getRemarks());
            tr.setStatus("COMPLETED");
            Long trId = transferDAO.create(conn, tr);
            tr.setTransferId(trId);

            // 4. Send Notifications
            Notification notifSender = new Notification();
            notifSender.setUserId(senderAcc.getCustomerId());
            notifSender.setTitle("Money Transferred");
            notifSender.setMessage("₹" + amount + " transferred to " + receiverAcc.getMaskedAccountNumber() + ". Ref: " + ref);
            notifSender.setNotificationType("TRANSFER");
            notificationDAO.create(conn, notifSender);

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
