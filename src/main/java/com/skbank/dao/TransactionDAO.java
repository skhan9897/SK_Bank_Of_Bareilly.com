package com.skbank.dao;

import com.skbank.model.Transaction;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public interface TransactionDAO {
    Transaction findById(Long transactionId) throws SQLException;
    Transaction findByReference(String reference) throws SQLException;
    Long create(Transaction transaction) throws SQLException;
    Long create(Connection conn, Transaction transaction) throws SQLException;
    List<Transaction> findByAccountId(Long accountId, int offset, int limit) throws SQLException;
    List<Transaction> findFiltered(Long accountId, Date startDate, Date endDate, String type, int offset, int limit) throws SQLException;
    long countFiltered(Long accountId, Date startDate, Date endDate, String type) throws SQLException;
    List<Transaction> findAllAdmin(int offset, int limit, String searchQuery, String typeFilter, Date startDate, Date endDate) throws SQLException;
    long countAllAdmin(String searchQuery, String typeFilter, Date startDate, Date endDate) throws SQLException;
    long getTodaysTransactionCount() throws SQLException;
}
