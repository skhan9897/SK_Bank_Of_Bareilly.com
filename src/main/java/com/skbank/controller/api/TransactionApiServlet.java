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
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = {"/api/customer/transactions", "/api/customer/transaction-details"})
public class TransactionApiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final TransactionService transactionService = new TransactionServiceImpl();
    private final AccountService accountService = new AccountServiceImpl();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long customerId = (Long) request.getAttribute("API_CUSTOMER_ID");
        String path = request.getServletPath();

        try {
            if ("/api/customer/transaction-details".equals(path)) {
                String ref = request.getParameter("ref");
                Transaction txn = transactionService.getTransactionByReference(ref);
                accountService.verifyAccountOwnership(txn.getAccountId(), customerId);

                response.getWriter().write(gson.toJson(ApiResponse.success("Transaction details", txn)));
            } else {
                List<Account> accounts = accountService.getCustomerAccounts(customerId);
                if (accounts.isEmpty()) {
                    response.getWriter().write(gson.toJson(ApiResponse.success("No accounts found", new HashMap<>())));
                    return;
                }

                String accIdStr = request.getParameter("accountId");
                Long accountId = (accIdStr != null && !accIdStr.trim().isEmpty()) ? Long.parseLong(accIdStr) : accounts.get(0).getAccountId();

                accountService.verifyAccountOwnership(accountId, customerId);

                String startDateStr = request.getParameter("startDate");
                String endDateStr = request.getParameter("endDate");
                String typeFilter = request.getParameter("type");

                Date startDate = (startDateStr != null && !startDateStr.trim().isEmpty()) ? Date.valueOf(startDateStr) : null;
                Date endDate = (endDateStr != null && !endDateStr.trim().isEmpty()) ? Date.valueOf(endDateStr) : null;

                int page = 1;
                String pageStr = request.getParameter("page");
                if (pageStr != null && !pageStr.trim().isEmpty()) {
                    try { page = Integer.parseInt(pageStr); } catch (NumberFormatException ignored) {}
                }
                int pageSize = 20;

                List<Transaction> transactions = transactionService.getFilteredTransactions(accountId, startDate, endDate, typeFilter, page, pageSize);
                long totalRecords = transactionService.countFilteredTransactions(accountId, startDate, endDate, typeFilter);

                Map<String, Object> data = new HashMap<>();
                data.put("transactions", transactions);
                data.put("currentPage", page);
                data.put("totalRecords", totalRecords);
                data.put("totalPages", (int) Math.ceil((double) totalRecords / pageSize));

                response.getWriter().write(gson.toJson(ApiResponse.success("Transactions loaded", data)));
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(ApiResponse.error(e.getMessage(), "TRANSACTION_ERROR")));
        }
    }
}
