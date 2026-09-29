package com.skbank.controller;

import com.skbank.model.Account;
import com.skbank.model.Customer;
import com.skbank.model.FixedDeposit;
import com.skbank.service.AccountService;
import com.skbank.service.FDService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/fixed-deposits")
public class FDServlet extends HttpServlet {

    private final FDService fdService = new FDService();
    private final AccountService accountService = new AccountService();

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
            List<FixedDeposit> fds = fdService.getCustomerFDs(customer.getCustomerId());
            List<Account> accounts = accountService.getCustomerAccounts(customer.getCustomerId());

            request.setAttribute("fixedDeposits", fds);
            request.setAttribute("accounts", accounts);
            request.getRequestDispatcher("/fixed-deposits.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading Fixed Deposits: " + e.getMessage());
            request.getRequestDispatcher("/fixed-deposits.jsp").forward(request, response);
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

        String accountIdStr = request.getParameter("accountId");
        String amountStr = request.getParameter("amount");
        String tenureStr = request.getParameter("tenureMonths");

        try {
            int accountId = Integer.parseInt(accountIdStr);
            double amount = Double.parseDouble(amountStr);
            int tenureMonths = Integer.parseInt(tenureStr);

            boolean created = fdService.createFixedDeposit(customer.getCustomerId(), accountId, amount, tenureMonths);
            if (created) {
                request.setAttribute("successMessage", "Fixed Deposit Created Successfully!");
            } else {
                request.setAttribute("errorMessage", "Failed to create Fixed Deposit.");
            }
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error: " + e.getMessage());
        }

        doGet(request, response);
    }
}
