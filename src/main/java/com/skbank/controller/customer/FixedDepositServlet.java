package com.skbank.controller.customer;

import com.skbank.model.Account;
import com.skbank.model.FixedDeposit;
import com.skbank.service.AccountService;
import com.skbank.service.FdService;
import com.skbank.service.impl.AccountServiceImpl;
import com.skbank.service.impl.FdServiceImpl;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(urlPatterns = {"/customer/fixed-deposits", "/customer/fd-details"})
public class FixedDepositServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final FdService fdService = new FdServiceImpl();
    private final AccountService accountService = new AccountServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Long customerId = (Long) session.getAttribute("CUSTOMER_ID");

        String path = request.getServletPath();

        try {
            if ("/customer/fd-details".equals(path)) {
                Long fdId = Long.parseLong(request.getParameter("id"));
                FixedDeposit fd = fdService.getFdById(fdId);
                if (!fd.getCustomerId().equals(customerId)) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }
                request.setAttribute("fd", fd);
                request.getRequestDispatcher("/WEB-INF/views/customer/fd-details.jsp").forward(request, response);
            } else {
                List<FixedDeposit> fds = fdService.getCustomerFds(customerId);
                List<Account> accounts = accountService.getCustomerAccounts(customerId);

                request.setAttribute("fds", fds);
                request.setAttribute("accounts", accounts);
                request.getRequestDispatcher("/WEB-INF/views/customer/fixed-deposits.jsp").forward(request, response);
            }
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading FD details: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/customer/fixed-deposits.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Long customerId = (Long) session.getAttribute("CUSTOMER_ID");

        try {
            Long accountId = Long.parseLong(request.getParameter("accountId"));
            accountService.verifyAccountOwnership(accountId, customerId);

            BigDecimal principal = new BigDecimal(request.getParameter("principalAmount"));
            int tenureMonths = Integer.parseInt(request.getParameter("tenureMonths"));

            FixedDeposit fd = fdService.openFd(customerId, accountId, principal, tenureMonths);

            response.sendRedirect(request.getContextPath() + "/customer/fixed-deposits?msg=Fixed Deposit " + fd.getFdNumber() + " opened successfully!");
        } catch (Exception e) {
            try {
                request.setAttribute("fds", fdService.getCustomerFds(customerId));
                request.setAttribute("accounts", accountService.getCustomerAccounts(customerId));
            } catch (Exception ignored) {}
            request.setAttribute("errorMessage", "FD Creation failed: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/customer/fixed-deposits.jsp").forward(request, response);
        }
    }
}
