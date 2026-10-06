package com.skbank.controller.api;

import com.google.gson.Gson;
import com.skbank.dto.ApiResponse;
import com.skbank.model.Transaction;
import com.skbank.service.AccountService;
import com.skbank.service.TransactionService;
import com.skbank.service.impl.AccountServiceImpl;
import com.skbank.service.impl.TransactionServiceImpl;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = {"/api/customer/transactions"})
public class TransactionApiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final TransactionService transactionService = new TransactionServiceImpl();
    private final AccountService accountService = new AccountServiceImpl();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long customerId = (Long) request.getAttribute("API_CUSTOMER_ID");
        String accIdStr = request.getParameter("accountId");
        int page = 1;
        String pageStr = request.getParameter("page");
        if (pageStr != null) {
            try { page = Integer.parseInt(pageStr); } catch (NumberFormatException ignored) {}
        }

        try {
            if (accIdStr == null || accIdStr.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(ApiResponse.error("accountId is required", "BAD_REQUEST")));
                return;
            }

            Long accountId = Long.parseLong(accIdStr);
            accountService.verifyAccountOwnership(accountId, customerId);

            List<Transaction> txns = transactionService.getAccountTransactions(accountId, page, 20);

            response.getWriter().write(gson.toJson(ApiResponse.success("Transactions loaded", txns)));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(ApiResponse.error(e.getMessage(), "TRANSACTION_ERROR")));
        }
    }
}
