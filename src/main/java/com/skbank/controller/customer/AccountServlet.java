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

@WebServlet(urlPatterns = {"/customer/accounts", "/customer/account-details"})
public class AccountServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final AccountService accountService = new AccountServiceImpl();
    private final TransactionService transactionService = new TransactionServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        String customerId = (String) session.getAttribute("CUSTOMER_ID");

        String path = request.getServletPath();

        try {
            if ("/customer/account-details".equals(path)) {
                Long accountId = Long.parseLong(request.getParameter("id"));
                accountService.verifyAccountOwnership(accountId, customerId);

                Account account = accountService.getAccountById(accountId);
                List<Transaction> transactions = transactionService.getAccountTransactions(accountId, 1, 10);

                request.setAttribute("account", account);
                request.setAttribute("transactions", transactions);
                request.getRequestDispatcher("/WEB-INF/views/customer/account-details.jsp").forward(request, response);
            } else {
                List<Account> accounts = accountService.getCustomerAccounts(customerId);
                request.setAttribute("accounts", accounts);
                request.getRequestDispatcher("/WEB-INF/views/customer/accounts.jsp").forward(request, response);
            }
        } catch (Exception e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/customer/accounts.jsp").forward(request, response);
        }
    }
}
