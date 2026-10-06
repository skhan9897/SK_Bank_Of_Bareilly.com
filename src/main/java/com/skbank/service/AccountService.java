package com.skbank.service;

import com.skbank.exception.BankException;
import com.skbank.model.Account;
import com.skbank.model.AccountType;
import com.skbank.model.Branch;

import java.util.List;

public interface AccountService {
    Account getAccountById(Long accountId) throws BankException;
    Account getAccountByNumber(String accountNumber) throws BankException;
    List<Account> getCustomerAccounts(Long customerId) throws BankException;
    List<Branch> getAllActiveBranches() throws BankException;
    List<AccountType> getAllActiveAccountTypes() throws BankException;
    Account createAccount(Long customerId, Long accountTypeId, Long branchId) throws BankException;
    void verifyAccountOwnership(Long accountId, Long customerId) throws BankException;
}
