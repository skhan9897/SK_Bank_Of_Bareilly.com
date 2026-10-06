package com.skbank.controller.api;

import com.google.gson.Gson;
import com.skbank.dto.ApiResponse;
import com.skbank.model.FixedDeposit;
import com.skbank.service.FdService;
import com.skbank.service.impl.FdServiceImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = {"/api/customer/fd"})
public class FdApiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final FdService fdService = new FdServiceImpl();
    private final Gson gson = new Gson();

    private static class FdPayload {
        Long accountId;
        BigDecimal amount;
        int tenureMonths;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long customerId = (Long) request.getAttribute("API_CUSTOMER_ID");

        try {
            List<FixedDeposit> fds = fdService.getCustomerFds(customerId);
            response.getWriter().write(gson.toJson(ApiResponse.success("FD list loaded", fds)));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(ApiResponse.error(e.getMessage(), "FD_ERROR")));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long customerId = (Long) request.getAttribute("API_CUSTOMER_ID");

        try {
            BufferedReader reader = request.getReader();
            FdPayload p = gson.fromJson(reader, FdPayload.class);

            FixedDeposit fd = fdService.openFd(customerId, p.accountId, p.amount, p.tenureMonths);

            response.getWriter().write(gson.toJson(ApiResponse.success("FD opened successfully", fd)));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(ApiResponse.error(e.getMessage(), "FD_OPEN_FAILED")));
        }
    }
}
