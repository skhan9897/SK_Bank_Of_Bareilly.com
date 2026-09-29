package com.skbank.controller;

import com.skbank.dao.CustomerDAO;
import com.skbank.model.Account;
import com.skbank.model.Customer;
import com.skbank.model.Transaction;
import com.skbank.model.User;
import com.skbank.service.AccountService;
import com.skbank.service.CardService;
import com.skbank.service.LoanService;
import com.skbank.dao.TransactionDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    private final AccountService accountService = new AccountService();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final CardService cardService = new CardService();
    private final LoanService loanService = new LoanService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("loggedInUser") : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            Customer customer = customerDAO.findByUserId(user.getUserId());
            if (customer == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            session.setAttribute("customerProfile", customer);

            List<Account> accounts = accountService.getCustomerAccounts(customer.getCustomerId());
            double totalBalance = accountService.getCustomerTotalBalance(customer.getCustomerId());
            List<Transaction> recentTxns = transactionDAO.findByCustomerId(customer.getCustomerId(), 10);
            var cards = cardService.getCustomerCards(customer.getCustomerId());
            var loans = loanService.getCustomerLoans(customer.getCustomerId());

            request.setAttribute("accounts", accounts);
            request.setAttribute("totalBalance", totalBalance);
            request.setAttribute("recentTransactions", recentTxns);
            request.setAttribute("cards", cards);
            request.setAttribute("loans", loans);

            request.getRequestDispatcher("/dashboard.jsp").forward(request, response);

        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading dashboard data: " + e.getMessage());
            request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
        }
    }
}
