package com.skbank.controller.admin;

import com.skbank.dao.AccountDAO;
import com.skbank.dao.CustomerDAO;
import com.skbank.dao.FDDAO;
import com.skbank.dao.LoanDAO;
import com.skbank.dao.TransactionDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/admin/reports")
public class AdminReportServlet extends HttpServlet {

    private final CustomerDAO customerDAO = new CustomerDAO();
    private final AccountDAO accountDAO = new AccountDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final LoanDAO loanDAO = new LoanDAO();
    private final FDDAO fdDAO = new FDDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setAttribute("totalCustomers", customerDAO.countTotalCustomers());
            request.setAttribute("totalAccounts", accountDAO.countTotalAccounts());
            request.setAttribute("totalDeposits", accountDAO.getTotalBankDeposits());
            request.setAttribute("totalLoansOutstanding", loanDAO.getTotalActiveLoanOutstanding());
            request.setAttribute("totalFDVolume", fdDAO.getTotalActiveFDAmount());
            request.setAttribute("todayVolume", transactionDAO.getTodayTransactionVolume());

            request.getRequestDispatcher("/admin/reports.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error generating reports: " + e.getMessage());
            request.getRequestDispatcher("/admin/reports.jsp").forward(request, response);
        }
    }
}
