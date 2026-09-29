package com.skbank.controller;

import com.skbank.dao.TransactionDAO;
import com.skbank.model.Customer;
import com.skbank.model.Transaction;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/transactions")
public class TransactionServlet extends HttpServlet {

    private final TransactionDAO transactionDAO = new TransactionDAO();

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
            List<Transaction> transactions = transactionDAO.findByCustomerId(customer.getCustomerId(), 100);
            request.setAttribute("transactions", transactions);
            request.getRequestDispatcher("/transactions.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading transactions: " + e.getMessage());
            request.getRequestDispatcher("/transactions.jsp").forward(request, response);
        }
    }
}
