package com.skbank.dao.impl;

import com.skbank.dao.LoanDAO;
import com.skbank.model.Loan;
import com.skbank.model.LoanStatus;
import com.skbank.model.LoanType;
import com.skbank.util.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoanDAOImpl implements LoanDAO {

    @Override
    public List<LoanType> findAllLoanTypes() throws SQLException {
        List<LoanType> list = new ArrayList<>();
        String sql = "SELECT * FROM loan_types ORDER BY loan_type_id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapLoanType(rs));
        }
        return list;
    }

    @Override
    public LoanType findLoanTypeById(Long loanTypeId) throws SQLException {
        String sql = "SELECT * FROM loan_types WHERE loan_type_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, loanTypeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapLoanType(rs);
            }
        }
        return null;
    }

    @Override
    public List<Loan> findByCustomerId(Long customerId) throws SQLException {
        List<Loan> list = new ArrayList<>();
        String sql = "SELECT l.*, lt.loan_name FROM loans l LEFT JOIN loan_types lt ON l.loan_type_id = lt.loan_type_id WHERE l.customer_id = ? ORDER BY l.loan_id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapLoan(rs));
            }
        }
        return list;
    }

    @Override
    public Loan findById(Long loanId) throws SQLException {
        String sql = "SELECT l.*, lt.loan_name FROM loans l LEFT JOIN loan_types lt ON l.loan_type_id = lt.loan_type_id WHERE l.loan_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, loanId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapLoan(rs);
            }
        }
        return null;
    }

    @Override
    public Long create(Loan loan) throws SQLException {
        String sql = "INSERT INTO loans (customer_id, loan_type_id, loan_number, principal_amount, interest_rate, tenure_months, emi_amount, outstanding_amount, status, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, loan.getCustomerId());
            ps.setLong(2, loan.getLoanTypeId());
            ps.setString(3, loan.getLoanNumber());
            ps.setBigDecimal(4, loan.getPrincipalAmount());
            ps.setBigDecimal(5, loan.getInterestRate());
            ps.setInt(6, loan.getTenureMonths());
            ps.setBigDecimal(7, loan.getEmiAmount());
            ps.setBigDecimal(8, loan.getOutstandingAmount());
            ps.setString(9, loan.getStatus() != null ? loan.getStatus().name() : LoanStatus.PENDING.name());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        }
        return null;
    }

    @Override
    public boolean updateOutstandingAmount(Connection conn, Long loanId, BigDecimal newOutstanding) throws SQLException {
        String sql = "UPDATE loans SET outstanding_amount = ?, updated_at = NOW() WHERE loan_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, newOutstanding);
            ps.setLong(2, loanId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateStatusAndApproval(Long loanId, String status) throws SQLException {
        String sql = "UPDATE loans SET status = ?, updated_at = NOW() WHERE loan_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setLong(2, loanId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public List<Loan> findAllAdmin(int offset, int limit, String statusFilter) throws SQLException {
        List<Loan> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT l.*, lt.loan_name FROM loans l LEFT JOIN loan_types lt ON l.loan_type_id = lt.loan_type_id ");
        if (statusFilter != null && !statusFilter.trim().isEmpty() && !"ALL".equalsIgnoreCase(statusFilter.trim())) {
            sql.append("WHERE l.status = ? ");
        }
        sql.append("ORDER BY l.loan_id DESC LIMIT ? OFFSET ?");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            if (statusFilter != null && !statusFilter.trim().isEmpty() && !"ALL".equalsIgnoreCase(statusFilter.trim())) {
                ps.setString(idx++, statusFilter.trim());
            }
            ps.setInt(idx++, limit);
            ps.setInt(idx, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapLoan(rs));
            }
        }
        return list;
    }

    @Override
    public long countAllAdmin(String statusFilter) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM loans ");
        if (statusFilter != null && !statusFilter.trim().isEmpty() && !"ALL".equalsIgnoreCase(statusFilter.trim())) {
            sql.append("WHERE status = ? ");
        }
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            if (statusFilter != null && !statusFilter.trim().isEmpty() && !"ALL".equalsIgnoreCase(statusFilter.trim())) {
                ps.setString(1, statusFilter.trim());
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
        }
        return 0;
    }

    @Override
    public long countPendingLoans() throws SQLException {
        String sql = "SELECT COUNT(*) FROM loans WHERE status = 'PENDING'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getLong(1);
        }
        return 0;
    }

    @Override
    public long countActiveLoans() throws SQLException {
        String sql = "SELECT COUNT(*) FROM loans WHERE status = 'APPROVED' OR status = 'DISBURSED'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getLong(1);
        }
        return 0;
    }

    @Override
    public int countActiveLoansByCustomerId(Long customerId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM loans WHERE customer_id = ? AND (status = 'APPROVED' OR status = 'DISBURSED')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    @Override
    public BigDecimal getTotalOutstandingByCustomerId(Long customerId) throws SQLException {
        String sql = "SELECT SUM(outstanding_amount) FROM loans WHERE customer_id = ? AND (status = 'APPROVED' OR status = 'DISBURSED')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal sum = rs.getBigDecimal(1);
                    return sum != null ? sum : BigDecimal.ZERO;
                }
            }
        }
        return BigDecimal.ZERO;
    }

    private LoanType mapLoanType(ResultSet rs) throws SQLException {
        LoanType lt = new LoanType();
        lt.setLoanTypeId(rs.getLong("loan_type_id"));
        lt.setLoanTypeCode(rs.getString("type_code"));
        lt.setLoanName(rs.getString("loan_name"));
        lt.setInterestRate(rs.getBigDecimal("interest_rate"));
        lt.setMaxAmount(rs.getBigDecimal("max_amount"));
        lt.setMaxTenureMonths(rs.getInt("max_tenure_months"));
        return lt;
    }

    private Loan mapLoan(ResultSet rs) throws SQLException {
        Loan l = new Loan();
        l.setLoanId(rs.getLong("loan_id"));
        l.setCustomerId(rs.getLong("customer_id"));
        l.setLoanTypeId(rs.getLong("loan_type_id"));
        l.setLoanNumber(rs.getString("loan_number"));
        l.setPrincipalAmount(rs.getBigDecimal("principal_amount"));
        l.setInterestRate(rs.getBigDecimal("interest_rate"));
        l.setTenureMonths(rs.getInt("tenure_months"));
        l.setEmiAmount(rs.getBigDecimal("emi_amount"));
        l.setOutstandingAmount(rs.getBigDecimal("outstanding_amount"));
        try { l.setStatus(LoanStatus.valueOf(rs.getString("status"))); } catch (Exception ignored) {}
        l.setCreatedAt(rs.getTimestamp("created_at"));
        l.setUpdatedAt(rs.getTimestamp("updated_at"));

        try { l.setLoanTypeName(rs.getString("loan_name")); } catch (Exception ignored) {}
        return l;
    }
}
