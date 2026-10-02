package com.skbank.controller.customer;

import com.skbank.model.Account;
import com.skbank.model.Transaction;
import com.skbank.service.AccountService;
import com.skbank.service.TransactionService;
import com.skbank.service.impl.AccountServiceImpl;
import com.skbank.service.impl.TransactionServiceImpl;

import java.io.IOException;
import java.sql.Date;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(urlPatterns = {"/customer/transactions", "/customer/transaction-details"})
public class TransactionServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final AccountService accountService = new AccountServiceImpl();
    private final TransactionService transactionService = new TransactionServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Long customerId = (Long) session.getAttribute("CUSTOMER_ID");

        String path = request.getServletPath();

        try {
            if ("/customer/transaction-details".equals(path)) {
                String ref = request.getParameter("ref");
                Transaction txn = transactionService.getTransactionByReference(ref);
                accountService.verifyAccountOwnership(txn.getAccountId(), customerId);

                request.setAttribute("txn", txn);
                request.getRequestDispatcher("/WEB-INF/views/customer/transaction-details.jsp").forward(request, response);
            } else {
                List<Account> accounts = accountService.getCustomerAccounts(customerId);
                if (accounts.isEmpty()) {
                    request.setAttribute("errorMessage", "No active accounts found.");
                    request.getRequestDispatcher("/WEB-INF/views/customer/transactions.jsp").forward(request, response);
                    return;
                }

                String accIdStr = request.getParameter("accountId");
                Long selectedAccountId = (accIdStr != null && !accIdStr.trim().isEmpty()) ? Long.parseLong(accIdStr) : accounts.get(0).getAccountId();

                accountService.verifyAccountOwnership(selectedAccountId, customerId);

                String startDateStr = request.getParameter("startDate");
                String endDateStr = request.getParameter("endDate");
                String typeFilter = request.getParameter("type");

                Date startDate = (startDateStr != null && !startDateStr.trim().isEmpty()) ? Date.valueOf(startDateStr) : null;
                Date endDate = (endDateStr != null && !endDateStr.trim().isEmpty()) ? Date.valueOf(endDateStr) : null;

                int page = 1;
                String pageStr = request.getParameter("page");
                if (pageStr != null && !pageStr.trim().isEmpty()) {
                    try { page = Integer.parseInt(pageStr); } catch (NumberFormatException ignored) {}
                }
                int pageSize = 15;

                List<Transaction> transactions = transactionService.getFilteredTransactions(selectedAccountId, startDate, endDate, typeFilter, page, pageSize);
                long totalRecords = transactionService.countFilteredTransactions(selectedAccountId, startDate, endDate, typeFilter);
                int totalPages = (int) Math.ceil((double) totalRecords / pageSize);

                request.setAttribute("accounts", accounts);
                request.setAttribute("selectedAccountId", selectedAccountId);
                request.setAttribute("transactions", transactions);
                request.setAttribute("currentPage", page);
                request.setAttribute("totalPages", totalPages);
                request.setAttribute("startDate", startDateStr);
                request.setAttribute("endDate", endDateStr);
                request.setAttribute("typeFilter", typeFilter);

                request.getRequestDispatcher("/WEB-INF/views/customer/transactions.jsp").forward(request, response);
            }
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading transactions: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/customer/transactions.jsp").forward(request, response);
        }
    }
}
