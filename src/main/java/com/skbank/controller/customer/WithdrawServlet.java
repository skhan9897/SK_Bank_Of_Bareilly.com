package com.skbank.controller.customer;

import com.skbank.model.Account;
import com.skbank.model.Transaction;
import com.skbank.service.AccountService;
import com.skbank.service.TransactionService;
import com.skbank.service.impl.AccountServiceImpl;
import com.skbank.service.impl.TransactionServiceImpl;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(urlPatterns = {"/customer/withdraw", "/customer/withdraw-success"})
public class WithdrawServlet extends HttpServlet {
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
            if ("/customer/withdraw-success".equals(path)) {
                request.getRequestDispatcher("/WEB-INF/views/customer/withdraw-success.jsp").forward(request, response);
            } else {
                List<Account> accounts = accountService.getCustomerAccounts(customerId);
                request.setAttribute("accounts", accounts);
                request.getRequestDispatcher("/WEB-INF/views/customer/withdraw.jsp").forward(request, response);
            }
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading withdrawal form: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/customer/withdraw.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        String customerId = (String) session.getAttribute("CUSTOMER_ID");

        try {
            Long accountId = Long.parseLong(request.getParameter("accountId"));
            accountService.verifyAccountOwnership(accountId, customerId);

            BigDecimal amount = new BigDecimal(request.getParameter("amount"));
            String description = request.getParameter("description");

            Transaction txn = transactionService.processWithdrawal(accountId, amount, description);

            session.setAttribute("LAST_WITHDRAWAL_TXN", txn);
            response.sendRedirect(request.getContextPath() + "/customer/withdraw-success");
        } catch (Exception e) {
            try {
                request.setAttribute("accounts", accountService.getCustomerAccounts(customerId));
            } catch (Exception ignored) {}
            request.setAttribute("errorMessage", "Withdrawal failed: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/customer/withdraw.jsp").forward(request, response);
        }
    }
}
