package com.skbank.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class AccountType implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long accountTypeId;
    private String typeCode;
    private String typeName;
    private String description;
    private BigDecimal minimumBalance;
    private BigDecimal interestRate;
    private String status;
    private Timestamp createdAt;

    public AccountType() {}

    public Long getAccountTypeId() { return accountTypeId; }
    public void setAccountTypeId(Long accountTypeId) { this.accountTypeId = accountTypeId; }

    public Long getTypeId() { return accountTypeId; }
    public void setTypeId(Long typeId) { this.accountTypeId = typeId; }

    public String getTypeCode() { return typeCode; }
    public void setTypeCode(String typeCode) { this.typeCode = typeCode; }

    public String getTypeName() { return typeName; }
    public void setTypeName(String typeName) { this.typeName = typeName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getMinimumBalance() { return minimumBalance; }
    public void setMinimumBalance(BigDecimal minimumBalance) { this.minimumBalance = minimumBalance; }

    public BigDecimal getInterestRate() { return interestRate; }
    public void setInterestRate(BigDecimal interestRate) { this.interestRate = interestRate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
