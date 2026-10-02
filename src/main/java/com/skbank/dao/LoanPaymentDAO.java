package com.skbank.dao;

import com.skbank.model.LoanPayment;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface LoanPaymentDAO {
    Long create(Connection conn, LoanPayment payment) throws SQLException;
    List<LoanPayment> findByLoanId(Long loanId) throws SQLException;
}
