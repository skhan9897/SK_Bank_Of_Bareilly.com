package com.skbank.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;

public class FixedDeposit implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long fdId;
    private String customerId;
    private Long accountId;
    private String fdNumber;
    private BigDecimal principalAmount = BigDecimal.ZERO;
    private BigDecimal interestRate = BigDecimal.ZERO;
    private int tenureMonths;
    private BigDecimal maturityAmount = BigDecimal.ZERO;
    private Date startDate;
    private Date maturityDate;
    private String status = "ACTIVE";

    // Joined fields
    private String accountNumber;
    private String customerName;

    public FixedDeposit() {}

    public Long getFdId() { return fdId; }
    public void setFdId(Long fdId) { this.fdId = fdId; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

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

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
}
