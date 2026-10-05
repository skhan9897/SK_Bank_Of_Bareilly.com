package com.skbank.dao;

import com.skbank.model.Account;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface AccountDAO {
    Account findById(Long accountId) throws SQLException;
    Account findByAccountNumber(String accountNumber) throws SQLException;
    Account findForUpdate(Connection conn, Long accountId) throws SQLException;
    List<Account> findByCustomerId(String customerId) throws SQLException;
    Long create(Account account) throws SQLException;
    Long create(Connection conn, Account account) throws SQLException;
    boolean updateBalance(Connection conn, Long accountId, BigDecimal newBalance, BigDecimal newAvailableBalance) throws SQLException;
    boolean updateStatus(Long accountId, String status) throws SQLException;
    List<Account> findAll(int offset, int limit, String searchQuery) throws SQLException;
    long countAll(String searchQuery) throws SQLException;
    BigDecimal getTotalBankBalance() throws SQLException;
}
