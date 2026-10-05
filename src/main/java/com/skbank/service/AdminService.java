package com.skbank.service;

import com.skbank.dto.AdminDashboardDTO;
import com.skbank.exception.BankException;
import com.skbank.model.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface AdminService {
    AdminDashboardDTO getAdminDashboardData() throws BankException;

    // Customer Management
    List<Customer> getAllCustomers(int page, int pageSize, String search) throws BankException;
    long countCustomers(String search) throws BankException;
    boolean setCustomerStatus(Long customerId, String status) throws BankException;

    // Account Management
    List<Account> getAllAccounts(int page, int pageSize, String search) throws BankException;
    long countAccounts(String search) throws BankException;
    boolean setAccountStatus(Long accountId, String status) throws BankException;

    // Financial Operations
    Transaction processAdminDeposit(Long accountId, BigDecimal amount, String remarks, Long adminUserId) throws BankException;
    Transaction processAdminWithdrawal(Long accountId, BigDecimal amount, String reason, Long adminUserId) throws BankException;

    // Loan Operations
    List<Loan> getAllLoans(int page, int pageSize, String statusFilter) throws BankException;
    long countLoans(String statusFilter) throws BankException;
    boolean approveLoan(Long loanId, Long adminUserId) throws BankException;
    boolean rejectLoan(Long loanId, Long adminUserId) throws BankException;

    // Complaints
    List<Complaint> getAllComplaints(int page, int pageSize, String statusFilter) throws BankException;
    long countComplaints(String statusFilter) throws BankException;

    // Audit Logs
    List<AuditLog> getAuditLogs(int page, int pageSize, String module) throws BankException;
    long countAuditLogs(String module) throws BankException;

    // System Settings
    List<SystemSetting> getSystemSettings() throws BankException;
    boolean updateSystemSettings(Map<String, String> newSettings, Long adminUserId) throws BankException;
}
