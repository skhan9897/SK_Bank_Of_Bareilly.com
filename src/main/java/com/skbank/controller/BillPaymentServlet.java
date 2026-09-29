package com.skbank.controller;

import com.skbank.model.Account;
import com.skbank.model.BillPayment;
import com.skbank.model.Customer;
import com.skbank.service.AccountService;
import com.skbank.service.BillPaymentService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/payments")
public class BillPaymentServlet extends HttpServlet {

    private final BillPaymentService billPaymentService = new BillPaymentService();
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
            List<BillPayment> billPayments = billPaymentService.getCustomerBillPayments(customer.getCustomerId());

            request.setAttribute("accounts", accounts);
            request.setAttribute("billPayments", billPayments);
            request.getRequestDispatcher("/payments.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading Bill Payments: " + e.getMessage());
            request.getRequestDispatcher("/payments.jsp").forward(request, response);
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

        String category = request.getParameter("category");
        String provider = request.getParameter("provider");
        String consumerNo = request.getParameter("consumerNumber");
        String accountIdStr = request.getParameter("accountId");
        String amountStr = request.getParameter("amount");

        try {
            int accountId = Integer.parseInt(accountIdStr);
            double amount = Double.parseDouble(amountStr);

            BillPayment bp = billPaymentService.processBillPayment(customer.getCustomerId(), accountId, category, provider, consumerNo, amount);
            request.setAttribute("successMessage", "Payment Successful! Ref No: " + bp.getReferenceNumber());
            request.setAttribute("receipt", bp);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Payment Failed: " + e.getMessage());
        }

        doGet(request, response);
    }
}
