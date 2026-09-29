package com.skbank.service;

import com.skbank.dao.AccountDAO;
import com.skbank.dao.CustomerDAO;
import com.skbank.dao.TransactionDAO;
import com.skbank.model.Account;
import com.skbank.model.AccountType;
import com.skbank.model.Customer;
import com.skbank.model.Transaction;
import com.skbank.util.AccountNumberGenerator;
import com.skbank.util.TransactionIdGenerator;

import java.sql.SQLException;
import java.util.List;

public class AccountService {

    private final AccountDAO accountDAO = new AccountDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final CardService cardService = new CardService();
    private final CustomerDAO customerDAO = new CustomerDAO();

    public boolean createDefaultSavingsAccount(String customerId) throws SQLException {
        Account account = new Account();
        account.setAccountNumber(AccountNumberGenerator.generateAccountNumber());
        account.setCustomerId(customerId);
        account.setTypeId(1); // Savings Account
        account.setBranchId(1); // Bareilly Main Branch
        account.setBalance(10000.00); // Initial welcome deposit
        account.setStatus("ACTIVE");

        boolean created = accountDAO.createAccount(account);
        if (created) {
            Account createdAccount = accountDAO.findByAccountNumber(account.getAccountNumber());
            if (createdAccount != null) {
                // Record initial deposit transaction
                Transaction txn = new Transaction();
                txn.setTransactionId(TransactionIdGenerator.generateTransactionId());
                txn.setAccountId(createdAccount.getAccountId());
                txn.setType("DEPOSIT");
                txn.setAmount(10000.00);
                txn.setBalanceAfter(10000.00);
                txn.setReferenceNumber(TransactionIdGenerator.generateReferenceNumber());
                txn.setDescription("Initial Welcome Deposit Bonus");
                txn.setStatus("SUCCESS");
                transactionDAO.createTransaction(txn);

                // Auto issue Virtual Debit Card for Customer
                Customer c = customerDAO.findByCustomerId(customerId);
                String holderName = (c != null) ? c.getFullName() : "VALUED CUSTOMER";
                cardService.createDebitCardForCustomer(customerId, createdAccount.getAccountId(), holderName);
            }
        }
        return created;
    }

    public List<Account> getCustomerAccounts(String customerId) throws SQLException {
        return accountDAO.findByCustomerId(customerId);
    }

    public Account getAccountByNumber(String accountNumber) throws SQLException {
        return accountDAO.findByAccountNumber(accountNumber);
    }

    public Account getAccountById(int accountId) throws SQLException {
        return accountDAO.findByAccountId(accountId);
    }

    public double getCustomerTotalBalance(String customerId) throws SQLException {
        return accountDAO.getTotalBalanceByCustomer(customerId);
    }

    public List<AccountType> getAllAccountTypes() throws SQLException {
        return accountDAO.getAllAccountTypes();
    }

    public boolean createNewAccount(String customerId, int typeId, double initialDeposit) throws SQLException {
        Account account = new Account();
        account.setAccountNumber(AccountNumberGenerator.generateAccountNumber());
        account.setCustomerId(customerId);
        account.setTypeId(typeId);
        account.setBranchId(1);
        account.setBalance(initialDeposit);
        account.setStatus("ACTIVE");

        boolean created = accountDAO.createAccount(account);
        if (created) {
            Account createdAcc = accountDAO.findByAccountNumber(account.getAccountNumber());
            if (createdAcc != null) {
                if (initialDeposit > 0) {
                    Transaction txn = new Transaction();
                    txn.setTransactionId(TransactionIdGenerator.generateTransactionId());
                    txn.setAccountId(createdAcc.getAccountId());
                    txn.setType("DEPOSIT");
                    txn.setAmount(initialDeposit);
                    txn.setBalanceAfter(initialDeposit);
                    txn.setReferenceNumber(TransactionIdGenerator.generateReferenceNumber());
                    txn.setDescription("Account Opening Deposit");
                    txn.setStatus("SUCCESS");
                    transactionDAO.createTransaction(txn);
                }

                // Auto issue Virtual Debit Card
                Customer c = customerDAO.findByCustomerId(customerId);
                String holderName = (c != null) ? c.getFullName() : "VALUED CUSTOMER";
                cardService.createDebitCardForCustomer(customerId, createdAcc.getAccountId(), holderName);
            }
        }
        return created;
    }
}
