package com.skbank.controller.customer;

import com.skbank.model.Account;
import com.skbank.model.Transaction;
import com.skbank.service.AccountService;
import com.skbank.service.TransactionService;
import com.skbank.service.impl.AccountServiceImpl;
import com.skbank.service.impl.TransactionServiceImpl;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(urlPatterns = {"/customer/statements"})
public class StatementServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final AccountService accountService = new AccountServiceImpl();
    private final TransactionService transactionService = new TransactionServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        String customerId = (String) session.getAttribute("CUSTOMER_ID");

        try {
            List<Account> accounts = accountService.getCustomerAccounts(customerId);
            request.setAttribute("accounts", accounts);

            String accIdStr = request.getParameter("accountId");
            String startDateStr = request.getParameter("startDate");
            String endDateStr = request.getParameter("endDate");

            if (accIdStr != null && !accIdStr.trim().isEmpty()) {
                Long accountId = Long.parseLong(accIdStr);
                accountService.verifyAccountOwnership(accountId, customerId);

                Account account = accountService.getAccountById(accountId);
                Date startDate = (startDateStr != null && !startDateStr.trim().isEmpty()) ? Date.valueOf(startDateStr) : new Date(System.currentTimeMillis() - 30L * 24 * 3600 * 1000);
                Date endDate = (endDateStr != null && !endDateStr.trim().isEmpty()) ? Date.valueOf(endDateStr) : new Date(System.currentTimeMillis());

                List<Transaction> statementTxns = transactionService.getFilteredTransactions(accountId, startDate, endDate, null, 1, 1000);

                BigDecimal totalCredits = BigDecimal.ZERO;
                BigDecimal totalDebits = BigDecimal.ZERO;

                for (Transaction t : statementTxns) {
                    if (t.getAmount() != null) {
                        if ("DEPOSIT".equalsIgnoreCase(t.getTransactionType().name()) ||
                            "INTEREST_CREDIT".equalsIgnoreCase(t.getTransactionType().name()) ||
                            "REFUND".equalsIgnoreCase(t.getTransactionType().name())) {
                            totalCredits = totalCredits.add(t.getAmount());
                        } else {
                            totalDebits = totalDebits.add(t.getAmount());
                        }
                    }
                }

                request.setAttribute("selectedAccount", account);
                request.setAttribute("statementTxns", statementTxns);
                request.setAttribute("startDate", startDate);
                request.setAttribute("endDate", endDate);
                request.setAttribute("totalCredits", totalCredits);
                request.setAttribute("totalDebits", totalDebits);
            }

            request.getRequestDispatcher("/WEB-INF/views/customer/statements.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error generating statement: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/customer/statements.jsp").forward(request, response);
        }
    }
}
