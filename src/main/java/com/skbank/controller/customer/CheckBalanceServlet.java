package com.skbank.controller.customer;

import com.skbank.model.Account;
import com.skbank.service.AccountService;
import com.skbank.service.impl.AccountServiceImpl;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(urlPatterns = {"/customer/check-balance"})
public class CheckBalanceServlet extends HttpServlet {
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
            request.getRequestDispatcher("/WEB-INF/views/customer/check-balance.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading balance checker: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/customer/check-balance.jsp").forward(request, response);
        }
    }
}
