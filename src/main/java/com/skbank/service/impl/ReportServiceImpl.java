package com.skbank.service.impl;

import com.skbank.dao.TransactionDAO;
import com.skbank.dao.impl.TransactionDAOImpl;
import com.skbank.exception.BankException;
import com.skbank.model.Transaction;
import com.skbank.service.ReportService;

import java.sql.Date;
import java.util.List;

public class ReportServiceImpl implements ReportService {

    private final TransactionDAO transactionDAO = new TransactionDAOImpl();

    @Override
    public List<Transaction> getAdminTransactionsReport(String searchQuery, String typeFilter, Date startDate, Date endDate, int page, int pageSize) throws BankException {
        try {
            int offset = (page - 1) * pageSize;
            return transactionDAO.findAllAdmin(offset, pageSize, searchQuery, typeFilter, startDate, endDate);
        } catch (Exception e) {
            throw new BankException("Error generating transactions report", e);
        }
    }

    @Override
    public long countAdminTransactionsReport(String searchQuery, String typeFilter, Date startDate, Date endDate) throws BankException {
        try {
            return transactionDAO.countAllAdmin(searchQuery, typeFilter, startDate, endDate);
        } catch (Exception e) {
            throw new BankException("Error counting report records", e);
        }
    }
}
