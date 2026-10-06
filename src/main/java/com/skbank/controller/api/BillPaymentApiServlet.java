package com.skbank.controller.api;

import com.google.gson.Gson;
import com.skbank.dto.ApiResponse;
import com.skbank.model.BillPayment;
import com.skbank.service.AccountService;
import com.skbank.service.BillPaymentService;
import com.skbank.service.impl.AccountServiceImpl;
import com.skbank.service.impl.BillPaymentServiceImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = {"/api/customer/bill-payments"})
public class BillPaymentApiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final BillPaymentService billService = new BillPaymentServiceImpl();
    private final AccountService accountService = new AccountServiceImpl();
    private final Gson gson = new Gson();

    private static class BillPayload {
        Long accountId;
        String billerType;
        String billerName;
        String consumerNumber;
        BigDecimal amount;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long customerId = (Long) request.getAttribute("API_CUSTOMER_ID");

        try {
            List<BillPayment> history = billService.getCustomerBillPayments(customerId);
            response.getWriter().write(gson.toJson(ApiResponse.success("Bill history loaded", history)));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(ApiResponse.error(e.getMessage(), "BILL_ERROR")));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long customerId = (Long) request.getAttribute("API_CUSTOMER_ID");

        try {
            BufferedReader reader = request.getReader();
            BillPayload p = gson.fromJson(reader, BillPayload.class);

            accountService.verifyAccountOwnership(p.accountId, customerId);

            BillPayment bp = billService.processBillPayment(customerId, p.accountId, p.billerType, p.billerName, p.consumerNumber, p.amount);

            response.getWriter().write(gson.toJson(ApiResponse.success("Bill payment successful", bp)));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(ApiResponse.error(e.getMessage(), "BILL_PAYMENT_FAILED")));
        }
    }
}
