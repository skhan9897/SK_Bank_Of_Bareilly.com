package com.skbank.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class AdminDashboardDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private long totalCustomers = 0;
    private long activeAccounts = 0;
    private BigDecimal totalDeposits = BigDecimal.ZERO;
    private long todaysTransactionsCount = 0;
    private long pendingLoansCount = 0;
    private long activeLoansCount = 0;
    private long totalFdsCount = 0;
    private long pendingComplaintsCount = 0;

    public AdminDashboardDTO() {}

    public long getTotalCustomers() { return totalCustomers; }
    public void setTotalCustomers(long totalCustomers) { this.totalCustomers = totalCustomers; }

    public long getActiveAccounts() { return activeAccounts; }
    public void setActiveAccounts(long activeAccounts) { this.activeAccounts = activeAccounts; }

    public BigDecimal getTotalDeposits() { return totalDeposits; }
    public void setTotalDeposits(BigDecimal totalDeposits) { this.totalDeposits = totalDeposits; }

    public long getTodaysTransactionsCount() { return todaysTransactionsCount; }
    public void setTodaysTransactionsCount(long todaysTransactionsCount) { this.todaysTransactionsCount = todaysTransactionsCount; }

    public long getPendingLoansCount() { return pendingLoansCount; }
    public void setPendingLoansCount(long pendingLoansCount) { this.pendingLoansCount = pendingLoansCount; }

    public long getActiveLoansCount() { return activeLoansCount; }
    public void setActiveLoansCount(long activeLoansCount) { this.activeLoansCount = activeLoansCount; }

    public long getTotalFdsCount() { return totalFdsCount; }
    public void setTotalFdsCount(long totalFdsCount) { this.totalFdsCount = totalFdsCount; }

    public long getPendingComplaintsCount() { return pendingComplaintsCount; }
    public void setPendingComplaintsCount(long pendingComplaintsCount) { this.pendingComplaintsCount = pendingComplaintsCount; }
}
