package com.skbank.service;

import com.skbank.dao.LoanDAO;
import com.skbank.model.Loan;
import com.skbank.util.EmiCalculatorUtil;

import java.sql.SQLException;
import java.util.List;

public class LoanService {

    private final LoanDAO loanDAO = new LoanDAO();

    public boolean applyLoan(Loan loan) throws SQLException {
        if (loan.getPrincipalAmount() <= 0 || loan.getTenureMonths() <= 0) {
            throw new IllegalArgumentException("Loan amount and tenure must be greater than zero.");
        }

        // Standard interest rates per loan type
        double rate = 10.5;
        if ("Home Loan".equalsIgnoreCase(loan.getLoanType())) rate = 8.5;
        else if ("Car Loan".equalsIgnoreCase(loan.getLoanType())) rate = 9.25;
        else if ("Education Loan".equalsIgnoreCase(loan.getLoanType())) rate = 7.5;
        else if ("Business Loan".equalsIgnoreCase(loan.getLoanType())) rate = 11.0;

        loan.setInterestRate(rate);
        double emi = EmiCalculatorUtil.calculateEmi(loan.getPrincipalAmount(), rate, loan.getTenureMonths());
        loan.setMonthlyEmi(emi);
        loan.setStatus("APPLIED");

        return loanDAO.applyLoan(loan);
    }

    public List<Loan> getCustomerLoans(String customerId) throws SQLException {
        return loanDAO.findByCustomerId(customerId);
    }

    public List<Loan> getAllLoanApplications() throws SQLException {
        return loanDAO.findAll();
    }

    public boolean updateStatus(int loanId, String status) throws SQLException {
        return loanDAO.updateLoanStatus(loanId, status);
    }
}
