package com.skbank.controller.customer;

import com.skbank.dto.TransferDTO;
import com.skbank.model.TransferRequest;
import com.skbank.service.TransferService;
import com.skbank.service.impl.TransferServiceImpl;

import java.io.IOException;
import java.math.BigDecimal;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(urlPatterns = {
    "/customer/transfer",
    "/customer/transfer-confirm",
    "/customer/transfer-success"
})
public class TransferServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final TransferService transferService = new TransferServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        if ("/customer/transfer-success".equals(path)) {
            request.getRequestDispatcher("/WEB-INF/views/customer/transfer-success.jsp").forward(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/customer/send-money");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Long customerId = (Long) session.getAttribute("CUSTOMER_ID");

        String path = request.getServletPath();

        if ("/customer/transfer-confirm".equals(path)) {
            // Confirmation Step
            try {
                TransferDTO dto = new TransferDTO();
                dto.setSenderAccountId(Long.parseLong(request.getParameter("senderAccountId")));
                dto.setReceiverAccountId(Long.parseLong(request.getParameter("receiverAccountId")));
                dto.setAmount(new BigDecimal(request.getParameter("amount")));
                dto.setTransferType(request.getParameter("transferType"));
                dto.setRemarks(request.getParameter("remarks"));
                dto.setRecipientName(request.getParameter("recipientName"));

                request.setAttribute("transferData", dto);
                request.getRequestDispatcher("/WEB-INF/views/customer/transfer-confirmation.jsp").forward(request, response);
            } catch (Exception e) {
                request.setAttribute("errorMessage", "Invalid transfer parameters: " + e.getMessage());
                response.sendRedirect(request.getContextPath() + "/customer/send-money?error=" + e.getMessage());
            }
        } else {
            // Execution Step
            try {
                TransferDTO dto = new TransferDTO();
                dto.setSenderAccountId(Long.parseLong(request.getParameter("senderAccountId")));
                dto.setReceiverAccountId(Long.parseLong(request.getParameter("receiverAccountId")));
                dto.setAmount(new BigDecimal(request.getParameter("amount")));
                dto.setTransferType(request.getParameter("transferType"));
                dto.setRemarks(request.getParameter("remarks"));
                dto.setRecipientName(request.getParameter("recipientName"));

                TransferRequest tr = transferService.processTransfer(dto, customerId);

                session.setAttribute("LAST_TRANSFER", tr);
                session.setAttribute("LAST_TRANSFER_DTO", dto);

                response.sendRedirect(request.getContextPath() + "/customer/transfer-success");
            } catch (Exception e) {
                request.setAttribute("errorMessage", "Transfer failed: " + e.getMessage());
                response.sendRedirect(request.getContextPath() + "/customer/send-money?error=" + e.getMessage());
            }
        }
    }
}
