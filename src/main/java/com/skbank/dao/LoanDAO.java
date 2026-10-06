package com.skbank.dao;

import com.skbank.model.Loan;
import com.skbank.model.LoanType;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface LoanDAO {
    List<LoanType> findAllLoanTypes() throws SQLException;
    LoanType findLoanTypeById(Long loanTypeId) throws SQLException;
    List<Loan> findByCustomerId(Long customerId) throws SQLException;
    Loan findById(Long loanId) throws SQLException;
    Long create(Loan loan) throws SQLException;
    boolean updateOutstandingAmount(Connection conn, Long loanId, BigDecimal newOutstanding) throws SQLException;
    boolean updateStatusAndApproval(Long loanId, String status) throws SQLException;
    List<Loan> findAllAdmin(int offset, int limit, String statusFilter) throws SQLException;
    long countAllAdmin(String statusFilter) throws SQLException;
    long countPendingLoans() throws SQLException;
    long countActiveLoans() throws SQLException;
    int countActiveLoansByCustomerId(Long customerId) throws SQLException;
    BigDecimal getTotalOutstandingByCustomerId(Long customerId) throws SQLException;
}
