package com.skbank.controller.api;

import com.google.gson.Gson;
import com.skbank.dto.ApiResponse;
import com.skbank.model.Loan;
import com.skbank.model.LoanPayment;
import com.skbank.model.LoanType;
import com.skbank.service.LoanService;
import com.skbank.service.impl.LoanServiceImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = {"/api/customer/loans", "/api/customer/loans/apply", "/api/customer/loans/pay-emi", "/api/customer/loans/types"})
public class LoanApiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final LoanService loanService = new LoanServiceImpl();
    private final Gson gson = new Gson();

    private static class LoanApplyPayload {
        Long loanTypeId;
        BigDecimal amount;
        int tenureMonths;
    }

    private static class EmiPayload {
        Long loanId;
        Long accountId;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();
        Long customerId = (Long) request.getAttribute("API_CUSTOMER_ID");

        try {
            if ("/api/customer/loans/types".equals(path)) {
                List<LoanType> types = loanService.getAllLoanTypes();
                response.getWriter().write(gson.toJson(ApiResponse.success("Loan types loaded", types)));
            } else {
                List<Loan> loans = loanService.getCustomerLoans(customerId);
                response.getWriter().write(gson.toJson(ApiResponse.success("Loans loaded", loans)));
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(ApiResponse.error(e.getMessage(), "LOAN_ERROR")));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();
        Long customerId = (Long) request.getAttribute("API_CUSTOMER_ID");
        BufferedReader reader = request.getReader();

        try {
            if ("/api/customer/loans/apply".equals(path)) {
                LoanApplyPayload p = gson.fromJson(reader, LoanApplyPayload.class);
                Loan loan = loanService.applyLoan(customerId, p.loanTypeId, p.amount, p.tenureMonths);
                response.getWriter().write(gson.toJson(ApiResponse.success("Loan application submitted", loan)));
            } else if ("/api/customer/loans/pay-emi".equals(path)) {
                EmiPayload p = gson.fromJson(reader, EmiPayload.class);
                LoanPayment lp = loanService.payEmi(p.loanId, p.accountId, customerId);
                response.getWriter().write(gson.toJson(ApiResponse.success("EMI payment successful", lp)));
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(ApiResponse.error(e.getMessage(), "LOAN_ACTION_FAILED")));
        }
    }
}
