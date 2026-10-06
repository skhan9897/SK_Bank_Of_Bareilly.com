package com.skbank.controller.customer;

import com.skbank.model.Account;
import com.skbank.model.UpiAccount;
import com.skbank.service.AccountService;
import com.skbank.service.UpiService;
import com.skbank.service.impl.AccountServiceImpl;
import com.skbank.service.impl.UpiServiceImpl;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(urlPatterns = {"/customer/upi"})
public class UpiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final UpiService upiService = new UpiServiceImpl();
    private final AccountService accountService = new AccountServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Long customerId = (Long) session.getAttribute("CUSTOMER_ID");

        try {
            List<Account> accounts = accountService.getCustomerAccounts(customerId);
            List<UpiAccount> upiAccounts = upiService.getCustomerUpiAccounts(customerId);

            request.setAttribute("accounts", accounts);
            request.setAttribute("upiAccounts", upiAccounts);
            request.getRequestDispatcher("/WEB-INF/views/customer/upi.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading UPI settings: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/customer/upi.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Long customerId = (Long) session.getAttribute("CUSTOMER_ID");

        try {
            Long accountId = Long.parseLong(request.getParameter("accountId"));
            String customHandle = request.getParameter("customHandle");
            String pin = request.getParameter("upiPin");

            UpiAccount upiAcc = upiService.registerUpi(customerId, accountId, customHandle, pin);

            response.sendRedirect(request.getContextPath() + "/customer/upi?msg=UPI ID " + upiAcc.getUpiAddress() + " created successfully.");
        } catch (Exception e) {
            try {
                request.setAttribute("accounts", accountService.getCustomerAccounts(customerId));
                request.setAttribute("upiAccounts", upiService.getCustomerUpiAccounts(customerId));
            } catch (Exception ignored) {}
            request.setAttribute("errorMessage", "UPI Registration failed: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/customer/upi.jsp").forward(request, response);
        }
    }
}
