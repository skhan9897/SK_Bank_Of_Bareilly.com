package com.skbank.dao;

import com.skbank.config.DatabaseConfig;
import com.skbank.model.Loan;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoanDAO {

    public List<Loan> findByCustomerId(String customerId) throws SQLException {
        List<Loan> list = new ArrayList<>();
        String sql = "SELECT * FROM loans WHERE customer_id = ? ORDER BY created_at DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapLoan(rs));
                }
            }
        }
        return list;
    }

    public Loan findById(int loanId) throws SQLException {
        String sql = "SELECT l.*, CONCAT(c.first_name, ' ', c.last_name) AS customer_name " +
                     "FROM loans l JOIN customers c ON l.customer_id = c.customer_id WHERE l.loan_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, loanId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapLoan(rs);
                }
            }
        }
        return null;
    }

    public boolean applyLoan(Loan loan) throws SQLException {
        String sql = "INSERT INTO loans (customer_id, loan_type, principal_amount, interest_rate, tenure_months, monthly_emi, outstanding_amount, purpose, monthly_income, employment_type, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, loan.getCustomerId());
            ps.setString(2, loan.getLoanType());
            ps.setDouble(3, loan.getPrincipalAmount());
            ps.setDouble(4, loan.getInterestRate());
            ps.setInt(5, loan.getTenureMonths());
            ps.setDouble(6, loan.getMonthlyEmi());
            ps.setDouble(7, loan.getPrincipalAmount()); // Initial outstanding = principal
            ps.setString(8, loan.getPurpose());
            ps.setDouble(9, loan.getMonthlyIncome());
            ps.setString(10, loan.getEmploymentType());
            ps.setString(11, loan.getStatus() != null ? loan.getStatus() : "APPLIED");
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateLoanStatus(int loanId, String status) throws SQLException {
        String sql = "UPDATE loans SET status = ? WHERE loan_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, loanId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateOutstandingAmount(int loanId, double newOutstanding) throws SQLException {
        String sql = "UPDATE loans SET outstanding_amount = ?, status = IF(? <= 0, 'CLOSED', status) WHERE loan_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, newOutstanding);
            ps.setDouble(2, newOutstanding);
            ps.setInt(3, loanId);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Loan> findAll() throws SQLException {
        List<Loan> list = new ArrayList<>();
        String sql = "SELECT l.*, CONCAT(c.first_name, ' ', c.last_name) AS customer_name " +
                     "FROM loans l JOIN customers c ON l.customer_id = c.customer_id ORDER BY l.created_at DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapLoan(rs));
            }
        }
        return list;
    }

    public double getTotalActiveLoanOutstanding() throws SQLException {
        String sql = "SELECT SUM(outstanding_amount) FROM loans WHERE status IN ('APPROVED', 'ACTIVE')";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        }
        return 0.0;
    }

    private Loan mapLoan(ResultSet rs) throws SQLException {
        Loan l = new Loan();
        l.setLoanId(rs.getInt("loan_id"));
        l.setCustomerId(rs.getString("customer_id"));
        l.setLoanType(rs.getString("loan_type"));
        l.setPrincipalAmount(rs.getDouble("principal_amount"));
        l.setInterestRate(rs.getDouble("interest_rate"));
        l.setTenureMonths(rs.getInt("tenure_months"));
        l.setMonthlyEmi(rs.getDouble("monthly_emi"));
        l.setOutstandingAmount(rs.getDouble("outstanding_amount"));
        l.setPurpose(rs.getString("purpose"));
        l.setMonthlyIncome(rs.getDouble("monthly_income"));
        l.setEmploymentType(rs.getString("employment_type"));
        l.setStatus(rs.getString("status"));
        l.setCreatedAt(rs.getTimestamp("created_at"));

        try { l.setCustomerName(rs.getString("customer_name")); } catch (Exception ignored) {}

        return l;
    }
}
