package com.skbank.model;

import java.sql.Timestamp;

public class Loan {
    private int loanId;
    private String customerId;
    private String loanType; // Personal Loan, Home Loan, Car Loan, Education Loan, Business Loan
    private double principalAmount;
    private double interestRate;
    private int tenureMonths;
    private double monthlyEmi;
    private double outstandingAmount;
    private String purpose;
    private double monthlyIncome;
    private String employmentType;
    private String status; // APPLIED, UNDER_REVIEW, APPROVED, REJECTED, ACTIVE, CLOSED
    private Timestamp createdAt;

    // Joined fields
    private String customerName;

    public Loan() {}

    public int getLoanId() { return loanId; }
    public void setLoanId(int loanId) { this.loanId = loanId; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getLoanType() { return loanType; }
    public void setLoanType(String loanType) { this.loanType = loanType; }

    public double getPrincipalAmount() { return principalAmount; }
    public void setPrincipalAmount(double principalAmount) { this.principalAmount = principalAmount; }

    public double getInterestRate() { return interestRate; }
    public void setInterestRate(double interestRate) { this.interestRate = interestRate; }

    public int getTenureMonths() { return tenureMonths; }
    public void setTenureMonths(int tenureMonths) { this.tenureMonths = tenureMonths; }

    public double getMonthlyEmi() { return monthlyEmi; }
    public void setMonthlyEmi(double monthlyEmi) { this.monthlyEmi = monthlyEmi; }

    public double getOutstandingAmount() { return outstandingAmount; }
    public void setOutstandingAmount(double outstandingAmount) { this.outstandingAmount = outstandingAmount; }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }

    public double getMonthlyIncome() { return monthlyIncome; }
    public void setMonthlyIncome(double monthlyIncome) { this.monthlyIncome = monthlyIncome; }

    public String getEmploymentType() { return employmentType; }
    public void setEmploymentType(String employmentType) { this.employmentType = employmentType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
}
