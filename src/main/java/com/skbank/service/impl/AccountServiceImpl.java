package com.skbank.service.impl;

import com.skbank.dao.*;
import com.skbank.dao.impl.*;
import com.skbank.exception.BankException;
import com.skbank.exception.UnauthorizedAccessException;
import com.skbank.model.*;
import com.skbank.service.AccountService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

public class AccountServiceImpl implements AccountService {

    private final AccountDAO accountDAO = new AccountDAOImpl();
    private final BranchDAO branchDAO = new BranchDAOImpl();
    private final AccountTypeDAO accountTypeDAO = new AccountTypeDAOImpl();

    @Override
    public Account getAccountById(Long accountId) throws BankException {
        try {
            Account acc = accountDAO.findById(accountId);
            if (acc == null) throw new BankException("Account not found");
            return acc;
        } catch (Exception e) {
            throw new BankException("Error fetching account: " + e.getMessage(), e);
        }
    }

    @Override
    public Account getAccountByNumber(String accountNumber) throws BankException {
        try {
            Account acc = accountDAO.findByAccountNumber(accountNumber);
            if (acc == null) throw new BankException("Account with number " + accountNumber + " not found");
            return acc;
        } catch (Exception e) {
            throw new BankException("Error fetching account: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Account> getCustomerAccounts(Long customerId) throws BankException {
        try {
            return accountDAO.findByCustomerId(customerId);
        } catch (Exception e) {
            throw new BankException("Error fetching customer accounts", e);
        }
    }

    @Override
    public List<Branch> getAllActiveBranches() throws BankException {
        try {
            return branchDAO.findAllActive();
        } catch (Exception e) {
            throw new BankException("Error fetching branches", e);
        }
    }

    @Override
    public List<AccountType> getAllActiveAccountTypes() throws BankException {
        try {
            return accountTypeDAO.findAllActive();
        } catch (Exception e) {
            throw new BankException("Error fetching account types", e);
        }
    }

    @Override
    public Account createAccount(Long customerId, Long accountTypeId, Long branchId) throws BankException {
        try {
            Account acc = new Account();
            acc.setCustomerId(customerId);
            acc.setAccountTypeId(accountTypeId);
            acc.setBranchId(branchId);

            Random rand = new Random();
            String accNum = "SK" + (1000000000L + (long)(rand.nextDouble() * 9000000000L));
            acc.setAccountNumber(accNum);

            // MUST BE ZERO INITIAL BALANCE
            acc.setBalance(BigDecimal.ZERO);
            acc.setAvailableBalance(BigDecimal.ZERO);
            acc.setStatus(AccountStatus.ACTIVE);

            Long id = accountDAO.create(acc);
            acc.setAccountId(id);
            return acc;
        } catch (Exception e) {
            throw new BankException("Error creating account: " + e.getMessage(), e);
        }
    }

    @Override
    public void verifyAccountOwnership(Long accountId, Long customerId) throws BankException {
        Account acc = getAccountById(accountId);
        if (!acc.getCustomerId().equals(customerId)) {
            throw new UnauthorizedAccessException("Unauthorized: You do not own this account.");
        }
    }
}
