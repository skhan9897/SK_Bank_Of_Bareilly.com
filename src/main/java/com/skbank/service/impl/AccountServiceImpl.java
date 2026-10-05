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
    public List<Account> getCustomerAccounts(String customerId) throws BankException {
        try {
            return accountDAO.findByCustomerId(customerId);
        } catch (Exception e) {
            throw new BankException("Error fetching customer accounts", e);
        }
    }

    @Override
    public List<Branch> getAllActiveBranches() throws BankException {
        try {
            List<Branch> list = branchDAO.findAllActive();
            if (list != null && !list.isEmpty()) {
                return list;
            }
        } catch (Exception ignored) {}

        // Fallback default branches if DB is empty/unseeded
        java.util.List<Branch> fallbacks = new java.util.ArrayList<>();
        Branch b1 = new Branch(); b1.setBranchId(1L); b1.setBranchCode("SKB001"); b1.setBranchName("Main Branch Bareilly"); b1.setIfscCode("SKBK0000001"); fallbacks.add(b1);
        Branch b2 = new Branch(); b2.setBranchId(2L); b2.setBranchCode("SKB002"); b2.setBranchName("Izzatnagar Branch"); b2.setIfscCode("SKBK0000002"); fallbacks.add(b2);
        Branch b3 = new Branch(); b3.setBranchId(3L); b3.setBranchCode("SKB003"); b3.setBranchName("Rajendra Nagar Branch"); b3.setIfscCode("SKBK0000003"); fallbacks.add(b3);
        Branch b4 = new Branch(); b4.setBranchId(4L); b4.setBranchCode("SKB004"); b4.setBranchName("Noida Cyber Branch"); b4.setIfscCode("SKBK0000004"); fallbacks.add(b4);
        return fallbacks;
    }

    @Override
    public List<AccountType> getAllActiveAccountTypes() throws BankException {
        try {
            List<AccountType> list = accountTypeDAO.findAllActive();
            if (list != null && !list.isEmpty()) {
                return list;
            }
        } catch (Exception ignored) {}

        // Fallback default account types if DB is empty/unseeded
        java.util.List<AccountType> fallbacks = new java.util.ArrayList<>();
        AccountType at1 = new AccountType(); at1.setAccountTypeId(1L); at1.setTypeCode("SAVINGS"); at1.setTypeName("Savings Account"); fallbacks.add(at1);
        AccountType at2 = new AccountType(); at2.setAccountTypeId(2L); at2.setTypeCode("CURRENT"); at2.setTypeName("Current Account"); fallbacks.add(at2);
        AccountType at3 = new AccountType(); at3.setAccountTypeId(3L); at3.setTypeCode("SALARY"); at3.setTypeName("Corporate Salary Account"); fallbacks.add(at3);
        AccountType at4 = new AccountType(); at4.setAccountTypeId(4L); at4.setTypeCode("BASIC_SAVINGS"); at4.setTypeName("Basic Savings Account (BSBD)"); fallbacks.add(at4);
        AccountType at5 = new AccountType(); at5.setAccountTypeId(5L); at5.setTypeCode("SENIOR_CITIZEN"); at5.setTypeName("Senior Citizen Savings Account"); fallbacks.add(at5);
        return fallbacks;
    }

    @Override
    public Account createAccount(String customerId, Long accountTypeId, Long branchId) throws BankException {
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
    public void verifyAccountOwnership(Long accountId, String customerId) throws BankException {
        Account acc = getAccountById(accountId);
        if (!acc.getCustomerId().equals(customerId)) {
            throw new UnauthorizedAccessException("Unauthorized: You do not own this account.");
        }
    }
}
