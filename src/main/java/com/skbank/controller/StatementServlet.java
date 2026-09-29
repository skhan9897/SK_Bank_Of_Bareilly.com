package com.skbank.controller;

import com.skbank.dao.TransactionDAO;
import com.skbank.model.Account;
import com.skbank.model.Customer;
import com.skbank.model.Transaction;
import com.skbank.service.AccountService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/statements")
public class StatementServlet extends HttpServlet {

    private final AccountService accountService = new AccountService();
    private final TransactionDAO transactionDAO = new TransactionDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Customer customer = (session != null) ? (Customer) session.getAttribute("customerProfile") : null;

        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            List<Account> accounts = accountService.getCustomerAccounts(customer.getCustomerId());
            String selectedAccStr = request.getParameter("accountId");
            String period = request.getParameter("period");
            if (period == null || period.isEmpty()) period = "30 Days";

            Account selectedAccount = null;
            List<Transaction> statementTxns = null;

            if (!accounts.isEmpty()) {
                if (selectedAccStr != null && !selectedAccStr.isEmpty()) {
                    int accId = Integer.parseInt(selectedAccStr);
                    selectedAccount = accountService.getAccountById(accId);
                } else {
                    selectedAccount = accounts.get(0);
                }

                if (selectedAccount != null) {
                    statementTxns = transactionDAO.filterTransactions(selectedAccount.getAccountId(), period);
                }
            }

            request.setAttribute("accounts", accounts);
            request.setAttribute("selectedAccount", selectedAccount);
            request.setAttribute("selectedPeriod", period);
            request.setAttribute("statementTransactions", statementTxns);

            request.getRequestDispatcher("/statements.jsp").forward(request, response);

        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error fetching statement: " + e.getMessage());
            request.getRequestDispatcher("/statements.jsp").forward(request, response);
        }
    }
}
