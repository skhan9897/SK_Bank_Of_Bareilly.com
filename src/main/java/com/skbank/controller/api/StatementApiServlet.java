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
import java.math.BigDecimal;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = {"/api/customer/statements"})
public class StatementApiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final AccountService accountService = new AccountServiceImpl();
    private final TransactionService transactionService = new TransactionServiceImpl();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String customerId = (String) request.getAttribute("API_CUSTOMER_ID");

        try {
            Long accountId = Long.parseLong(request.getParameter("accountId"));
            accountService.verifyAccountOwnership(accountId, customerId);

            Account account = accountService.getAccountById(accountId);
            String startDateStr = request.getParameter("startDate");
            String endDateStr = request.getParameter("endDate");

            Date startDate = (startDateStr != null && !startDateStr.trim().isEmpty()) ? Date.valueOf(startDateStr) : new Date(System.currentTimeMillis() - 30L * 24 * 3600 * 1000);
            Date endDate = (endDateStr != null && !endDateStr.trim().isEmpty()) ? Date.valueOf(endDateStr) : new Date(System.currentTimeMillis());

            List<Transaction> transactions = transactionService.getFilteredTransactions(accountId, startDate, endDate, null, 1, 1000);

            BigDecimal totalCredits = BigDecimal.ZERO;
            BigDecimal totalDebits = BigDecimal.ZERO;

            for (Transaction t : transactions) {
                if (t.getAmount() != null) {
                    if ("DEPOSIT".equalsIgnoreCase(t.getTransactionType().name()) ||
                        "INTEREST_CREDIT".equalsIgnoreCase(t.getTransactionType().name()) ||
                        "REFUND".equalsIgnoreCase(t.getTransactionType().name())) {
                        totalCredits = totalCredits.add(t.getAmount());
                    } else {
                        totalDebits = totalDebits.add(t.getAmount());
                    }
                }
            }

            Map<String, Object> data = new HashMap<>();
            data.put("account", account);
            data.put("startDate", startDate);
            data.put("endDate", endDate);
            data.put("totalCredits", totalCredits);
            data.put("totalDebits", totalDebits);
            data.put("transactions", transactions);

            response.getWriter().write(gson.toJson(ApiResponse.success("Statement generated", data)));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(ApiResponse.error(e.getMessage(), "STATEMENT_ERROR")));
        }
    }
}
