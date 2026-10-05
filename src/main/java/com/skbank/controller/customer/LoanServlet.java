package com.skbank.controller.customer;

import com.skbank.model.Account;
import com.skbank.model.Loan;
import com.skbank.model.LoanPayment;
import com.skbank.model.LoanType;
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

@WebServlet(urlPatterns = {
    "/customer/loans",
    "/customer/loan-apply",
    "/customer/loan-details",
    "/customer/loan-emi"
})
public class LoanServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final LoanService loanService = new LoanServiceImpl();
    private final AccountService accountService = new AccountServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        String customerId = (String) session.getAttribute("CUSTOMER_ID");

        String path = request.getServletPath();

        try {
            if ("/customer/loan-apply".equals(path)) {
                List<LoanType> loanTypes = loanService.getAllLoanTypes();
                request.setAttribute("loanTypes", loanTypes);
                request.getRequestDispatcher("/WEB-INF/views/customer/loan-apply.jsp").forward(request, response);
            } else if ("/customer/loan-details".equals(path)) {
                Long loanId = Long.parseLong(request.getParameter("id"));
                Loan loan = loanService.getLoanById(loanId);
                if (!loan.getCustomerId().equals(customerId)) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }
                List<LoanPayment> payments = loanService.getLoanPayments(loanId);
                List<Account> accounts = accountService.getCustomerAccounts(customerId);

                request.setAttribute("loan", loan);
                request.setAttribute("payments", payments);
                request.setAttribute("accounts", accounts);
                request.getRequestDispatcher("/WEB-INF/views/customer/loan-details.jsp").forward(request, response);
            } else {
                List<Loan> loans = loanService.getCustomerLoans(customerId);
                request.setAttribute("loans", loans);
                request.getRequestDispatcher("/WEB-INF/views/customer/loans.jsp").forward(request, response);
            }
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading loans: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/customer/loans.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        String customerId = (String) session.getAttribute("CUSTOMER_ID");

        String path = request.getServletPath();

        try {
            if ("/customer/loan-apply".equals(path)) {
                Long loanTypeId = Long.parseLong(request.getParameter("loanTypeId"));
                BigDecimal principal = new BigDecimal(request.getParameter("principalAmount"));
                int tenureMonths = Integer.parseInt(request.getParameter("tenureMonths"));

                Loan loan = loanService.applyLoan(customerId, loanTypeId, principal, tenureMonths);

                response.sendRedirect(request.getContextPath() + "/customer/loans?msg=Loan application " + loan.getLoanNumber() + " submitted successfully.");
            } else if ("/customer/loan-emi".equals(path)) {
                Long loanId = Long.parseLong(request.getParameter("loanId"));
                Long accountId = Long.parseLong(request.getParameter("accountId"));

                loanService.payEmi(loanId, accountId, customerId);

                response.sendRedirect(request.getContextPath() + "/customer/loan-details?id=" + loanId + "&msg=EMI paid successfully.");
            }
        } catch (Exception e) {
            try {
                if ("/customer/loan-apply".equals(path)) {
                    request.setAttribute("loanTypes", loanService.getAllLoanTypes());
                    request.setAttribute("errorMessage", "Application failed: " + e.getMessage());
                    request.getRequestDispatcher("/WEB-INF/views/customer/loan-apply.jsp").forward(request, response);
                    return;
                }
            } catch (Exception ignored) {}
            response.sendRedirect(request.getContextPath() + "/customer/loans?error=" + e.getMessage());
        }
    }
}
