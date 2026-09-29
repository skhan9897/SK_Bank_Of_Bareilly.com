package com.skbank.controller.admin;

import com.skbank.model.Loan;
import com.skbank.service.LoanService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin/loans")
public class AdminLoanServlet extends HttpServlet {

    private final LoanService loanService = new LoanService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Loan> loans = loanService.getAllLoanApplications();
            request.setAttribute("loans", loans);
            request.getRequestDispatcher("/admin/loans.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading loans: " + e.getMessage());
            request.getRequestDispatcher("/admin/loans.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        String loanIdStr = request.getParameter("loanId");

        try {
            int loanId = Integer.parseInt(loanIdStr);
            if ("approve".equalsIgnoreCase(action)) {
                loanService.updateStatus(loanId, "APPROVED");
                request.setAttribute("successMessage", "Loan Approved Successfully.");
            } else if ("reject".equalsIgnoreCase(action)) {
                loanService.updateStatus(loanId, "REJECTED");
                request.setAttribute("successMessage", "Loan Application Rejected.");
            }
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error: " + e.getMessage());
        }

        doGet(request, response);
    }
}
