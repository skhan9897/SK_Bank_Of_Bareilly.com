package com.skbank.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class Loan implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long loanId;
    private Long customerId;
    private Long loanTypeId;
    private String loanNumber;
    private BigDecimal principalAmount = BigDecimal.ZERO;
    private BigDecimal interestRate = BigDecimal.ZERO;
    private int tenureMonths;
    private BigDecimal emiAmount = BigDecimal.ZERO;
    private BigDecimal outstandingAmount = BigDecimal.ZERO;
    private LoanStatus status = LoanStatus.PENDING;
    private Timestamp appliedAt;
    private Timestamp approvedAt;

    // Joined fields
    private String loanTypeName;
    private String customerName;

    public Loan() {}

    public Long getLoanId() { return loanId; }
    public void setLoanId(Long loanId) { this.loanId = loanId; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public Long getLoanTypeId() { return loanTypeId; }
    public void setLoanTypeId(Long loanTypeId) { this.loanTypeId = loanTypeId; }

    public String getLoanNumber() { return loanNumber; }
    public void setLoanNumber(String loanNumber) { this.loanNumber = loanNumber; }

    public BigDecimal getPrincipalAmount() { return principalAmount; }
    public void setPrincipalAmount(BigDecimal principalAmount) { this.principalAmount = principalAmount; }

    public BigDecimal getInterestRate() { return interestRate; }
    public void setInterestRate(BigDecimal interestRate) { this.interestRate = interestRate; }

    public int getTenureMonths() { return tenureMonths; }
    public void setTenureMonths(int tenureMonths) { this.tenureMonths = tenureMonths; }

    public BigDecimal getEmiAmount() { return emiAmount; }
    public void setEmiAmount(BigDecimal emiAmount) { this.emiAmount = emiAmount; }

    public BigDecimal getOutstandingAmount() { return outstandingAmount; }
    public void setOutstandingAmount(BigDecimal outstandingAmount) { this.outstandingAmount = outstandingAmount; }

    public LoanStatus getStatus() { return status; }
    public void setStatus(LoanStatus status) { this.status = status; }

    public Timestamp getAppliedAt() { return appliedAt; }
    public void setAppliedAt(Timestamp appliedAt) { this.appliedAt = appliedAt; }

    public Timestamp getApprovedAt() { return approvedAt; }
    public void setApprovedAt(Timestamp approvedAt) { this.approvedAt = approvedAt; }

    public String getLoanTypeName() { return loanTypeName; }
    public void setLoanTypeName(String loanTypeName) { this.loanTypeName = loanTypeName; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
}
