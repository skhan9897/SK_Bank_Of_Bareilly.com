package com.skbank.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class LoanType implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long loanTypeId;
    private String loanTypeCode;
    private String loanName;
    private BigDecimal interestRate = BigDecimal.ZERO;
    private BigDecimal maxAmount = BigDecimal.ZERO;
    private int maxTenureMonths;

    public LoanType() {}

    public Long getLoanTypeId() { return loanTypeId; }
    public void setLoanTypeId(Long loanTypeId) { this.loanTypeId = loanTypeId; }

    public String getLoanTypeCode() { return loanTypeCode; }
    public void setLoanTypeCode(String loanTypeCode) { this.loanTypeCode = loanTypeCode; }

    public String getLoanName() { return loanName; }
    public void setLoanName(String loanName) { this.loanName = loanName; }

    public BigDecimal getInterestRate() { return interestRate; }
    public void setInterestRate(BigDecimal interestRate) { this.interestRate = interestRate; }

    public BigDecimal getMaxAmount() { return maxAmount; }
    public void setMaxAmount(BigDecimal maxAmount) { this.maxAmount = maxAmount; }

    public int getMaxTenureMonths() { return maxTenureMonths; }
    public void setMaxTenureMonths(int maxTenureMonths) { this.maxTenureMonths = maxTenureMonths; }
}
