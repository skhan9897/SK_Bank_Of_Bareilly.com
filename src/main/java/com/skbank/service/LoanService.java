package com.skbank.service;

import com.skbank.dto.EmiCalculatorDTO;
import com.skbank.exception.BankException;
import com.skbank.model.Loan;
import com.skbank.model.LoanPayment;
import com.skbank.model.LoanType;

import java.math.BigDecimal;
import java.util.List;

public interface LoanService {
    EmiCalculatorDTO calculateEmi(BigDecimal principal, BigDecimal annualRate, int tenureMonths);
    List<LoanType> getAllLoanTypes() throws BankException;
    Loan applyLoan(Long customerId, Long loanTypeId, BigDecimal principal, int tenureMonths) throws BankException;
    List<Loan> getCustomerLoans(Long customerId) throws BankException;
    Loan getLoanById(Long loanId) throws BankException;
    LoanPayment payEmi(Long loanId, Long accountId, Long customerId) throws BankException;
    List<LoanPayment> getLoanPayments(Long loanId) throws BankException;
}
