package com.skbank.controller.customer;

import com.skbank.dto.CustomerDashboardDTO;
import com.skbank.model.Account;
import com.skbank.model.Transaction;
import com.skbank.service.AccountService;
import com.skbank.service.CustomerService;
import com.skbank.service.TransactionService;
import com.skbank.service.impl.AccountServiceImpl;
import com.skbank.service.impl.CustomerServiceImpl;
import com.skbank.service.impl.TransactionServiceImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(urlPatterns = {"/customer/dashboard"})
public class CustomerDashboardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final CustomerService customerService = new CustomerServiceImpl();
    private final AccountService accountService = new AccountServiceImpl();
    private final TransactionService transactionService = new TransactionServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        String customerId = (String) session.getAttribute("CUSTOMER_ID");
        Long userId = (Long) session.getAttribute("USER_ID");

        try {
            CustomerDashboardDTO stats = customerService.getCustomerDashboardData(customerId, userId);
            List<Account> accounts = accountService.getCustomerAccounts(customerId);

            List<Transaction> recentTransactions = new ArrayList<>();
            if (!accounts.isEmpty()) {
                recentTransactions = transactionService.getAccountTransactions(accounts.get(0).getAccountId(), 1, 5);
            }

            request.setAttribute("stats", stats);
            request.setAttribute("accounts", accounts);
            request.setAttribute("recentTransactions", recentTransactions);

            request.getRequestDispatcher("/WEB-INF/views/customer/dashboard.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading dashboard: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/customer/dashboard.jsp").forward(request, response);
        }
    }
}
