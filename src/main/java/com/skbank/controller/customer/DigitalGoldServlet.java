package com.skbank.controller.customer;

import com.skbank.model.Account;
import com.skbank.service.AccountService;
import com.skbank.service.impl.AccountServiceImpl;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(urlPatterns = {"/customer/digital-gold"})
public class DigitalGoldServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final AccountService accountService = new AccountServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Long customerId = (Long) session.getAttribute("CUSTOMER_ID");

        try {
            List<Account> accounts = accountService.getCustomerAccounts(customerId);
            request.setAttribute("accounts", accounts);
            request.getRequestDispatcher("/WEB-INF/views/customer/digital-gold.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading Digital Gold: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/customer/digital-gold.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Long customerId = (Long) session.getAttribute("CUSTOMER_ID");

        try {
            Long accountId = Long.parseLong(request.getParameter("accountId"));
            BigDecimal amount = new BigDecimal(request.getParameter("amount"));
            String action = request.getParameter("action"); // "BUY" or "SELL"

            if ("BUY".equalsIgnoreCase(action)) {
                accountService.withdraw(accountId, amount, "24K 99.9% Pure Digital Gold Purchase");
                response.sendRedirect(request.getContextPath() + "/customer/digital-gold?msg=Successfully+purchased+24K+Digital+Gold+worth+₹" + amount);
            } else {
                accountService.deposit(accountId, amount, "24K Digital Gold Sale Proceeds Credited");
                response.sendRedirect(request.getContextPath() + "/customer/digital-gold?msg=Successfully+sold+Digital+Gold!+₹" + amount + "+credited+to+your+account.");
            }
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Gold transaction failed: " + e.getMessage());
            doGet(request, response);
        }
    }
}
