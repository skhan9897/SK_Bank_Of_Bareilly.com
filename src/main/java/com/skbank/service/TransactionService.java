package com.skbank.service;

import com.skbank.exception.BankException;
import com.skbank.model.Transaction;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

public interface TransactionService {
    Transaction processWithdrawal(Long accountId, BigDecimal amount, String description) throws BankException;
    Transaction processWithdrawal(Long accountId, BigDecimal amount, String description, Long customerId) throws BankException;
    List<Transaction> getAccountTransactions(Long accountId, int page, int pageSize) throws BankException;
    long countAccountTransactions(Long accountId) throws BankException;
    List<Transaction> getFilteredTransactions(Long accountId, Date startDate, Date endDate, String type, int page, int pageSize) throws BankException;
    long countFilteredTransactions(Long accountId, Date startDate, Date endDate, String type) throws BankException;
    Transaction getTransactionByReference(String reference) throws BankException;
}
