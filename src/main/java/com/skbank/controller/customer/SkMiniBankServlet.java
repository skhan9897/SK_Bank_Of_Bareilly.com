package com.skbank.controller.customer;

import com.skbank.dao.AccountDAO;
import com.skbank.dao.CustomerDAO;
import com.skbank.dao.UpiDAO;
import com.skbank.dao.impl.AccountDAOImpl;
import com.skbank.dao.impl.CustomerDAOImpl;
import com.skbank.dao.impl.UpiDAOImpl;
import com.skbank.dto.TransferDTO;
import com.skbank.model.Account;
import com.skbank.model.Customer;
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

@WebServlet(urlPatterns = {"/customer/sk-mini-bank"})
public class SkMiniBankServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final AccountDAO accountDAO = new AccountDAOImpl();
    private final CustomerDAO customerDAO = new CustomerDAOImpl();
    private final UpiDAO upiDAO = new UpiDAOImpl();
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
            UpiAccount upi = upiService.getOrCreateDefaultUpiAccount(customerId);
            request.setAttribute("accounts", accounts);
            request.setAttribute("upi", upi);
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
            String miniBankDetail = request.getParameter("miniBankDetail");
            BigDecimal amount = new BigDecimal(request.getParameter("amount"));
            String remarks = request.getParameter("remarks");

            // Resolve exact receiver account in shared database
            Account receiverAccount = resolveAccount(miniBankDetail);

            if (receiverAccount == null) {
                throw new Exception("SK Mini Bank account or UPI ID '" + miniBankDetail + "' not found in database.");
            }

            if (receiverAccount.getAccountId().equals(senderAccountId)) {
                throw new Exception("Sender and receiver account cannot be the same.");
            }

            TransferDTO dto = new TransferDTO();
            dto.setSenderAccountId(senderAccountId);
            dto.setReceiverAccountId(receiverAccount.getAccountId());
            dto.setTransferType("MINI_BANK_UPI");
            dto.setAmount(amount);
            dto.setRemarks(remarks != null && !remarks.trim().isEmpty() ? remarks : "SK Mini Bank Inter-Bank Transfer");
            dto.setRecipientName("SK Mini Bank (" + miniBankDetail + ")");

            TransferRequest tr = transferService.processTransfer(dto, customerId);

            session.setAttribute("LAST_TRANSFER", tr);
            session.setAttribute("LAST_TRANSFER_DTO", dto);

            response.sendRedirect(request.getContextPath() + "/customer/transfer-success");
        } catch (Exception e) {
            try {
                request.setAttribute("accounts", accountService.getCustomerAccounts(customerId));
                request.setAttribute("upi", upiService.getOrCreateDefaultUpiAccount(customerId));
            } catch (Exception ignored) {}
            request.setAttribute("errorMessage", "SK Mini Bank Transfer Failed: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/customer/sk-mini-bank.jsp").forward(request, response);
        }
    }

    private Account resolveAccount(String detail) {
        if (detail == null || detail.trim().isEmpty()) return null;
        String clean = detail.trim();

        try {
            // 1. Check by exact UPI Address
            UpiAccount upi = upiDAO.findByUpiAddress(clean);
            if (upi == null && !clean.contains("@")) {
                upi = upiDAO.findByUpiAddress(clean + "@skminibank");
                if (upi == null) {
                    upi = upiDAO.findByUpiAddress(clean + "@skbank");
                }
            } else if (upi == null && clean.contains("@")) {
                String prefix = clean.substring(0, clean.indexOf("@"));
                upi = upiDAO.findByUpiAddress(prefix + "@skminibank");
                if (upi == null) {
                    upi = upiDAO.findByUpiAddress(prefix + "@skbank");
                }
            }
            if (upi != null) {
                return accountDAO.findById(upi.getAccountId());
            }

            // 2. Check by Account Number
            Account acc = accountDAO.findByAccountNumber(clean);
            if (acc != null) return acc;

            // 3. Check by Customer Mobile
            Customer cust = customerDAO.findByMobile(clean);
            if (cust != null) {
                List<Account> list = accountDAO.findByCustomerId(cust.getCustomerId());
                if (!list.isEmpty()) return list.get(0);
            }

            // 4. Check by numeric Account ID
            if (clean.matches("\\d+")) {
                return accountDAO.findById(Long.parseLong(clean));
            }

            // 5. Fallback: if details contain MINI or user, find first available mini or other account
            List<Account> allAccs = accountDAO.findAll(0, 100, null);
            for (Account a : allAccs) {
                if (a.getAccountNumber() != null && a.getAccountNumber().contains(clean)) {
                    return a;
                }
            }
        } catch (Exception ignored) {}
        return null;
    }
}
