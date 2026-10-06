package com.skbank.controller.customer;

import com.skbank.model.*;
import com.skbank.service.AccountService;
import com.skbank.service.LoanService;
import com.skbank.service.impl.AccountServiceImpl;
import com.skbank.service.impl.LoanServiceImpl;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(urlPatterns = {"/customer/loans", "/customer/loan-apply"})
public class LoanServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final LoanService loanService = new LoanServiceImpl();
    private final AccountService accountService = new AccountServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Long customerId = (Long) session.getAttribute("CUSTOMER_ID");
        String path = request.getServletPath();

        try {
            if ("/customer/loan-apply".equals(path)) {
                List<LoanType> loanTypes = loanService.getAllLoanTypes();
                request.setAttribute("loanTypes", loanTypes);
                request.getRequestDispatcher("/WEB-INF/views/customer/loan-apply.jsp").forward(request, response);
                return;
            }

            String idStr = request.getParameter("id");
            if (idStr != null && !idStr.trim().isEmpty()) {
                Long loanId = Long.parseLong(idStr);
                Loan loan = loanService.getLoanById(loanId);
                if (!loan.getCustomerId().equals(customerId)) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }
                List<Account> accounts = accountService.getCustomerAccounts(customerId);
                List<LoanPayment> payments = loanService.getLoanPayments(loanId);

                request.setAttribute("loan", loan);
                request.setAttribute("accounts", accounts);
                request.setAttribute("payments", payments);
                request.getRequestDispatcher("/WEB-INF/views/customer/loan-details.jsp").forward(request, response);
            } else {
                List<Loan> loans = loanService.getCustomerLoans(customerId);
                request.setAttribute("loans", loans);
                request.getRequestDispatcher("/WEB-INF/views/customer/loans.jsp").forward(request, response);
            }
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading loan information: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/customer/loans.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Long customerId = (Long) session.getAttribute("CUSTOMER_ID");

        String action = request.getParameter("action");

        try {
            if ("payEmi".equalsIgnoreCase(action)) {
                Long loanId = Long.parseLong(request.getParameter("loanId"));
                Long accountId = Long.parseLong(request.getParameter("accountId"));

                LoanPayment lp = loanService.payEmi(loanId, accountId, customerId);

                response.sendRedirect(request.getContextPath() + "/customer/loans?id=" + loanId + "&msg=EMI of ₹" + lp.getAmount() + " paid successfully. Ref: " + lp.getPaymentReference());
            } else {
                Long loanTypeId = Long.parseLong(request.getParameter("loanTypeId"));
                BigDecimal amount = new BigDecimal(request.getParameter("principalAmount"));
                int tenureMonths = Integer.parseInt(request.getParameter("tenureMonths"));

                Loan loan = loanService.applyLoan(customerId, loanTypeId, amount, tenureMonths);

                response.sendRedirect(request.getContextPath() + "/customer/loans?id=" + loan.getLoanId() + "&msg=Loan Application submitted successfully.");
            }
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/customer/loans?error=" + e.getMessage());
        }
    }
}
