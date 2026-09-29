package com.skbank.controller;

import com.skbank.dao.BeneficiaryDAO;
import com.skbank.model.Account;
import com.skbank.model.Beneficiary;
import com.skbank.model.Customer;
import com.skbank.model.Transaction;
import com.skbank.service.AccountService;
import com.skbank.service.TransferService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/transfer")
public class TransferServlet extends HttpServlet {

    private final AccountService accountService = new AccountService();
    private final BeneficiaryDAO beneficiaryDAO = new BeneficiaryDAO();
    private final TransferService transferService = new TransferService();

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
            List<Beneficiary> beneficiaries = beneficiaryDAO.findByCustomerId(customer.getCustomerId());

            request.setAttribute("accounts", accounts);
            request.setAttribute("beneficiaries", beneficiaries);
            request.getRequestDispatcher("/transfer.jsp").forward(request, response);

        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading transfer page: " + e.getMessage());
            request.getRequestDispatcher("/transfer.jsp").forward(request, response);
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

        String fromAccount = request.getParameter("fromAccount");
        String toAccount = request.getParameter("toAccount");
        String amountStr = request.getParameter("amount");
        String transferType = request.getParameter("transferType");
        String remarks = request.getParameter("remarks");

        try {
            double amount = Double.parseDouble(amountStr);
            Transaction txn = transferService.processTransfer(fromAccount, toAccount, amount, transferType, remarks);

            request.setAttribute("successMessage", "Transfer Successful! Transaction ID: " + txn.getTransactionId());
            request.setAttribute("transactionResult", txn);
            request.getRequestDispatcher("/transfer.jsp").forward(request, response);

        } catch (IllegalArgumentException | IllegalStateException e) {
            request.setAttribute("errorMessage", e.getMessage());
            doGet(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Transfer Failed: " + e.getMessage());
            doGet(request, response);
        }
    }
}
