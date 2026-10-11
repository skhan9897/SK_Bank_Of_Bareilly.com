package com.skbank.controller.customer;

import com.skbank.dto.TransferDTO;
import com.skbank.model.Account;
import com.skbank.model.TransferRequest;
import com.skbank.model.UpiAccount;
import com.skbank.service.AccountService;
import com.skbank.service.TransferService;
import com.skbank.service.UpiService;
import com.skbank.service.impl.AccountServiceImpl;
import com.skbank.service.impl.TransferServiceImpl;
import com.skbank.service.impl.UpiServiceImpl;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(urlPatterns = {"/customer/qr-pay"})
public class QrPayServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final AccountService accountService = new AccountServiceImpl();
    private final UpiService upiService = new UpiServiceImpl();
    private final TransferService transferService = new TransferServiceImpl();

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
            request.getRequestDispatcher("/WEB-INF/views/customer/qr-pay.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading QR Pay: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/customer/qr-pay.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Long customerId = (Long) session.getAttribute("CUSTOMER_ID");

        try {
            Long senderAccountId = Long.parseLong(request.getParameter("senderAccountId"));
            String recipientDetail = request.getParameter("recipientDetail");
            BigDecimal amount = new BigDecimal(request.getParameter("amount"));
            String remarks = request.getParameter("remarks");
            String upiPin = request.getParameter("upiPin");

            TransferDTO dto = new TransferDTO();
            dto.setSenderAccountId(senderAccountId);
            dto.setTransferMode("UPI");
            dto.setRecipientDetail(recipientDetail);
            dto.setAmount(amount);
            dto.setRemarks(remarks != null && !remarks.trim().isEmpty() ? remarks : "PhonePe QR Pay");

            TransferRequest tr = transferService.processTransfer(dto, customerId);

            session.setAttribute("LAST_TRANSFER", tr);
            session.setAttribute("LAST_TRANSFER_DTO", dto);

            response.sendRedirect(request.getContextPath() + "/customer/transfer-success");
        } catch (Exception e) {
            try {
                request.setAttribute("accounts", accountService.getCustomerAccounts(customerId));
                request.setAttribute("upiAccounts", upiService.getCustomerUpiAccounts(customerId));
            } catch (Exception ignored) {}
            request.setAttribute("errorMessage", "QR Payment Failed: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/customer/qr-pay.jsp").forward(request, response);
        }
    }
}
