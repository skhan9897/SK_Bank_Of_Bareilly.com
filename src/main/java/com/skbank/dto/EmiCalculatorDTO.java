package com.skbank.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class EmiCalculatorDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private BigDecimal principalAmount = BigDecimal.ZERO;
    private BigDecimal annualInterestRate = BigDecimal.ZERO;
    private int tenureMonths;
    private BigDecimal emiAmount = BigDecimal.ZERO;
    private BigDecimal totalInterestPayable = BigDecimal.ZERO;
    private BigDecimal totalAmountPayable = BigDecimal.ZERO;

    public EmiCalculatorDTO() {}

    public BigDecimal getPrincipalAmount() { return principalAmount; }
    public void setPrincipalAmount(BigDecimal principalAmount) { this.principalAmount = principalAmount; }

    public BigDecimal getAnnualInterestRate() { return annualInterestRate; }
    public void setAnnualInterestRate(BigDecimal annualInterestRate) { this.annualInterestRate = annualInterestRate; }

    public int getTenureMonths() { return tenureMonths; }
    public void setTenureMonths(int tenureMonths) { this.tenureMonths = tenureMonths; }

    public BigDecimal getEmiAmount() { return emiAmount; }
    public void setEmiAmount(BigDecimal emiAmount) { this.emiAmount = emiAmount; }

    public BigDecimal getTotalInterestPayable() { return totalInterestPayable; }
    public void setTotalInterestPayable(BigDecimal totalInterestPayable) { this.totalInterestPayable = totalInterestPayable; }

    public BigDecimal getTotalAmountPayable() { return totalAmountPayable; }
    public void setTotalAmountPayable(BigDecimal totalAmountPayable) { this.totalAmountPayable = totalAmountPayable; }
}
