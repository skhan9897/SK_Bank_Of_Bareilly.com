package com.skbank.controller.api;

import com.google.gson.Gson;
import com.skbank.dto.ApiResponse;
import com.skbank.dto.PaymentRequestDTO;
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

@WebServlet(urlPatterns = {"/api/customer/payment-bank/wallet", "/api/customer/payment-bank/pay", "/api/customer/payment-bank/history"})
public class PaymentBankApiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final PaymentBankService paymentBankService = new PaymentBankServiceImpl();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long customerId = (Long) request.getAttribute("API_CUSTOMER_ID");
        String path = request.getServletPath();

        try {
            if ("/api/customer/payment-bank/wallet".equals(path)) {
                PaymentWallet wallet = paymentBankService.getWallet(customerId);
                response.getWriter().write(gson.toJson(ApiResponse.success("Wallet loaded", wallet)));
            } else if ("/api/customer/payment-bank/history".equals(path)) {
                int page = 1;
                String pageStr = request.getParameter("page");
                if (pageStr != null) {
                    try { page = Integer.parseInt(pageStr); } catch (NumberFormatException ignored) {}
                }
                List<PaymentTransaction> history = paymentBankService.getPaymentHistory(customerId, page, 10);
                response.getWriter().write(gson.toJson(ApiResponse.success("Payment history loaded", history)));
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(ApiResponse.error(e.getMessage(), "PAYMENT_BANK_ERROR")));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long customerId = (Long) request.getAttribute("API_CUSTOMER_ID");

        try {
            BufferedReader reader = request.getReader();
            PaymentRequestDTO payReq = gson.fromJson(reader, PaymentRequestDTO.class);

            PaymentTransaction pt = paymentBankService.processPayment(customerId, payReq);

            response.getWriter().write(gson.toJson(ApiResponse.success("Payment successful", pt)));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(ApiResponse.error(e.getMessage(), "PAYMENT_FAILED")));
        }
    }
}
