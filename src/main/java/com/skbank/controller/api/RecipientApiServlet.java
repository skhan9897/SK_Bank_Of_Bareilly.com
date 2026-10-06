package com.skbank.controller.api;

import com.google.gson.Gson;
import com.skbank.dto.ApiResponse;
import com.skbank.dto.RecipientLookupDTO;
import com.skbank.service.TransferService;
import com.skbank.service.impl.TransferServiceImpl;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = {"/api/customer/recipient/lookup"})
public class RecipientApiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final TransferService transferService = new TransferServiceImpl();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long senderCustomerId = (Long) request.getAttribute("API_CUSTOMER_ID");
        String mobile = request.getParameter("mobile");
        String account = request.getParameter("accountNumber");
        String upi = request.getParameter("upiAddress");

        try {
            RecipientLookupDTO dto;
            if (mobile != null && !mobile.trim().isEmpty()) {
                dto = transferService.lookupByMobile(mobile, senderCustomerId);
            } else if (account != null && !account.trim().isEmpty()) {
                dto = transferService.lookupByAccount(account, senderCustomerId);
            } else if (upi != null && !upi.trim().isEmpty()) {
                dto = transferService.lookupByUpi(upi, senderCustomerId);
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(ApiResponse.error("Please provide mobile, accountNumber, or upiAddress", "BAD_REQUEST")));
                return;
            }

            response.getWriter().write(gson.toJson(ApiResponse.success("Recipient lookup complete", dto)));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(ApiResponse.error(e.getMessage(), "LOOKUP_FAILED")));
        }
    }
}
