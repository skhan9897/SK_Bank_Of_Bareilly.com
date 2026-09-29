package com.skbank.service;

import com.skbank.config.DatabaseConfig;
import com.skbank.dao.AccountDAO;
import com.skbank.dao.FDDAO;
import com.skbank.dao.TransactionDAO;
import com.skbank.model.Account;
import com.skbank.model.FixedDeposit;
import com.skbank.model.Transaction;
import com.skbank.util.AccountNumberGenerator;
import com.skbank.util.EmiCalculatorUtil;
import com.skbank.util.TransactionIdGenerator;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class FDService {

    private final FDDAO fdDAO = new FDDAO();
    private final AccountDAO accountDAO = new AccountDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();

    public boolean createFixedDeposit(String customerId, int accountId, double amount, int tenureMonths) throws SQLException {
        if (amount < 1000) {
            throw new IllegalArgumentException("Minimum Fixed Deposit amount is ₹1,000.");
        }

        Account account = accountDAO.findByAccountId(accountId);
        if (account == null || !"ACTIVE".equals(account.getStatus())) {
            throw new IllegalArgumentException("Selected account is not active.");
        }
        if (account.getBalance() < amount) {
            throw new IllegalStateException("Insufficient account balance for Fixed Deposit creation.");
        }

        // Determine Interest Rate based on tenure
        double interestRate = 6.0;
        if (tenureMonths >= 12 && tenureMonths < 24) interestRate = 6.75;
        else if (tenureMonths >= 24 && tenureMonths < 36) interestRate = 7.10;
        else if (tenureMonths >= 36) interestRate = 7.50;

        double maturityAmount = EmiCalculatorUtil.calculateFdMaturityAmount(amount, interestRate, tenureMonths);
        LocalDate maturityLocalDate = LocalDate.now().plusMonths(tenureMonths);

        // Deduct FD amount from account
        double newBalance = account.getBalance() - amount;
        String updateAccSql = "UPDATE accounts SET balance = ? WHERE account_id = ?";
        try (var conn = DatabaseConfig.getConnection();
             var ps = conn.prepareStatement(updateAccSql)) {
            ps.setDouble(1, newBalance);
            ps.setInt(2, accountId);
            ps.executeUpdate();
        }

        // Create FD
        FixedDeposit fd = new FixedDeposit();
        fd.setCustomerId(customerId);
        fd.setAccountId(accountId);
        fd.setReceiptNumber(AccountNumberGenerator.generateFdReceiptNumber());
        fd.setDepositAmount(amount);
        fd.setInterestRate(interestRate);
        fd.setTenureMonths(tenureMonths);
        fd.setMaturityAmount(maturityAmount);
        fd.setMaturityDate(Date.valueOf(maturityLocalDate));
        fd.setStatus("ACTIVE");

        boolean created = fdDAO.createFD(fd);
        if (created) {
            // Record Debit Transaction
            Transaction txn = new Transaction();
            txn.setTransactionId(TransactionIdGenerator.generateTransactionId());
            txn.setAccountId(accountId);
            txn.setType("FD_INVESTMENT");
            txn.setAmount(amount);
            txn.setBalanceAfter(newBalance);
            txn.setReferenceNumber(TransactionIdGenerator.generateReferenceNumber());
            txn.setDescription("FD Investment Creation Receipt #" + fd.getReceiptNumber());
            txn.setStatus("SUCCESS");
            transactionDAO.createTransaction(txn);
        }
        return created;
    }

    public List<FixedDeposit> getCustomerFDs(String customerId) throws SQLException {
        return fdDAO.findByCustomerId(customerId);
    }

    public List<FixedDeposit> getAllFDs() throws SQLException {
        return fdDAO.findAll();
    }
}
