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

@WebServlet(urlPatterns = {
    "/api/customer/recipient/mobile",
    "/api/customer/recipient/account",
    "/api/customer/recipient/upi"
})
public class RecipientApiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final TransferService transferService = new TransferServiceImpl();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String senderCustomerId = (String) request.getAttribute("API_CUSTOMER_ID");
        String path = request.getServletPath();

        try {
            RecipientLookupDTO result;
            if ("/api/customer/recipient/mobile".equals(path)) {
                String mobile = request.getParameter("mobile");
                result = transferService.lookupByMobile(mobile, senderCustomerId);
            } else if ("/api/customer/recipient/account".equals(path)) {
                String account = request.getParameter("accountNumber");
                result = transferService.lookupByAccount(account, senderCustomerId);
            } else {
                String upi = request.getParameter("upiAddress");
                result = transferService.lookupByUpi(upi, senderCustomerId);
            }

            response.getWriter().write(gson.toJson(ApiResponse.success("Recipient lookup complete", result)));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(ApiResponse.error(e.getMessage(), "RECIPIENT_NOT_FOUND")));
        }
    }
}
