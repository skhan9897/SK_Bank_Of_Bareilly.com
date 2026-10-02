package com.skbank.controller.api;

import com.google.gson.Gson;
import com.skbank.dto.ApiResponse;
import com.skbank.model.UpiAccount;
import com.skbank.service.UpiService;
import com.skbank.service.impl.UpiServiceImpl;

import java.io.BufferedReader;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = {"/api/customer/upi"})
public class UpiApiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final UpiService upiService = new UpiServiceImpl();
    private final Gson gson = new Gson();

    private static class UpiPayload {
        String action;
        Long accountId;
        String upiAddress;
        String upiPin;
        String oldPin;
        String newPin;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long customerId = (Long) request.getAttribute("API_CUSTOMER_ID");

        try {
            UpiAccount upi = upiService.getUpiByCustomerId(customerId);
            response.getWriter().write(gson.toJson(ApiResponse.success("UPI account loaded", upi)));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(ApiResponse.error(e.getMessage(), "UPI_ERROR")));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long customerId = (Long) request.getAttribute("API_CUSTOMER_ID");

        try {
            BufferedReader reader = request.getReader();
            UpiPayload p = gson.fromJson(reader, UpiPayload.class);

            if ("create".equalsIgnoreCase(p.action)) {
                UpiAccount upi = upiService.createUpiAccount(customerId, p.accountId, p.upiAddress, p.upiPin);
                response.getWriter().write(gson.toJson(ApiResponse.success("UPI handle created successfully", upi)));
            } else if ("changePin".equalsIgnoreCase(p.action)) {
                upiService.changeUpiPin(customerId, p.oldPin, p.newPin);
                response.getWriter().write(gson.toJson(ApiResponse.success("UPI PIN changed successfully", null)));
            } else if ("disable".equalsIgnoreCase(p.action)) {
                upiService.disableUpi(customerId);
                response.getWriter().write(gson.toJson(ApiResponse.success("UPI handle disabled", null)));
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(ApiResponse.error("Invalid action", "BAD_REQUEST")));
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(ApiResponse.error(e.getMessage(), "UPI_ACTION_FAILED")));
        }
    }
}
