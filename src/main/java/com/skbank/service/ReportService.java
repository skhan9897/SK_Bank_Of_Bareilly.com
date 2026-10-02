package com.skbank.service;

import com.skbank.exception.BankException;
import com.skbank.model.Transaction;

import java.sql.Date;
import java.util.List;

public interface ReportService {
    List<Transaction> getAdminTransactionsReport(String searchQuery, String typeFilter, Date startDate, Date endDate, int page, int pageSize) throws BankException;
    long countAdminTransactionsReport(String searchQuery, String typeFilter, Date startDate, Date endDate) throws BankException;
}
