package com.skbankofbareilly.mobile.model;

import java.math.BigDecimal;

public class LoanType {
    private Long loanTypeId;
    private String loanCode;
    private String loanName;
    private String description;
    private BigDecimal interestRate = BigDecimal.ZERO;
    private BigDecimal maxAmount = BigDecimal.ZERO;
    private int maxTenureMonths;

    public LoanType() {}

    public Long getLoanTypeId() { return loanTypeId; }
    public void setLoanTypeId(Long loanTypeId) { this.loanTypeId = loanTypeId; }

    public String getLoanCode() { return loanCode; }
    public void setLoanCode(String loanCode) { this.loanCode = loanCode; }

    public String getLoanName() { return loanName; }
    public void setLoanName(String loanName) { this.loanName = loanName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getInterestRate() { return interestRate; }
    public void setInterestRate(BigDecimal interestRate) { this.interestRate = interestRate; }

    public BigDecimal getMaxAmount() { return maxAmount; }
    public void setMaxAmount(BigDecimal maxAmount) { this.maxAmount = maxAmount; }

    public int getMaxTenureMonths() { return maxTenureMonths; }
    public void setMaxTenureMonths(int maxTenureMonths) { this.maxTenureMonths = maxTenureMonths; }
}
