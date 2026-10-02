package com.skbankofbareilly.mobile.model;

import java.math.BigDecimal;

public class FixedDeposit {
    private Long fdId;
    private Long customerId;
    private Long accountId;
    private String fdNumber;
    private BigDecimal principalAmount = BigDecimal.ZERO;
    private BigDecimal interestRate = BigDecimal.ZERO;
    private int tenureMonths;
    private BigDecimal maturityAmount = BigDecimal.ZERO;
    private String startDate;
    private String maturityDate;
    private String status = "ACTIVE";

    public FixedDeposit() {}

    public Long getFdId() { return fdId; }
    public void setFdId(Long fdId) { this.fdId = fdId; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }

    public String getFdNumber() { return fdNumber; }
    public void setFdNumber(String fdNumber) { this.fdNumber = fdNumber; }

    public BigDecimal getPrincipalAmount() { return principalAmount; }
    public void setPrincipalAmount(BigDecimal principalAmount) { this.principalAmount = principalAmount; }

    public BigDecimal getInterestRate() { return interestRate; }
    public void setInterestRate(BigDecimal interestRate) { this.interestRate = interestRate; }

    public int getTenureMonths() { return tenureMonths; }
    public void setTenureMonths(int tenureMonths) { this.tenureMonths = tenureMonths; }

    public BigDecimal getMaturityAmount() { return maturityAmount; }
    public void setMaturityAmount(BigDecimal maturityAmount) { this.maturityAmount = maturityAmount; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getMaturityDate() { return maturityDate; }
    public void setMaturityDate(String maturityDate) { this.maturityDate = maturityDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
