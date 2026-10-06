package com.skbank.controller.api;

import com.google.gson.Gson;
import com.skbank.dto.ApiResponse;
import com.skbank.model.Account;
import com.skbank.model.Transaction;
import com.skbank.service.AccountService;
import com.skbank.service.TransactionService;
import com.skbank.service.impl.AccountServiceImpl;
import com.skbank.service.impl.TransactionServiceImpl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = {"/api/customer/accounts", "/api/customer/account-details"})
public class AccountApiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final AccountService accountService = new AccountServiceImpl();
    private final TransactionService transactionService = new TransactionServiceImpl();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long customerId = (Long) request.getAttribute("API_CUSTOMER_ID");
        String path = request.getServletPath();

        try {
            if ("/api/customer/account-details".equals(path)) {
                Long accountId = Long.parseLong(request.getParameter("accountId"));
                accountService.verifyAccountOwnership(accountId, customerId);

                Account account = accountService.getAccountById(accountId);
                List<Transaction> transactions = transactionService.getAccountTransactions(accountId, 1, 10);

                Map<String, Object> data = new HashMap<>();
                data.put("account", account);
                data.put("transactions", transactions);

                response.getWriter().write(gson.toJson(ApiResponse.success("Account details loaded", data)));
            } else {
                List<Account> accounts = accountService.getCustomerAccounts(customerId);
                response.getWriter().write(gson.toJson(ApiResponse.success("Accounts loaded", accounts)));
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(ApiResponse.error(e.getMessage(), "ACCOUNT_ERROR")));
        }
    }
}
