package com.skbank.controller.admin;

import com.skbank.config.DatabaseConfig;
import com.skbank.dao.AccountDAO;
import com.skbank.dao.CustomerDAO;
import com.skbank.dao.TransactionDAO;
import com.skbank.model.Account;
import com.skbank.model.Customer;
import com.skbank.model.Transaction;
import com.skbank.util.TransactionIdGenerator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

@WebServlet("/admin/deposit")
public class AdminDepositServlet extends HttpServlet {

    private final CustomerDAO customerDAO = new CustomerDAO();
    private final AccountDAO accountDAO = new AccountDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String searchQuery = request.getParameter("search");
        try {
            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                List<Customer> matches = customerDAO.searchCustomers(searchQuery.trim());
                if (!matches.isEmpty()) {
                    Customer selectedCustomer = matches.get(0);
                    List<Account> accounts = accountDAO.findByCustomerId(selectedCustomer.getCustomerId());
                    request.setAttribute("selectedCustomer", selectedCustomer);
                    request.setAttribute("customerAccounts", accounts);
                } else {
                    request.setAttribute("errorMessage", "No active customer account found for query: " + searchQuery);
                }
            }
            request.getRequestDispatcher("/admin/deposit.jsp").forward(request, response);

        } catch (Exception e) {
            request.setAttribute("errorMessage", "Search Error: " + e.getMessage());
            request.getRequestDispatcher("/admin/deposit.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accountIdStr = request.getParameter("accountId");
        String amountStr = request.getParameter("amount");
        String remarks = request.getParameter("remarks");

        Connection conn = null;
        try {
            int accountId = Integer.parseInt(accountIdStr);
            double depositAmount = Double.parseDouble(amountStr);

            if (depositAmount <= 0) {
                throw new IllegalArgumentException("Deposit amount must be greater than zero.");
            }

            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false); // Begin DB Transaction

            // 1. Lock Account Row
            String lockSql = "SELECT * FROM accounts WHERE account_id = ? FOR UPDATE";
            PreparedStatement psLock = conn.prepareStatement(lockSql);
            psLock.setInt(1, accountId);
            ResultSet rsAcc = psLock.executeQuery();

            if (!rsAcc.next()) {
                throw new IllegalArgumentException("Target account not found.");
            }

            double oldBalance = rsAcc.getDouble("balance");
            String accNo = rsAcc.getString("account_number");
            String custId = rsAcc.getString("customer_id");
            double newBalance = oldBalance + depositAmount;

            // 2. Update Balance and Available Balance
            String updateSql = "UPDATE accounts SET balance = ?, available_balance = ? WHERE account_id = ?";
            PreparedStatement psUpdate = conn.prepareStatement(updateSql);
            psUpdate.setDouble(1, newBalance);
            psUpdate.setDouble(2, newBalance);
            psUpdate.setInt(3, accountId);
            psUpdate.executeUpdate();

            // 3. Create Authorized Deposit Transaction
            String depRef = "SKDEP" + System.currentTimeMillis();
            String insertTxn = "INSERT INTO transactions (transaction_reference, account_id, type, direction, amount, balance_before, balance_after, sender_account, receiver_account, description, status) " +
                               "VALUES (?, ?, 'DEPOSIT', 'CREDIT', ?, ?, ?, 'CASH_COUNTER', ?, ?, 'SUCCESS')";
            PreparedStatement psTxn = conn.prepareStatement(insertTxn);
            psTxn.setString(1, depRef);
            psTxn.setInt(2, accountId);
            psTxn.setDouble(3, depositAmount);
            psTxn.setDouble(4, oldBalance);
            psTxn.setDouble(5, newBalance);
            psTxn.setString(6, accNo);
            psTxn.setString(7, "Authorized Branch Counter Deposit. " + (remarks != null ? remarks : ""));
            psTxn.executeUpdate();

            // Commit
            conn.commit();

            Customer customer = customerDAO.findByCustomerId(custId);
            Account updatedAcc = accountDAO.findByAccountId(accountId);

            Transaction completedTxn = new Transaction();
            completedTxn.setTransactionId(depRef);
            completedTxn.setAccountId(accountId);
            completedTxn.setType("DEPOSIT");
            completedTxn.setDirection("CREDIT");
            completedTxn.setAmount(depositAmount);
            completedTxn.setBalanceBefore(oldBalance);
            completedTxn.setBalanceAfter(newBalance);
            completedTxn.setAccountNumber(accNo);
            completedTxn.setDescription("Counter Cash Deposit by Admin Officer");

            request.setAttribute("successMessage", "Deposit of ₹" + depositAmount + " completed successfully!");
            request.setAttribute("completedDeposit", completedTxn);
            request.setAttribute("depositCustomer", customer);
            request.setAttribute("depositAccount", updatedAcc);

        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (Exception ignored) {}
            }
            request.setAttribute("errorMessage", "Deposit Error: " + e.getMessage());
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (Exception ignored) {}
            }
        }

        request.getRequestDispatcher("/admin/deposit.jsp").forward(request, response);
    }
}
