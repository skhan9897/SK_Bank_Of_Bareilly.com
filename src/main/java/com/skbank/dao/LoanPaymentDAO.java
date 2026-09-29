package com.skbank.dao;

import com.skbank.util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LoanPaymentDAO {

    public boolean recordEmiPayment(long loanId, int installmentNo, Date dueDate, double principal, double interest, double emi, double outstandingAfter) throws SQLException {
        String sql = "INSERT INTO loan_payments (loan_id, installment_number, due_date, payment_date, principal_amount, interest_amount, emi_amount, outstanding_after_payment, status) " +
                     "VALUES (?, ?, ?, NOW(), ?, ?, ?, ?, 'PAID')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, loanId);
            ps.setInt(2, installmentNo);
            ps.setDate(3, dueDate);
            ps.setDouble(4, principal);
            ps.setDouble(5, interest);
            ps.setDouble(6, emi);
            ps.setDouble(7, outstandingAfter);
            return ps.executeUpdate() > 0;
        }
    }
}
