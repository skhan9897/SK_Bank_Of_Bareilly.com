package com.skbank.controller.customer;

import com.skbank.model.Account;
import com.skbank.model.Transaction;
import com.skbank.service.AccountService;
import com.skbank.service.TransactionService;
import com.skbank.service.impl.AccountServiceImpl;
import com.skbank.service.impl.TransactionServiceImpl;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(urlPatterns = {"/customer/transactions"})
public class TransactionServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final AccountService accountService = new AccountServiceImpl();
    private final TransactionService transactionService = new TransactionServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Long customerId = (Long) session.getAttribute("CUSTOMER_ID");

        try {
            List<Account> accounts = accountService.getCustomerAccounts(customerId);
            request.setAttribute("accounts", accounts);

            String accIdStr = request.getParameter("accountId");
            if (accIdStr != null && !accIdStr.trim().isEmpty()) {
                Long accountId = Long.parseLong(accIdStr);
                accountService.verifyAccountOwnership(accountId, customerId);

                int page = 1;
                String pageStr = request.getParameter("page");
                if (pageStr != null && !pageStr.trim().isEmpty()) {
                    try { page = Integer.parseInt(pageStr); } catch (NumberFormatException ignored) {}
                }
                int pageSize = 10;

                Account account = accountService.getAccountById(accountId);
                List<Transaction> transactions = transactionService.getAccountTransactions(accountId, page, pageSize);
                long totalRecords = transactionService.countAccountTransactions(accountId);
                int totalPages = (int) Math.ceil((double) totalRecords / pageSize);

                request.setAttribute("selectedAccount", account);
                request.setAttribute("transactions", transactions);
                request.setAttribute("currentPage", page);
                request.setAttribute("totalPages", totalPages);
            }

            request.getRequestDispatcher("/WEB-INF/views/customer/transactions.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading transactions: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/customer/transactions.jsp").forward(request, response);
        }
    }
}
