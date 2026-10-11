package com.skbank.controller.customer;

import com.skbank.model.Account;
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

@WebServlet(urlPatterns = {"/customer/rewards"})
public class RewardsServlet extends HttpServlet {
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
            request.getRequestDispatcher("/WEB-INF/views/customer/rewards.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading rewards: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/customer/rewards.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Long customerId = (Long) session.getAttribute("CUSTOMER_ID");

        try {
            BigDecimal rewardAmount = new BigDecimal(request.getParameter("rewardAmount"));
            List<Account> accounts = accountService.getCustomerAccounts(customerId);

            if (!accounts.isEmpty()) {
                Account mainAcc = accounts.get(0);
                transactionService.processDeposit(mainAcc.getAccountId(), rewardAmount, "Cashback Reward Earned (PhonePe Bank Rewards)", customerId);
            }

            response.sendRedirect(request.getContextPath() + "/customer/rewards?msg=Cashback+of+₹" + rewardAmount + "+credited+to+your+account!");
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Claim reward failed: " + e.getMessage());
            doGet(request, response);
        }
    }
}
