package com.skbank.controller.admin;

import com.skbank.dao.AccountDAO;
import com.skbank.dao.CustomerDAO;
import com.skbank.dao.FDDAO;
import com.skbank.dao.LoanDAO;
import com.skbank.dao.TransactionDAO;
import com.skbank.model.Transaction;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {

    private final CustomerDAO customerDAO = new CustomerDAO();
    private final AccountDAO accountDAO = new AccountDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final LoanDAO loanDAO = new LoanDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int totalCustomers = customerDAO.countTotalCustomers();
            int totalAccounts = accountDAO.countTotalAccounts();
            double totalDeposits = accountDAO.getTotalBankDeposits();
            double activeLoans = loanDAO.getTotalActiveLoanOutstanding();
            int todayTxnCount = transactionDAO.countTodayTransactions();
            double todayTxnVolume = transactionDAO.getTodayTransactionVolume();
            List<Transaction> recentTransactions = transactionDAO.findAll(10);

            request.setAttribute("totalCustomers", totalCustomers);
            request.setAttribute("totalAccounts", totalAccounts);
            request.setAttribute("totalDeposits", totalDeposits);
            request.setAttribute("activeLoans", activeLoans);
            request.setAttribute("todayTxnCount", todayTxnCount);
            request.setAttribute("todayTxnVolume", todayTxnVolume);
            request.setAttribute("recentTransactions", recentTransactions);

            request.getRequestDispatcher("/admin/dashboard.jsp").forward(request, response);

        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading admin metrics: " + e.getMessage());
            request.getRequestDispatcher("/admin/dashboard.jsp").forward(request, response);
        }
    }
}
