package com.skbank.controller.api;

import com.google.gson.Gson;
import com.skbank.dto.ApiResponse;
import com.skbank.model.Transaction;
import com.skbank.service.AccountService;
import com.skbank.service.TransactionService;
import com.skbank.service.impl.AccountServiceImpl;
import com.skbank.service.impl.TransactionServiceImpl;

import java.io.IOException;
import java.sql.Date;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = {"/api/customer/statement"})
public class StatementApiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final AccountService accountService = new AccountServiceImpl();
    private final TransactionService transactionService = new TransactionServiceImpl();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long customerId = (Long) request.getAttribute("API_CUSTOMER_ID");
        String accIdStr = request.getParameter("accountId");
        String startDateStr = request.getParameter("startDate");
        String endDateStr = request.getParameter("endDate");

        try {
            if (accIdStr == null || accIdStr.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(ApiResponse.error("accountId is required", "BAD_REQUEST")));
                return;
            }

            Long accountId = Long.parseLong(accIdStr);
            accountService.verifyAccountOwnership(accountId, customerId);

            Date startDate = (startDateStr != null && !startDateStr.trim().isEmpty()) ? Date.valueOf(startDateStr) : new Date(System.currentTimeMillis() - 30L * 24 * 3600 * 1000);
            Date endDate = (endDateStr != null && !endDateStr.trim().isEmpty()) ? Date.valueOf(endDateStr) : new Date(System.currentTimeMillis());

            List<Transaction> txns = transactionService.getFilteredTransactions(accountId, startDate, endDate, null, 1, 1000);

            response.getWriter().write(gson.toJson(ApiResponse.success("Statement generated", txns)));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(ApiResponse.error(e.getMessage(), "STATEMENT_FAILED")));
        }
    }
}
