package com.skbank.controller.api;

import com.google.gson.Gson;
import com.skbank.dto.ApiResponse;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = {"/api/customer/dashboard"})
public class CustomerDashboardApiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final CustomerService customerService = new CustomerServiceImpl();
    private final AccountService accountService = new AccountServiceImpl();
    private final TransactionService transactionService = new TransactionServiceImpl();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long customerId = (Long) request.getAttribute("API_CUSTOMER_ID");
        Long userId = (Long) request.getAttribute("API_USER_ID");

        try {
            CustomerDashboardDTO stats = customerService.getCustomerDashboardData(customerId, userId);
            List<Account> accounts = accountService.getCustomerAccounts(customerId);

            List<Transaction> recentTransactions = new ArrayList<>();
            if (!accounts.isEmpty()) {
                recentTransactions = transactionService.getAccountTransactions(accounts.get(0).getAccountId(), 1, 5);
            }

            Map<String, Object> data = new HashMap<>();
            data.put("stats", stats);
            data.put("accounts", accounts);
            data.put("recentTransactions", recentTransactions);

            response.getWriter().write(gson.toJson(ApiResponse.success("Dashboard data loaded", data)));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(ApiResponse.error(e.getMessage(), "SERVER_ERROR")));
        }
    }
}
