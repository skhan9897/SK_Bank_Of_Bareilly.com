package com.skbank.controller.admin;

import com.skbank.model.Account;
import com.skbank.model.Transaction;
import com.skbank.service.AccountService;
import com.skbank.service.AdminService;
import com.skbank.service.TransactionService;
import com.skbank.service.impl.AccountServiceImpl;
import com.skbank.service.impl.AdminServiceImpl;
import com.skbank.service.impl.TransactionServiceImpl;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = {"/admin/accounts", "/admin/account-details"})
public class AdminAccountServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final AdminService adminService = new AdminServiceImpl();
    private final AccountService accountService = new AccountServiceImpl();
    private final TransactionService transactionService = new TransactionServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();
        String action = request.getParameter("action");

        try {
            if ("status".equalsIgnoreCase(action)) {
                Long accountId = Long.parseLong(request.getParameter("id"));
                String newStatus = request.getParameter("status");
                adminService.setAccountStatus(accountId, newStatus);
                response.sendRedirect(request.getContextPath() + "/admin/accounts?msg=Account status updated to " + newStatus);
                return;
            }

            if ("/admin/account-details".equals(path)) {
                Long accountId = Long.parseLong(request.getParameter("id"));
                Account account = accountService.getAccountById(accountId);
                List<Transaction> transactions = transactionService.getAccountTransactions(accountId, 1, 15);

                request.setAttribute("account", account);
                request.setAttribute("transactions", transactions);
                request.getRequestDispatcher("/WEB-INF/views/admin/account-details.jsp").forward(request, response);
            } else {
                String search = request.getParameter("search");
                int page = 1;
                String pageStr = request.getParameter("page");
                if (pageStr != null && !pageStr.trim().isEmpty()) {
                    try { page = Integer.parseInt(pageStr); } catch (NumberFormatException ignored) {}
                }
                int pageSize = 10;

                List<Account> accounts = adminService.getAllAccounts(page, pageSize, search);
                long totalRecords = adminService.countAccounts(search);
                int totalPages = (int) Math.ceil((double) totalRecords / pageSize);

                request.setAttribute("accounts", accounts);
                request.setAttribute("currentPage", page);
                request.setAttribute("totalPages", totalPages);
                request.setAttribute("search", search);

                request.getRequestDispatcher("/WEB-INF/views/admin/accounts.jsp").forward(request, response);
            }
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading account data: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/accounts.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String customerId = request.getParameter("customerId");
            Long accountTypeId = Long.parseLong(request.getParameter("accountTypeId"));
            Long branchId = Long.parseLong(request.getParameter("branchId"));

            // MANDATORY RULE: Balance = ₹0.00
            Account acc = accountService.createAccount(customerId, accountTypeId, branchId);

            response.sendRedirect(request.getContextPath() + "/admin/accounts?msg=New account " + acc.getAccountNumber() + " created with initial balance ₹0.00.");
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/admin/accounts?error=Failed to create account: " + e.getMessage());
        }
    }
}
