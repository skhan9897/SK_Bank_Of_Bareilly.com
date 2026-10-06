package com.skbank.controller.api;

import com.google.gson.Gson;
import com.skbank.dto.ApiResponse;
import com.skbank.model.Transaction;
import com.skbank.service.TransactionService;
import com.skbank.service.impl.TransactionServiceImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = {"/api/customer/withdraw"})
public class WithdrawApiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final TransactionService transactionService = new TransactionServiceImpl();
    private final Gson gson = new Gson();

    private static class WithdrawPayload {
        Long accountId;
        BigDecimal amount;
        String reason;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long customerId = (Long) request.getAttribute("API_CUSTOMER_ID");

        try {
            BufferedReader reader = request.getReader();
            WithdrawPayload p = gson.fromJson(reader, WithdrawPayload.class);

            Transaction txn = transactionService.processWithdrawal(p.accountId, p.amount, p.reason, customerId);

            response.getWriter().write(gson.toJson(ApiResponse.success("Withdrawal successful", txn)));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(ApiResponse.error(e.getMessage(), "WITHDRAWAL_FAILED")));
        }
    }
}
