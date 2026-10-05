package com.skbank.controller.customer;

import com.google.gson.Gson;
import com.skbank.dto.RecipientLookupDTO;
import com.skbank.service.TransferService;
import com.skbank.service.impl.TransferServiceImpl;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(urlPatterns = {
    "/customer/recipient/mobile",
    "/customer/recipient/account",
    "/customer/recipient/upi"
})
public class RecipientServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final TransferService transferService = new TransferServiceImpl();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        String senderCustomerId = (String) session.getAttribute("CUSTOMER_ID");

        String path = request.getServletPath();
        RecipientLookupDTO result;

        try {
            if ("/customer/recipient/mobile".equals(path)) {
                String mobile = request.getParameter("mobile");
                result = transferService.lookupByMobile(mobile, senderCustomerId);
            } else if ("/customer/recipient/account".equals(path)) {
                String account = request.getParameter("accountNumber");
                result = transferService.lookupByAccount(account, senderCustomerId);
            } else {
                String upi = request.getParameter("upiAddress");
                result = transferService.lookupByUpi(upi, senderCustomerId);
            }
        } catch (Exception e) {
            result = new RecipientLookupDTO();
            result.setSuccess(false);
            result.setMessage("Error during recipient lookup: " + e.getMessage());
        }

        response.getWriter().write(gson.toJson(result));
    }
}
