package com.skbank.controller.admin;

import com.skbank.config.DatabaseConfig;
import com.skbank.dao.AccountDAO;
import com.skbank.dao.CardDAO;
import com.skbank.dao.CustomerDAO;
import com.skbank.dao.TransactionDAO;
import com.skbank.dao.UserDAO;
import com.skbank.model.Account;
import com.skbank.model.Card;
import com.skbank.model.Customer;
import com.skbank.model.Transaction;
import com.skbank.model.User;
import com.skbank.service.CardService;
import com.skbank.util.PasswordUtil;
import com.skbank.util.TransactionIdGenerator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin/customers")
public class AdminCustomerServlet extends HttpServlet {

    private final CustomerDAO customerDAO = new CustomerDAO();
    private final UserDAO userDAO = new UserDAO();
    private final AccountDAO accountDAO = new AccountDAO();
    private final CardDAO cardDAO = new CardDAO();
    private final CardService cardService = new CardService();
    private final TransactionDAO transactionDAO = new TransactionDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String customerId = request.getParameter("id");
        if (customerId == null || customerId.trim().isEmpty()) {
            customerId = request.getParameter("customerId");
        }

        try {
            if (customerId != null && !customerId.trim().isEmpty()) {
                Customer customer = customerDAO.findByCustomerId(customerId.trim());
                if (customer != null) {
                    User user = userDAO.findById(customer.getUserId());
                    List<Account> accounts = accountDAO.findByCustomerId(customer.getCustomerId());
                    List<Card> cards = cardDAO.findByCustomerId(customer.getCustomerId());
                    List<Transaction> transactions = transactionDAO.findByCustomerId(customer.getCustomerId(), 50);

                    request.setAttribute("selectedCustomer", customer);
                    request.setAttribute("selectedUser", user);
                    request.setAttribute("customerAccounts", accounts);
                    request.setAttribute("customerCards", cards);
                    request.setAttribute("customerTransactions", transactions);

                    request.getRequestDispatcher("/admin/customer-detail.jsp").forward(request, response);
                    return;
                }
            }

            // List view
            String searchQuery = request.getParameter("search");
            List<Customer> customers;
            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                customers = customerDAO.searchCustomers(searchQuery.trim());
            } else {
                customers = customerDAO.findAll();
            }
            request.setAttribute("customers", customers);
            request.setAttribute("searchQuery", searchQuery);
            request.getRequestDispatcher("/admin/customers.jsp").forward(request, response);

        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading customer details: " + e.getMessage());
            request.getRequestDispatcher("/admin/customers.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        String customerId = request.getParameter("customerId");

        try {
            Customer customer = customerDAO.findByCustomerId(customerId);
            if (customer == null) {
                request.setAttribute("errorMessage", "Customer not found.");
                doGet(request, response);
                return;
            }

            if ("updateUserStatus".equalsIgnoreCase(action)) {
                String status = request.getParameter("status");
                userDAO.updateStatus(customer.getUserId(), status);
                request.setAttribute("successMessage", "Customer account status updated to " + status + ".");

            } else if ("updateKycStatus".equalsIgnoreCase(action)) {
                String kycStatus = request.getParameter("kycStatus");
                customerDAO.updateKycStatus(customerId, kycStatus);
                request.setAttribute("successMessage", "Customer KYC Status updated to " + kycStatus + ".");

            } else if ("adminAdjustBalance".equalsIgnoreCase(action)) {
                int accountId = Integer.parseInt(request.getParameter("accountId"));
                String type = request.getParameter("type"); // CREDIT or DEBIT
                double amount = Double.parseDouble(request.getParameter("amount"));
                String remark = request.getParameter("remark");

                Account acc = accountDAO.findByAccountId(accountId);
                if (acc != null) {
                    double newBalance = "CREDIT".equalsIgnoreCase(type) ? acc.getBalance() + amount : acc.getBalance() - amount;
                    if (newBalance < 0) {
                        throw new IllegalStateException("Insufficient funds in account for debit adjustment.");
                    }

                    // Update account balance
                    acc.setBalance(newBalance);
                    String updateSql = "UPDATE accounts SET balance = " + newBalance + " WHERE account_id = " + accountId;
                    try (var conn = DatabaseConfig.getConnection();
                         var stmt = conn.createStatement()) {
                        stmt.executeUpdate(updateSql);
                    }

                    // Record Audit Transaction
                    Transaction txn = new Transaction();
                    txn.setTransactionId(TransactionIdGenerator.generateTransactionId());
                    txn.setAccountId(accountId);
                    txn.setType("CREDIT".equalsIgnoreCase(type) ? "DEPOSIT" : "WITHDRAWAL");
                    txn.setDirection(type);
                    txn.setAmount(amount);
                    txn.setBalanceAfter(newBalance);
                    txn.setReferenceNumber(TransactionIdGenerator.generateReferenceNumber());
                    txn.setDescription("Admin Adjustment (" + type + "): " + (remark != null ? remark : "Bank Officer Manual Adjustment"));
                    txn.setStatus("SUCCESS");
                    transactionDAO.createTransaction(txn);

                    request.setAttribute("successMessage", "Account balance adjusted by ₹" + amount + ". New Balance: ₹" + newBalance);
                }

            } else if ("adminResetPassword".equalsIgnoreCase(action)) {
                String newPassword = request.getParameter("newPassword");
                if (newPassword != null && newPassword.length() >= 8) {
                    userDAO.updatePassword(customer.getUserId(), PasswordUtil.hashPassword(newPassword));
                    request.setAttribute("successMessage", "Customer Internet Banking password reset successfully!");
                } else {
                    request.setAttribute("errorMessage", "New password must be at least 8 characters long.");
                }

            } else if ("adminIssueCard".equalsIgnoreCase(action)) {
                int accountId = Integer.parseInt(request.getParameter("accountId"));
                cardService.createDebitCardForCustomer(customerId, accountId, customer.getFullName());
                request.setAttribute("successMessage", "New Debit Card issued for customer!");

            } else if ("adminToggleCardBlock".equalsIgnoreCase(action)) {
                int cardId = Integer.parseInt(request.getParameter("cardId"));
                String currentStatus = request.getParameter("currentStatus");
                cardService.toggleCardBlock(cardId, currentStatus);
                request.setAttribute("successMessage", "Card status updated successfully.");
            }

        } catch (Exception e) {
            request.setAttribute("errorMessage", "Admin Action Error: " + e.getMessage());
        }

        // Forward back to Customer Detail View
        request.setAttribute("id", customerId);
        doGet(request, response);
    }
}
