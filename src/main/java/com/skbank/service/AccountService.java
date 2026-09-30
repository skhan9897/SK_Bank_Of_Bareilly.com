package com.skbank.service;

import com.skbank.dao.AccountDAO;
import com.skbank.dao.CustomerDAO;
import com.skbank.model.Account;
import com.skbank.model.AccountType;
import com.skbank.model.Customer;
import com.skbank.util.AccountNumberGenerator;

import java.sql.SQLException;
import java.util.List;

public class AccountService {

    private final AccountDAO accountDAO = new AccountDAO();
    private final CardService cardService = new CardService();
    private final CustomerDAO customerDAO = new CustomerDAO();

    public boolean createDefaultSavingsAccount(String customerId) throws SQLException {
        return createDefaultSavingsAccount(customerId, 1, 1);
    }

    /**
     * Creates a default Savings Account for new customer registration.
     * INITIAL BALANCE MUST BE EXACTLY ₹0.00 (NO AUTOMATIC DEPOSIT).
     */
    public boolean createDefaultSavingsAccount(String customerId, int typeId, int branchId) throws SQLException {
        Account account = new Account();
        account.setAccountNumber(AccountNumberGenerator.generateAccountNumber());
        account.setCustomerId(customerId);
        account.setTypeId(typeId > 0 ? typeId : 1); // Default to Savings Account
        account.setBranchId(branchId > 0 ? branchId : 1); // Default to Main Branch
        account.setBalance(0.00); // STRICT RULE: NO AUTOMATIC DEPOSIT
        account.setStatus("ACTIVE");

        boolean created = accountDAO.createAccount(account);
        if (created) {
            Account createdAccount = accountDAO.findByAccountNumber(account.getAccountNumber());
            if (createdAccount != null) {
                // Auto issue Virtual Debit Card
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

    public boolean createNewAccount(String customerId, int typeId, int branchId) throws SQLException {
        Account account = new Account();
        account.setAccountNumber(AccountNumberGenerator.generateAccountNumber());
        account.setCustomerId(customerId);
        account.setTypeId(typeId);
        account.setBranchId(branchId > 0 ? branchId : 1);
        account.setBalance(0.00); // STRICT RULE: 0.00 Initial Balance
        account.setStatus("ACTIVE");

        boolean created = accountDAO.createAccount(account);
        if (created) {
            Account createdAcc = accountDAO.findByAccountNumber(account.getAccountNumber());
            if (createdAcc != null) {
                Customer c = customerDAO.findByCustomerId(customerId);
                String holderName = (c != null) ? c.getFullName() : "VALUED CUSTOMER";
                cardService.createDebitCardForCustomer(customerId, createdAcc.getAccountId(), holderName);
            }
        }
        return created;
    }
}
