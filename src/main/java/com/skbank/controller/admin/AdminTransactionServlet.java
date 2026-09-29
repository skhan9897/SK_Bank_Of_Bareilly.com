package com.skbank.controller.admin;

import com.skbank.dao.TransactionDAO;
import com.skbank.model.Transaction;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin/transactions")
public class AdminTransactionServlet extends HttpServlet {

    private final TransactionDAO transactionDAO = new TransactionDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Transaction> transactions = transactionDAO.findAll(200);
            request.setAttribute("transactions", transactions);
            request.getRequestDispatcher("/admin/transactions.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading transactions: " + e.getMessage());
            request.getRequestDispatcher("/admin/transactions.jsp").forward(request, response);
        }
    }
}
