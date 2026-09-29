package com.skbank.controller.admin;

import com.skbank.dao.AccountDAO;
import com.skbank.model.Account;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin/accounts")
public class AdminAccountServlet extends HttpServlet {

    private final AccountDAO accountDAO = new AccountDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Account> accounts = accountDAO.findAll();
            request.setAttribute("accounts", accounts);
            request.getRequestDispatcher("/admin/accounts.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading accounts: " + e.getMessage());
            request.getRequestDispatcher("/admin/accounts.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        String accountIdStr = request.getParameter("accountId");

        try {
            int accountId = Integer.parseInt(accountIdStr);
            if ("block".equalsIgnoreCase(action)) {
                accountDAO.updateStatus(accountId, "BLOCKED");
                request.setAttribute("successMessage", "Account Blocked.");
            } else if ("activate".equalsIgnoreCase(action)) {
                accountDAO.updateStatus(accountId, "ACTIVE");
                request.setAttribute("successMessage", "Account Activated.");
            } else if ("close".equalsIgnoreCase(action)) {
                accountDAO.updateStatus(accountId, "CLOSED");
                request.setAttribute("successMessage", "Account Closed.");
            }
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error: " + e.getMessage());
        }

        doGet(request, response);
    }
}
