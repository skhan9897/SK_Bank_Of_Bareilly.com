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
            UpiAccount upi = upiService.getUpiByCustomerId(customerId);
            List<Account> accounts = accountService.getCustomerAccounts(customerId);

            request.setAttribute("upi", upi);
            request.setAttribute("accounts", accounts);
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

        String action = request.getParameter("action");

        try {
            if ("create".equalsIgnoreCase(action)) {
                Long accountId = Long.parseLong(request.getParameter("accountId"));
                String upiAddress = request.getParameter("upiAddress");
                String pin = request.getParameter("upiPin");

                upiService.createUpiAccount(customerId, accountId, upiAddress, pin);
                response.sendRedirect(request.getContextPath() + "/customer/upi?msg=UPI ID created successfully!");
            } else if ("changePin".equalsIgnoreCase(action)) {
                String oldPin = request.getParameter("oldPin");
                String newPin = request.getParameter("newPin");

                upiService.changeUpiPin(customerId, oldPin, newPin);
                response.sendRedirect(request.getContextPath() + "/customer/upi?msg=UPI PIN changed successfully!");
            } else if ("disable".equalsIgnoreCase(action)) {
                upiService.disableUpi(customerId);
                response.sendRedirect(request.getContextPath() + "/customer/upi?msg=UPI ID disabled.");
            } else {
                response.sendRedirect(request.getContextPath() + "/customer/upi");
            }
        } catch (Exception e) {
            try {
                request.setAttribute("upi", upiService.getUpiByCustomerId(customerId));
                request.setAttribute("accounts", accountService.getCustomerAccounts(customerId));
            } catch (Exception ignored) {}
            request.setAttribute("errorMessage", "UPI Operation failed: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/customer/upi.jsp").forward(request, response);
        }
    }
}
