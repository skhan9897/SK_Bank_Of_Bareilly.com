package com.skbank.controller;

import com.skbank.model.Customer;
import com.skbank.model.Loan;
import com.skbank.service.LoanService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/loans")
public class LoanServlet extends HttpServlet {

    private final LoanService loanService = new LoanService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Customer customer = (session != null) ? (Customer) session.getAttribute("customerProfile") : null;

        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            List<Loan> loans = loanService.getCustomerLoans(customer.getCustomerId());
            request.setAttribute("loans", loans);
            request.getRequestDispatcher("/loans.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading loans: " + e.getMessage());
            request.getRequestDispatcher("/loans.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Customer customer = (session != null) ? (Customer) session.getAttribute("customerProfile") : null;

        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String loanType = request.getParameter("loanType");
        String amountStr = request.getParameter("amount");
        String tenureStr = request.getParameter("tenureMonths");
        String incomeStr = request.getParameter("monthlyIncome");
        String empType = request.getParameter("employmentType");
        String purpose = request.getParameter("purpose");

        try {
            double amount = Double.parseDouble(amountStr);
            int tenureMonths = Integer.parseInt(tenureStr);
            double monthlyIncome = Double.parseDouble(incomeStr);

            Loan loan = new Loan();
            loan.setCustomerId(customer.getCustomerId());
            loan.setLoanType(loanType);
            loan.setPrincipalAmount(amount);
            loan.setTenureMonths(tenureMonths);
            loan.setMonthlyIncome(monthlyIncome);
            loan.setEmploymentType(empType);
            loan.setPurpose(purpose);

            boolean applied = loanService.applyLoan(loan);
            if (applied) {
                request.setAttribute("successMessage", "Loan Application Submitted Successfully! It is currently under review by Bank Administration.");
            } else {
                request.setAttribute("errorMessage", "Failed to submit loan application.");
            }
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error: " + e.getMessage());
        }

        doGet(request, response);
    }
}
