package com.skbank.controller.customer;

import com.skbank.model.Account;
import com.skbank.model.BillPayment;
import com.skbank.service.AccountService;
import com.skbank.service.BillPaymentService;
import com.skbank.service.impl.AccountServiceImpl;
import com.skbank.service.impl.BillPaymentServiceImpl;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(urlPatterns = {"/customer/bill-payments"})
public class BillPaymentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final BillPaymentService billPaymentService = new BillPaymentServiceImpl();
    private final AccountService accountService = new AccountServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Long customerId = (Long) session.getAttribute("CUSTOMER_ID");

        try {
            List<Account> accounts = accountService.getCustomerAccounts(customerId);
            List<BillPayment> history = billPaymentService.getCustomerBillPayments(customerId);

            request.setAttribute("accounts", accounts);
            request.setAttribute("history", history);
            request.getRequestDispatcher("/WEB-INF/views/customer/bill-payments.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading bill payments: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/customer/bill-payments.jsp").forward(request, response);
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

            String billerType = request.getParameter("billerType");
            String billerName = request.getParameter("billerName");
            String consumerNumber = request.getParameter("consumerNumber");
            BigDecimal amount = new BigDecimal(request.getParameter("amount"));

            BillPayment bp = billPaymentService.processBillPayment(customerId, accountId, billerType, billerName, consumerNumber, amount);

            response.sendRedirect(request.getContextPath() + "/customer/bill-payments?msg=Payment of ₹" + amount + " to " + billerName + " completed. Ref: " + bp.getPaymentReference());
        } catch (Exception e) {
            try {
                request.setAttribute("accounts", accountService.getCustomerAccounts(customerId));
                request.setAttribute("history", billPaymentService.getCustomerBillPayments(customerId));
            } catch (Exception ignored) {}
            request.setAttribute("errorMessage", "Bill payment failed: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/customer/bill-payments.jsp").forward(request, response);
        }
    }
}
