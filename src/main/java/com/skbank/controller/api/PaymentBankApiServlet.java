package com.skbank.controller.api;

import com.google.gson.Gson;
import com.skbank.dto.ApiResponse;
import com.skbank.dto.PaymentRequestDTO;
import com.skbank.model.PaymentProvider;
import com.skbank.model.PaymentTransaction;
import com.skbank.model.PaymentWallet;
import com.skbank.service.PaymentBankService;
import com.skbank.service.impl.PaymentBankServiceImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = {
    "/api/payment-bank/balance",
    "/api/payment-bank/pay",
    "/api/payment-bank/history",
    "/api/payment-bank/providers"
})
public class PaymentBankApiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final PaymentBankService paymentBankService = new PaymentBankServiceImpl();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String customerId = (String) request.getAttribute("API_CUSTOMER_ID");
        String path = request.getServletPath();

        try {
            if ("/api/payment-bank/balance".equals(path)) {
                PaymentWallet wallet = paymentBankService.getWallet(customerId);
                response.getWriter().write(gson.toJson(ApiResponse.success("Payment wallet balance", wallet)));
            } else if ("/api/payment-bank/history".equals(path)) {
                int page = 1;
                String pageStr = request.getParameter("page");
                if (pageStr != null && !pageStr.trim().isEmpty()) {
                    try { page = Integer.parseInt(pageStr); } catch (NumberFormatException ignored) {}
                }
                List<PaymentTransaction> history = paymentBankService.getPaymentHistory(customerId, page, 20);
                response.getWriter().write(gson.toJson(ApiResponse.success("Payment history", history)));
            } else if ("/api/payment-bank/providers".equals(path)) {
                String type = request.getParameter("type");
                List<PaymentProvider> providers = paymentBankService.getProvidersByType(type);
                response.getWriter().write(gson.toJson(ApiResponse.success("Providers loaded", providers)));
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(ApiResponse.error(e.getMessage(), "PAYMENT_BANK_ERROR")));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String customerId = (String) request.getAttribute("API_CUSTOMER_ID");
        String path = request.getServletPath();

        // Idempotency Key header check
        String idempotencyKey = request.getHeader("X-Idempotency-Key");

        try {
            BufferedReader reader = request.getReader();
            PaymentRequestDTO req = gson.fromJson(reader, PaymentRequestDTO.class);
            if (idempotencyKey != null && !idempotencyKey.trim().isEmpty()) {
                req.setIdempotencyKey(idempotencyKey.trim());
            }

            PaymentTransaction pt = paymentBankService.processPayment(customerId, req);

            response.getWriter().write(gson.toJson(ApiResponse.success("Payment processed successfully", pt)));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(ApiResponse.error(e.getMessage(), "PAYMENT_FAILED")));
        }
    }
}
