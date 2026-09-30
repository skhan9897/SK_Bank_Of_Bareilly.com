package com.skbank.controller.customer;

import com.skbank.model.RecipientDTO;
import com.skbank.service.RecipientService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/customer/recipient/mobile")
public class RecipientMobileServlet extends HttpServlet {

    private final RecipientService recipientService = new RecipientService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String mobile = request.getParameter("mobile");
        try {
            RecipientDTO recipient = recipientService.findRecipientByMobile(mobile);
            if (recipient != null) {
                out.print(toJson(recipient));
            } else {
                out.print("{\"found\": false, \"message\": \"No active customer found with mobile number " + mobile + "\"}");
            }
        } catch (Exception e) {
            out.print("{\"found\": false, \"message\": \"Search error: " + e.getMessage() + "\"}");
        }
    }

    private String toJson(RecipientDTO dto) {
        return String.format(
            "{\"found\": true, \"customerId\": \"%s\", \"customerName\": \"%s\", \"maskedMobile\": \"%s\", \"accountNumber\": \"%s\", \"maskedAccountNumber\": \"%s\", \"upiId\": \"%s\", \"bankName\": \"%s\", \"branchName\": \"%s\", \"ifsc\": \"%s\", \"status\": \"%s\"}",
            dto.getCustomerId(), dto.getCustomerName(), dto.getMaskedMobile(), dto.getAccountNumber(), dto.getMaskedAccountNumber(), dto.getUpiId(), dto.getBankName(), dto.getBranchName(), dto.getIfsc(), dto.getStatus()
        );
    }
}
