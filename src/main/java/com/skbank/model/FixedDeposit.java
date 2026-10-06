package com.skbank.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

public class FixedDeposit implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long fdId;
    private Long customerId;
    private Long accountId;
    private String fdNumber;
    private BigDecimal principalAmount = BigDecimal.ZERO;
    private BigDecimal interestRate = BigDecimal.ZERO;
    private int tenureMonths;
    private BigDecimal maturityAmount = BigDecimal.ZERO;
    private Date startDate;
    private Date maturityDate;
    private String status = "ACTIVE";
    private Timestamp createdAt;

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

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public Date getMaturityDate() { return maturityDate; }
    public void setMaturityDate(Date maturityDate) { this.maturityDate = maturityDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
