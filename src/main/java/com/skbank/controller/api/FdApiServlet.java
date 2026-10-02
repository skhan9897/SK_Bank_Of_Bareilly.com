package com.skbank.controller.api;

import com.google.gson.Gson;
import com.skbank.dto.ApiResponse;
import com.skbank.model.FixedDeposit;
import com.skbank.service.AccountService;
import com.skbank.service.FdService;
import com.skbank.service.impl.AccountServiceImpl;
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

@WebServlet(urlPatterns = {"/api/customer/fixed-deposits", "/api/customer/fd-details"})
public class FdApiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final FdService fdService = new FdServiceImpl();
    private final AccountService accountService = new AccountServiceImpl();
    private final Gson gson = new Gson();

    private static class FdPayload {
        Long accountId;
        BigDecimal principalAmount;
        int tenureMonths;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long customerId = (Long) request.getAttribute("API_CUSTOMER_ID");
        String path = request.getServletPath();

        try {
            if ("/api/customer/fd-details".equals(path)) {
                Long fdId = Long.parseLong(request.getParameter("fdId"));
                FixedDeposit fd = fdService.getFdById(fdId);
                if (!fd.getCustomerId().equals(customerId)) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write(gson.toJson(ApiResponse.error("Access denied", "UNAUTHORIZED")));
                    return;
                }
                response.getWriter().write(gson.toJson(ApiResponse.success("FD details", fd)));
            } else {
                List<FixedDeposit> fds = fdService.getCustomerFds(customerId);
                response.getWriter().write(gson.toJson(ApiResponse.success("Fixed deposits loaded", fds)));
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
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

            accountService.verifyAccountOwnership(p.accountId, customerId);

            FixedDeposit fd = fdService.openFd(customerId, p.accountId, p.principalAmount, p.tenureMonths);

            response.getWriter().write(gson.toJson(ApiResponse.success("Fixed Deposit opened successfully", fd)));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(ApiResponse.error(e.getMessage(), "FD_OPEN_FAILED")));
        }
    }
}
