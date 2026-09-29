package com.skbank.dao;

import com.skbank.config.DatabaseConfig;
import com.skbank.model.Account;
import com.skbank.model.AccountType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {

    public List<Account> findByCustomerId(String customerId) throws SQLException {
        List<Account> list = new ArrayList<>();
        String sql = "SELECT a.*, at.type_name, b.ifsc_code AS ifsc, b.branch_name " +
                     "FROM accounts a " +
                     "JOIN account_types at ON a.type_id = at.type_id " +
                     "JOIN branches b ON a.branch_id = b.branch_id " +
                     "WHERE a.customer_id = ? ORDER BY a.created_at ASC";
        try (Connection conn = DatabaseConfig.getConnection();
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

    public Account findByAccountNumber(String accountNumber) throws SQLException {
        String sql = "SELECT a.*, at.type_name, b.ifsc_code AS ifsc, b.branch_name, CONCAT(c.first_name, ' ', c.last_name) AS customer_name " +
                     "FROM accounts a " +
                     "JOIN account_types at ON a.type_id = at.type_id " +
                     "JOIN branches b ON a.branch_id = b.branch_id " +
                     "JOIN customers c ON a.customer_id = c.customer_id " +
                     "WHERE a.account_number = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapAccount(rs);
                }
            }
        }
        return null;
    }

    public Account findByAccountId(int accountId) throws SQLException {
        String sql = "SELECT a.*, at.type_name, b.ifsc_code AS ifsc, b.branch_name, CONCAT(c.first_name, ' ', c.last_name) AS customer_name " +
                     "FROM accounts a " +
                     "JOIN account_types at ON a.type_id = at.type_id " +
                     "JOIN branches b ON a.branch_id = b.branch_id " +
                     "JOIN customers c ON a.customer_id = c.customer_id " +
                     "WHERE a.account_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapAccount(rs);
                }
            }
        }
        return null;
    }

    public boolean createAccount(Account account) throws SQLException {
        String sql = "INSERT INTO accounts (account_number, customer_id, type_id, branch_id, balance, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, account.getAccountNumber());
            ps.setString(2, account.getCustomerId());
            ps.setInt(3, account.getTypeId());
            ps.setInt(4, account.getBranchId() > 0 ? account.getBranchId() : 1);
            ps.setDouble(5, account.getBalance());
            ps.setString(6, account.getStatus() != null ? account.getStatus() : "ACTIVE");
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateStatus(int accountId, String status) throws SQLException {
        String sql = "UPDATE accounts SET status = ? WHERE account_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, accountId);
            return ps.executeUpdate() > 0;
        }
    }

    public double getTotalBalanceByCustomer(String customerId) throws SQLException {
        String sql = "SELECT SUM(balance) FROM accounts WHERE customer_id = ? AND status = 'ACTIVE'";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        }
        return 0.0;
    }

    public double getTotalBankDeposits() throws SQLException {
        String sql = "SELECT SUM(balance) FROM accounts WHERE status = 'ACTIVE'";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        }
        return 0.0;
    }

    public int countTotalAccounts() throws SQLException {
        String sql = "SELECT COUNT(*) FROM accounts";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public List<Account> findAll() throws SQLException {
        List<Account> list = new ArrayList<>();
        String sql = "SELECT a.*, at.type_name, b.ifsc_code AS ifsc, b.branch_name, CONCAT(c.first_name, ' ', c.last_name) AS customer_name " +
                     "FROM accounts a " +
                     "JOIN account_types at ON a.type_id = at.type_id " +
                     "JOIN branches b ON a.branch_id = b.branch_id " +
                     "JOIN customers c ON a.customer_id = c.customer_id " +
                     "ORDER BY a.created_at DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapAccount(rs));
            }
        }
        return list;
    }

    public List<AccountType> getAllAccountTypes() throws SQLException {
        List<AccountType> list = new ArrayList<>();
        String sql = "SELECT * FROM account_types ORDER BY type_id ASC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                AccountType type = new AccountType();
                type.setTypeId(rs.getInt("type_id"));
                type.setTypeName(rs.getString("type_name"));
                type.setMinBalance(rs.getDouble("minimum_balance"));
                type.setInterestRate(rs.getDouble("interest_rate"));
                type.setDescription(rs.getString("description"));
                list.add(type);
            }
        }
        return list;
    }

    private Account mapAccount(ResultSet rs) throws SQLException {
        Account a = new Account();
        a.setAccountId(rs.getInt("account_id"));
        a.setAccountNumber(rs.getString("account_number"));
        a.setCustomerId(rs.getString("customer_id"));
        a.setTypeId(rs.getInt("type_id"));
        a.setBranchId(rs.getInt("branch_id"));
        a.setBalance(rs.getDouble("balance"));
        a.setStatus(rs.getString("status"));
        a.setCreatedAt(rs.getTimestamp("created_at"));

        try { a.setAccountTypeName(rs.getString("type_name")); } catch (Exception ignored) {}
        try { a.setIfscCode(rs.getString("ifsc")); } catch (Exception ignored) {}
        try { a.setBranchName(rs.getString("branch_name")); } catch (Exception ignored) {}
        try { a.setCustomerName(rs.getString("customer_name")); } catch (Exception ignored) {}

        return a;
    }
}
