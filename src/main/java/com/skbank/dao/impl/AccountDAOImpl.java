package com.skbank.dao.impl;

import com.skbank.dao.AccountDAO;
import com.skbank.model.Account;
import com.skbank.model.AccountStatus;
import com.skbank.util.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDAOImpl implements AccountDAO {

    private static final String SELECT_JOIN_SQL = 
        "SELECT a.*, COALESCE(at.type_name, 'Savings Account') AS account_type_name, b.branch_name, b.ifsc_code, c.full_name AS customer_name " +
        "FROM accounts a " +
        "LEFT JOIN account_types at ON (a.account_type_id = at.account_type_id OR a.account_type_id = at.type_id) " +
        "LEFT JOIN branches b ON a.branch_id = b.branch_id " +
        "LEFT JOIN customers c ON a.customer_id = c.customer_id ";

    @Override
    public Account findById(Long accountId) throws SQLException {
        String sql = SELECT_JOIN_SQL + "WHERE a.account_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapAccount(rs);
            }
        }
        return null;
    }

    @Override
    public Account findByAccountNumber(String accountNumber) throws SQLException {
        String sql = SELECT_JOIN_SQL + "WHERE a.account_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapAccount(rs);
            }
        }
        return null;
    }

    @Override
    public Account findForUpdate(Connection conn, Long accountId) throws SQLException {
        String sql = SELECT_JOIN_SQL + "WHERE a.account_id = ? FOR UPDATE";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapAccount(rs);
            }
        }
        return null;
    }

    @Override
    public List<Account> findByCustomerId(String customerId) throws SQLException {
        List<Account> list = new ArrayList<>();
        String sql = SELECT_JOIN_SQL + "WHERE a.customer_id = ? ORDER BY a.account_id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapAccount(rs));
                }
            }
        }
        return list;
    }

    @Override
    public Long create(Account account) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return create(conn, account);
        }
    }

    @Override
    public Long create(Connection conn, Account account) throws SQLException {
        String sql = "INSERT INTO accounts (customer_id, account_type_id, branch_id, account_number, balance, available_balance, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, account.getCustomerId());
            ps.setLong(2, account.getAccountTypeId());
            ps.setLong(3, account.getBranchId());
            ps.setString(4, account.getAccountNumber());
            BigDecimal initialBal = account.getBalance() != null ? account.getBalance() : BigDecimal.ZERO;
            ps.setBigDecimal(5, initialBal);
            ps.setBigDecimal(6, initialBal);
            ps.setString(7, account.getStatus() != null ? account.getStatus().name() : AccountStatus.ACTIVE.name());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        }
        return null;
    }

    @Override
    public boolean updateBalance(Connection conn, Long accountId, BigDecimal newBalance, BigDecimal newAvailableBalance) throws SQLException {
        String sql = "UPDATE accounts SET balance = ?, available_balance = ? WHERE account_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, newBalance);
            ps.setBigDecimal(2, newAvailableBalance);
            ps.setLong(3, accountId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateStatus(Long accountId, String status) throws SQLException {
        String sql = "UPDATE accounts SET status = ? WHERE account_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setLong(2, accountId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public List<Account> findAll(int offset, int limit, String searchQuery) throws SQLException {
        List<Account> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(SELECT_JOIN_SQL);
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            sql.append("WHERE a.account_number LIKE ? OR c.full_name LIKE ? OR c.mobile LIKE ? ");
        }
        sql.append("ORDER BY a.account_id DESC LIMIT ? OFFSET ?");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int paramIdx = 1;
            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                String q = "%" + searchQuery.trim() + "%";
                ps.setString(paramIdx++, q);
                ps.setString(paramIdx++, q);
                ps.setString(paramIdx++, q);
            }
            ps.setInt(paramIdx++, limit);
            ps.setInt(paramIdx, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapAccount(rs));
                }
            }
        }
        return list;
    }

    @Override
    public long countAll(String searchQuery) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM accounts a JOIN customers c ON a.customer_id = c.customer_id ");
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            sql.append("WHERE a.account_number LIKE ? OR c.full_name LIKE ? OR c.mobile LIKE ? ");
        }
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                String q = "%" + searchQuery.trim() + "%";
                ps.setString(1, q);
                ps.setString(2, q);
                ps.setString(3, q);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
        }
        return 0;
    }

    @Override
    public BigDecimal getTotalBankBalance() throws SQLException {
        String sql = "SELECT SUM(balance) FROM accounts WHERE status = 'ACTIVE'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                BigDecimal sum = rs.getBigDecimal(1);
                return sum != null ? sum : BigDecimal.ZERO;
            }
        }
        return BigDecimal.ZERO;
    }

    private Account mapAccount(ResultSet rs) throws SQLException {
        Account a = new Account();
        a.setAccountId(rs.getLong("account_id"));
        a.setCustomerId(rs.getString("customer_id"));
        a.setAccountTypeId(rs.getLong("account_type_id"));
        a.setBranchId(rs.getLong("branch_id"));
        a.setAccountNumber(rs.getString("account_number"));
        a.setBalance(rs.getBigDecimal("balance"));
        a.setAvailableBalance(rs.getBigDecimal("available_balance"));
        a.setStatus(AccountStatus.valueOf(rs.getString("status")));

        try {
            a.setOpenedAt(rs.getTimestamp("opened_at"));
        } catch (SQLException e1) {
            try { a.setOpenedAt(rs.getTimestamp("created_at")); } catch (SQLException ignored) {}
        }

        try {
            a.setClosedAt(rs.getTimestamp("closed_at"));
        } catch (SQLException ignored) {}

        try { a.setAccountTypeName(rs.getString("account_type_name")); } catch (SQLException ignored) {}
        try { a.setBranchName(rs.getString("branch_name")); } catch (SQLException ignored) {}
        try { a.setIfscCode(rs.getString("ifsc_code")); } catch (SQLException ignored) {}
        try { a.setCustomerName(rs.getString("customer_name")); } catch (SQLException ignored) {}
        return a;
    }
}
