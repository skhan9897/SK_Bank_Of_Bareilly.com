package com.skbank.dao.impl;

import com.skbank.dao.LoanPaymentDAO;
import com.skbank.model.LoanPayment;
import com.skbank.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoanPaymentDAOImpl implements LoanPaymentDAO {

    @Override
    public Long create(Connection conn, LoanPayment lp) throws SQLException {
        String sql = "INSERT INTO loan_payments (loan_id, amount, principal_component, interest_component, payment_reference, payment_date, status) " +
                     "VALUES (?, ?, ?, ?, ?, NOW(), 'SUCCESS')";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, lp.getLoanId());
            ps.setBigDecimal(2, lp.getAmount());
            ps.setBigDecimal(3, lp.getPrincipalComponent());
            ps.setBigDecimal(4, lp.getInterestComponent());
            ps.setString(5, lp.getPaymentReference());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        }
        return null;
    }

    @Override
    public List<LoanPayment> findByLoanId(Long loanId) throws SQLException {
        List<LoanPayment> list = new ArrayList<>();
        String sql = "SELECT * FROM loan_payments WHERE loan_id = ? ORDER BY payment_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, loanId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LoanPayment lp = new LoanPayment();
                    lp.setPaymentId(rs.getLong("payment_id"));
                    lp.setLoanId(rs.getLong("loan_id"));
                    lp.setAmount(rs.getBigDecimal("amount"));
                    lp.setPrincipalComponent(rs.getBigDecimal("principal_component"));
                    lp.setInterestComponent(rs.getBigDecimal("interest_component"));
                    lp.setPaymentReference(rs.getString("payment_reference"));
                    lp.setPaymentDate(rs.getTimestamp("payment_date"));
                    lp.setStatus(rs.getString("status"));
                    list.add(lp);
                }
            }
        }
        return list;
    }
}
