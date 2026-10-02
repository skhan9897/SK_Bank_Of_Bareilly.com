package com.skbank.service.impl;

import com.skbank.dao.*;
import com.skbank.dao.impl.*;
import com.skbank.dto.AdminDashboardDTO;
import com.skbank.exception.BankException;
import com.skbank.exception.InsufficientBalanceException;
import com.skbank.model.*;
import com.skbank.service.AdminService;
import com.skbank.util.DatabaseConnection;
import com.skbank.util.SystemSettingsUtil;
import com.skbank.util.ValidationUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AdminServiceImpl implements AdminService {

    private final CustomerDAO customerDAO = new CustomerDAOImpl();
    private final AccountDAO accountDAO = new AccountDAOImpl();
    private final TransactionDAO transactionDAO = new TransactionDAOImpl();
    private final LoanDAO loanDAO = new LoanDAOImpl();
    private final FixedDepositDAO fdDAO = new FixedDepositDAOImpl();
    private final ComplaintDAO complaintDAO = new ComplaintDAOImpl();
    private final NotificationDAO notificationDAO = new NotificationDAOImpl();
    private final AuditLogDAO auditLogDAO = new AuditLogDAOImpl();
    private final SystemSettingsDAO settingsDAO = new SystemSettingsDAOImpl();
    private final UserDAO userDAO = new UserDAOImpl();

    @Override
    public AdminDashboardDTO getAdminDashboardData() throws BankException {
        try {
            AdminDashboardDTO dto = new AdminDashboardDTO();
            dto.setTotalCustomers(customerDAO.countAll(null));
            dto.setActiveAccounts(accountDAO.countAll(null));
            dto.setTotalDeposits(accountDAO.getTotalBankBalance());
            dto.setTodaysTransactionsCount(transactionDAO.getTodaysTransactionCount());
            dto.setPendingLoansCount(loanDAO.countPendingLoans());
            dto.setActiveLoansCount(loanDAO.countActiveLoans());
            dto.setTotalFdsCount(fdDAO.countActiveFds());
            dto.setPendingComplaintsCount(complaintDAO.countPendingComplaints());
            return dto;
        } catch (Exception e) {
            throw new BankException("Error loading admin dashboard stats", e);
        }
    }

    @Override
    public List<Customer> getAllCustomers(int page, int pageSize, String search) throws BankException {
        try {
            int offset = (page - 1) * pageSize;
            return customerDAO.findAll(offset, pageSize, search);
        } catch (Exception e) {
            throw new BankException("Error fetching customers", e);
        }
    }

    @Override
    public long countCustomers(String search) throws BankException {
        try {
            return customerDAO.countAll(search);
        } catch (Exception e) {
            throw new BankException("Error counting customers", e);
        }
    }

    @Override
    public boolean setCustomerStatus(Long customerId, String status) throws BankException {
        try {
            Customer cust = customerDAO.findById(customerId);
            if (cust == null) throw new BankException("Customer not found");

            customerDAO.updateStatus(customerId, status);
            userDAO.updateStatus(cust.getUserId(), status);
            return true;
        } catch (Exception e) {
            throw new BankException("Error updating customer status", e);
        }
    }

    @Override
    public List<Account> getAllAccounts(int page, int pageSize, String search) throws BankException {
        try {
            int offset = (page - 1) * pageSize;
            return accountDAO.findAll(offset, pageSize, search);
        } catch (Exception e) {
            throw new BankException("Error fetching accounts", e);
        }
    }

    @Override
    public long countAccounts(String search) throws BankException {
        try {
            return accountDAO.countAll(search);
        } catch (Exception e) {
            throw new BankException("Error counting accounts", e);
        }
    }

    @Override
    public boolean setAccountStatus(Long accountId, String status) throws BankException {
        try {
            return accountDAO.updateStatus(accountId, status);
        } catch (Exception e) {
            throw new BankException("Error updating account status", e);
        }
    }

    @Override
    public Transaction processAdminDeposit(Long accountId, BigDecimal amount, String remarks, Long adminUserId) throws BankException {
        if (!ValidationUtil.isValidAmount(amount)) {
            throw new BankException("Deposit amount must be greater than zero");
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            Account account = accountDAO.findForUpdate(conn, accountId);
            if (account == null) throw new BankException("Account not found");
            if (account.getStatus() != AccountStatus.ACTIVE) {
                throw new BankException("Account is not active");
            }

            BigDecimal before = account.getBalance();
            BigDecimal after = before.add(amount);

            accountDAO.updateBalance(conn, accountId, after, after);

            String ref = "SKDEP" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 3).toUpperCase();
            Transaction txn = new Transaction();
            txn.setTransactionReference(ref);
            txn.setAccountId(accountId);
            txn.setTransactionType(TransactionType.DEPOSIT);
            txn.setAmount(amount);
            txn.setBalanceBefore(before);
            txn.setBalanceAfter(after);
            txn.setDescription("Admin Cash Deposit: " + (remarks != null && !remarks.trim().isEmpty() ? remarks : "Counter Deposit"));
            txn.setStatus(TransactionStatus.SUCCESS);

            Long txnId = transactionDAO.create(conn, txn);
            txn.setTransactionId(txnId);

            Customer cust = customerDAO.findById(account.getCustomerId());
            if (cust != null) {
                Notification n = new Notification();
                n.setUserId(cust.getUserId());
                n.setTitle("Deposit Credited");
                n.setMessage("₹" + amount + " credited to account " + account.getMaskedAccountNumber() + ". Ref: " + ref);
                n.setNotificationType("DEPOSIT");
                notificationDAO.create(conn, n);
            }

            conn.commit();
            return txn;
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            throw new BankException("Deposit failed: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {}
            }
        }
    }

    @Override
    public Transaction processAdminWithdrawal(Long accountId, BigDecimal amount, String reason, Long adminUserId) throws BankException {
        if (!ValidationUtil.isValidAmount(amount)) {
            throw new BankException("Withdrawal amount must be greater than zero");
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            Account account = accountDAO.findForUpdate(conn, accountId);
            if (account == null) throw new BankException("Account not found");
            if (account.getStatus() != AccountStatus.ACTIVE) {
                throw new BankException("Account is not active");
            }
            if (account.getAvailableBalance().compareTo(amount) < 0) {
                throw new InsufficientBalanceException("Insufficient balance in account for withdrawal");
            }

            BigDecimal before = account.getBalance();
            BigDecimal after = before.subtract(amount);

            accountDAO.updateBalance(conn, accountId, after, after);

            String ref = "SKWD" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 3).toUpperCase();
            Transaction txn = new Transaction();
            txn.setTransactionReference(ref);
            txn.setAccountId(accountId);
            txn.setTransactionType(TransactionType.WITHDRAWAL);
            txn.setAmount(amount);
            txn.setBalanceBefore(before);
            txn.setBalanceAfter(after);
            txn.setDescription("Admin Authorized Withdrawal: " + (reason != null && !reason.trim().isEmpty() ? reason : "Counter Cash Withdrawal"));
            txn.setStatus(TransactionStatus.SUCCESS);

            Long txnId = transactionDAO.create(conn, txn);
            txn.setTransactionId(txnId);

            Customer cust = customerDAO.findById(account.getCustomerId());
            if (cust != null) {
                Notification n = new Notification();
                n.setUserId(cust.getUserId());
                n.setTitle("Withdrawal Debited");
                n.setMessage("₹" + amount + " debited from account " + account.getMaskedAccountNumber() + ". Ref: " + ref);
                n.setNotificationType("WITHDRAWAL");
                notificationDAO.create(conn, n);
            }

            conn.commit();
            return txn;
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            throw new BankException("Admin withdrawal failed: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {}
            }
        }
    }

    @Override
    public List<Loan> getAllLoans(int page, int pageSize, String statusFilter) throws BankException {
        try {
            int offset = (page - 1) * pageSize;
            return loanDAO.findAllAdmin(offset, pageSize, statusFilter);
        } catch (Exception e) {
            throw new BankException("Error fetching loans", e);
        }
    }

    @Override
    public long countLoans(String statusFilter) throws BankException {
        try {
            return loanDAO.countAllAdmin(statusFilter);
        } catch (Exception e) {
            throw new BankException("Error counting loans", e);
        }
    }

    @Override
    public boolean approveLoan(Long loanId, Long adminUserId) throws BankException {
        try {
            Loan loan = loanDAO.findById(loanId);
            if (loan == null) throw new BankException("Loan not found");
            if (loan.getStatus() != LoanStatus.PENDING) {
                throw new BankException("Loan is not in pending status");
            }

            boolean ok = loanDAO.updateStatusAndApproval(loanId, "APPROVED");
            if (ok) {
                Customer cust = customerDAO.findById(loan.getCustomerId());
                if (cust != null) {
                    Notification n = new Notification();
                    n.setUserId(cust.getUserId());
                    n.setTitle("Loan Approved!");
                    n.setMessage("Your loan " + loan.getLoanNumber() + " for ₹" + loan.getPrincipalAmount() + " has been approved!");
                    n.setNotificationType("LOAN");
                    notificationDAO.create(n);
                }
            }
            return ok;
        } catch (Exception e) {
            throw new BankException("Error approving loan", e);
        }
    }

    @Override
    public boolean rejectLoan(Long loanId, Long adminUserId) throws BankException {
        try {
            Loan loan = loanDAO.findById(loanId);
            if (loan == null) throw new BankException("Loan not found");

            boolean ok = loanDAO.updateStatusAndApproval(loanId, "REJECTED");
            if (ok) {
                Customer cust = customerDAO.findById(loan.getCustomerId());
                if (cust != null) {
                    Notification n = new Notification();
                    n.setUserId(cust.getUserId());
                    n.setTitle("Loan Application Update");
                    n.setMessage("Your loan application " + loan.getLoanNumber() + " was not approved.");
                    n.setNotificationType("LOAN");
                    notificationDAO.create(n);
                }
            }
            return ok;
        } catch (Exception e) {
            throw new BankException("Error rejecting loan", e);
        }
    }

    @Override
    public List<Complaint> getAllComplaints(int page, int pageSize, String statusFilter) throws BankException {
        try {
            int offset = (page - 1) * pageSize;
            return complaintDAO.findAllAdmin(offset, pageSize, statusFilter);
        } catch (Exception e) {
            throw new BankException("Error fetching complaints", e);
        }
    }

    @Override
    public long countComplaints(String statusFilter) throws BankException {
        try {
            return complaintDAO.countAllAdmin(statusFilter);
        } catch (Exception e) {
            throw new BankException("Error counting complaints", e);
        }
    }

    @Override
    public List<AuditLog> getAuditLogs(int page, int pageSize, String module) throws BankException {
        try {
            int offset = (page - 1) * pageSize;
            return auditLogDAO.findAll(offset, pageSize, module);
        } catch (Exception e) {
            throw new BankException("Error fetching audit logs", e);
        }
    }

    @Override
    public long countAuditLogs(String module) throws BankException {
        try {
            return auditLogDAO.countAll(module);
        } catch (Exception e) {
            throw new BankException("Error counting audit logs", e);
        }
    }

    @Override
    public List<SystemSetting> getSystemSettings() throws BankException {
        try {
            return settingsDAO.findAll();
        } catch (Exception e) {
            throw new BankException("Error fetching system settings", e);
        }
    }

    @Override
    public boolean updateSystemSettings(Map<String, String> newSettings, Long adminUserId) throws BankException {
        try {
            for (Map.Entry<String, String> entry : newSettings.entrySet()) {
                settingsDAO.updateSetting(entry.getKey(), entry.getValue());
            }
            SystemSettingsUtil.updateSettings(newSettings);
            return true;
        } catch (Exception e) {
            throw new BankException("Error updating system settings", e);
        }
    }
}
