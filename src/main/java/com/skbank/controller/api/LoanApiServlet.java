package com.skbank.controller.api;

import com.google.gson.Gson;
import com.skbank.dto.ApiResponse;
import com.skbank.dto.EmiCalculatorDTO;
import com.skbank.model.Loan;
import com.skbank.model.LoanPayment;
import com.skbank.model.LoanType;
import com.skbank.service.LoanService;
import com.skbank.service.impl.LoanServiceImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = {
    "/api/customer/loans",
    "/api/customer/loan-types",
    "/api/customer/loan-apply",
    "/api/customer/loan-details",
    "/api/customer/loan-emi",
    "/api/customer/loan-calculate-emi"
})
public class LoanApiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final LoanService loanService = new LoanServiceImpl();
    private final Gson gson = new Gson();

    private static class LoanApplyPayload {
        Long loanTypeId;
        BigDecimal principalAmount;
        int tenureMonths;
    }

    private static class EmiPayPayload {
        Long loanId;
        Long accountId;
    }

    private static class EmiCalcPayload {
        BigDecimal principal;
        BigDecimal annualRate;
        int tenureMonths;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long customerId = (Long) request.getAttribute("API_CUSTOMER_ID");
        String path = request.getServletPath();

        try {
            if ("/api/customer/loan-types".equals(path)) {
                List<LoanType> loanTypes = loanService.getAllLoanTypes();
                response.getWriter().write(gson.toJson(ApiResponse.success("Loan categories", loanTypes)));
            } else if ("/api/customer/loan-details".equals(path)) {
                Long loanId = Long.parseLong(request.getParameter("loanId"));
                Loan loan = loanService.getLoanById(loanId);
                if (!loan.getCustomerId().equals(customerId)) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write(gson.toJson(ApiResponse.error("Access denied", "UNAUTHORIZED")));
                    return;
                }
                List<LoanPayment> payments = loanService.getLoanPayments(loanId);

                Map<String, Object> data = new HashMap<>();
                data.put("loan", loan);
                data.put("payments", payments);

                response.getWriter().write(gson.toJson(ApiResponse.success("Loan details", data)));
            } else {
                List<Loan> loans = loanService.getCustomerLoans(customerId);
                response.getWriter().write(gson.toJson(ApiResponse.success("Loans loaded", loans)));
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(ApiResponse.error(e.getMessage(), "LOAN_ERROR")));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long customerId = (Long) request.getAttribute("API_CUSTOMER_ID");
        String path = request.getServletPath();

        try {
            BufferedReader reader = request.getReader();

            if ("/api/customer/loan-calculate-emi".equals(path)) {
                EmiCalcPayload p = gson.fromJson(reader, EmiCalcPayload.class);
                EmiCalculatorDTO dto = loanService.calculateEmi(p.principal, p.annualRate, p.tenureMonths);
                response.getWriter().write(gson.toJson(ApiResponse.success("EMI calculation", dto)));
            } else if ("/api/customer/loan-apply".equals(path)) {
                LoanApplyPayload p = gson.fromJson(reader, LoanApplyPayload.class);
                Loan loan = loanService.applyLoan(customerId, p.loanTypeId, p.principalAmount, p.tenureMonths);
                response.getWriter().write(gson.toJson(ApiResponse.success("Loan application submitted", loan)));
            } else if ("/api/customer/loan-emi".equals(path)) {
                EmiPayPayload p = gson.fromJson(reader, EmiPayPayload.class);
                LoanPayment lp = loanService.payEmi(p.loanId, p.accountId, customerId);
                response.getWriter().write(gson.toJson(ApiResponse.success("EMI paid successfully", lp)));
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(ApiResponse.error(e.getMessage(), "LOAN_ACTION_FAILED")));
        }
    }
}
