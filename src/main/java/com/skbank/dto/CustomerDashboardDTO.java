package com.skbank.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class CustomerDashboardDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private BigDecimal totalBalance = BigDecimal.ZERO;
    private BigDecimal availableBalance = BigDecimal.ZERO;
    private BigDecimal totalDeposit = BigDecimal.ZERO;
    private int activeLoansCount = 0;
    private BigDecimal loanOutstandingAmount = BigDecimal.ZERO;
    private BigDecimal fdInvestmentAmount = BigDecimal.ZERO;
    private String kycStatus = "PENDING";
    private int totalAccountsCount = 0;
    private int unreadNotificationsCount = 0;

    public CustomerDashboardDTO() {}

    public BigDecimal getTotalBalance() { return totalBalance; }
    public void setTotalBalance(BigDecimal totalBalance) { this.totalBalance = totalBalance; }

    public BigDecimal getAvailableBalance() { return availableBalance; }
    public void setAvailableBalance(BigDecimal availableBalance) { this.availableBalance = availableBalance; }

    public BigDecimal getTotalDeposit() { return totalDeposit; }
    public void setTotalDeposit(BigDecimal totalDeposit) { this.totalDeposit = totalDeposit; }

    public int getActiveLoansCount() { return activeLoansCount; }
    public void setActiveLoansCount(int activeLoansCount) { this.activeLoansCount = activeLoansCount; }

    public BigDecimal getLoanOutstandingAmount() { return loanOutstandingAmount; }
    public void setLoanOutstandingAmount(BigDecimal loanOutstandingAmount) { this.loanOutstandingAmount = loanOutstandingAmount; }

    public BigDecimal getFdInvestmentAmount() { return fdInvestmentAmount; }
    public void setFdInvestmentAmount(BigDecimal fdInvestmentAmount) { this.fdInvestmentAmount = fdInvestmentAmount; }

    public String getKycStatus() { return kycStatus; }
    public void setKycStatus(String kycStatus) { this.kycStatus = kycStatus; }

    public int getTotalAccountsCount() { return totalAccountsCount; }
    public void setTotalAccountsCount(int totalAccountsCount) { this.totalAccountsCount = totalAccountsCount; }

    public int getUnreadNotificationsCount() { return unreadNotificationsCount; }
    public void setUnreadNotificationsCount(int unreadNotificationsCount) { this.unreadNotificationsCount = unreadNotificationsCount; }
}
