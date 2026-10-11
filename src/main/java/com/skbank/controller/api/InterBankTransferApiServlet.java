package com.skbank.controller.api;

import com.google.gson.Gson;
import com.skbank.dao.AccountDAO;
import com.skbank.dao.CustomerDAO;
import com.skbank.dao.UpiDAO;
import com.skbank.dao.impl.AccountDAOImpl;
import com.skbank.dao.impl.CustomerDAOImpl;
import com.skbank.dao.impl.UpiDAOImpl;
import com.skbank.dto.ApiResponse;
import com.skbank.dto.TransferDTO;
import com.skbank.model.Account;
import com.skbank.model.Customer;
import com.skbank.model.TransferRequest;
import com.skbank.model.UpiAccount;
import com.skbank.service.TransferService;
import com.skbank.service.UpiService;
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

@WebServlet(urlPatterns = {"/api/interbank/transfer"})
public class InterBankTransferApiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final AccountDAO accountDAO = new AccountDAOImpl();
    private final CustomerDAO customerDAO = new CustomerDAOImpl();
    private final UpiDAO upiDAO = new UpiDAOImpl();
    private final TransferService transferService = new TransferServiceImpl();
    private final UpiService upiService = new UpiServiceImpl();
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            String senderDetail = request.getParameter("senderDetail");
            String receiverDetail = request.getParameter("receiverDetail");
            BigDecimal amount = new BigDecimal(request.getParameter("amount"));
            String remarks = request.getParameter("remarks");
            String upiPin = request.getParameter("upiPin");

            if (senderDetail == null || receiverDetail == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(ApiResponse.error("Invalid transfer parameters", "INVALID_INPUT")));
                return;
            }

            // 1. Resolve Sender Account
            Account senderAcc = resolveAccount(senderDetail);
            if (senderAcc == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write(gson.toJson(ApiResponse.error("Sender Account / UPI ID not found", "SENDER_NOT_FOUND")));
                return;
            }

            // 2. Verify UPI PIN
            if (upiPin != null && !upiPin.trim().isEmpty()) {
                upiService.verifyUpiPin(senderAcc.getCustomerId(), upiPin);
            }

            // 3. Resolve Receiver Account
            Account receiverAcc = resolveAccount(receiverDetail);
            if (receiverAcc == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write(gson.toJson(ApiResponse.error("Receiver Account / UPI ID not found", "RECEIVER_NOT_FOUND")));
                return;
            }

            if (senderAcc.getAccountId().equals(receiverAcc.getAccountId())) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(ApiResponse.error("Sender and Receiver cannot be the same account", "SAME_ACCOUNT")));
                return;
            }

            // 4. Process Inter-Bank Transfer in Shared Database
            TransferDTO dto = new TransferDTO();
            dto.setSenderAccountId(senderAcc.getAccountId());
            dto.setReceiverAccountId(receiverAcc.getAccountId());
            dto.setTransferType("INTERBANK_SKMINI");
            dto.setAmount(amount);
            dto.setRemarks(remarks != null && !remarks.trim().isEmpty() ? remarks : "Inter-Bank Transfer SK Mini Bank");
            dto.setRecipientName(receiverDetail);

            TransferRequest tr = transferService.processTransfer(dto, senderAcc.getCustomerId());

            response.getWriter().write(gson.toJson(ApiResponse.success("Inter-Bank Transfer Executed Successfully", tr)));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(ApiResponse.error("Inter-Bank Transfer Failed: " + e.getMessage(), "TRANSFER_FAILED")));
        }
    }

    private Account resolveAccount(String detail) {
        if (detail == null || detail.trim().isEmpty()) return null;
        String clean = detail.trim();

        try {
            // Check by UPI Address
            UpiAccount upi = upiDAO.findByUpiAddress(clean);
            if (upi == null && !clean.contains("@")) {
                upi = upiDAO.findByUpiAddress(clean + "@skbank");
                if (upi == null) {
                    upi = upiDAO.findByUpiAddress(clean + "@skminibank");
                }
            }
            if (upi != null) {
                return accountDAO.findById(upi.getAccountId());
            }

            // Check by Account Number
            Account acc = accountDAO.findByAccountNumber(clean);
            if (acc != null) return acc;

            // Check by Customer Mobile
            Customer cust = customerDAO.findByMobile(clean);
            if (cust != null) {
                List<Account> list = accountDAO.findByCustomerId(cust.getCustomerId());
                if (!list.isEmpty()) return list.get(0);
            }

            // Check by numeric Account ID
            if (clean.matches("\\d+")) {
                return accountDAO.findById(Long.parseLong(clean));
            }
        } catch (Exception ignored) {}
        return null;
    }
}
