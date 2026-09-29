package com.skbank.controller;

import com.skbank.model.Account;
import com.skbank.model.Customer;
import com.skbank.service.AccountService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/accounts")
public class AccountServlet extends HttpServlet {

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
            List<Account> accounts = accountService.getCustomerAccounts(customer.getCustomerId());
            var accountTypes = accountService.getAllAccountTypes();

            request.setAttribute("accounts", accounts);
            request.setAttribute("accountTypes", accountTypes);
            request.getRequestDispatcher("/accounts.jsp").forward(request, response);

        } catch (Exception e) {
            request.setAttribute("errorMessage", "Unable to retrieve account details: " + e.getMessage());
            request.getRequestDispatcher("/accounts.jsp").forward(request, response);
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

        String action = request.getParameter("action");

        try {
            if ("requestChequeBook".equalsIgnoreCase(action)) {
                String accNo = request.getParameter("accountNumber");
                String leaves = request.getParameter("leaves");
                request.setAttribute("successMessage", "Cheque Book Request (" + leaves + " leaves) submitted successfully for Account " + accNo + ". Dispatch in 3 working days.");

            } else {
                String typeIdStr = request.getParameter("typeId");
                String depositStr = request.getParameter("initialDeposit");

                int typeId = Integer.parseInt(typeIdStr);
                double initialDeposit = (depositStr != null && !depositStr.isEmpty()) ? Double.parseDouble(depositStr) : 1000.0;

                boolean created = accountService.createNewAccount(customer.getCustomerId(), typeId, initialDeposit);
                if (created) {
                    request.setAttribute("successMessage", "New Bank Account created successfully!");
                } else {
                    request.setAttribute("errorMessage", "Failed to create new account.");
                }
            }
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error: " + e.getMessage());
        }

        doGet(request, response);
    }
}
