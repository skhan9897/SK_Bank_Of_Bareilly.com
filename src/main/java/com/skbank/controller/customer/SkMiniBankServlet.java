package com.skbank.controller.customer;

import com.skbank.dto.TransferDTO;
import com.skbank.model.Account;
import com.skbank.model.TransferRequest;
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

@WebServlet(urlPatterns = {"/customer/sk-mini-bank"})
public class SkMiniBankServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final AccountService accountService = new AccountServiceImpl();
    private final TransferService transferService = new TransferServiceImpl();
    private final UpiService upiService = new UpiServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Long customerId = (Long) session.getAttribute("CUSTOMER_ID");

        try {
            List<Account> accounts = accountService.getCustomerAccounts(customerId);
            request.setAttribute("accounts", accounts);
            request.getRequestDispatcher("/WEB-INF/views/customer/sk-mini-bank.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading SK Mini Bank Terminal: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/customer/sk-mini-bank.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Long customerId = (Long) session.getAttribute("CUSTOMER_ID");

        try {
            String upiPin = request.getParameter("upiPin");
            upiService.verifyUpiPin(customerId, upiPin);

            Long senderAccountId = Long.parseLong(request.getParameter("senderAccountId"));
            String miniBankDetail = request.getParameter("miniBankDetail"); // Account No or UPI ID
            BigDecimal amount = new BigDecimal(request.getParameter("amount"));
            String remarks = request.getParameter("remarks");

            // Look up secondary account or process transfer
            List<Account> senderAccounts = accountService.getCustomerAccounts(customerId);
            Long receiverAccountId = senderAccounts.get(0).getAccountId();
            for (Account acc : senderAccounts) {
                if (!acc.getAccountId().equals(senderAccountId)) {
                    receiverAccountId = acc.getAccountId();
                    break;
                }
            }

            TransferDTO dto = new TransferDTO();
            dto.setSenderAccountId(senderAccountId);
            dto.setReceiverAccountId(receiverAccountId);
            dto.setTransferType("MINI_BANK_UPI");
            dto.setAmount(amount);
            dto.setRemarks(remarks != null && !remarks.trim().isEmpty() ? remarks : "SK Mini Bank Transfer");
            dto.setRecipientName("SK Mini Bank (" + miniBankDetail + ")");

            TransferRequest tr = transferService.processTransfer(dto, customerId);

            session.setAttribute("LAST_TRANSFER", tr);
            session.setAttribute("LAST_TRANSFER_DTO", dto);

            response.sendRedirect(request.getContextPath() + "/customer/transfer-success");
        } catch (Exception e) {
            try {
                request.setAttribute("accounts", accountService.getCustomerAccounts(customerId));
            } catch (Exception ignored) {}
            request.setAttribute("errorMessage", "SK Mini Bank Payment Failed: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/customer/sk-mini-bank.jsp").forward(request, response);
        }
    }
}
