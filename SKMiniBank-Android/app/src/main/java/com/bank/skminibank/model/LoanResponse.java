package com.bank.skminibank.model;

public class LoanResponse {
    private boolean success;
    private String message;
    private int loanId;
    private String accountNumber;
    private String loanType;
    private double loanAmount;
    private double interestRate;
    private int tenureMonths;
    private double monthlyIncome;
    private String purpose;
    private String status;
    private String remarks;
    private String approvedBy;
    private String requestDate;
    private String approvalDate;
    private String disbursementDate;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public int getLoanId() { return loanId; }
    public String getAccountNumber() { return accountNumber; }
    public String getLoanType() { return loanType; }
    public double getLoanAmount() { return loanAmount; }
    public double getInterestRate() { return interestRate; }
    public int getTenureMonths() { return tenureMonths; }
    public double getMonthlyIncome() { return monthlyIncome; }
    public String getPurpose() { return purpose; }
    public String getStatus() { return status; }
    public String getRemarks() { return remarks; }
    public String getApprovedBy() { return approvedBy; }
    public String getRequestDate() { return requestDate; }
    public String getApprovalDate() { return approvalDate; }
    public String getDisbursementDate() { return disbursementDate; }
}
