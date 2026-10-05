package com.skbank.dao;

import com.skbank.model.Loan;
import com.skbank.model.LoanType;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface LoanDAO {
    Loan findById(Long loanId) throws SQLException;
    Loan findByLoanNumber(String loanNumber) throws SQLException;
    List<Loan> findByCustomerId(String customerId) throws SQLException;
    Long create(Loan loan) throws SQLException;
    boolean updateStatusAndApproval(Long loanId, String status) throws SQLException;
    boolean updateOutstandingAmount(Connection conn, Long loanId, BigDecimal newOutstanding) throws SQLException;
    List<LoanType> findAllLoanTypes() throws SQLException;
    LoanType findLoanTypeById(Long loanTypeId) throws SQLException;
    int countActiveLoansByCustomerId(String customerId) throws SQLException;
    BigDecimal getTotalOutstandingByCustomerId(String customerId) throws SQLException;
    long countPendingLoans() throws SQLException;
    long countActiveLoans() throws SQLException;
    List<Loan> findAllAdmin(int offset, int limit, String statusFilter) throws SQLException;
    long countAllAdmin(String statusFilter) throws SQLException;
}
